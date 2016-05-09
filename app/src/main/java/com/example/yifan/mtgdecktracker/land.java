package com.example.yifan.mtgdecktracker;

/**
 * Created by Yifan on 5/9/2016.
 */
abstract class Land extends Card {

    public enum BaseLandType {
        ISLAND,
        MOUNTAIN,
        SWAMP,
        FOREST,
        PLAINS,
        NONE
    }

    public enum LandYield {
        BLUE,
        RED,
        BLACK,
        GREEN,
        WHITE,
        COLORLESS
    }

    private BaseLandType manaYield1;
    private BaseLandType manaYield2;

}
