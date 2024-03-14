package WordNet;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class LiteralWithSenseComparator implements Comparator<Literal> {

    private Locale locale = new Locale("en");


    /**
     * An empty constructor.
     */
    public LiteralWithSenseComparator() {
    }

    /**
     * Another constructor that sets the locale of the code.
     *
     * @param locale Locale
     */
    public LiteralWithSenseComparator(Locale locale) {
        this.locale = locale;
    }

    /**
     * Compares the source string to the target string according to the
     * collation rules for this Collator.  Returns an integer less than,
     * equal to or greater than zero depending on whether the source String is
     * less than, equal to or greater than the target string.
     *
     * @param literalA the source string
     * @param literalB the target string
     * @return Returns an integer value. Value is less than zero if source is less than
     * target, value is zero if source and target are equal, value is greater than zero
     * if source is greater than target
     */
    public int compare(Literal literalA, Literal literalB) {
        Collator collator = Collator.getInstance(locale);
        if (collator.compare(literalA.getName().toLowerCase(locale), literalB.getName().toLowerCase(locale)) == 0) {
            return Integer.compare(literalA.getSense(), literalB.getSense());
        } else {
            return collator.compare(literalA.getName().toLowerCase(locale), literalB.getName().toLowerCase(locale));
        }
    }

}

