package WordNet.Annotation;

import WordNet.SynSet;

import java.util.ArrayList;

public class InterlingualMatchedSynSets {
    private SynSet first = null;
    private ArrayList<SynSet> second = null;

    public InterlingualMatchedSynSets(SynSet synSet, boolean isFirst){
        if (isFirst){
            first = synSet;
        } else {
            second = new ArrayList<>();
            second.add(synSet);
        }
    }

    public SynSet getFirst(){
        return first;
    }

    public ArrayList<SynSet> getSecond(){
        return second;
    }

    public void remove(SynSet synSet){
        second.remove(synSet);
    }

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
