package WordNet;

import Corpus.Sentence;
import Corpus.SentenceSplitter;
import Corpus.TurkishSplitter;
import Dictionary.Pos;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class SynSet {

    private String id;
    private Pos pos;
    private String[] definition = null;
    private String example = null;
    private final Synonym synonym;
    private final ArrayList<Relation> relations;
    private String note;
    private String wikiPage = null;
    private int bcs;

    /**
     * Constructor initialize SynSet ID, synonym and relations list.
     *
     * @param id Synset ID
     */
    public SynSet(String id) {
        this.id = id;
        this.synonym = new Synonym();
        relations = new ArrayList<>();
    }

    /**
     * Accessor for the SynSet ID.
     *
     * @return SynSet ID
     */
    public String getId() {
        return id;
    }

    /**
     * Mutator method for the SynSet ID.
     *
     * @param id SynSet ID to be set
     */
    public void setId(String id) {
        this.id = id;
        for (int i = 0; i < synonym.literalSize(); i++) {
            synonym.getLiteral(i).setSynSetId(id);
        }
    }

    /**
     * Mutator method for the definition.
     *
     * @param definition String definition
     */
    public void setDefinition(String definition) {
        this.definition = definition.split("\\|");
    }

    /**
     * Removes the specified definition from long definition.
     *
     * @param definition definition to be removed
     */
    public void removeDefinition(String definition) {
        String longDefinition = getLongDefinition();
        if (longDefinition.startsWith(definition + "|")) {
            setDefinition(longDefinition.replace(definition + "|", ""));
        } else {
            if (longDefinition.endsWith("|" + definition)) {
                setDefinition(longDefinition.replace("|" + definition, ""));
            } else {
                if (longDefinition.contains("|" + definition + "|")) {
                    setDefinition(longDefinition.replace("|" + definition, ""));
                }
            }
        }
    }

    /**
     * Removes the same definitions from long definition.
     *
     * @param locale Locale of the programme that will be used in converting upper/lower cases
     */
    public void removeSameDefinitions(Locale locale) {
        String definition = getLongDefinition();
        boolean removed = true;
        while (definition != null && removed) {
            removed = false;
            for (int j = 0; j < getSynonym().literalSize(); j++) {
                Literal literal = getSynonym().getLiteral(j);
                String word = literal.getName().toLowerCase(locale);
                String uppercaseWord = Word.toCapital(literal.getName());
                if (definition.contains("|" + word + "|")) {
                    definition = definition.replace("|" + word + "|", "|");
                    removed = true;
                }
                if (definition.contains("|" + word + "; ")) {
                    definition = definition.replace("|" + word + "; ", "|");
                    removed = true;
                }
                if (definition.contains("|" + uppercaseWord + "|")) {
                    definition = definition.replace("|" + uppercaseWord + "|", "|");
                    removed = true;
                }
                if (definition.contains("|" + uppercaseWord + "; ")) {
                    definition = definition.replace("|" + uppercaseWord + "; ", "|");
                    removed = true;
                }
                if (definition.contains("; " + word + "|")) {
                    removed = true;
                    definition = definition.replace("; " + word + "|", "|");
                }
                if (definition.contains("; " + uppercaseWord + "|")) {
                    removed = true;
                    definition = definition.replace("; " + uppercaseWord + "|", "|");
                }
                if (definition.endsWith("; " + word)) {
                    definition = definition.replace("; " + word, "");
                    removed = true;
                }
                if (definition.endsWith("|" + word)) {
                    definition = definition.replace("|" + word, "");
                    removed = true;
                }
                if (definition.startsWith(word + "|")) {
                    definition = definition.replace(word + "|", "");
                    removed = true;
                }
                if (definition.startsWith(uppercaseWord + "|")) {
                    definition = definition.replace(uppercaseWord + "|", "");
                    removed = true;
                }
                if (definition.endsWith("; " + uppercaseWord)) {
                    definition = definition.replace("; " + uppercaseWord, "");
                    removed = true;
                }
                if (definition.endsWith("|" + uppercaseWord)) {
                    definition = definition.replace("|" + uppercaseWord, "");
                    removed = true;
                }
                if (definition.equalsIgnoreCase(word)) {
                    definition = "";
                    removed = true;
                }
            }
        }
        if (definition != null && !definition.isEmpty()) {
            setDefinition(definition);
        } else {
            setDefinition("NO DEFINITION");
        }
    }

    /**
     * Accessor for the definition.
     *
     * @return definition
     */
    public String getDefinition() {
        if (definition != null) {
            return definition[0];
        } else {
            return null;
        }
    }

    /**
     * Returns the first literal's name.
     *
     * @return the first literal's name.
     */
    public String representative() {
        return getSynonym().getLiteral(0).getName();
    }

    /**
     * Returns all the definitions in the list.
     *
     * @return all the definitions
     */
    public String getLongDefinition() {
        if (definition != null) {
            StringBuilder longDefinition = new StringBuilder(definition[0]);
            for (int i = 1; i < definition.length; i++) {
                longDefinition.append("|").append(definition[i]);
            }
            return longDefinition.toString();
        } else {
            return null;
        }
    }

    /**
     * Sorts definitions list according to their lengths.
     */
    public void sortDefinitions() {
        if (definition != null) {
            for (int i = 0; i < definition.length; i++) {
                for (int j = i + 1; j < definition.length; j++) {
                    if (definition[i].length() < definition[j].length()) {
                        String tmp = definition[i];
                        definition[i] = definition[j];
                        definition[j] = tmp;
                    }
                }
            }
        }
    }

    /**
     * Accessor for the definition at specified index.
     *
     * @param index definition index to be accessed
     * @return definition at specified index
     */
    public String getDefinition(int index) {
        if (index < definition.length && index >= 0) {
            return definition[index];
        } else {
            return null;
        }
    }

    /**
     * Returns number of definitions in the list.
     *
     * @return number of definitions in the list.
     */
    public int numberOfDefinitions() {
        if (definition != null) {
            return definition.length;
        } else {
            return 0;
        }
    }

    /**
     * Mutator for the example.
     *
     * @param example String that will be used to set
     */
    public void setExample(String example) {
        this.example = example;
    }

    /**
     * Accessor for the example.
     *
     * @return String example
     */
    public String getExample() {
        return example;
    }

    /**
     * Returns modified version of the original example sentence where the original
     * literal of the synSet is replaced with newLiteral.
     * @param newLiteral New literal.
     * @param fsm Morphological analyzer.
     * @return Modified version of the original sentence.
     */
    public String getModifiedExample(String newLiteral, FsmMorphologicalAnalyzer fsm){
        SentenceSplitter s = new TurkishSplitter();
        Sentence newExampleSentence = s.split(example).get(0);
        if (getPos().equals(Pos.VERB)){
            newLiteral = newLiteral.substring(0, newLiteral.length() - 3);
        }
        FsmParseList[] parseList = fsm.morphologicalAnalysis(newExampleSentence);
        for (int k = 0; k < synonym.literalSize(); k++){
            String searchedLiteral, lastWord;
            searchedLiteral = synonym.getLiteral(k).name;
            if (getPos().equals(Pos.VERB)){
                searchedLiteral = searchedLiteral.substring(0, searchedLiteral.length() - 3);
            }
            if (searchedLiteral.contains(" ")){
                lastWord = searchedLiteral.split(" ")[searchedLiteral.split(" ").length - 1];
            } else {
                lastWord = searchedLiteral;
            }
            for (FsmParseList fsmParseList : parseList) {
                if (fsmParseList.size() > 0 && fsmParseList.getFsmParse(0).getSurfaceForm().equalsIgnoreCase(lastWord)){
                    return fsm.replaceWord(newExampleSentence, searchedLiteral, newLiteral).toString();
                }
                for (int j = 0; j < fsmParseList.size(); j++) {
                    if (fsmParseList.getFsmParse(j).getWord().getName().equalsIgnoreCase(lastWord)) {
                        return fsm.replaceWord(newExampleSentence, searchedLiteral, newLiteral).toString();
                    }
                }
            }
        }
        return example;
    }

    /**
     * Mutator for the bcs value which enables the connection with the BalkaNet.
     *
     * @param bcs bcs value
     */
    public void setBcs(int bcs) {
        if (bcs >= 1 && bcs <= 3) {
            this.bcs = bcs;
        }
    }

    /**
     * Accessor for the bcs value
     *
     * @return bcs value
     */
    public int getBcs() {
        return bcs;
    }

    /**
     * Mutator for the part of speech tags.
     *
     * @param pos part of speech tag
     */
    public void setPos(Pos pos) {
        this.pos = pos;
    }

    /**
     * Accessor for the part of speech tag.
     *
     * @return part of speech tag
     */
    public Pos getPos() {
        return pos;
    }

    /**
     * Mutator for the available notes.
     *
     * @param note String note to be set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Accessor for the available notes.
     *
     * @return String note
     */
    public String getNote() {
        return note;
    }

    /**
     * Accessor for the Wiki page of the synset.
     *
     * @return String Wiki page
     */
    public String getWikiPage(){
        return wikiPage;
    }

    /**
     * Mutator for the available notes.
     *
     * @param wikiPage String Wiki page to be set
     */
    public void setWikiPage(String wikiPage){
        this.wikiPage = wikiPage;
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
     * Removes the first occurrence of the specified element from relations list according to relation name,
     * if it is present. If the list does not contain the element, it stays unchanged.
     *
     * @param name element to be removed from the list, if present
     */
    public void removeRelation(String name) {
        for (int i = 0; i < relations.size(); i++) {
            if (relations.get(i).getName().equals(name)) {
                relations.remove(i);
                break;
            }
        }
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
     * Returns SynSets with the synonym interlingual dependencies.
     *
     * @param secondLanguage WordNet in other language to find relations
     * @return a list of SynSets that has interlingual relations in it
     */
    public ArrayList<SynSet> getInterlingual(WordNet secondLanguage) {
        ArrayList<SynSet> result = new ArrayList<>();
        for (Relation value : relations) {
            if (value instanceof InterlingualRelation) {
                InterlingualRelation relation = (InterlingualRelation) value;
                if (relation.getType().equals(InterlingualDependencyType.SYNONYM)) {
                    SynSet second = secondLanguage.getSynSetWithId(relation.getName());
                    if (second != null) {
                        result.add(second);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns interlingual relations with the synonym interlingual dependencies.
     *
     * @return a list of SynSets that has interlingual relations in it
     */
    public ArrayList<String> getInterlingual() {
        ArrayList<String> result = new ArrayList<>();
        for (Relation value : relations) {
            if (value instanceof InterlingualRelation) {
                InterlingualRelation relation = (InterlingualRelation) value;
                if (relation.getType().equals(InterlingualDependencyType.SYNONYM)) {
                    result.add(relation.getName());
                }
            }
        }
        return result;
    }

    /**
     * Returns the size of the relations list.
     *
     * @return the size of the relations list
     */
    public int relationSize() {
        return relations.size();
    }

    /**
     * Adds a specified literal to the synonym.
     *
     * @param literal literal to be added
     */
    public void addLiteral(Literal literal) {
        synonym.addLiteral(literal);
    }

    /**
     * Accessor for the synonym.
     *
     * @return synonym
     */
    public Synonym getSynonym() {
        return synonym;
    }

    /**
     * Compares literals of synonym and the specified SynSet, returns true if their have same literals.
     *
     * @param synSet SynSet to compare
     * @return true if SynSets have same literals, false otherwise
     */
    public boolean containsSameLiteral(SynSet synSet) {
        for (int i = 0; i < synonym.literalSize(); i++) {
            String literal1 = synonym.getLiteral(i).getName();
            for (int j = 0; j < synSet.getSynonym().literalSize(); j++) {
                String literal2 = synSet.getSynonym().getLiteral(j).getName();
                if (literal1.equalsIgnoreCase(literal2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if relations list contains the specified relation.
     *
     * @param relation element whose presence in the list is to be tested
     * @return true if the list contains the specified element
     */
    public boolean containsRelation(Relation relation) {
        return relations.contains(relation);
    }

    /**
     * Returns true if specified semantic relation type presents in the relations list.
     *
     * @param semanticRelationType element whose presence in the list is to be tested
     * @return true if specified semantic relation type presents in the relations list
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
     * Merges synonym and a specified SynSet with their definitions, relations, part of speech tags and examples.
     *
     * @param synSet SynSet to be merged
     */
    public void mergeSynSet(SynSet synSet) {
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++) {
            if (!synonym.contains(synSet.getSynonym().getLiteral(i))) {
                synonym.addLiteral(synSet.getSynonym().getLiteral(i));
            }
        }
        if (definition == null && synSet.getDefinition() != null) {
            setDefinition(synSet.getDefinition());
        } else {
            if (definition != null && synSet.getDefinition() != null && !getLongDefinition().equalsIgnoreCase(synSet.getLongDefinition())) {
                setDefinition(getLongDefinition() + "|" + synSet.getLongDefinition());
            }
        }
        if (synSet.relationSize() != 0) {
            for (int i = 0; i < synSet.relationSize(); i++) {
                if (!containsRelation(synSet.getRelation(i)) && !synSet.getRelation(i).getName().equals(id)) {
                    addRelation(synSet.getRelation(i));
                }
            }
        }
        if (pos == null && synSet.getPos() != null) {
            setPos(synSet.getPos());
        }
        if (example == null && synSet.getExample() != null) {
            example = synSet.getExample();
        }
    }

    /**
     * An overridden equals method to compare two {@code Object}s.
     *
     * @param secondObject the reference object with which to compare
     * @return {@code true} if this object's ID is the same as the obj argument's ID; {@code false} otherwise.
     */
    public boolean equals(Object secondObject) {
        if (!(secondObject instanceof SynSet)) {
            return false;
        }
        SynSet second = (SynSet) secondObject;
        return id.equals(second.id);
    }

    /**
     * Returns a hash code for the ID.
     *
     * @return a hash code for the ID
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Overridden toString method to print the first definition or representative.
     *
     * @return print the first definition or representative.
     */
    public String toString() {
        if (definition != null) {
            return definition[0];
        } else {
            return representative();
        }
    }

    /**
     * Method to write SynSets to the specified file as LMF.
     *
     * @param outfile BufferedWriter to write files
     * @param ili     String input
     */
    public void saveAsLmf(BufferedWriter outfile, String ili) {
        String posChar;
        try {
            if (pos != null) {
                switch (pos) {
                    case NOUN:
                        posChar = "n";
                        break;
                    case ADJECTIVE:
                        posChar = "a";
                        break;
                    case ADVERB:
                        posChar = "r";
                        break;
                    case VERB:
                        posChar = "v";
                        break;
                    default:
                        posChar = "x";
                        break;
                }
            } else {
                posChar = "x";
            }
            outfile.write("\t<Synset id=\"" + id + "\" ili=\"" + ili + "\" partOfSpeech=\"" + posChar + "\">\n");
            if (getLongDefinition() != null) {
                String longDefinition = getLongDefinition();
                if (longDefinition.contains("\"")) {
                    longDefinition = longDefinition.replaceAll("\"", "&quot;");
                }
                if (longDefinition.isEmpty()){
                    outfile.write("\t\t<Definition>NO DEFINITION</Definition>\n");
                } else {
                    outfile.write("\t\t<Definition>" + longDefinition + "</Definition>\n");
                }
            }
            for (Relation r : relations) {
                if (r instanceof SemanticRelation) {
                    switch (((SemanticRelation) r).getRelationType()) {
                        case MEMBER_TOPIC:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"has_domain_topic\"/>\n");
                            break;
                        case MEMBER_REGION:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"has_domain_region\"/>\n");
                            break;
                        case PART_HOLONYM:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"holo_part\"/>\n");
                            break;
                        case PART_MERONYM:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"mero_part\"/>\n");
                            break;
                        case MEMBER_HOLONYM:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"holo_member\"/>\n");
                            break;
                        case MEMBER_MERONYM:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"mero_member\"/>\n");
                            break;
                        case SUBSTANCE_HOLONYM:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"holo_substance\"/>\n");
                            break;
                        case SUBSTANCE_MERONYM:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"mero_substance\"/>\n");
                            break;
                        case ALSO_SEE:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"also\"/>\n");
                            break;
                        case DERIVATION_RELATED:
                            break;
                        default:
                            outfile.write("\t\t<SynsetRelation target=\"" + r.getName() + "\" relType=\"" + ((SemanticRelation) r).getTypeAsString().toLowerCase() + "\"/>\n");
                            break;
                    }
                }
            }
            if (example != null) {
                outfile.write("\t\t<Example>" + getExample() + "</Example>\n");
            }
            outfile.write("\t</Synset>\n");
        } catch (IOException ignored) {
        }
    }

    /**
     * Method to write SynSets to the specified file in the XML format.
     *
     * @param outfile BufferedWriter to write XML files
     */
    public void saveAsXml(BufferedWriter outfile) {
        try {
            outfile.write("<SYNSET>");
            outfile.write("<ID>" + id + "</ID>");
            synonym.saveAsXml(outfile);
            if (pos != null) {
                switch (pos){
                    case NOUN:
                        outfile.write("<POS>n</POS>");
                        break;
                    case ADJECTIVE:
                        outfile.write("<POS>a</POS>");
                        break;
                    case VERB:
                        outfile.write("<POS>v</POS>");
                        break;
                    case ADVERB:
                        outfile.write("<POS>b</POS>");
                        break;
                    case CONJUNCTION:
                        outfile.write("<POS>c</POS>");
                        break;
                    case PRONOUN:
                        outfile.write("<POS>r</POS>");
                        break;
                    case INTERJECTION:
                        outfile.write("<POS>i</POS>");
                        break;
                    case PREPOSITION:
                        outfile.write("<POS>p</POS>");
                        break;
                }
            }
            for (Relation r : relations) {
                if (r instanceof InterlingualRelation) {
                    outfile.write("<ILR>" + r.getName() + "<TYPE>" + ((InterlingualRelation) r).getTypeAsString() + "</TYPE></ILR>");
                } else {
                    if (r instanceof SemanticRelation) {
                        outfile.write("<SR>" + r.getName() + "<TYPE>" + ((SemanticRelation) r).getTypeAsString() + "</TYPE></SR>");
                    }
                }
            }
            if (wikiPage != null) {
                outfile.write("<WIKI>" + getWikiPage() + "</WIKI>");
            }
            if (definition != null) {
                outfile.write("<DEF>" + getLongDefinition() + "</DEF>");
            }
            if (example != null) {
                outfile.write("<EXAMPLE>" + example + "</EXAMPLE>");
            }
            outfile.write("</SYNSET>\n");
        } catch (IOException ignored) {
        }
    }
}
