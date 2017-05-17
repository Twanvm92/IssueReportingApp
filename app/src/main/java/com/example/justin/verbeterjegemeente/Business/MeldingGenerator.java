package com.example.justin.verbeterjegemeente.Business;
import com.example.justin.verbeterjegemeente.domain.Melding;
import java.util.ArrayList;


public class MeldingGenerator {

    private ArrayList<Melding> meldingen;

    public MeldingGenerator() {
        meldingen = new ArrayList<Melding>();
    }

    public ArrayList<Melding> getMeldingen() {
        return meldingen;
    }

    public void generate() {

        meldingen.clear();

        Melding a1 = new Melding("Peter", "Slechte wegen");
        meldingen.add(a1);
        meldingen.add(new Melding("Mark", "Kapotte lantaarnpaal"));
        meldingen.add(new Melding("Lisa", "Overal ligt poep op straat"));
        meldingen.add(new Melding("Anne", "Muizenoverlast"));
        meldingen.add(new Melding("Justin", "Drugsafval in de sloot gegooid"));
        meldingen.add(new Melding("Max", "Kapotte stoeptegels op straat"));
    }
}

