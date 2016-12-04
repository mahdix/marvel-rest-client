package com.mahdix.app.datasource;

import java.util.*;
import java.io.*;

import com.mahdix.app.entities.*;

/*
 * This class is responsible for storing a collection of comics, figures (chracters) and roles (relation between chracter and comic)
 */
public class ComicDatabase {
    private Map<Integer, Figure> figures;
    private Map<Integer, MComic> comics;
    private List<Role> roles;

    public ComicDatabase clone() {
        Map<Integer, Figure> figures = new HashMap<>();
        Map<Integer, MComic> comics = new HashMap<>();
        List<Role> roles = new ArrayList<>();

        for(Figure f: this.getFiguresList()) {
            figures.put(f.getId(), new Figure(f));
        }

        for(MComic m: this.getComicsList()) {
            comics.put(m.getId(), new MComic(m));
        }

        for(Role r: this.roles) {
            Figure newFigure = figures.get(r.getFigure().getId());
            MComic newComic = comics.get(r.getComic().getId());

            roles.add(new Role(newFigure, newComic));
        }

        return new ComicDatabase(figures, comics, roles);
    }

    public Map<Integer, Figure> getFigures() {
        return figures;
    }

    public Map<Integer, MComic> getComics() {
        return comics;
    }

    public List<Role> getRoles() {
        return roles;
    }

    /* Create a new instance of database. If database file exists, load the data else 
     * create an empty database
     */
    public ComicDatabase() {
        File f = new File("db");
        if(f.exists() && !f.isDirectory()) { 
            //if db file exists, read it to continue update process
            loadDatabase();
        } else {
            //just create an empty db
            figures = new HashMap<>();
            comics = new HashMap<>();
            roles = new ArrayList<>();
        }
    }

    private void loadDatabase() {
        try {
            FileInputStream fileIn = new FileInputStream("db");
            ObjectInputStream in = new ObjectInputStream(fileIn);

            ArrayList<Figure> figures = (ArrayList<Figure>)in.readObject();
            this.figures = new HashMap<>();
            for(Figure f: figures) {
                this.figures.put(f.getId(), f);
            }

            ArrayList<MComic> comics = (ArrayList<MComic>)in.readObject();
            this.comics = new HashMap<>();
            for(MComic m: comics) {
                this.comics.put(m.getId(), m);
            }

            List<Role> roles = (List<Role>)in.readObject();
            this.roles = roles;

            in.close();
            fileIn.close();
        }catch(IOException exc) {
            exc.printStackTrace();
        }catch(ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public boolean save() {
        try {
            FileOutputStream fileOut = new FileOutputStream("db");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);

            out.writeObject(this.getFiguresList());
            out.writeObject(this.getComicsList());
            out.writeObject(this.roles);

            out.close();
            fileOut.close();
        } catch(IOException exc) {
            exc.printStackTrace();
            return false;
        }

        return true;
    }

    public ComicDatabase(Map<Integer, Figure> figures, Map<Integer, MComic> comics, List<Role> roles) {
        this.figures = figures;
        this.roles = roles;
        this.comics = comics;
    }

    public List<Figure> getFiguresList() {
        List<Figure> result = new ArrayList<>();
        for(Map.Entry<Integer, Figure> item: figures.entrySet()) {
            result.add(item.getValue());
        }

        return result;
    }

    public List<MComic> getComicsList() {
        List<MComic> result = new ArrayList<>();
        for(Map.Entry<Integer, MComic> item: comics.entrySet()) {
            result.add(item.getValue());
        }

        return result;
    }

    public List<Figure> getComicFigures(MComic m) {
        List<Figure> result = new ArrayList<>();

        for(Role r: roles) {
            if ( r.getComic().getId() == m.getId() ) {
                result.add(r.getFigure());
            }
        }

        return result;
    }

    private Map<Integer, List<MComic>> fcCache = new HashMap<>();

    //This is a frequent and rather expensive call, so we will cache it.
    public List<MComic> getFigureComics(Figure f) {
        if ( fcCache.containsKey(f.getId())) {
            return fcCache.get(f.getId());
        }

        List<MComic> result = new ArrayList<>();

        for(Role r: roles) {
            if ( r.getFigure().getId() == f.getId() ) {
                result.add(r.getComic());
            }
        }

        fcCache.put(f.getId(), result);

        return result;
    }

    private void eliminateComic(MComic m) {
        comics.remove(m.getId());

        List<Role> toDelete = new LinkedList<>();
        for(Role r: roles) {
            if ( r.getComic().getId() == m.getId() ) {
                toDelete.add(r);
            }
        }

        for(Role r: toDelete) roles.remove(r);
    }

    public void eliminateFigure(Figure f) {
        fcCache.clear();

        List<MComic> figureComics = getFigureComics(f);

        figures.remove(f.getId());

        List<Role> toDelete = new LinkedList<>();

        for(Role r: roles) {
            if ( r.getFigure().getId() == f.getId() ) {
                toDelete.add(r);
            }
        }

        for(Role r: toDelete) roles.remove(r);

        for(MComic m: figureComics) {
            eliminateComic(m);
        }
    }

    public String toString() {
        return String.format("[ComicDatabase(%d comics, %d characters, %d roles)]%n", this.comics.size(), this.figures.size(), this.roles.size());
    }
}
