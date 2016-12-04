package com.mahdix.app.calculator;

import com.mahdix.app.entities.*;
import com.mahdix.app.datasource.*;

public interface Calculator {
    double calculateIndividualInfluence(ComicDatabase db, Figure figure);
}
