package WordNet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Synonym {

    private ArrayList<Literal> literals;

    public Synonym(){
        literals = new ArrayList<>();
    }

    public void addLiteral(Literal literal){
        literals.add(literal);
    }

    public Literal getLiteral(int index){
        return literals.get(index);
    }

    public Literal getLiteral(String name){
        for (Literal literal:literals){
            if (literal.getName().equalsIgnoreCase(name)){
                return literal;
            }
        }
        return null;
    }

    public int literalSize(){
        return literals.size();
    }

    public boolean contains(Literal literal){
        return literals.contains(literal);
    }

    public boolean containsLiteral(String literalName){
        for (Literal literal:literals){
            if (literal.getName().equals(literalName)){
                return true;
            }
        }
        return false;
    }

    public void removeLiteral(Literal toBeRemoved){
        literals.remove(toBeRemoved);
    }

    public void saveAsXml(BufferedWriter outfile){
        try {
            outfile.write("<SYNONYM>");
            for (Literal literal:literals){
                literal.saveAsXml(outfile);
            }
            outfile.write("</SYNONYM>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        String result = "";
        for (Literal literal : literals){
            result = result + literal.getName() + " ";
        }
        return result;
    }
}
