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

    public Literal(String name, int sense, String synSetId){
        this.name = name;
        this.sense = sense;
        this.synSetId = synSetId;
        relations = new ArrayList<Relation>();
    }

    @Override
    public boolean equals(Object literal){
        if (literal == null)
            return false;
        if (literal == this)
            return true;
        if (!(literal instanceof Literal))
            return false;
        Literal secondLiteral = (Literal) literal;
        if (name.equalsIgnoreCase(secondLiteral.getName()) && sense == secondLiteral.getSense()){
            return true;
        } else {
            return false;
        }
    }

    public String getSynSetId(){
        return synSetId;
    }

    public String getName(){
        return name;
    }

    public int getSense(){
        return sense;
    }

    public String getOrigin(){
        return origin;
    }

    public void setOrigin(String origin){
        this.origin = origin;
    }

    public void setSense(int sense){
        this.sense = sense;
    }

    public void addRelation(Relation relation){
        relations.add(relation);
    }

    public void removeRelation(Relation relation){
        relations.remove(relation);
    }

    public boolean containsRelation(Relation relation){
        return relations.contains(relation);
    }

    public boolean containsRelationType(SemanticRelationType semanticRelationType){
        for (Relation relation : relations){
            if (relation instanceof SemanticRelation && ((SemanticRelation) relation).getRelationType().equals(semanticRelationType)){
                return true;
            }
        }
        return false;
    }

    public Relation getRelation(int index){
        return relations.get(index);
    }

    public int relationSize(){
        return relations.size();
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSynSetId(String synSetId){
        this.synSetId = synSetId;
    }

    public void saveAsXml(BufferedWriter outfile){
        try {
            if (name.equals("&")){
                outfile.write("<LITERAL>&amp;<SENSE>" + sense + "</SENSE>");
            } else {
                outfile.write("<LITERAL>" + name + "<SENSE>" + sense + "</SENSE>");
            }
            if (origin != null){
                outfile.write("<ORIGIN>" + origin + "</ORIGIN>");
            }
            for (Relation r:relations){
                if (r instanceof InterlingualRelation){
                    outfile.write("<ILR>" + r.getName() + "<TYPE>" + ((InterlingualRelation) r).getTypeAsString() + "</TYPE></ILR>");
                } else {
                    if (r instanceof SemanticRelation){
                        if (((SemanticRelation) r).toIndex() == 0){
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

    public String toString(){
        return name + " " + sense;
    }
}
