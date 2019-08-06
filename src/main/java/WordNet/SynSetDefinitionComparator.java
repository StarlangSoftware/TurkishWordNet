package WordNet;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class SynSetDefinitionComparator implements Comparator<SynSet> {

    /**
     * Compares the source SynSet's definition to the target SynSet's definition according to the
     * collation rules for this Collator. Returns an integer less than,
     * equal to or greater than zero depending on whether the source SynSet's definition is
     * less than, equal to or greater than the target SynSet's definition.
     *
     * @param synSetA the source SynSet
     * @param synSetB the target SynSet
     * @return Returns an integer value. Value is less than zero if source is less than
     * target, value is zero if source and target are equal, value is greater than zero
     * if source is greater than target
     */
    public int compare(SynSet synSetA, SynSet synSetB) {
        Locale locale = new Locale("tr");
        Collator collator = Collator.getInstance(locale);
        return collator.compare(synSetA.getDefinition().toLowerCase(locale), synSetB.getDefinition().toLowerCase(locale));
    }

}
