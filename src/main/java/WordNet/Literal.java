package WordNet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Literal {

    protected String name;
    protected int sense;
    protected String synSetId;
    protected String origin = null;
    protected ArrayList<Relation> relations;

    /**
     * A constructor that initializes name, sense, SynSet ID and the relations.
     *
     * @param name     name of a literal
     * @param sense    index of sense
     * @param synSetId ID of the SynSet
     */
    public Literal(String name, int sense, String synSetId) {
        this.name = name;
        this.sense = sense;
        this.synSetId = synSetId;
        relations = new ArrayList<Relation>();
    }

    /**
     * Overridden equals method returns true if the specified object literal equals to the current literal's name.
     *
     * @param literal Object literal to compare
     * @return true if the specified object literal equals to the current literal's name
     */
    @Override
    public boolean equals(Object literal) {
        if (literal == null)
            return false;
        if (literal == this)
            return true;
        if (!(literal instanceof Literal))
            return false;
        Literal secondLiteral = (Literal) literal;
        if (name.equalsIgnoreCase(secondLiteral.getName()) && sense == secondLiteral.getSense()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Accessor method to return SynSet ID.
     *
     * @return String of SynSet ID
     */
    public String getSynSetId() {
        return synSetId;
    }

    /**
     * Accessor method to return name of the literal.
     *
     * @return name of the literal
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor method to return the index of sense of the literal.
     *
     * @return index of sense of the literal
     */
    public int getSense() {
        return sense;
    }

    /**
     * Accessor method to return the origin of the literal.
     *
     * @return origin of the literal
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Mutator method to set the origin with specified origin.
     *
     * @param origin origin of the literal to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Mutator method to set the sense index of the literal.
     *
     * @param sense sense index of the literal to set
     */
    public void setSense(int sense) {
        this.sense = sense;
    }

    /**
     * Appends the specified Relation to the end of relations list.
     *
     * @param relation element to be appended to the list
     */
    public void addRelation(Relation relation) {
        relations.add(relation);
    }

    /**
     * Removes the first occurrence of the specified element from relations list,
     * if it is present. If the list does not contain the element, it stays unchanged.
     *
     * @param relation element to be removed from the list, if present
     */
    public void removeRelation(Relation relation) {
        relations.remove(relation);
    }

    /**
     * Returns <tt>true</tt> if relations list contains the specified relation.
     *
     * @param relation element whose presence in the list is to be tested
     * @return <tt>true</tt> if the list contains the specified element
     */
    public boolean containsRelation(Relation relation) {
        return relations.contains(relation);
    }

    /**
     * Returns <tt>true</tt> if specified semantic relation type presents in the relations list.
     *
     * @param semanticRelationType element whose presence in the list is to be tested
     * @return <tt>true</tt> if specified semantic relation type presents in the relations list
     */
    public boolean containsRelationType(SemanticRelationType semanticRelationType) {
        for (Relation relation : relations) {
            if (relation instanceof SemanticRelation && ((SemanticRelation) relation).getRelationType().equals(semanticRelationType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the element at the specified position in relations list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in the list
     */
    public Relation getRelation(int index) {
        return relations.get(index);
    }

    /**
     * Returns size of relations list.
     *
     * @return the size of the list
     */
    public int relationSize() {
        return relations.size();
    }

    /**
     * Mutator method to set name of a literal.
     *
     * @param name name of the literal to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Mutator method to set SynSet ID of a literal.
     *
     * @param synSetId SynSet ID of the literal to set
     */
    public void setSynSetId(String synSetId) {
        this.synSetId = synSetId;
    }

    /**
     * Method to write Literals to the specified file in the XML format.
     *
     * @param outfile BufferedWriter to write XML files
     */
    public void saveAsXml(BufferedWriter outfile) {
        try {
            if (name.equals("&")) {
                outfile.write("<LITERAL>&amp;<SENSE>" + sense + "</SENSE>");
            } else {
                outfile.write("<LITERAL>" + name + "<SENSE>" + sense + "</SENSE>");
            }
            if (origin != null) {
                outfile.write("<ORIGIN>" + origin + "</ORIGIN>");
            }
            for (Relation r : relations) {
                if (r instanceof InterlingualRelation) {
                    outfile.write("<ILR>" + r.getName() + "<TYPE>" + ((InterlingualRelation) r).getTypeAsString() + "</TYPE></ILR>");
                } else {
                    if (r instanceof SemanticRelation) {
                        if (((SemanticRelation) r).toIndex() == 0) {
                            outfile.write("<SR>" + r.getName() + "<TYPE>" + ((SemanticRelation) r).getTypeAsString() + "</TYPE></SR>");
                        } else {
                            outfile.write("<SR>" + r.getName() + "<TYPE>" + ((SemanticRelation) r).getTypeAsString() + "</TYPE>" + "<TO>" + ((SemanticRelation) r).toIndex() + "</TO>" + "</SR>");
                        }
                    }
                }
            }
            outfile.write("</LITERAL>");
        } catch (IOException e) {
        }
    }

    /**
     * Overridden toString method to print names and sense of literals.
     *
     * @return concatenated names and senses of literals
     */
    public String toString() {
        return name + " " + sense;
    }
}
