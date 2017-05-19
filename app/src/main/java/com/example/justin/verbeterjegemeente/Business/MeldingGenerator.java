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

        Melding a1 = new Melding("Riolering", "Stank", "But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?");
        meldingen.add(a1);
        meldingen.add(new Melding("Verlichting", "Lantaarnpaal", "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful."));
        meldingen.add(new Melding("Afval", "Dumping", "Overal ligt poep op straat"));
        meldingen.add(new Melding("Groen en Bomen", "Bermen", "On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided."));
        meldingen.add(new Melding("Afval", "Zwerfvuil", "Drugsafval in de sloot gegooid"));
        meldingen.add(new Melding("Dieren", "", "The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains"));
    }
}

