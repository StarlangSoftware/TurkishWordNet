package WordNet;

import Dictionary.Pos;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class SynSet {

    private String id;
    private Pos pos;
    private String[] definition = null;
    private String example = null;
    private Synonym synonym;
    private ArrayList<Relation> relations;
    private String note;
    private int bcs;

    public SynSet(String id){
        this.id = id;
        this.synonym = new Synonym();
        relations = new ArrayList<>();
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setDefinition(String definition){
        this.definition = definition.split("\\|");
    }

    public void removeDefinition(String definition){
        String longDefinition = getLongDefinition();
        if (longDefinition.startsWith(definition + "|")){
            setDefinition(longDefinition.replace(definition + "|", ""));
        } else {
            if (longDefinition.endsWith("|" + definition)){
                setDefinition(longDefinition.replace("|" + definition, ""));
            } else {
                if (longDefinition.contains("|" + definition + "|")){
                    setDefinition(longDefinition.replace("|" + definition, ""));
                }
            }
        }
    }

    public void removeSameDefinitions(Locale locale){
        String definition = getLongDefinition();
        boolean removed = true;
        while (definition != null && removed){
            removed = false;
            for (int j = 0; j < getSynonym().literalSize(); j++){
                Literal literal = getSynonym().getLiteral(j);
                String word = literal.getName().toLowerCase(locale);
                String uppercaseWord = literal.getName().substring(0, 1).toUpperCase(locale) + literal.getName().substring(1);
                if (definition.contains("|" + word + "|")){
                    definition = definition.replace("|" + word + "|", "|");
                    removed = true;
                }
                if (definition.contains("|" + word + "; ")){
                    definition = definition.replace("|" + word + "; ", "|");
                    removed = true;
                }
                if (definition.contains("|" + uppercaseWord + "|")){
                    definition = definition.replace("|" + uppercaseWord + "|", "|");
                    removed = true;
                }
                if (definition.contains("|" + uppercaseWord + "; ")){
                    definition = definition.replace("|" + uppercaseWord + "; ", "|");
                    removed = true;
                }
                if (definition.contains("; " + word + "|")){
                    removed = true;
                    definition = definition.replace("; " + word + "|", "|");
                }
                if (definition.contains("; " + uppercaseWord + "|")){
                    removed = true;
                    definition = definition.replace("; " + uppercaseWord + "|", "|");
                }
                if (definition.endsWith("; " + word)){
                    definition = definition.replace("; " + word, "");
                    removed = true;
                }
                if (definition.endsWith("|" + word)){
                    definition = definition.replace("|" + word, "");
                    removed = true;
                }
                if (definition.startsWith(word + "|")){
                    definition = definition.replace(word + "|", "");
                    removed = true;
                }
                if (definition.startsWith(uppercaseWord + "|")){
                    definition = definition.replace(uppercaseWord + "|", "");
                    removed = true;
                }
                if (definition.endsWith("; " + uppercaseWord)){
                    definition = definition.replace("; " + uppercaseWord, "");
                    removed = true;
                }
                if (definition.endsWith("|" + uppercaseWord)){
                    definition = definition.replace("|" + uppercaseWord, "");
                    removed = true;
                }
                if (definition.equalsIgnoreCase(word)){
                    definition = "";
                    removed = true;
                }
            }
        }
        if (definition != null && definition.length() > 0){
            setDefinition(definition);
        } else {
            setDefinition("NO DEFINITION");
        }
    }

    public String getDefinition(){
        if (definition != null){
            return definition[0];
        } else {
            return null;
        }
    }

    public String representative(){
        return getSynonym().getLiteral(0).getName();
    }

    public String getLongDefinition(){
        if (definition != null){
            String longDefinition = definition[0];
            for (int i = 1; i < definition.length; i++){
                longDefinition = longDefinition + "|" + definition[i];
            }
            return longDefinition;
        } else {
            return null;
        }
    }

    public void sortDefinitions(){
        if (definition != null){
            for (int i = 0; i < definition.length; i++){
                for (int j = i + 1; j < definition.length; j++){
                    if (definition[i].length() < definition[j].length()){
                        String tmp = definition[i];
                        definition[i] = definition[j];
                        definition[j] = tmp;
                    }
                }
            }
        }
    }

    public String getDefinition(int index){
        if (index < definition.length && index >= 0){
            return definition[index];
        } else {
            return null;
        }
    }

    public int numberOfDefinitions(){
        if (definition != null){
            return definition.length;
        } else {
            return 0;
        }
    }

    public void setExample(String example){
        this.example = example;
    }

    public String getExample(){
        return example;
    }

    public void setBcs(int bcs){
        if (bcs >= 1 && bcs <= 3){
            this.bcs = bcs;
        }
    }

    public int getBcs(){
        return bcs;
    }

    public void setPos(Pos pos){
        this.pos = pos;
    }

    public Pos getPos(){
        return pos;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getNote(){
        return note;
    }

    public void addRelation(Relation relation){
        relations.add(relation);
    }

    public void removeRelation(Relation relation){
        relations.remove(relation);
    }

    public void removeRelation(String name){
        for (int i = 0; i < relations.size(); i++){
            if (relations.get(i).getName().equals(name)){
                relations.remove(i);
                break;
            }
        }
    }

    public Relation getRelation(int index){
        return relations.get(index);
    }

    public ArrayList<SynSet> getInterlingual(WordNet secondLanguage){
        ArrayList<SynSet> result = new ArrayList<>();
        for (int i = 0; i < relations.size(); i++){
            if (relations.get(i) instanceof InterlingualRelation){
                InterlingualRelation relation = (InterlingualRelation) relations.get(i);
                if (relation.getType().equals(InterlingualDependencyType.SYNONYM)){
                    SynSet second = secondLanguage.getSynSetWithId(relation.getName());
                    if (second != null){
                        result.add(second);
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<String> getInterlingual(){
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < relations.size(); i++){
            if (relations.get(i) instanceof InterlingualRelation){
                InterlingualRelation relation = (InterlingualRelation) relations.get(i);
                if (relation.getType().equals(InterlingualDependencyType.SYNONYM)){
                    result.add(relation.getName());
                }
            }
        }
        return result;
    }

    public int relationSize(){
        return relations.size();
    }

    public void addLiteral(Literal literal){
        synonym.addLiteral(literal);
    }

    public Synonym getSynonym(){
        return synonym;
    }

    public boolean containsSameLiteral(SynSet synSet){
        for (int i = 0; i < synonym.literalSize(); i++){
            String literal1 = synonym.getLiteral(i).getName();
            for (int j = 0; j < synSet.getSynonym().literalSize(); j++){
                String literal2 = synSet.getSynonym().getLiteral(j).getName();
                if (literal1.equalsIgnoreCase(literal2)){
                    return true;
                }
            }
        }
        return false;
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

    public void mergeSynSet(SynSet synSet){
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            if (!synonym.contains(synSet.getSynonym().getLiteral(i))){
                synonym.addLiteral(synSet.getSynonym().getLiteral(i));
            }
        }
        if (definition == null && synSet.getDefinition() != null){
            setDefinition(synSet.getDefinition());
        } else {
            if (definition != null && synSet.getDefinition() != null && !getLongDefinition().equalsIgnoreCase(synSet.getLongDefinition())){
                setDefinition(getLongDefinition() + "|" + synSet.getLongDefinition());
            }
        }
        if (synSet.relationSize() != 0){
            for (int i = 0; i < synSet.relationSize(); i++){
                if (!relations.contains(synSet.getRelation(i))){
                    addRelation(synSet.getRelation(i));
                }
            }
        }
        if (pos == null && synSet.getPos() != null){
            setPos(synSet.getPos());
        }
        if (example == null && synSet.example != null){
            example = synSet.example;
        }
    }

    public boolean equals(Object secondObject){
        if (!(secondObject instanceof SynSet)){
            return false;
        }
        SynSet second = (SynSet) secondObject;
        return id.equals(second.id);
    }

    public int hashCode(){
        return id.hashCode();
    }

    public String toString(){
        if (definition != null){
            return definition[0];
        } else {
            return representative();
        }
    }

    public void saveAsLmf(BufferedWriter outfile){
        try {
            outfile.write("\t<Synset id=\"" + id + "\">\n");
            outfile.write("\t\t<Definition gloss=\"" + getLongDefinition() + "\">\n");
            if (example != null){
                outfile.write("\t\t\t<Statement example=\"" + getExample() + "\"/>\n");
            }
            outfile.write("\t\t</Definition>\n");
            int semanticRelationCount = 0;
            for (Relation r:relations){
                if (r instanceof SemanticRelation){
                    semanticRelationCount++;
                }
            }
            if (semanticRelationCount > 0){
                outfile.write("\t\t<SynsetRelations>\n");
                for (Relation r:relations){
                    if (r instanceof SemanticRelation){
                        outfile.write("\t\t\t<SynsetRelation targets=\"" + r.getName() + "\" relType=\"" + ((SemanticRelation) r).getTypeAsString().toLowerCase() + "\"/>\n");
                    }
                }
                outfile.write("\t\t</SynsetRelations>\n");
            }
            outfile.write("\t</Synset>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAsXml(BufferedWriter outfile){
        try {
            outfile.write("<SYNSET>");
            outfile.write("<ID>" + id + "</ID>");
            synonym.saveAsXml(outfile);
            if (pos != null){
                if (pos.equals(Pos.NOUN)){
                    outfile.write("<POS>n</POS>");
                } else {
                    if (pos.equals(Pos.ADJECTIVE)){
                        outfile.write("<POS>a</POS>");
                    } else {
                        if (pos.equals(Pos.VERB)){
                            outfile.write("<POS>v</POS>");
                        } else {
                            if (pos.equals(Pos.ADVERB)){
                                outfile.write("<POS>b</POS>");
                            } else {
                                if (pos.equals(Pos.CONJUNCTION)){
                                    outfile.write("<POS>c</POS>");
                                } else {
                                    if (pos.equals(Pos.PRONOUN)){
                                        outfile.write("<POS>r</POS>");
                                    } else {
                                        if (pos.equals(Pos.INTERJECTION)){
                                            outfile.write("<POS>i</POS>");
                                        } else {
                                            if (pos.equals(Pos.PREPOSITION)){
                                                outfile.write("<POS>p</POS>");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (Relation r:relations){
                if (r instanceof InterlingualRelation){
                    outfile.write("<ILR>" + r.getName() + "<TYPE>" + ((InterlingualRelation) r).getTypeAsString() + "</TYPE></ILR>");
                } else {
                    if (r instanceof SemanticRelation){
                        outfile.write("<SR>" + r.getName() + "<TYPE>" + ((SemanticRelation) r).getTypeAsString() + "</TYPE></SR>");
                    }
                }
            }
            if (definition != null){
                outfile.write("<DEF>" + getLongDefinition() + "</DEF>");
            }
            if (example != null){
                outfile.write("<EXAMPLE>" + example + "</EXAMPLE>");
            }
            outfile.write("</SYNSET>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
