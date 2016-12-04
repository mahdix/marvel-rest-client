package com.mahdix.app.entities;

import java.io.*;

public class Role implements Serializable
{
    private Figure figure;
    private MComic comic;

    public Role(Figure ch, MComic mv) {
        this.figure = ch;
        this.comic = mv;
    }

    public Figure getFigure() {
        return figure;
    }

    public MComic getComic() {
        return comic;
    }
}

