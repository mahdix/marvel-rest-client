package com.mahdix.app.report;

import java.util.*;
import java.io.*;

import com.mahdix.app.calculator.*;
import com.mahdix.app.entities.*;
import com.mahdix.app.datasource.*;

public class StandardReport implements Report {
    private final ComicDatabase db;
    private final Calculator calculator;
    private PrintStream out;
    private int target = 0;

    public StandardReport(ComicDatabase db, Calculator calculator, int target) {
        this.calculator = calculator;
        this.target = target;
        this.db = db;
    }

    public void writeReport(PrintStream out){
        this.out = out;

        if ( target == 0 || target == 1 ) reportSortedFigures();
        if ( target == 0 || target == 2 ) reportPopularFigures(10);
        if ( target == 0 || target == 3 ) reportInfluentialFigures(10);
        if ( target == 0 || target == 4 ) reportHighCoverageFigures(10);
    }

    private void reportSortedFigures() {
        List<Figure> figures = db.getFiguresList();

        Collections.sort(figures, new Comparator<Figure>() {
            public int compare(Figure a, Figure b) {
                return a.getName().compareTo(b.getName());
            }
        });

        out.println();
        out.format("Sorted characters%n");
        out.format("=========================");
        for(Figure f: figures) {
            out.println(f.getName());
        }
    }

    private void reportPopularFigures(int count) {
        List<Figure> figures = db.getFiguresList();

        Collections.sort(figures, new Comparator<Figure>() {
            public int compare(Figure a, Figure b) {
                return db.getFigureComics(a).size() - db.getFigureComics(b).size();
            }
        });

        out.println();
        out.format("Top %d popular characters%n", count);
        out.format("=========================");
        for(int i=0;i<count;i++) {
            Figure f = figures.get(i);
            int comicCount = db.getFigureComics(f).size();

            out.format("%s (played in %d comics)%n", f.getName(), comicCount);
        }
    }

    private void reportInfluentialFigures(int count) {
        List<Figure> figures = db.getFiguresList();

        Collections.sort(figures, new Comparator<Figure>() {
            public int compare(Figure a, Figure b) {
                double d1 = calculator.calculateIndividualInfluence(db, a);
                double d2 = calculator.calculateIndividualInfluence(db, b);

                if ( d1 > d2 ) return 1;
                if ( d1 < d2 ) return -1;

                return 0;
            }
        });

        out.println();
        out.format("Top %d influential characters%n", count);
        out.format("=========================");
        for(int i=0;i<count;i++) {
            Figure f = figures.get(i);

            out.println(f.getName());
        }
    }

    private void reportHighCoverageFigures(int count) {
        //we are going to manipulate data
        final ComicDatabase db = this.db.clone();

        out.println();
        out.format("Top %d characters with most coverage%n", count);
        out.format("=========================");
        for(int i=0;i<count;i++) {
            List<Figure> figures = db.getFiguresList();

            Collections.sort(figures, new Comparator<Figure>() {
                public int compare(Figure a, Figure b) {
                    double d1 = calculator.calculateIndividualInfluence(db, a);
                    double d2 = calculator.calculateIndividualInfluence(db, b);

                    if ( d1 > d2 ) return 1;
                    if ( d1 < d2 ) return -1;

                    return 0;
                }
            });

            //pick the most influential character
            Figure f = figures.get(figures.size()-1);
            out.println(f.getName());

            //Now remove this figure and his roles from the database
            db.eliminateFigure(f);
        }
    }
}
