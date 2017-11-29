package com.example.justin.verbeterjegemeente.app.utils;

/**
 * Created by twanv on 29-11-2017.
 */

public class StringWithTag {

    public String string;
    public Object tag;

    public StringWithTag(String stringPart, Object tagPart) {
        string = stringPart;
        tag = tagPart;
    }

    @Override
    public String toString() {
        return string;
    }

}
