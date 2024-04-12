package WordNet.Annotation;

import Dictionary.TxtWord;
import MorphologicalAnalysis.Transition;
import WordNet.SynSet;
import WordNet.WordNet;

import java.util.ArrayList;

public class SynSetListObject {

    ArrayList<SynSet> synSets;
    ArrayList<SynSet> extraSynSets = new ArrayList<>();

    /**
     * Constructor for SynSetObject that stores all synsets for a given literal. The synsets consists of the domain
     * wordnet synsets and the synsets in the general wordnet but not in the domain wordnet. The method also checks
     * verb form of the root.
     * @param domainWordNet Domain wordnet
     * @param turkish General wordnet
     * @param root Root of the word
     * @param word Word for the literal
     */
    public SynSetListObject(WordNet domainWordNet, WordNet turkish, String root, TxtWord word){
        Transition verbTransition = new Transition("mAk");
        if (word != null){
            String verbForm = verbTransition.makeTransition(word, word.getName());
            synSets = domainWordNet.getSynSetsWithLiteral(word.getName());
            synSets.addAll(domainWordNet.getSynSetsWithLiteral(verbForm));
            ArrayList<SynSet> candidates = turkish.getSynSetsWithLiteral(word.getName());
            for (SynSet synSet : candidates){
                if (!synSets.contains(synSet)){
                    extraSynSets.add(synSet);
                }
            }
            candidates = turkish.getSynSetsWithLiteral(verbForm);
            for (SynSet synSet : candidates){
                if (!synSets.contains(synSet)){
                    extraSynSets.add(synSet);
                }
            }
        } else {
            synSets = domainWordNet.getSynSetsWithLiteral(root);
            ArrayList<SynSet> candidates = turkish.getSynSetsWithLiteral(root);
            for (SynSet synSet : candidates){
                if (!synSets.contains(synSet)){
                    extraSynSets.add(synSet);
                }
            }
        }
    }

}
