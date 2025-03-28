package WordNet;

import Dictionary.ExceptionalWord;
import Dictionary.Pos;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.MetamorphicParse;
import MorphologicalAnalysis.MorphologicalParse;
import Util.FileUtils;
import Xml.XmlDocument;
import Xml.XmlElement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class WordNet {

    private TreeMap<String, SynSet> synSetList;
    private TreeMap<String, ArrayList<Literal>> literalList;
    private final Locale locale;
    private HashMap<String, ArrayList<ExceptionalWord>> exceptionList;
    public HashMap<String, ArrayList<SynSet>> interlingualList;

    /**
     * Reads a wordnet from a Xml file. A wordnet consists of a list of synsets encapsulated inside SYNSET tag. A synset
     * has an id (represented with ID tag), a set of literals encapsulated inside SYNONYM tag, part of speech tag
     * (represented with POS tag), a set of semantic relations encapsulated inside SR tag, a definition (represented
     * with DEF tag), and a possible example (represented with EXAMPLE tag). Each literal has a name, possibly a group
     * number (represented with GROUP tag), a sense number (represented with SENSE tag) and a set of semantic relations
     * encapsulated inside SR tag. A semantic relation has a name and a type (represented with TYPE tag).
     * @param inputStream File stream that contains the wordnet.
     */
    private void readWordNet(InputStream inputStream) {
        XmlElement rootNode, synSetNode, partNode, srNode, typeNode, toNode, literalNode, senseNode;
        SynSet currentSynSet = null;
        Literal currentLiteral;
        XmlDocument doc = new XmlDocument(inputStream);
        doc.parse();
        interlingualList = new HashMap<>();
        synSetList = new TreeMap<>();
        literalList = new TreeMap<>((Comparator) (o1, o2) -> {
            Locale locale1 = new Locale("tr");
            Collator collator = Collator.getInstance(locale1);
            return collator.compare(((String) o1).toLowerCase(locale1), ((String) o2).toLowerCase(locale1));
        });
        rootNode = doc.getFirstChild();
        if (rootNode == null){
            return;
        }
        synSetNode = rootNode.getFirstChild();
        while (synSetNode != null) {
            partNode = synSetNode.getFirstChild();
            while (partNode != null) {
                if (partNode.getName().equals("ID")) {
                    currentSynSet = new SynSet(partNode.getPcData());
                    addSynSet(currentSynSet);
                } else {
                    if (partNode.getName().equals("DEF") && currentSynSet != null) {
                        currentSynSet.setDefinition(partNode.getPcData());
                    } else {
                        if (partNode.getName().equals("EXAMPLE") && currentSynSet != null) {
                            currentSynSet.setExample(partNode.getPcData());
                        } else {
                            if (partNode.getName().equals("BCS") && currentSynSet != null) {
                                currentSynSet.setBcs(Integer.parseInt(partNode.getPcData()));
                            } else {
                                if (partNode.getName().equals("POS") && currentSynSet != null) {
                                    switch (partNode.getPcData().charAt(0)) {
                                        case 'a':
                                            currentSynSet.setPos(Pos.ADJECTIVE);
                                            break;
                                        case 'v':
                                            currentSynSet.setPos(Pos.VERB);
                                            break;
                                        case 'b':
                                            currentSynSet.setPos(Pos.ADVERB);
                                            break;
                                        case 'n':
                                            currentSynSet.setPos(Pos.NOUN);
                                            break;
                                        case 'i':
                                            currentSynSet.setPos(Pos.INTERJECTION);
                                            break;
                                        case 'c':
                                            currentSynSet.setPos(Pos.CONJUNCTION);
                                            break;
                                        case 'p':
                                            currentSynSet.setPos(Pos.PREPOSITION);
                                            break;
                                        case 'r':
                                            currentSynSet.setPos(Pos.PRONOUN);
                                            break;
                                        default:
                                            System.out.println("Pos " + partNode.getPcData() + " is not defined for SynSet " + currentSynSet.getId());
                                            break;
                                    }
                                } else {
                                    if (partNode.getName().equals("SR") && currentSynSet != null) {
                                        typeNode = partNode.getFirstChild();
                                        if (typeNode != null && typeNode.getName().equals("TYPE")) {
                                            toNode = typeNode.getNextSibling();
                                            if (toNode != null && toNode.getName().equals("TO")) {
                                                currentSynSet.addRelation(new SemanticRelation(partNode.getPcData(), typeNode.getPcData(), Integer.parseInt(toNode.getPcData())));
                                            } else {
                                                currentSynSet.addRelation(new SemanticRelation(partNode.getPcData(), typeNode.getPcData()));
                                            }
                                        } else {
                                            System.out.println("SR node " + partNode.getPcData() + " of synSet " + currentSynSet.getId() + " does not contain type value");
                                        }
                                    } else {
                                        if (partNode.getName().equals("ILR") && currentSynSet != null) {
                                            typeNode = partNode.getFirstChild();
                                            if (typeNode != null && typeNode.getName().equals("TYPE")) {
                                                String interlingualId = partNode.getPcData();
                                                ArrayList<SynSet> synSetList;
                                                if (interlingualList.containsKey(interlingualId)) {
                                                    synSetList = interlingualList.get(interlingualId);
                                                } else {
                                                    synSetList = new ArrayList<>();
                                                }
                                                synSetList.add(currentSynSet);
                                                interlingualList.put(interlingualId, synSetList);
                                                currentSynSet.addRelation(new InterlingualRelation(interlingualId, typeNode.getPcData()));
                                            } else {
                                                System.out.println("ILR node " + partNode.getPcData() + " of synSet " + currentSynSet.getId() + " does not contain type value");
                                            }
                                        } else {
                                            if (partNode.getName().equals("SYNONYM") && currentSynSet != null) {
                                                literalNode = partNode.getFirstChild();
                                                while (literalNode != null) {
                                                    if (literalNode.getName().equals("LITERAL")) {
                                                        senseNode = literalNode.getFirstChild();
                                                        if (senseNode != null) {
                                                            if (senseNode.getName().equals("SENSE") && !senseNode.getPcData().isEmpty()) {
                                                                currentLiteral = new Literal(literalNode.getPcData(), Integer.parseInt(senseNode.getPcData()), currentSynSet.getId());
                                                                currentSynSet.addLiteral(currentLiteral);
                                                                addLiteralToLiteralList(currentLiteral);
                                                                srNode = senseNode.getNextSibling();
                                                                while (srNode != null) {
                                                                    if (srNode.getName().equals("ORIGIN")){
                                                                        currentLiteral.setOrigin(srNode.getPcData());
                                                                    } else {
                                                                        if (srNode.getName().equals("GROUP")){
                                                                            currentLiteral.setGroupNo(Integer.parseInt(srNode.getPcData()));
                                                                        } else {
                                                                            if (srNode.getName().equals("SR")) {
                                                                                typeNode = srNode.getFirstChild();
                                                                                if (typeNode != null && typeNode.getName().equals("TYPE")) {
                                                                                    toNode = typeNode.getNextSibling();
                                                                                    if (toNode != null && toNode.getName().equals("TO")) {
                                                                                        currentLiteral.addRelation(new SemanticRelation(srNode.getPcData(), typeNode.getPcData(), Integer.parseInt(toNode.getPcData())));
                                                                                    } else {
                                                                                        currentLiteral.addRelation(new SemanticRelation(srNode.getPcData(), typeNode.getPcData()));
                                                                                    }
                                                                                } else {
                                                                                    System.out.println("SR node " + srNode.getPcData() + "of literal " + currentLiteral.getName() + " of synSet " + currentSynSet.getId() + " does not contain type value");
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    srNode = srNode.getNextSibling();
                                                                }
                                                            } else {
                                                                System.out.println("Literal Node " + literalNode.getPcData() + " of SynSet " + currentSynSet.getId() + " include nodes other than sense node");
                                                            }
                                                        } else {
                                                            System.out.println("Literal Node " + literalNode.getPcData() + " of SynSet " + currentSynSet.getId() + " does not include sense node");
                                                        }
                                                    } else {
                                                        System.out.println("SynSet " + currentSynSet.getId() + " includes nodes other than literal node");
                                                    }
                                                    literalNode = literalNode.getNextSibling();
                                                }
                                            } else {
                                                if (partNode.getName().equals("SNOTE") && currentSynSet != null) {
                                                    currentSynSet.setNote(partNode.getPcData());
                                                } else {
                                                    if (partNode.getName().equals("WIKI") && currentSynSet != null){
                                                        currentSynSet.setWikiPage(partNode.getPcData());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                partNode = partNode.getNextSibling();
            }
            synSetNode = synSetNode.getNextSibling();
        }
    }

    /**
     * Method constructs a DOM parser using the dtd/xml schema parser configuration and using this parser it
     * reads exceptions from file and puts to exceptionList HashMap.
     *
     * @param exceptionFileName exception file to be read
     */
    public void readExceptionFile(String exceptionFileName) {
        String wordName, rootForm;
        Pos pos;
        XmlElement wordNode, rootNode;
        XmlDocument doc = new XmlDocument(FileUtils.getInputStream(exceptionFileName));
        doc.parse();
        rootNode = doc.getFirstChild();
        wordNode = rootNode.getFirstChild();
        exceptionList = new HashMap<>();
        while (wordNode != null) {
            if (wordNode.hasAttributes()) {
                wordName = wordNode.getAttributeValue("name");
                rootForm = wordNode.getAttributeValue("root");
                switch (wordNode.getAttributeValue("pos")) {
                    case "Adj":
                        pos = Pos.ADJECTIVE;
                        break;
                    case "Adv":
                        pos = Pos.ADVERB;
                        break;
                    case "Verb":
                        pos = Pos.VERB;
                        break;
                    default:
                        pos = Pos.NOUN;
                        break;
                }
                ArrayList<ExceptionalWord> rootList;
                if (exceptionList.containsKey(wordName)){
                    rootList = exceptionList.get(wordName);
                } else {
                    rootList = new ArrayList<>();
                }
                rootList.add(new ExceptionalWord(wordName, rootForm, pos));
                exceptionList.put(wordName, rootList);
            }
            wordNode = wordNode.getNextSibling();
        }
    }

    /**
     * A constructor that initializes the SynSet list, literal list and schedules the {@code SwingWorker} for execution
     * on a <i>worker</i> thread.
     */
    public WordNet() {
        synSetList = new TreeMap<>();
        literalList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.locale = new Locale("tr");
        readWordNet(FileUtils.getInputStream("turkish_wordnet.xml"));
    }

    /**
     * Another constructor that initializes the SynSet list, literal list, reads exception,
     * and schedules the {@code SwingWorker} according to file with a specified name for execution on a <i>worker</i> thread.
     *
     * @param fileName resource to be read for the WordNet task
     */
    public WordNet(String fileName) {
        synSetList = new TreeMap<>();
        literalList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.locale = new Locale("en");
        readExceptionFile("english_exception.xml");
        readWordNet(FileUtils.getInputStream(fileName));
    }

    /**
     * Another constructor that initializes the SynSet list, literal list, reads exception,
     * sets the Locale of the programme with the specified locale, and schedules the {@code SwingWorker} according
     * to file with a specified name for execution on a <i>worker</i> thread.
     *
     * @param fileName resource to be read for the WordNet task
     * @param locale   the locale to be used to set
     */
    public WordNet(final String fileName, Locale locale) {
        synSetList = new TreeMap<>();
        literalList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.locale = locale;
        readWordNet(FileUtils.getInputStream(fileName));
    }

    /**
     * Another constructor that initializes the SynSet list, literal list, reads exception file with a specified name,
     * sets the Locale of the programme with the specified locale, and schedules the {@code SwingWorker} according
     * to file with a specified name for execution on a <i>worker</i> thread.
     *
     * @param fileName          resource to be read for the WordNet task
     * @param exceptionFileName exception file to be read
     * @param locale            the locale to be used to set
     */
    public WordNet(final String fileName, String exceptionFileName, Locale locale) {
        this(fileName, locale);
        readExceptionFile(exceptionFileName);
    }

    /**
     * Adds a specified literal to the literal list.
     *
     * @param literal literal to be added
     */
    public void addLiteralToLiteralList(Literal literal) {
        ArrayList<Literal> literals;
        if (literalList.containsKey(literal.getName())) {
            literals = literalList.get(literal.getName());
        } else {
            literals = new ArrayList<>();
        }
        literals.add(literal);
        literalList.put(literal.getName(), literals);
    }

    /**
     * Return Locale of the programme.
     *
     * @return Locale of the programme
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Updates the wordnet according to the situation that an old synset replaced with a new synset. There are three
     * possibilities: (i) The new synset has a relation with the old synset, then the relation is removed,
     * (ii) A synset has the same type of relation with old synset and new synset, then the relation is removed,
     * (iii) None of the above, then the old synset id in the relation is replaced with the new synset id.
     * @param oldSynSet Old synset to be replaced
     * @param newSynSet New synset replacing the old synset
     */
    private void updateAllRelationsAccordingToNewSynSet(SynSet oldSynSet, SynSet newSynSet){
        for (SynSet synSet : synSetList()){
            for (int i = 0; i < synSet.relationSize(); i++){
                if (synSet.getRelation(i) instanceof SemanticRelation){
                    if (synSet.getRelation(i).getName().equals(oldSynSet.getId())){
                        if (synSet.getId().equals(newSynSet.getId()) || synSet.containsRelation(new SemanticRelation(newSynSet.getId(), ((SemanticRelation) synSet.getRelation(i)).getRelationType()))){
                            synSet.removeRelation(synSet.getRelation(i));
                            i--;
                        } else {
                            synSet.getRelation(i).setName(newSynSet.getId());
                        }
                    }
                }
            }
        }
    }

    /**
     * Method reads the specified SynSet file, gets the SynSets according to IDs in the file, and merges SynSets.
     *
     * @param synSetFile SynSet file to be read and merged
     */
    public void mergeSynSets(String synSetFile) {
        try {
            BufferedReader infile = new BufferedReader(new FileReader(synSetFile));
            String line = infile.readLine();
            while (line != null) {
                String[] synSetIds = line.split(" ");
                SynSet mergedOne = getSynSetWithId(synSetIds[0]);
                if (mergedOne != null) {
                    for (int i = 1; i < synSetIds.length; i++) {
                        SynSet toBeMerged = getSynSetWithId(synSetIds[i]);
                        if (toBeMerged != null){
                            if (mergedOne.getPos().equals(toBeMerged.getPos())) {
                                if (!containsSameLiteral(mergedOne, toBeMerged)){
                                    mergedOne.mergeSynSet(toBeMerged);
                                    removeSynSet(toBeMerged);
                                    updateAllRelationsAccordingToNewSynSet(toBeMerged, mergedOne);
                                } else {
                                    System.out.println(line + " contains the same literals");
                                }
                            } else {
                                System.out.println(line + " contains synsets with different pos");
                            }
                        } else {
                            System.out.println(line + " contains " + synSetIds[i] + " which does not exist");
                        }
                    }
                } else {
                    System.out.println(line + " contains " + synSetIds[0] + " which does not exist");
                }
                line = infile.readLine();
            }
            infile.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Returns the values of the SynSet list.
     *
     * @return values of the SynSet list
     */
    public Collection<SynSet> synSetList() {
        return synSetList.values();
    }

    /**
     * Returns the keys of the literal list.
     *
     * @return keys of the literal list
     */
    public Collection<String> literalList() {
        return literalList.keySet();
    }

    /**
     * Adds specified SynSet to the SynSet list.
     *
     * @param synSet SynSet to be added
     */
    public void addSynSet(SynSet synSet) {
        synSetList.put(synSet.getId(), synSet);
    }

    /**
     * Removes specified SynSet from the SynSet list.
     *
     * @param synSet SynSet to be removed
     */
    public void removeSynSet(SynSet synSet) {
        synSetList.remove(synSet.getId());
    }

    /**
     * Removes specified SynSet from the SynSet list.
     *
     * @param synSet SynSet to be removed
     */
    public void removeSynSetWithRelations(SynSet synSet) {
        for (int i = 0; i < synSet.relationSize(); i++){
            if (synSet.getRelation(i) instanceof SemanticRelation){
                SemanticRelation relation = (SemanticRelation) synSet.getRelation(i);
                removeReverseRelation(synSet, relation);
            }
        }
        synSetList.remove(synSet.getId());
    }

    /**
     * Changes ID of a specified SynSet with the specified new ID.
     *
     * @param synSet SynSet whose ID will be updated
     * @param newId  new ID
     */
    public void changeSynSetId(SynSet synSet, String newId) {
        synSetList.remove(synSet.getId());
        synSet.setId(newId);
        synSetList.put(newId, synSet);
    }

    /**
     * Returns SynSet with the specified SynSet ID.
     *
     * @param synSetId ID of the SynSet to be returned
     * @return SynSet with the specified SynSet ID
     */
    public SynSet getSynSetWithId(String synSetId) {
        if (synSetList.containsKey(synSetId)) {
            return synSetList.get(synSetId);
        }
        return null;
    }

    /**
     * Returns SynSet with the specified literal and sense index.
     *
     * @param literal SynSet literal
     * @param sense   SynSet's corresponding sense index
     * @return SynSet with the specified literal and sense index
     */
    public SynSet getSynSetWithLiteral(String literal, int sense) {
        ArrayList<Literal> literals;
        literals = literalList.get(literal);
        if (literals != null) {
            for (Literal current : literals) {
                if (current.getSense() == sense) {
                    return getSynSetWithId(current.getSynSetId());
                }
            }
        }
        return null;
    }

    /**
     * Returns the number of SynSets with a specified literal.
     *
     * @param literal literal to be searched in SynSets
     * @return the number of SynSets with a specified literal
     */
    public int numberOfSynSetsWithLiteral(String literal) {
        if (literalList.containsKey(literal)) {
            return literalList.get(literal).size();
        } else {
            return 0;
        }
    }

    /**
     * Returns a list of SynSets with a specified part of speech tag.
     *
     * @param pos part of speech tag to be searched in SynSets
     * @return a list of SynSets with a specified part of speech tag
     */
    public ArrayList<SynSet> getSynSetsWithPartOfSpeech(Pos pos) {
        ArrayList<SynSet> result = new ArrayList<>();
        for (SynSet synSet : synSetList.values()) {
            if (synSet.getPos() != null && synSet.getPos().equals(pos)) {
                result.add(synSet);
            }
        }
        return result;
    }

    /**
     * Returns a list of literals with a specified literal String.
     *
     * @param literal literal String to be searched in literal list
     * @return a list of literals with a specified literal String
     */
    public ArrayList<Literal> getLiteralsWithName(String literal) {
        if (literalList.containsKey(literal)) {
            return literalList.get(literal);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Finds the SynSet with specified literal String and part of speech tag and adds to the given SynSet list.
     *
     * @param result  SynSet list to add the specified SynSet
     * @param literal literal String to be searched in literal list
     * @param pos     part of speech tag to be searched in SynSets
     */
    private void addSynSetsWithLiteralToList(ArrayList<SynSet> result, String literal, Pos pos) {
        SynSet synSet;
        for (Literal current : literalList.get(literal)) {
            synSet = getSynSetWithId(current.getSynSetId());
            if (synSet != null && synSet.getPos().equals(pos)) {
                result.add(synSet);
            }
        }
    }

    /**
     * Finds SynSets with specified literal String and adds to the newly created SynSet list.
     *
     * @param literal literal String to be searched in literal list
     * @return returns a list of SynSets with specified literal String
     */
    public ArrayList<SynSet> getSynSetsWithLiteral(String literal) {
        SynSet synSet;
        ArrayList<SynSet> result = new ArrayList<>();
        if (literalList.containsKey(literal)) {
            for (Literal current : literalList.get(literal)) {
                synSet = getSynSetWithId(current.getSynSetId());
                if (synSet != null) {
                    result.add(synSet);
                }
            }
        }
        return result;
    }

    /**
     * Finds literals with specified literal String and adds to the newly created literal String list. Ex: cleanest - clean
     *
     * @param literal literal String to be searched in literal list
     * @return returns a list of literals with specified literal String
     */
    public ArrayList<String> getLiteralsWithPossibleModifiedLiteral(String literal) {
        ArrayList<String> result = new ArrayList<>();
        result.add(literal);
        String wordWithoutLastOne = literal.substring(0, literal.length() - 1);
        String wordWithoutLastTwo = literal.substring(0, literal.length() - 2);
        String wordWithoutLastThree = literal.substring(0, literal.length() - 3);
        if (exceptionList.containsKey(literal)) {
            for (ExceptionalWord exceptionalWord : exceptionList.get(literal)){
                result.add(exceptionalWord.getRoot());
            }
        }
        if (literal.endsWith("s") && literalList.containsKey(wordWithoutLastOne)) {
            result.add(wordWithoutLastOne);
        }
        if ((literal.endsWith("es") || literal.endsWith("ed") || literal.endsWith("er")) && literalList.containsKey(wordWithoutLastTwo)) {
            result.add(wordWithoutLastTwo);
        }
        if (literal.endsWith("ed") && literalList.containsKey(wordWithoutLastTwo + literal.charAt(literal.length() - 3))) {
            result.add(wordWithoutLastTwo + literal.charAt(literal.length() - 3));
        }
        if ((literal.endsWith("ed") || literal.endsWith("er")) && literalList.containsKey(wordWithoutLastTwo + "e")) {
            result.add(wordWithoutLastTwo + "e");
        }
        if ((literal.endsWith("ing") || literal.endsWith("est")) && literalList.containsKey(wordWithoutLastThree)) {
            result.add(wordWithoutLastThree);
        }
        if (literal.endsWith("ing") && literalList.containsKey(wordWithoutLastThree + literal.charAt(literal.length() - 4))) {
            result.add(wordWithoutLastThree + literal.charAt(literal.length() - 4));
        }
        if ((literal.endsWith("ing") || literal.endsWith("est")) && literalList.containsKey(wordWithoutLastThree + "e")) {
            result.add(wordWithoutLastThree + "e");
        }
        if (literal.endsWith("ies") && literalList.containsKey(wordWithoutLastThree + "y")) {
            result.add(wordWithoutLastThree + "y");
        }
        return result;
    }

    /**
     * Finds SynSets with specified literal String and part of speech tag, then adds to the newly created SynSet list. Ex: cleanest - clean
     *
     * @param literal literal String to be searched in literal list
     * @param pos     part of speech tag to be searched in SynSets
     * @return returns a list of SynSets with specified literal String and part of speech tag
     */
    public ArrayList<SynSet> getSynSetsWithPossiblyModifiedLiteral(String literal, Pos pos) {
        ArrayList<SynSet> result = new ArrayList<>();
        ArrayList<String> modifiedLiterals = getLiteralsWithPossibleModifiedLiteral(literal);
        for (String modifiedLiteral : modifiedLiterals) {
            if (literalList.containsKey(modifiedLiteral)) {
                addSynSetsWithLiteralToList(result, modifiedLiteral, pos);
            }
        }
        return result;
    }

    /**
     * Adds the reverse relations to the SynSet.
     *
     * @param synSet           SynSet to add the reverse relations
     * @param semanticRelation relation whose reverse will be added
     */
    public void addReverseRelation(SynSet synSet, SemanticRelation semanticRelation) {
        SynSet otherSynSet = getSynSetWithId(semanticRelation.getName());
        if (otherSynSet != null && SemanticRelation.reverse(semanticRelation.getRelationType()) != null) {
            Relation otherRelation = new SemanticRelation(synSet.getId(), SemanticRelation.reverse(semanticRelation.getRelationType()));
            if (!otherSynSet.containsRelation(otherRelation)) {
                otherSynSet.addRelation(otherRelation);
            }
        }
    }

    /**
     * Removes the reverse relations from the SynSet.
     *
     * @param synSet           SynSet to remove the reverse relation
     * @param semanticRelation relation whose reverse will be removed
     */
    public void removeReverseRelation(SynSet synSet, SemanticRelation semanticRelation) {
        SynSet otherSynSet = getSynSetWithId(semanticRelation.getName());
        if (otherSynSet != null && SemanticRelation.reverse(semanticRelation.getRelationType()) != null) {
            Relation otherRelation = new SemanticRelation(synSet.getId(), SemanticRelation.reverse(semanticRelation.getRelationType()));
            if (otherSynSet.containsRelation(otherRelation)) {
                otherSynSet.removeRelation(otherRelation);
            }
        }
    }

    /**
     * Loops through the SynSet list and adds the possible reverse relations.
     */
    private void equalizeSemanticRelations() {
        for (SynSet synSet : synSetList.values()) {
            for (int i = 0; i < synSet.relationSize(); i++) {
                if (synSet.getRelation(i) instanceof SemanticRelation) {
                    SemanticRelation relation = (SemanticRelation) synSet.getRelation(i);
                    addReverseRelation(synSet, relation);
                }
            }
        }
    }

    /**
     * Creates a list of literals with a specified word, or possible words corresponding to morphological parse.
     *
     * @param word      literal String
     * @param parse     morphological parse to get possible words
     * @param metaParse metamorphic parse to get possible words
     * @param fsm       finite state machine morphological analyzer to be used at getting possible words
     * @return a list of literal
     */
    public ArrayList<Literal> constructLiterals(String word, MorphologicalParse parse, MetamorphicParse metaParse, FsmMorphologicalAnalyzer fsm) {
        ArrayList<Literal> result = new ArrayList<>();
        if (parse.size() > 0) {
            if (!parse.isPunctuation() && !parse.isCardinal() && !parse.isReal()) {
                HashSet<String> possibleWords = fsm.getPossibleWords(parse, metaParse);
                for (String possibleWord : possibleWords) {
                    result.addAll(getLiteralsWithName(possibleWord));
                }
            } else {
                result.addAll(getLiteralsWithName(word));
            }
        } else {
            result.addAll(getLiteralsWithName(word));
        }
        return result;
    }

    /**
     * Creates a list of SynSets with a specified word, or possible words corresponding to morphological parse.
     *
     * @param word      literal String  to get SynSets with
     * @param parse     morphological parse to get SynSets with proper literals
     * @param metaParse metamorphic parse to get possible words
     * @param fsm       finite state machine morphological analyzer to be used at getting possible words
     * @return a list of SynSets
     */
    public ArrayList<SynSet> constructSynSets(String word, MorphologicalParse parse, MetamorphicParse metaParse, FsmMorphologicalAnalyzer fsm) {
        ArrayList<SynSet> result = new ArrayList<>();
        if (parse.size() > 0) {
            if (parse.isProperNoun()) {
                result.add(getSynSetWithLiteral("(özel isim)", 1));
            }
            if (parse.isTime()) {
                result.add(getSynSetWithLiteral("(zaman)", 1));
            }
            if (parse.isDate()) {
                result.add(getSynSetWithLiteral("(tarih)", 1));
            }
            if (parse.isHashTag()) {
                result.add(getSynSetWithLiteral("(hashtag)", 1));
            }
            if (parse.isEmail()) {
                result.add(getSynSetWithLiteral("(eposta)", 1));
            }
            if (parse.isOrdinal()) {
                result.add(getSynSetWithLiteral("(sayı sıra sıfatı)", 1));
            }
            if (parse.isPercent()) {
                result.add(getSynSetWithLiteral("(yüzde)", 1));
            }
            if (parse.isFraction()) {
                result.add(getSynSetWithLiteral("(kesir sayı)", 1));
            }
            if (parse.isRange()) {
                result.add(getSynSetWithLiteral("(sayı aralığı)", 1));
            }
            if (parse.isReal()) {
                result.add(getSynSetWithLiteral("(reel sayı)", 1));
            }
            if (!parse.isPunctuation() && !parse.isCardinal() && !parse.isReal()) {
                HashSet<String> possibleWords = fsm.getPossibleWords(parse, metaParse);
                for (String possibleWord : possibleWords) {
                    ArrayList<SynSet> synSets = getSynSetsWithLiteral(possibleWord);
                    if (!synSets.isEmpty()) {
                        for (SynSet synSet : synSets) {
                            if (synSet.getPos() != null && (parse.getPos().equals("NOUN") || parse.getPos().equals("ADVERB") || parse.getPos().equals("VERB") || parse.getPos().equals("ADJ") || parse.getPos().equals("CONJ"))) {
                                if (synSet.getPos().equals(Pos.NOUN)) {
                                    if (parse.getPos().equals("NOUN") || parse.getRootPos().equals("NOUN")) {
                                        result.add(synSet);
                                    }
                                } else {
                                    if (synSet.getPos().equals(Pos.ADVERB)) {
                                        if (parse.getPos().equals("ADVERB") || parse.getRootPos().equals("ADVERB")) {
                                            result.add(synSet);
                                        }
                                    } else {
                                        if (synSet.getPos().equals(Pos.VERB)) {
                                            if (parse.getPos().equals("VERB") || parse.getRootPos().equals("VERB")) {
                                                result.add(synSet);
                                            }
                                        } else {
                                            if (synSet.getPos().equals(Pos.ADJECTIVE)) {
                                                if (parse.getPos().equals("ADJ") || parse.getRootPos().equals("ADJ")) {
                                                    result.add(synSet);
                                                }
                                            } else {
                                                if (synSet.getPos().equals(Pos.CONJUNCTION)) {
                                                    if (parse.getPos().equals("CONJ") || parse.getRootPos().equals("CONJ")) {
                                                        result.add(synSet);
                                                    }
                                                } else {
                                                    result.add(synSet);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                result.add(synSet);
                            }
                        }
                    }
                }
                if (result.isEmpty()) {
                    for (String possibleWord : possibleWords) {
                        ArrayList<SynSet> synSets = getSynSetsWithLiteral(possibleWord);
                        result.addAll(synSets);
                    }
                }
            } else {
                result.addAll(getSynSetsWithLiteral(word));
            }
            if (parse.isCardinal() && result.isEmpty()) {
                result.add(getSynSetWithLiteral("(tam sayı)", 1));
            }
        } else {
            result.addAll(getSynSetsWithLiteral(word));
        }
        return result;
    }

    /**
     * Returns a list of literals using 5 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param morphologicalParse3 morphological parse to get possible words
     * @param morphologicalParse4 morphological parse to get possible words
     * @param morphologicalParse5 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param metaParse3          metamorphic parse to get possible words
     * @param metaParse4         metamorphic parse to get possible words
     * @param metaParse5         metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of literals
     */
    public ArrayList<Literal> constructIdiomLiterals(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MorphologicalParse morphologicalParse4, MorphologicalParse morphologicalParse5, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, MetamorphicParse metaParse4, MetamorphicParse metaParse5, FsmMorphologicalAnalyzer fsm) {
        ArrayList<Literal> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        HashSet<String> possibleWords4 = fsm.getPossibleWords(morphologicalParse4, metaParse4);
        HashSet<String> possibleWords5 = fsm.getPossibleWords(morphologicalParse5, metaParse5);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                for (String possibleWord3 : possibleWords3) {
                    for (String possibleWord4 : possibleWords4) {
                        for (String possibleWord5 : possibleWords5) {
                            result.addAll(getLiteralsWithName(possibleWord1 + " " + possibleWord2 + " " + possibleWord3 + " " + possibleWord4 + " " + possibleWord5));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of SynSets using 5 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param morphologicalParse3 morphological parse to get possible words
     * @param morphologicalParse4 morphological parse to get possible words
     * @param morphologicalParse5 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param metaParse3          metamorphic parse to get possible words
     * @param metaParse4          metamorphic parse to get possible words
     * @param metaParse5          metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of SynSets
     */
    public ArrayList<SynSet> constructIdiomSynSets(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MorphologicalParse morphologicalParse4, MorphologicalParse morphologicalParse5, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, MetamorphicParse metaParse4, MetamorphicParse metaParse5, FsmMorphologicalAnalyzer fsm) {
        ArrayList<SynSet> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        HashSet<String> possibleWords4 = fsm.getPossibleWords(morphologicalParse4, metaParse4);
        HashSet<String> possibleWords5 = fsm.getPossibleWords(morphologicalParse5, metaParse5);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                for (String possibleWord3 : possibleWords3) {
                    for (String possibleWord4 : possibleWords4) {
                        for (String possibleWord5 : possibleWords5) {
                            if (numberOfSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3 + " " + possibleWord4 + " " + possibleWord5) > 0) {
                                result.addAll(getSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3 + " " + possibleWord4 + " " + possibleWord5));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of literals using 4 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param morphologicalParse3 morphological parse to get possible words
     * @param morphologicalParse4 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param metaParse3          metamorphic parse to get possible words
     * @param metaParse4         metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of literals
     */
    public ArrayList<Literal> constructIdiomLiterals(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MorphologicalParse morphologicalParse4, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, MetamorphicParse metaParse4, FsmMorphologicalAnalyzer fsm) {
        ArrayList<Literal> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        HashSet<String> possibleWords4 = fsm.getPossibleWords(morphologicalParse4, metaParse4);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                for (String possibleWord3 : possibleWords3) {
                    for (String possibleWord4 : possibleWords4) {
                        result.addAll(getLiteralsWithName(possibleWord1 + " " + possibleWord2 + " " + possibleWord3 + " " + possibleWord4));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of SynSets using 4 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param morphologicalParse3 morphological parse to get possible words
     * @param morphologicalParse4 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param metaParse3          metamorphic parse to get possible words
     * @param metaParse4          metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of SynSets
     */
    public ArrayList<SynSet> constructIdiomSynSets(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MorphologicalParse morphologicalParse4, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, MetamorphicParse metaParse4, FsmMorphologicalAnalyzer fsm) {
        ArrayList<SynSet> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        HashSet<String> possibleWords4 = fsm.getPossibleWords(morphologicalParse4, metaParse4);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                for (String possibleWord3 : possibleWords3) {
                    for (String possibleWord4 : possibleWords4) {
                        if (numberOfSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3 + " " + possibleWord4) > 0) {
                            result.addAll(getSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3 + " " + possibleWord4));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of literals using 3 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param morphologicalParse3 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param metaParse3          metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of literals
     */
    public ArrayList<Literal> constructIdiomLiterals(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, FsmMorphologicalAnalyzer fsm) {
        ArrayList<Literal> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                for (String possibleWord3 : possibleWords3) {
                    result.addAll(getLiteralsWithName(possibleWord1 + " " + possibleWord2 + " " + possibleWord3));
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of SynSets using 3 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param morphologicalParse3 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param metaParse3          metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of SynSets
     */
    public ArrayList<SynSet> constructIdiomSynSets(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, FsmMorphologicalAnalyzer fsm) {
        ArrayList<SynSet> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                for (String possibleWord3 : possibleWords3) {
                    if (numberOfSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3) > 0) {
                        result.addAll(getSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a list of literals using 2 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of literals
     */
    public ArrayList<Literal> constructIdiomLiterals(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MetamorphicParse metaParse1, MetamorphicParse metaParse2, FsmMorphologicalAnalyzer fsm) {
        ArrayList<Literal> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                result.addAll(getLiteralsWithName(possibleWord1 + " " + possibleWord2));
            }
        }
        return result;
    }

    /**
     * Returns a list of SynSets using 2 possible words gathered with the specified morphological parses and metamorphic parses.
     *
     * @param morphologicalParse1 morphological parse to get possible words
     * @param morphologicalParse2 morphological parse to get possible words
     * @param metaParse1          metamorphic parse to get possible words
     * @param metaParse2          metamorphic parse to get possible words
     * @param fsm                 finite state machine morphological analyzer to be used at getting possible words
     * @return a list of SynSets
     */
    public ArrayList<SynSet> constructIdiomSynSets(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MetamorphicParse metaParse1, MetamorphicParse metaParse2, FsmMorphologicalAnalyzer fsm) {
        ArrayList<SynSet> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        for (String possibleWord1 : possibleWords1) {
            for (String possibleWord2 : possibleWords2) {
                if (numberOfSynSetsWithLiteral(possibleWord1 + " " + possibleWord2) > 0) {
                    result.addAll(getSynSetsWithLiteral(possibleWord1 + " " + possibleWord2));
                }
            }
        }
        return result;
    }

    /**
     * Sorts definitions of SynSets in SynSet list according to their lengths.
     */
    public void sortDefinitions() {
        for (SynSet synSet : synSetList()) {
            synSet.sortDefinitions();
        }
    }

    /**
     * Returns a list of SynSets with the interlingual relations of a specified SynSet ID.
     *
     * @param synSetId SynSet ID to be searched
     * @return a list of SynSets with the interlingual relations of a specified SynSet ID
     */
    public ArrayList<SynSet> getInterlingual(String synSetId) {
        if (interlingualList.containsKey(synSetId)) {
            return interlingualList.get(synSetId);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Checks if all senses of literals in the wordnet are in the correct order.
     * @return True if correct, false otherwise.
     */
    public boolean literalCorrectOrderCheck(){
        boolean isOk = true;
        for (String literalName : literalList()){
            ArrayList<Literal> literals = getLiteralsWithName(literalName);
            for (int i = 1; i <= literals.size(); i++){
                if (getSynSetWithLiteral(literalName, i) == null){
                    System.out.println("Literal " + literalName + " with sense " + i + " is missing");
                    isOk = false;
                }
            }
            for (Literal literal : literals){
                if (literal.getSense() > literals.size()){
                    System.out.println("Literal " + literal.getName() + " with sense " + literal.getSense() + " is extra");
                    isOk = false;
                }
            }
        }
        return isOk;
    }

    /**
     * Returns the literals with same senses.
     * @return A list of literals with same senses.
     */
    public ArrayList<Literal> sameLiteralSameSenseCheck() {
        ArrayList<Literal> errorList = new ArrayList<>();
        for (String name : literalList.keySet()) {
            ArrayList<Literal> literals = literalList.get(name);
            for (int i = 0; i < literals.size(); i++) {
                for (int j = i + 1; j < literals.size(); j++) {
                    if (literals.get(i).getSense() == literals.get(j).getSense() && literals.get(i).getName().equals(literals.get(j).getName())) {
                        errorList.add(literals.get(i));
                    }
                }
            }
        }
        return errorList;
    }

    /**
     * Returns true if both synsets contains same literals, false otherwise.
     * @param synSet1 First synset.
     * @param synSet2 Second synset.
     * @return True if both synsets contains same literals, false otherwise.
     */
    private boolean containsSameLiteral(SynSet synSet1, SynSet synSet2){
        for (int i = 0; i < synSet1.getSynonym().literalSize(); i++) {
            Literal literal1 = synSet1.getSynonym().getLiteral(i);
            for (int j = i + 1; j < synSet2.getSynonym().literalSize(); j++) {
                Literal literal2 = synSet2.getSynonym().getLiteral(j);
                if (literal1.getName().equals(literal2.getName()) && synSet1.getPos() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the literals with same SynSets.
     * @return A list of synsets with same literals.
     */
    public ArrayList<SynSet> sameLiteralSameSynSetCheck() {
        ArrayList<SynSet> errorList = new ArrayList<>();
        for (SynSet synSet : synSetList()) {
            if (containsSameLiteral(synSet, synSet)){
                errorList.add(synSet);
            }
        }
        return errorList;
    }

    /**
     * Returns the synSets without part of speech tags.
     * @return A list of synsets without part of speech tags.
     */
    public ArrayList<SynSet> noPosCheck() {
        ArrayList<SynSet> errorList = new ArrayList<>();
        for (SynSet synSet : synSetList()) {
            if (synSet.getPos() == null) {
                errorList.add(synSet);
            }
        }
        return errorList;
    }

    /**
     * Returns the synSets without definitions.
     * @return A list of synsets without definitions.
     */
    public ArrayList<SynSet> noDefinitionCheck() {
        ArrayList<SynSet> errorList = new ArrayList<>();
        for (SynSet synSet : synSetList()) {
            if (synSet.getDefinition() == null) {
                errorList.add(synSet);
            }
        }
        return errorList;
    }

    /**
     * Returns literals related to unexisting synsets.
     * @param modify If true, the relations will be deleted.
     * @return A list of relations which are related to unexisting synsets.
     */
    public ArrayList<Relation> semanticRelationRelatedToNonExistingSynSetCheck(boolean modify) {
        ArrayList<Relation> errorList = new ArrayList<>();
        for (SynSet synSet : synSetList()) {
            for (int j = 0; j < synSet.relationSize(); j++) {
                Relation relation = synSet.getRelation(j);
                if (relation instanceof SemanticRelation && getSynSetWithId(relation.getName()) == null) {
                    errorList.add(relation);
                    if (modify){
                        synSet.removeRelation(relation);
                        j--;
                    }
                }
            }
        }
        return errorList;
    }

    /**
     * Returns SynSets with same relations.
     * @param modify If true, the relations will be deleted.
     * @return A list of synsets with same relations.
     */
    public ArrayList<SynSet> sameSemanticRelationCheck(boolean modify) {
        ArrayList<SynSet> errorList = new ArrayList<>();
        for (SynSet synSet : synSetList()) {
            for (int j = 0; j < synSet.relationSize(); j++) {
                Relation relation = synSet.getRelation(j);
                if (relation instanceof SemanticRelation){
                    Relation same = null;
                    for (int k = j + 1; k < synSet.relationSize(); k++) {
                        if (synSet.getRelation(k) instanceof SemanticRelation && relation.getName().equalsIgnoreCase(synSet.getRelation(k).getName()) && ((SemanticRelation) relation).getRelationType().equals(((SemanticRelation) synSet.getRelation(k)).getRelationType())) {
                            same = synSet.getRelation(k);
                        }
                    }
                    if (same != null) {
                        errorList.add(synSet);
                        if (modify){
                            synSet.removeRelation(same);
                            j--;
                        }
                    }
                }
            }
        }
        return errorList;
    }

    /**
     * Returns SynSets related to itself.
     * @param modify If true, the relations will be deleted.
     * @return A list of synsets which are related to itself.
     */
    public ArrayList<SynSet> inbreeedingRelationCheck(boolean modify) {
        ArrayList<SynSet> errorList = new ArrayList<>();
        for (SynSet synSet : synSetList()) {
            for (int j = 0; j < synSet.relationSize(); j++) {
                Relation relation = synSet.getRelation(j);
                if (relation instanceof SemanticRelation && relation.getName().equals(synSet.getId())) {
                    errorList.add(synSet);
                    if (modify){
                        synSet.removeRelation(relation);
                        j--;
                    }
                }
            }
        }
        return errorList;
    }

    /**
     * Returns synsets where the reverse of the relations does not exist.
     * @param modify If true, the reverse relations will be added.
     * @return A list of synsets where the reverse of the relations does not exist.
     */
    public ArrayList<SynSet> noReverseRelationCheck(boolean modify) {
        ArrayList<SynSet> errorList = new ArrayList<>();
        for (SynSet synSet : synSetList()) {
            for (int j = 0; j < synSet.relationSize(); j++) {
                Relation relation = synSet.getRelation(j);
                String id = relation.getName();
                if (relation instanceof SemanticRelation){
                    SemanticRelationType reverseType = SemanticRelation.reverse(((SemanticRelation) relation).getRelationType());
                    if (reverseType != null){
                        SynSet reverseSynSet = getSynSetWithId(id);
                        if (reverseSynSet == null){
                            errorList.add(synSet);
                            if (modify){
                                synSet.removeRelation(relation);
                            }
                        } else {
                            if (!reverseSynSet.containsRelation(new SemanticRelation(synSet.getId(), reverseType))){
                                errorList.add(synSet);
                                if (modify){
                                    reverseSynSet.addRelation(new SemanticRelation(synSet.getId(), reverseType));
                                }
                            }
                        }
                    }
                }
            }
        }
        return errorList;
    }

    /**
     * Method to write SynSets to the specified file in the XML format.
     *
     * @param fileName file name to write XML files
     */
    public void saveAsXml(String fileName) {
        BufferedWriter outfile;
        try {
            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(fileName)), StandardCharsets.UTF_8);
            outfile = new BufferedWriter(writer);
            outfile.write("<SYNSETS>\n");
            for (SynSet synSet : synSetList.values()) {
                synSet.saveAsXml(outfile);
            }
            outfile.write("</SYNSETS>\n");
            outfile.close();
        } catch (IOException ioException) {
            System.out.println("Output file can not be opened");
        }
    }

    /**
     * Method to write SynSets to the specified file as LMF.
     *
     * @param fileName file name to write files
     */
    public void saveAsLmf(String fileName) {
        BufferedWriter outfile;
        String wordIdString = null;
        String senseId;
        IdMapping iliMapping = new IdMapping("ili-mapping.txt");
        try {
            OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(fileName)), StandardCharsets.UTF_8);
            outfile = new BufferedWriter(writer);
            outfile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE LexicalResource SYSTEM \"http://globalwordnet.github.io/schemas/WN-LMF-1.0.dtd\">\n" +
                    "<LexicalResource xmlns:dc=\"http://purl.org/dc/elements/1.1/\">");
            outfile.write("<Lexicon id=\"tr\" label=\"Kenet\" language=\"tr\" email=\"olcaytaner@isikun.edu.tr\" license=\"https://creativecommons.org/publicdomain/zero/1.0/\" version=\"1.0\" citation=\"R. Ehsani, E. Solak, O. T. Yildiz , Constructing a WordNet for Turkish Using Manual and Automatic Annotation, ACM Transactions on Asian and Low-Resource Language Information Processing, Vol. 17, No. 3, Article 24, 2018.\" url=\"https://github.com/olcaytaner/WordNet/\">\n");
            int wordId = 0;
            for (String literal : literalList.keySet()) {
                if (Word.isPunctuation(literal) || literal.startsWith("(")) {
                    continue;
                }
                ArrayList<Literal> literals = literalList.get(literal);
                HashSet<SynSet> synSetSet = new HashSet<>();
                for (Literal literal1 : literals) {
                    if (getSynSetWithId(literal1.synSetId).getPos() == null) {
                        continue;
                    }
                    synSetSet.add(getSynSetWithId(literal1.synSetId));
                }
                if (synSetSet.isEmpty()) {
                    continue;
                }
                ArrayList<SynSet> synSets = new ArrayList<>();
                synSets.addAll(synSetSet);
                synSets.sort(Comparator.comparing((SynSet o) -> o.getPos().toString()));
                SynSet previous = null;
                for (SynSet current : synSets) {
                    if (previous == null || !current.getPos().equals(previous.getPos())) {
                        wordIdString = "w" + wordId;
                        wordId++;
                        if (previous != null) {
                            outfile.write("\t</LexicalEntry>\n");
                        }
                        outfile.write("\t<LexicalEntry id=\"" + wordIdString + "\">\n");
                        switch (current.getPos()) {
                            case NOUN:
                                outfile.write("\t\t<Lemma writtenForm=\"" + literal + "\" partOfSpeech=\"n\"/>\n");
                                break;
                            case ADJECTIVE:
                                outfile.write("\t\t<Lemma writtenForm=\"" + literal + "\" partOfSpeech=\"a\"/>\n");
                                break;
                            case VERB:
                                outfile.write("\t\t<Lemma writtenForm=\"" + literal + "\" partOfSpeech=\"v\"/>\n");
                                break;
                            case ADVERB:
                                outfile.write("\t\t<Lemma writtenForm=\"" + literal + "\" partOfSpeech=\"r\"/>\n");
                                break;
                            default:
                                if (literal.equals("\"")) {
                                    outfile.write("\t\t<Lemma writtenForm=\"&quot;\" partOfSpeech=\"x\"/>\n");
                                } else {
                                    if (literal.equals("&")) {
                                        outfile.write("\t\t<Lemma writtenForm=\"&amp;\" partOfSpeech=\"x\"/>\n");
                                    } else {
                                        outfile.write("\t\t<Lemma writtenForm=\"" + literal + "\" partOfSpeech=\"x\"/>\n");
                                    }
                                }
                                break;
                        }
                    }
                    senseId = current.getId();
                    outfile.write("\t\t<Sense id=\"" + wordIdString + "_" + senseId.substring(senseId.length() - 7) + "\" synset=\"" + senseId + "\"/>\n");
                    previous = current;
                }
                outfile.write("\t</LexicalEntry>\n");
            }
            for (SynSet synSet : synSetList.values()) {
                if (synSet.getSynonym().getLiteral(0).getName().startsWith("(")) {
                    continue;
                }
                ArrayList<String> interlinguals = synSet.getInterlingual();
                if (!interlinguals.isEmpty() && iliMapping.map(interlinguals.get(0)) != null) {
                    synSet.saveAsLmf(outfile, iliMapping.map(interlinguals.get(0)));
                } else {
                    synSet.saveAsLmf(outfile, "");
                }
            }
            outfile.write("</Lexicon>\n");
            outfile.write("</LexicalResource>\n");
            outfile.close();
        } catch (IOException ioException) {
            System.out.println("Output file can not be opened");
        }
    }

    /**
     * Returns the size of the SynSet list.
     *
     * @return the size of the SynSet list
     */
    public int size() {
        return synSetList.size();
    }

    /*
     * Helper functions: These methods conduct common operations between similarity metrics.
     */

    /**
     * Conduct common operations between similarity metrics.
     *
     * @param pathToRootOfSynSet1 first list of Strings
     * @param pathToRootOfSynSet2 second list of Strings
     * @return path length
     */
    public int findPathLength(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        // There might not be a path between nodes, due to missing nodes. Keep track of that as well. Break when the LCS if found.
        for (int i = 0; i < pathToRootOfSynSet1.size(); i++) {
            int foundIndex = pathToRootOfSynSet2.indexOf(pathToRootOfSynSet1.get(i));
            if (foundIndex != -1) {
                // Index of two lists - 1 is equal to path length. If there is not path, return -1
                return i + foundIndex - 1;
            }
        }
        return -1;
    }

    /**
     * Returns the depth of path.
     *
     * @param pathToRootOfSynSet1 first list of Strings
     * @param pathToRootOfSynSet2 second list of Strings
     * @return LCS depth
     */
    public int findLCSdepth(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        SimpleEntry<String, Integer> temp = findLCS(pathToRootOfSynSet1, pathToRootOfSynSet2);
        if (temp != null) {
            return temp.getValue();
        }
        return -1;
    }

    /**
     * Returns the ID of LCS of path.
     *
     * @param pathToRootOfSynSet1 first list of Strings
     * @param pathToRootOfSynSet2 second list of Strings
     * @return LCS ID
     */
    public String findLCSid(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        SimpleEntry<String, Integer> temp = findLCS(pathToRootOfSynSet1, pathToRootOfSynSet2);
        if (temp != null) {
            return temp.getKey();
        }
        return null;
    }

    /**
     * Returns depth and ID of the LCS.
     *
     * @param pathToRootOfSynSet1 first list of Strings
     * @param pathToRootOfSynSet2 second list of Strings
     * @return depth and ID of the LCS
     */
    private SimpleEntry<String, Integer> findLCS(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        for (int i = 0; i < pathToRootOfSynSet1.size(); i++) {
            String LCSid = pathToRootOfSynSet1.get(i);
            if (pathToRootOfSynSet2.contains(LCSid)) {
                return new SimpleEntry<>(LCSid, pathToRootOfSynSet1.size() - i + 1);
            }
        }
        return null;
    }

    /**
     * Finds the path to the root node of a SynSets.
     *
     * @param synSet SynSet whose root path will be found
     * @return list of String corresponding to nodes in the path
     */
    public ArrayList<String> findPathToRoot(SynSet synSet) {
        ArrayList<String> pathToRoot = new ArrayList<>();
        while (synSet != null) {
            if (pathToRoot.contains(synSet.getId())){
                break;
            }
            pathToRoot.add(synSet.getId());
            synSet = percolateUp(synSet);
        }
        return pathToRoot;
    }

    /**
     * Finds the parent of a node. It does not move until the root, instead it goes one level up.
     *
     * @param root SynSet whose parent will be find
     * @return parent SynSet
     */
    public SynSet percolateUp(SynSet root) {
        for (int i = 0; i < root.relationSize(); i++) {
            Relation r = root.getRelation(i);
            if (r instanceof SemanticRelation) {
                if (((SemanticRelation) r).getRelationType().equals(SemanticRelationType.HYPERNYM) || ((SemanticRelation) r).getRelationType().equals(SemanticRelationType.INSTANCE_HYPERNYM)) {
                    root = getSynSetWithId(r.getName());
                    // return even if one hypernym is found.
                    return root;
                }
            }
        }
        return null;
    }

    /**
     * Returns the part of speech tag of a synset according to the TDK rules.
     * @param synSet Synset for which the part of speech tag will be returned.
     * @return Part of speech symbol for the given synset.
     */
    public String getPos(SynSet synSet){
        switch (synSet.getPos()){
            case NOUN:
                return "i.";
            case ADJECTIVE:
                return "s.";
            case VERB:
                return "f.";
            case ADVERB:
                return "z.";
            case INTERJECTION:
                return "c.";
            case PRONOUN:
                return "zm.";
            case CONJUNCTION:
                return "b.";
            default:
                return "";
        }
    }

    /**
     * Returns true if the literal is a single character, false otherwise.
     * @param literal Literal name
     * @return True if the literal is a single character, false otherwise.
     */
    public boolean isLetter(String literal){
        if ((literal.charAt(0) >= 'a' && literal.charAt(0) <= 'z') || (literal.charAt(0) >= 'A' && literal.charAt(0) <= 'Z')){
            return true;
        }
        return literal.charAt(0) == 'ç' || literal.charAt(0) == 'ö' || literal.charAt(0) == 'ğ' || literal.charAt(0) == 'ü' || literal.charAt(0) == 'ş' || literal.charAt(0) == 'ı' || literal.charAt(0) == 'â' || literal.charAt(0) == 'û' || literal.charAt(0) == 'î';
    }

    /**
     * Converts the Turkish characters 'çöğüşı' in the text to its Latex counterparts.
     * @param text Text to be converted.
     * @return Same text with 'çöğüşı' characters replaced with their Latex counterparts.
     */
    public String latexTurkish(String text){
        if (text.equals("$") || text.equals("&")){
            return "";
        }
        return text.replaceAll("ğ", "\\\\u{g}").
                replaceAll("ş", "\\\\c{s}").
                replaceAll("ı", "{\\\\i}").
                replaceAll("\\(\\^\\)", "\\\\^").
                replaceAll("İ",  " \\\\.{I}").
                replaceAll("Ş",  " \\\\c{S}").replaceAll("%", "yüzde");
    }

    public String preamble(String literal, SynSet synSet, ArrayList<SynSet> synSets){
        if (synSets.size() > 1){
            return synSet.getSynonym().getLiteral(literal).sense + ". ";
        }
        return "";
    }

    /**
     * Checks if two characters are capped versions of themselves.
     * @param ch1 First character
     * @param ch2 Second character
     * @return True if the second character is a capped version of the first character, false otherwise.
     */
    public boolean areEqual(char ch1, char ch2){
        if (ch1 == 'a' && ch2 == 'â'){
            return true;
        }
        if (ch2 == 'a' && ch1 == 'â'){
            return true;
        }
        if (ch1 == 'u' && ch2 == 'û'){
            return true;
        }
        if (ch2 == 'u' && ch1 == 'û'){
            return true;
        }
        if (ch1 == 'ı' && ch2 == 'î'){
            return true;
        }
        if (ch2 == 'ı' && ch1 == 'î'){
            return true;
        }
        return (ch1 + "").toUpperCase(new Locale("tr")).equals((ch2 + "").toUpperCase(new Locale("tr")));
    }

    /**
     * Generates a definition for the literal in a given synset. Definition is generated as follows: The first character
     * of the definition is capitalized, then the rest of the definition is concatenated. Other than that, all the
     * literals except the given literal are added as synonyms separated via semicolons.
     * @param synSet SynSet for which definition will be generated.
     * @param literal Literal for which definition will be generated.
     * @return A definition string for a literal.
     */
    public String getDefinition(SynSet synSet, String literal){
        StringBuilder result;
        if (!synSet.getDefinition().isEmpty()){
            result = new StringBuilder(latexTurkish((synSet.getDefinition().charAt(0) + "").toUpperCase(new Locale("tr")) + synSet.getDefinition().substring(1)));
        } else {
            result = new StringBuilder();
        }
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            if (!synSet.getSynonym().getLiteral(i).getName().equals(literal)){
                if (getSynSetsWithLiteral(synSet.getSynonym().getLiteral(i).getName()).size() > 1){
                    result.append("; ").append(latexTurkish(synSet.getSynonym().getLiteral(i).getName())).append("$^{").append(synSet.getSynonym().getLiteral(i).sense).append("}$");
                } else {
                    result.append("; ").append(latexTurkish(synSet.getSynonym().getLiteral(i).getName()));
                }
            }
        }
        return result.toString();
    }

    /**
     * Generates a TDK style dictionary for this wordnet synsets.
     * @param fileName Output file name for TDK dictionary.
     * @param fsm Finite state morphological analyzer.
     */
    public void generateDictionary(String fileName, FsmMorphologicalAnalyzer fsm){
        try {
            PrintWriter output = new PrintWriter(fileName);
            output.println("\\documentclass[10pt,a4paper,twoside]{article}\n" +
                    "\n" +
                    "\\usepackage[top=3.5cm,bottom=3.5cm,left=1.7cm,right=1.7cm,columnsep=30pt]{geometry}\n" +
                    "\n" +
                    "\\usepackage[utf8]{inputenc}\n" +
                    "\\usepackage[T1]{fontenc}\n" +
                    "\n" +
                    "\\usepackage{palatino}\n" +
                    "\n" +
                    "\\usepackage{microtype}\n" +
                    "\n" +
                    "\\usepackage{multicol}\n" +
                    "\n" +
                    "\\usepackage[bf,sf,center]{titlesec}\n" +
                    "\n" +
                    "\\usepackage{fancyhdr}\n" +
                    "\\fancyhead[L]{\\textsf{\\rightmark}}\n" +
                    "\\fancyhead[R]{\\textsf{\\leftmark}}\n" +
                    "\\renewcommand{\\headrulewidth}{1.4pt}\n" +
                    "\\fancyfoot[C]{\\textbf{\\textsf{\\thepage}}}\n" +
                    "\\renewcommand{\\footrulewidth}{1.4pt}\n" +
                    "\\pagestyle{fancy}\n" +
                    "\n" +
                    "\\newcommand{\\entry}[3]{\\markboth{#1}{#1}\\textbf{#1}\\ {(#2)}\\ $\\bullet$\\ {#3}}\n" +
                    "\n" +
                    "\\begin{document}\n");
            String prev = "0";
            for (String literal : literalList()){
                if (!isLetter(literal)){
                    continue;
                }
                if (!areEqual(literal.charAt(0), prev.charAt(0))){
                    if (!prev.equals("0")){
                        output.println("\\end{multicols}\n");
                    }
                    output.println("\\section*{" + (literal.charAt(0) + "").toUpperCase(new Locale("tr")) + "}");
                    output.println("\\begin{multicols}{2}");
                    prev = literal;
                }
                ArrayList<SynSet> synSets = getSynSetsWithLiteral(literal);
                output.print("\\entry{" + latexTurkish(literal) + "}{" + getPos(synSets.get(0)) + "}{");
                for (int i = 0; i < synSets.size(); i++){
                    for (int j = i + 1; j < synSets.size(); j++){
                        if (synSets.get(i).getSynonym().getLiteral(literal).sense > synSets.get(j).getSynonym().getLiteral(literal).sense){
                            Collections.swap(synSets, i, j);
                        }
                    }
                }
                for (int i = 0; i < synSets.size(); i++){
                    SynSet synSet = synSets.get(i);
                    if (i > 0 && !synSet.getPos().equals(synSets.get(i - 1).getPos())){
                        if (synSet.getExample() == null){
                            output.print(preamble(literal, synSet, synSets) + getPos(synSet) + " " + latexTurkish(getDefinition(synSet, literal)) + " ");
                        } else {
                            output.print(preamble(literal, synSet, synSets) + getPos(synSet) + " " + latexTurkish(getDefinition(synSet, literal) + " {\\em " + synSet.getModifiedExample(literal, fsm) + "} "));
                        }
                    } else {
                        if (synSet.getExample() == null){
                            output.print(preamble(literal, synSet, synSets) + latexTurkish(getDefinition(synSet, literal) + " "));
                        } else {
                            output.print(preamble(literal, synSet, synSets) + latexTurkish(getDefinition(synSet, literal) + " {\\em " + synSet.getModifiedExample(literal, fsm) + "} "));
                        }
                    }
                }
                output.println("}");
                output.println();
            }
            output.println("\\end{multicols}\n");
            output.println("\\end{document}");
            output.close();
        } catch (FileNotFoundException ignored) {
        }
    }

    public String toJson() {
        StringBuilder result = new StringBuilder();
        result.append("[\n");
        for (SynSet synSet : synSetList()){
            result.append("\t{\"id\":\"").append(synSet.getId()).append("\", \"pos\":\"").append(synSet.getPos()).append("\", \"definition\":\"").append(synSet.getLongDefinition().replaceAll("\"", "")).append("\", \"words\":[");
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                Literal literal = synSet.getSynonym().getLiteral(i);
                if (i == 0){
                    result.append("\"").append(literal.name).append("\"");
                } else {
                    result.append(",\"").append(literal.name).append("\"");
                }
            }
            result.append("]");
            result.append("},\n");
        }
        result.append("]\n");
        return result.toString();
    }

}
