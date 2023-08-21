package WordNet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Synonym {

    private ArrayList<Literal> literals;

    /**
     * A constructor that creates a new {@link ArrayList} literals.
     */
    public Synonym() {
        literals = new ArrayList<>();
    }

    /**
     * Appends the specified Literal to the end of literals list.
     *
     * @param literal element to be appended to the list
     */
    public void addLiteral(Literal literal) {
        literals.add(literal);
    }

    /**
     * Moves the specified literal to the first of literals list.
     *
     * @param literal element to be moved to the first element of the list
     */
    public void moveFirst(Literal literal){
        if (contains(literal)){
            literals.remove(literal);
            literals.add(0, literal);
        }
    }

    public ArrayList<Synonym> getUniqueLiterals(){
        ArrayList<Synonym> literalGroups = new ArrayList<>();
        int groupNo = -1;
        Synonym synonym = new Synonym();
        for (Literal literal : literals){
            if (literal.getGroupNo() != groupNo){
                if (groupNo != -1){
                    literalGroups.add(synonym);
                }
                groupNo = literal.getGroupNo();
                synonym = new Synonym();
            } else {
                if (groupNo == 0){
                    literalGroups.add(synonym);
                    synonym = new Synonym();
                }
            }
            synonym.addLiteral(literal);
        }
        literalGroups.add(synonym);
        return literalGroups;
    }

    /**
     * Returns the element at the specified position in literals list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in the list
     */
    public Literal getLiteral(int index) {
        return literals.get(index);
    }

    /**
     * Returns the element with the specified name in literals list.
     *
     * @param name name of the element to return
     * @return the element with the specified name in the list
     */
    public Literal getLiteral(String name) {
        for (Literal literal : literals) {
            if (literal.getName().equalsIgnoreCase(name)) {
                return literal;
            }
        }
        return null;
    }

    /**
     * Returns size of literals list.
     *
     * @return the size of the list
     */
    public int literalSize() {
        return literals.size();
    }

    /**
     * Returns true if literals list contains the specified literal.
     *
     * @param literal element whose presence in the list is to be tested
     * @return true if the list contains the specified element
     */
    public boolean contains(Literal literal) {
        return literals.contains(literal);
    }

    /**
     * Returns true if literals list contains the specified String literal.
     *
     * @param literalName element whose presence in the list is to be tested
     * @return true if the list contains the specified element
     */
    public boolean containsLiteral(String literalName) {
        for (Literal literal : literals) {
            if (literal.getName().equals(literalName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the first occurrence of the specified element from literals list,
     * if it is present. If the list does not contain the element, it stays unchanged.
     *
     * @param toBeRemoved element to be removed from the list, if present
     */
    public void removeLiteral(Literal toBeRemoved) {
        literals.remove(toBeRemoved);
    }

    /**
     * Method to write Synonyms to the specified file in the XML format.
     *
     * @param outfile BufferedWriter to write XML files
     */
    public void saveAsXml(BufferedWriter outfile) {
        try {
            outfile.write("<SYNONYM>");
            for (Literal literal : literals) {
                literal.saveAsXml(outfile);
            }
            outfile.write("</SYNONYM>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Overridden toString method to print literals.
     *
     * @return concatenated literals
     */
    public String toString() {
        String result = "";
        for (Literal literal : literals) {
            result = result + literal.getName() + " ";
        }
        return result;
    }
}
