package com.mahdix.app.datasource;

/* A data-source which can read comic/character data from external source and
 * populate a database
 */
public interface DataSource {
    void fetchData(ComicDatabase db);
}
