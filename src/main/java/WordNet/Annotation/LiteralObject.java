package WordNet.Annotation;

import Dictionary.Pos;
import WordNet.Literal;

public class LiteralObject {
    final Literal literal;
    final Pos pos;

    /**
     * Constructor for the literal object that stores a literal and its part of speech tag.
     * @param literal Literal
     * @param pos Part of speech tag.
     */
    public LiteralObject(Literal literal, Pos pos){
        this.literal = literal;
        this.pos = pos;
    }

    public String toString(){
        return literal.getName() + " (" + pos + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralObject that = (LiteralObject) o;
        return literal.getName().equals(that.literal.getName()) && pos == that.pos;
    }

}
