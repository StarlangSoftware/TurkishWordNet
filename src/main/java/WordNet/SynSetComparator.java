package WordNet;

import java.util.Comparator;

public class SynSetComparator implements Comparator<SynSet> {

    /**
     * Compares the source SynSet's ID to the target SynSet's ID according to the
     * collation rules for this Collator. Returns an integer less than,
     * equal to or greater than zero depending on whether the source SynSet's ID is
     * less than, equal to or greater than the target SynSet's ID.
     *
     * @param synSetA the source SynSet
     * @param synSetB the target SynSet
     * @return Returns an integer value. Value is less than zero if source is less than
     * target, value is zero if source and target are equal, value is greater than zero
     * if source is greater than target
     */
    public int compare(SynSet synSetA, SynSet synSetB) {
        return synSetA.getId().compareTo(synSetB.getId());
    }

}
