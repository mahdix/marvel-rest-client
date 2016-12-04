package com.mahdix.app.entities;

import java.io.*;

public class MComic implements Serializable
{
    private int id;
    private String title;

    public MComic(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public MComic(MComic m) {
        this.id = m.id;
        this.title = m.title;
    }

    public String getTitle() {
        return this.title;
    }

    public int getId() {
        return this.id;
    }
}

