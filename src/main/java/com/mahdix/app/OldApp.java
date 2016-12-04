package com.mahdix.app;

import com.github.codingricky.marvel.model.*;
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
/**
 * Hello world!
 *
 */
public class OldApp 
{
    public static void main( String[] args )
    {
        try {
            RestClient restClient = new RestClient("1f2f1e9639807aeb030a92b9285d5cf5f9d12337", "cc0f2e679b61fba9fc9610b9497fd1e9");

            int currentOffset = 0;
            int totalCount = -1;

            while ( totalCount == -1 || currentOffset < totalCount ) {
                Result<Comic> comics = restClient.getComics(
                        new ComicParametersBuilder().withOffset(currentOffset).withLimit(100).create());

                Container<Comic> data = comics.getData();

                for(Comic mc: data.getResults()) {
                    System.out.println(mc.getTitle());
                }

                totalCount = data.getTotal();
                currentOffset += data.getCount();
                System.out.format("offset=%d%n", currentOffset);
                Thread.sleep(1000);
            }
            // Result<MarvelCharacter> characters = restClient.getCharacters(new CharacterParameterBuilder().create());
            // characters = restClient.getCharacters(new CharacterParameterBuilder().withLimit(100).create());

            // for(MarvelCharacter mc: characters.getData().getResults()) {
            //     ComicList cl = mc.getComics();
            //     int comicCount = cl.getItems().size();

            //     System.out.println(mc.getName() +" has " + String.valueOf(comicCount) + "comics");
            // }

            // Result<Comic> comics = restClient.getComics();
            // for(Comic cc: comics.getData().getResults()) {
            //     System.out.println(cc.getTitle());
            // }

            // Comic first = comics.getData().getResults().get(0);
            // int firstId = first.getId();
            // firstId = 21366;

            // Result<MarvelCharacter> chars = restClient.getComicsCharacters(new ComicParametersBuilder(firstId).create());
            // System.out.println("in that comic we have " + String.valueOf(chars.getData().getCount()) + " characters");
            // for(MarvelCharacter c: chars.getData().getResults()) {
            //     System.out.println(c.getName());
            // }


        }catch(Exception exc) {
            System.out.println("Exc " + exc.getMessage());

            exc.printStackTrace();
        }

        System.out.println("Done!");
    }
}
