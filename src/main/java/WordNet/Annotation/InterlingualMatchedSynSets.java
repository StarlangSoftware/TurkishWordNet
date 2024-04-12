package WordNet.Annotation;

import WordNet.SynSet;

import java.util.ArrayList;

public class InterlingualMatchedSynSets {
    private SynSet first = null;
    private ArrayList<SynSet> second = null;

    /**
     * Constructor for the InterlingualMatchedSynSets, which stores the matched interlingual synsets for a specific
     * synset. The class may also contain a single interlingual synset without matching synset in the original wordnet.
     * @param synSet Synset to be added.
     * @param isFirst If true, synset will be added as an original synset, otherwise a single interlingual synset
     *                without matching synset in the original wordnet.
     */
    public InterlingualMatchedSynSets(SynSet synSet, boolean isFirst){
        if (isFirst){
            first = synSet;
        } else {
            second = new ArrayList<>();
            second.add(synSet);
        }
    }

    /**
     * Accessor for the first.
     * @return The synset in the original wordnet
     */
    public SynSet getFirst(){
        return first;
    }

    /**
     * Accessor for the second
     * @return The interlingual synset list in the matching wordnet.
     */
    public ArrayList<SynSet> getSecond(){
        return second;
    }

    /**
     * Removes a synset from the interlingual list.
     * @param synSet Synset to be removed.
     */
    public void remove(SynSet synSet){
        second.remove(synSet);
    }

    /**
     * Adds a synset to the interlingual list.
     * @param second Synset to be added.
     */
    public void addSecond(SynSet second){
        if (this.second == null){
            this.second = new ArrayList<>();
        }
        this.second.add(second);
    }

    public String toString(){
        if (first != null){
            return first.toString();
        } else {
            if (second != null && !second.isEmpty()){
                return second.get(0).toString();
            } else {
                return "";
            }
        }
    }
}
