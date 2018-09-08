package WordNet;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class LiteralOnlyComparator implements Comparator<Literal> {
    private Locale locale = new Locale("en");

    public LiteralOnlyComparator(){
    }

    public LiteralOnlyComparator(Locale locale){
        this.locale = locale;
    }

    public int compare(Literal literalA, Literal literalB) {
        Collator collator = Collator.getInstance(locale);
        return collator.compare(literalA.getName().toLowerCase(locale), literalB.getName().toLowerCase(locale));
    }

}
