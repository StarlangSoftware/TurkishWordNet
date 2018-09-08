package WordNet;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class LiteralWithSenseComparator implements Comparator<Literal> {
    private Locale locale = new Locale("en");

    public LiteralWithSenseComparator(){
    }

    public LiteralWithSenseComparator(Locale locale){
        this.locale = locale;
    }

    public int compare(Literal literalA, Literal literalB) {
        Collator collator = Collator.getInstance(locale);
        if (collator.compare(literalA.getName().toLowerCase(locale), literalB.getName().toLowerCase(locale)) == 0){
            if (literalA.getSense() < literalB.getSense()){
                return -1;
            } else {
                if (literalA.getSense() > literalB.getSense()){
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return collator.compare(literalA.getName().toLowerCase(locale), literalB.getName().toLowerCase(locale));
        }
    }

}

