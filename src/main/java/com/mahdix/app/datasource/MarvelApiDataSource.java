package com.mahdix.app.datasource;

import java.util.*;
import java.text.*;

import com.github.codingricky.marvel.model.Comic;
import com.github.codingricky.marvel.model.ComicSummary;
import com.github.codingricky.marvel.model.Container;
import com.github.codingricky.marvel.model.ComicList;
import com.github.codingricky.marvel.model.Event;
import com.github.codingricky.marvel.model.MarvelCharacter;
import com.github.codingricky.marvel.model.Result;
import com.github.codingricky.marvel.model.Series;
import com.github.codingricky.marvel.model.Story;
import com.github.codingricky.marvel.parameter.CharacterOrderBy;
import com.github.codingricky.marvel.parameter.CharacterParameterBuilder;
import com.github.codingricky.marvel.parameter.ComicOrderBy;
import com.github.codingricky.marvel.parameter.ComicParametersBuilder;
import com.github.codingricky.marvel.parameter.EventParametersBuilder;
import com.github.codingricky.marvel.parameter.SeriesParametersBuilder;
import com.github.codingricky.marvel.parameter.StoryParametersBuilder;
import com.github.codingricky.marvel.RestClient;

import com.mahdix.app.entities.*;

/* An implementation of DataSource interface for Marvel-API
 */
public class MarvelApiDataSource implements DataSource {
    private RestClient restClient;
    private ComicDatabase db;

    public MarvelApiDataSource(String apiKeyPublic, String apiKeyPrivate) {
        this.restClient = new RestClient(apiKeyPrivate, apiKeyPublic);
    }

    public void fetchData(ComicDatabase db) {
        this.db = db;

        log("Start updating database...");

        try {
            //Other calls (to fetch Comics and their characters)  are SOOOO SLOW. 
            //I just read information about comics and roles inside above function call.
            readAllData();
        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(-1);
        }

        log("Finished updateing database.");
    }

    private void log(String message) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date)+ "\t" + message);
    }

    private void readAllData() throws Exception {
        Map<Integer, Figure> figures = db.getFigures();
        Map<Integer, MComic> comics = db.getComics();
        List<Role> roles = db.getRoles();

        int currentOffset = figures.size();
        int totalCount = -1;

        while ( totalCount == -1 || currentOffset < totalCount ) {
            Result<MarvelCharacter> characters = restClient.getCharacters(
                    new CharacterParameterBuilder().withOffset(currentOffset).withLimit(100).create());

            Container<MarvelCharacter> data = characters.getData();

            for(MarvelCharacter mc: data.getResults()) {
                Figure figure = new Figure(mc.getId(), mc.getName());

                figures.put(mc.getId(), figure);

                //Also read this character's Comics and update database
                for(ComicSummary summary: mc.getComics().getItems() ) {
                    String uri = summary.getResourceURI();
                    String title = summary.getName();

                    //uri has this form: http://gateway.marvel.com/v1/public/comics/21546
                    int id = Integer.valueOf(uri.substring(uri.lastIndexOf('/')+1));

                    if ( !comics.containsKey(id) ) {
                        comics.put(id, new MComic(id, title));
                    }

                    MComic thisComic = comics.get(id);

                    //add a new Role
                    roles.add(new Role(figure, thisComic));
                }
            }

            totalCount = data.getTotal();
            currentOffset += data.getCount();
            log(String.format("%d/%d comic characters processed...",currentOffset, totalCount));
        }

        db.save();
        log("DB saved " + db.toString());
    }
}
