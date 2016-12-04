package com.mahdix.app;

import java.io.*;

import com.mahdix.app.datasource.*;
import com.mahdix.app.calculator.*;
import com.mahdix.app.report.*;

public class Main 
{
    public static void main( String[] args )
    {
        if ( args.length == 0 ) {
            System.out.println("Usage: Main (fetch|report)");
            return;
        }

        //Shall we fetch or process the data?
        if ( args[0].equals("fetch")) {

            //Load database. If there exists a db file, load data from it
            ComicDatabase db = new ComicDatabase();

            String apiKeyPublic = "cc0f2e679b61fba9fc9610b9497fd1e9";
            String apiKeyPrivate = "1f2f1e9639807aeb030a92b9285d5cf5f9d12337";

            //initialize connection to Marvel API 
            MarvelApiDataSource source = new MarvelApiDataSource(apiKeyPublic, apiKeyPrivate);

            //fetch (or resume previously interrupted fetch) from Marvel API
            //and populate comics, characters and roles
            source.fetchData(db);

            //persist read data
            boolean result = db.save();

            if ( result ) {
                System.out.println("Saved database.");
            } else {
                System.out.println("Error saving database!");
            }
            
            return;
        }

        if ( !args[0].equals("report")) {
            System.out.println("Invalid argument");
            return;
        }

        //which part of the report is requested? 
        int count = 0;
        if ( args.length == 2 ) {
            count = Integer.valueOf(args[1]);
            if ( count < 0 || count > 4 ) {
                System.out.println("Invalid number for report!");
                return;
            }
        }

        ComicDatabase db = new ComicDatabase();
        System.out.format("Cache has %d figures, %d comics and %d roles%n", db.getFigures().size(), db.getComics().size(), db.getRoles().size());

        Calculator calculator = new StandardCalculator();

        Report report = new StandardReport(db, calculator, count);

        report.writeReport(System.out);
    }
}
