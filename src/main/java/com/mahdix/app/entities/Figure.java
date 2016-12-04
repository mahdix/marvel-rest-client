package com.mahdix.app.entities;

import java.io.*;

public class Figure implements Serializable
{
    private String name;
    private int id;

    public Figure(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Figure(Figure f) {
        this.name = f.name;
        this.id = f.id;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }
}
