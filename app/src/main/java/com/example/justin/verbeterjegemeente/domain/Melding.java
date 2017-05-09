package com.example.justin.verbeterjegemeente.domain;

import android.net.Uri;

/**
 * Created by Mika Krooswijk on 8-5-2017.
 */

public class Melding  {

    private String categorie, beschrijving, voornaam, achternaam, email;
    private boolean update;
    private Uri foto;
    private Locatie locatie;

    public void Melding(){

    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

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

    public Uri getFoto() {
        return foto;
    }

    @Override
    public String toString() {
        return "Melding{" +
                "categorie='" + categorie + '\'' +
                ", beschrijving='" + beschrijving + '\'' +
                ", voornaam='" + voornaam + '\'' +
                ", achternaam='" + achternaam + '\'' +
                ", email='" + email + '\'' +
                ", update=" + update +
                ", foto=" + foto +
                ", locatie=" + locatie +
                '}';
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }


}
