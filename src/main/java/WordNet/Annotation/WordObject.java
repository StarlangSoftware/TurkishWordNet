package WordNet.Annotation;

import Dictionary.Pos;
import Dictionary.Word;

public class WordObject {

    final Word word;
    final Pos pos;

    /**
     * Constructor for a word object that also stores the part of speech tag.
     * @param word Word object
     * @param pos Part of speech tag of the word.
     */
    WordObject(Word word, Pos pos){
        this.word = word;
        this.pos = pos;
    }

    public String toString(){
        return word.getName() + " (" + pos + ")";
    }

}
