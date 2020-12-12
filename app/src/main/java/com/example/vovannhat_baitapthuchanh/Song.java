package com.example.vovannhat_baitapthuchanh;

public class Song {
    private int id;
    private String name;

    public Song(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Song() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
