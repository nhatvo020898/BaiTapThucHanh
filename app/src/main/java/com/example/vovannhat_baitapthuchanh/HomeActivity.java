package com.example.vovannhat_baitapthuchanh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refresh;
    private ArrayList<Song> songs= new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;

    Dialog dialog;

    private String url="https://5fcebeba3e19cc00167c61c8.mockapi.io/Song";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        refresh= findViewById(R.id.swipedown);
        recyclerView= findViewById(R.id.rcv_song);

        dialog= new Dialog(this);
        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                songs.clear();
                GetData();
            }
        });
    }

    private void GetData(){

        refresh.setRefreshing(true);

        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject= null;
                for (int  i = 0;i<response.length(); i++){
                    try {
                        jsonObject= response.getJSONObject(i);

                        Song s= new Song();
                        s.setId(jsonObject.getInt("id"));
                        s.setName(jsonObject.getString("name"));
                        songs.add(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapterPush(songs);
                refresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Error make by API server!", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Song> songs) {
        songAdapter = new SongAdapter(this, songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songAdapter);

    }

    public void addSong(View v){
        TextView close;
        final EditText edt_name;
        Button btnSubmit;

        dialog.setContentView(R.layout.update_dialog);


        close= dialog.findViewById(R.id.txt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edt_name= dialog.findViewById(R.id.name);
        btnSubmit= dialog.findViewById(R.id.submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data= edt_name.getText().toString();
                Submit(data);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void Submit(final String data) {
        StringRequest request= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                refresh.post(new Runnable() {
                    @Override
                    public void run() {
                        songs.clear();
                        GetData();
                    }
                });
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params= new HashMap<>();
                params.put("name", data);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onRefresh() {
        songs.clear();
        GetData();
    }
}