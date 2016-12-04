package com.mahdix.app.calculator;

import java.util.*;

import com.mahdix.app.entities.*;
import com.mahdix.app.datasource.*;

/* The default implementation of Calculator interface.
 */
public class StandardCalculator implements Calculator {

    /* Calculate influence of a character only based on his work experience and
     * without paying attention to his co-characters (other characters that he has work with)
     */
    private double calculateInfluence(ComicDatabase db, Figure f) {
        int figureComicCount = db.getFigureComics(f).size();

        return (double)figureComicCount / db.getComics().size();
    }

    /* Calculate actual influence of a character based on his influence and work activity
     * and his co-figures
     */
    public double calculateIndividualInfluence(ComicDatabase db, Figure figure) {
        int totalComics = db.getComics().size();

        List<MComic> comics = db.getFigureComics(figure);

        double totalInfluence = 0;
        double coFigureInfluence = 0;

        List<Figure> coFigures = new ArrayList<Figure>();

        for(MComic m: db.getFigureComics(figure) ) {
           for(Figure f: db.getComicFigures(m) ) {
               if ( f.getId() != figure.getId() ) {
                   coFigures.add(f);
               }
           }
        }

        for(Figure coFigure: coFigures) {
            coFigureInfluence += calculateInfluence(db, coFigure);
        }

        for(Figure f: db.getFiguresList()) {
            totalInfluence += calculateInfluence(db, f);
        }

        double part1 = coFigureInfluence / totalInfluence;
        double part2 = (double) comics.size() / totalComics;

        double result = part1 * part2;

        return result;
    }
}
