package com.mahdix.app.datasource;

import java.util.*;
import java.text.*;

import com.github.codingricky.marvel.model.Comic;
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
            readAllCharacters();
            readAllComics();
            readAllRoles();
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

    private void readAllCharacters() throws Exception {
        Map<Integer, Figure> result = db.getFigures();
        int currentOffset = result.size();
        int totalCount = -1;

        while ( totalCount == -1 || currentOffset < totalCount ) {
            Result<MarvelCharacter> characters = restClient.getCharacters(
                    new CharacterParameterBuilder().withOffset(currentOffset).withLimit(100).create());

            Container<MarvelCharacter> data = characters.getData();

            for(MarvelCharacter mc: data.getResults()) {
                result.put(mc.getId(), new Figure(mc.getId(), mc.getName()));
            }

            totalCount = data.getTotal();
            currentOffset += data.getCount();
            log(String.format("%d/%d comic characters read...",currentOffset, totalCount));
        }

        db.save();
        log("DB saved");
    }

    private void readAllComics() throws Exception {
        Map<Integer, MComic> result = db.getComics();
        int initialSize = result.size();
        int currentOffset = result.size();
        int totalCount = -1;

        while ( totalCount == -1 || currentOffset < totalCount ) {
            Result<Comic> comics = restClient.getComics(
                    new ComicParametersBuilder().withOffset(currentOffset).withLimit(100).withNoVariants(true).create());

            Container<Comic> data = comics.getData();

            for(Comic cc: data.getResults()) {
                result.put(cc.getId(), new MComic(cc.getId(), cc.getTitle()));
            }

            totalCount = data.getTotal();
            currentOffset += data.getCount();
            log(String.format("%d/%d comics read...",currentOffset, totalCount));

            db.save();
            log("DB saved");

            if ( (currentOffset - initialSize) % 1000 == 0 ) {
                Thread.sleep(1000);
            }
        }
    }

    private void readAllRoles() throws Exception {
        List<Role> result = db.getRoles();
        Map<Integer, MComic> comics = db.getComics();
        Map<Integer, Figure> figures = db.getFigures();

        int counter = 0;

        for(Map.Entry<Integer, MComic> entry: comics.entrySet() ) {
            MComic comic = entry.getValue();

            readComicRoles(result, comic, figures);
            counter++;
            log(String.format("%d/%d comics character roles read...", counter, comics.size()));

            db.save();
            log("DB saved");
        }
    }

    private void readComicRoles(List<Role> result, MComic comic, Map<Integer, Figure> figures) throws Exception {
        int currentOffset = result.size();
        int totalCount = -1;

        int comicId = comic.getId();

        while ( totalCount == -1 || currentOffset < totalCount ) {
            Result<MarvelCharacter> characters = restClient.getComicsCharacters(
                    new ComicParametersBuilder(comicId).withOffset(currentOffset).withLimit(100).create());

            Container<MarvelCharacter> data = characters.getData();

            for(MarvelCharacter mc: data.getResults()) {
                result.add(new Role(figures.get(mc.getId()), comic));
            }

            totalCount = data.getTotal();
            currentOffset += data.getCount();
        }
    }
}
