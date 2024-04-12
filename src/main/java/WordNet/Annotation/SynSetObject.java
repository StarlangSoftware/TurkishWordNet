package WordNet.Annotation;

import WordNet.SemanticRelation;
import WordNet.SemanticRelationType;
import WordNet.SynSet;

public class SynSetObject {

    final SynSet synSet;

    /**
     * Constructor for a temporary synset object just to write another toString method.
     * @param synSet
     */
    public SynSetObject(SynSet synSet){
        this.synSet = synSet;
    }

    public String toString(){
        String color = "black";
        int count = 0;
        for (int i = 0; i < synSet.relationSize(); i++){
            if (synSet.getRelation(i) instanceof SemanticRelation && (((SemanticRelation) synSet.getRelation(i)).getRelationType().equals(SemanticRelationType.HYPERNYM) || ((SemanticRelation) synSet.getRelation(i)).getRelationType().equals(SemanticRelationType.INSTANCE_HYPERNYM))){
                count++;
            }
        }
        if (count > 1){
            color = "red";
        }
        StringBuilder literal = new StringBuilder("<html><font color=\"" + color + "\">" + synSet.getSynonym().getLiteral(0).getName());
        for (int i = 1; i < synSet.getSynonym().literalSize(); i++){
            literal.append("::").append(synSet.getSynonym().getLiteral(i).getName());
        }
        return literal + " (" + synSet.getDefinition() + ")" + "</font></html>";
    }

}
