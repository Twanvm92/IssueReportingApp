package com.example.justin.verbeterjegemeente.domain;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Mika Krooswijk on 8-5-2017.
 */

public class Melding implements Serializable {

    private String hoofdcategorie, subcategorie, beschrijving, voornaam, achternaam, email;
    private boolean update;
    private String fotoUrl;
    private Locatie locatie;

    public Melding(String hoofdcategorie, String subcategorie, String beschrijving) {
        this.hoofdcategorie = hoofdcategorie;
        this.subcategorie = subcategorie;
        this.beschrijving = beschrijving;
    }

    public String getHoofdcategorie() {return hoofdcategorie;}

    public void setHoofdcategorie(String hoofdcategorie) {this.hoofdcategorie = hoofdcategorie;}

    public String getSubcategorie() {return subcategorie;}

    public void setSubcategorie(String subcategorie) {this.subcategorie = subcategorie;}

    public String getBeschrijving() {
        return beschrijving;
    }

    public Locatie getLocatie() {
        return locatie;
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }


    @Override
    public String toString() {
        return "Melding{" +
                "hoofdcategorie='" + hoofdcategorie + '\'' +
                ", subcategorie='" + subcategorie + '\'' +
                ", beschrijving='" + beschrijving + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", achternaam='" + achternaam + '\'' +
                ", email='" + email + '\'' +
                ", update=" + update +
                ", fotoUrl='" + fotoUrl + '\'' +
                ", locatie=" + locatie +
                '}';
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
}
