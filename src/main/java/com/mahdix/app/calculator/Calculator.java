package com.mahdix.app.calculator;

import com.mahdix.app.entities.*;
import com.mahdix.app.datasource.*;

/* General calculator formula which is used to calculate important metric.
 * You can define different/multiple implementations based on different models
 */
public interface Calculator {
    double calculateIndividualInfluence(ComicDatabase db, Figure figure);
}
