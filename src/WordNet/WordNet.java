package WordNet;

import Dictionary.*;
import MorphologicalAnalysis.*;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.*;
import java.text.Collator;
import java.util.*;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class WordNet {

    private TreeMap<String, SynSet> synSetList;
    private TreeMap<String, ArrayList<Literal>> literalList;
    private Locale locale;
    private HashMap<String, ExceptionalWord> exceptionList;
    private HashMap<String, ArrayList<SynSet>> interlingualList;

    private class ReadWordNetTask extends SwingWorker {
        private String fileName;

        public ReadWordNetTask(String fileName){
            this.fileName = fileName;
        }

        protected Object doInBackground() throws Exception {
            Node rootNode, synSetNode, partNode, ilrNode, srNode, typeNode, toNode, literalNode, textNode, senseNode;
            Document doc;
            DOMParser parser = new DOMParser();
            SynSet currentSynSet = null;
            Literal currentLiteral;
            int parsedCount, totalCount;
            try {
                parser.parse(fileName);
            } catch (SAXException | IOException e) {
                e.printStackTrace();
            }
            doc = parser.getDocument();
            interlingualList = new HashMap<>();
            synSetList = new TreeMap<>();
            literalList = new TreeMap<>((Comparator) (o1, o2) -> {
                Locale locale1 = new Locale("tr");
                Collator collator = Collator.getInstance(locale1);
                return collator.compare(((String) o1).toLowerCase(locale1), ((String) o2).toLowerCase(locale1));
            });
            rootNode = doc.getFirstChild();
            synSetNode = rootNode.getFirstChild();
            parsedCount = 0;
            totalCount = rootNode.getChildNodes().getLength();
            while (synSetNode != null){
                partNode = synSetNode.getFirstChild();
                while (partNode != null){
                    if (partNode.getNodeName().equals("ID")){
                        currentSynSet = new SynSet(partNode.getFirstChild().getNodeValue());
                        addSynSet(currentSynSet);
                    } else {
                        if (partNode.getNodeName().equals("DEF") && currentSynSet != null){
                            currentSynSet.setDefinition(partNode.getFirstChild().getNodeValue());
                        } else {
                            if (partNode.getNodeName().equals("EXAMPLE") && currentSynSet != null){
                                currentSynSet.setExample(partNode.getFirstChild().getNodeValue());
                            } else {
                                if (partNode.getNodeName().equals("BCS") && currentSynSet != null) {
                                    currentSynSet.setBcs(Integer.parseInt(partNode.getFirstChild().getNodeValue()));
                                } else {
                                    if (partNode.getNodeName().equals("POS") && currentSynSet != null) {
                                        switch (partNode.getFirstChild().getNodeValue().charAt(0)) {
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
                                                System.out.println("Pos " + partNode.getFirstChild().getNodeValue() + " is not defined for SynSet " + currentSynSet.getId());
                                                break;
                                        }
                                    } else {
                                        if (partNode.getNodeName().equals("SR") && currentSynSet != null) {
                                            srNode = partNode.getFirstChild();
                                            if (srNode != null) {
                                                typeNode = srNode.getNextSibling();
                                                if (typeNode != null && typeNode.getNodeName().equals("TYPE")) {
                                                    toNode = typeNode.getNextSibling();
                                                    if (toNode != null && typeNode.getNodeName().equals("TO")) {
                                                        currentSynSet.addRelation(new SemanticRelation(srNode.getNodeValue(), typeNode.getFirstChild().getNodeValue(), Integer.parseInt(toNode.getFirstChild().getNodeValue())));
                                                    } else {
                                                        currentSynSet.addRelation(new SemanticRelation(srNode.getNodeValue(), typeNode.getFirstChild().getNodeValue()));
                                                    }
                                                } else {
                                                    System.out.println("SR node " + srNode.getNodeValue() + " of synSet " + currentSynSet.getId() + " does not contain type value");
                                                }
                                            } else {
                                                System.out.println("SR node of synSet " + currentSynSet.getId() + " does not contain name");
                                            }
                                        } else {
                                            if (partNode.getNodeName().equals("ILR") && currentSynSet != null) {
                                                ilrNode = partNode.getFirstChild();
                                                if (ilrNode != null) {
                                                    typeNode = ilrNode.getNextSibling();
                                                    if (typeNode != null && typeNode.getNodeName().equals("TYPE")) {
                                                        String interlingualId = ilrNode.getNodeValue();
                                                        ArrayList<SynSet> synSetList;
                                                        if (interlingualList.containsKey(interlingualId)){
                                                            synSetList = interlingualList.get(interlingualId);
                                                        } else {
                                                            synSetList = new ArrayList<>();
                                                        }
                                                        synSetList.add(currentSynSet);
                                                        interlingualList.put(interlingualId, synSetList);
                                                        currentSynSet.addRelation(new InterlingualRelation(interlingualId, typeNode.getFirstChild().getNodeValue()));
                                                    } else {
                                                        System.out.println("ILR node " + ilrNode.getNodeValue() + " of synSet " + currentSynSet.getId() + " does not contain type value");
                                                    }
                                                } else {
                                                    System.out.println("ILR node of synSet " + currentSynSet.getId() + " does not contain name");
                                                }
                                            } else {
                                                if (partNode.getNodeName().equals("SYNONYM") && currentSynSet != null) {
                                                    literalNode = partNode.getFirstChild();
                                                    while (literalNode != null) {
                                                        textNode = literalNode.getFirstChild();
                                                        if (textNode != null) {
                                                            if (literalNode.getNodeName().equals("LITERAL")) {
                                                                senseNode = textNode.getNextSibling();
                                                                if (senseNode != null) {
                                                                    if (senseNode.getNodeName().equals("SENSE") && senseNode.getFirstChild() != null) {
                                                                        currentLiteral = new Literal(textNode.getNodeValue(), Integer.parseInt(senseNode.getFirstChild().getNodeValue()), currentSynSet.getId());
                                                                        currentSynSet.addLiteral(currentLiteral);
                                                                        addLiteralToLiteralList(currentLiteral);
                                                                        srNode = senseNode.getNextSibling();
                                                                        while (srNode != null) {
                                                                            if (srNode.getNodeName().equals("SR")) {
                                                                                typeNode = srNode.getFirstChild().getNextSibling();
                                                                                if (typeNode != null && typeNode.getNodeName().equals("TYPE")) {
                                                                                    currentLiteral.addRelation(new SemanticRelation(srNode.getFirstChild().getNodeValue(), typeNode.getFirstChild().getNodeValue()));
                                                                                } else {
                                                                                    System.out.println("SR node " + srNode.getFirstChild().getNodeValue() + "of literal " + currentLiteral.getName() + " of synSet " + currentSynSet.getId() + " does not contain type value");
                                                                                }
                                                                            }
                                                                            srNode = srNode.getNextSibling();
                                                                        }
                                                                    } else {
                                                                        System.out.println("Literal Node " + textNode.getNodeValue() + " of SynSet " + currentSynSet.getId() + " include nodes other than sense node");
                                                                    }
                                                                } else {
                                                                    System.out.println("Literal Node " + textNode.getNodeValue() + " of SynSet " + currentSynSet.getId() + " does not include sense node");
                                                                }
                                                            } else {
                                                                System.out.println("SynSet " + currentSynSet.getId() + " includes nodes other than literal node");
                                                            }
                                                        } else {
                                                            System.out.println("Literal Node of SynSet " + currentSynSet.getId() + " does not exist");
                                                        }
                                                        literalNode = literalNode.getNextSibling();
                                                    }
                                                } else {
                                                    if (partNode.getNodeName().equals("SNOTE") && currentSynSet != null) {
                                                        currentSynSet.setNote(partNode.getFirstChild().getNodeValue());
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
                parsedCount++;
                setProgress((100 * parsedCount) / totalCount);
                synSetNode = synSetNode.getNextSibling();
            }
            return 0;
        }
    }

    public void readExceptionFile(String exceptionFileName){
        NamedNodeMap attributes;
        String wordName, rootForm;
        Pos pos;
        Node wordNode, rootNode;
        DOMParser parser = new DOMParser();
        Document doc;
        try {
            parser.parse(exceptionFileName);
            exceptionList = new HashMap<>();
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        doc = parser.getDocument();
        rootNode = doc.getFirstChild();
        wordNode = rootNode.getFirstChild();
        while (wordNode != null){
            if (wordNode.hasAttributes()){
                attributes = wordNode.getAttributes();
                wordName = attributes.getNamedItem("name").getNodeValue();
                rootForm = attributes.getNamedItem("root").getNodeValue();
                switch (attributes.getNamedItem("pos").getNodeValue()){
                    case "Adj":
                        pos = Pos.ADJECTIVE;
                        break;
                    case "Adv":
                        pos = Pos.ADVERB;
                        break;
                    case "Noun":
                        pos = Pos.NOUN;
                        break;
                    case "Verb":
                        pos = Pos.VERB;
                        break;
                    default:
                        pos = Pos.NOUN;
                        break;
                }
                exceptionList.put(wordName, new ExceptionalWord(wordName, rootForm, pos));
            }
            wordNode = wordNode.getNextSibling();
        }
    }

    public WordNet(){
        this("Data/Wordnet/turkish_wordnet.xml", new Locale("tr"));
    }

    public WordNet(final String fileName, String exceptionFileName, Locale locale, final JProgressBar progressBar){
        this(fileName, locale, progressBar);
        readExceptionFile(exceptionFileName);
    }

    public WordNet(final String fileName, Locale locale, final JProgressBar progressBar){
        synSetList = new TreeMap<>();
        literalList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.locale = locale;
        ReadWordNetTask task = new ReadWordNetTask(fileName);
        task.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                Runtime runtime = Runtime.getRuntime();
                progressBar.setString("Reading " + fileName + " %" + evt.getNewValue() + " complete. Memory Usage: " + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");
                if ((Integer) evt.getNewValue() == 100) {
                    progressBar.setVisible(false);
                }
            }
        });
        task.execute();
    }

    public WordNet(final String fileName, Locale locale){
        synSetList = new TreeMap<>();
        literalList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.locale = locale;
        ReadWordNetTask task = new ReadWordNetTask(fileName);
        task.execute();
        try {
            task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public WordNet(final String fileName, String exceptionFileName, Locale locale){
        this(fileName, locale);
        readExceptionFile(exceptionFileName);
    }

    public WordNet(String fileType, String version){
        int literalOffset, literalSize;
        synSetList = new TreeMap<>();
        literalList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.locale = new Locale("en");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data." + fileType), "UTF8"));
            String line = br.readLine();
            while (line != null){
                String[] first = line.split("[|]");
                String definition = first[1].trim().split(";")[0];
                String[] second = first[0].split(" ");
                String id = "ENG" + version + "-" + second[0] + "-" + second[2];
                System.out.println(id);
                SynSet synSet = new SynSet(id);
                synSet.setDefinition(definition);
                if (second[2].equalsIgnoreCase("a") || second[2].equalsIgnoreCase("s")){
                    synSet.setPos(Pos.ADJECTIVE);
                } else {
                    if (second[2].equalsIgnoreCase("n")){
                        synSet.setPos(Pos.NOUN);
                    } else {
                        if (second[2].equalsIgnoreCase("r")){
                            synSet.setPos(Pos.ADVERB);
                        } else {
                            synSet.setPos(Pos.VERB);
                        }
                    }
                }
                literalOffset = 3;
                literalSize = Integer.parseInt(second[literalOffset], 16);
                for (int i = 1; i <= literalSize; i++){
                    Literal literal = new Literal(second[literalOffset + 2 * i - 1].replaceAll("_", " "), Integer.parseInt(second[literalOffset + 2 * i], 16), id);
                    addLiteralToLiteralList(literal);
                    synSet.addLiteral(literal);
                }
                addSynSet(synSet);
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLiteralToLiteralList(Literal literal){
        ArrayList<Literal> literals;
        if (literalList.containsKey(literal.getName())){
            literals = literalList.get(literal.getName());
        } else {
            literals = new ArrayList<>();
        }
        literals.add(literal);
        literalList.put(literal.getName(), literals);
    }

    public Locale getLocale(){
        return locale;
    }

    public void mergeSynSets(String synSetFile){
        try {
            BufferedReader infile = new BufferedReader(new FileReader(synSetFile));
            BufferedWriter outfile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Data/Wordnet/mapping.txt"), "UTF-8"));
            String line = infile.readLine();
            while (line != null){
                String[] synSetIds = line.split(" ");
                SynSet mergedOne = getSynSetWithId(synSetIds[0]);
                for (int i = 1; i < synSetIds.length; i++){
                    SynSet toBeMerged = getSynSetWithId(synSetIds[i]);
                    mergedOne.mergeSynSet(toBeMerged);
                    outfile.write(synSetIds[i] + "->" + synSetIds[0] + "\n");
                    removeSynSet(toBeMerged);
                }
                line = infile.readLine();
            }
            infile.close();
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRelations(String fileType, String version){
        int literalSize, literalOffset, relationSize, relationOffset;
        SynSet currentSynSet;
        SemanticRelationType relationType;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data." + fileType), "UTF8"));
            String line = br.readLine();
            while (line != null){
                String[] first = line.split("[|]");
                String[] second = first[0].split(" ");
                String id = "ENG" + version + "-" + second[0] + "-" + second[2];
                System.out.println(id);
                currentSynSet = getSynSetWithId(id);
                literalOffset = 3;
                literalSize = Integer.parseInt(second[literalOffset], 16);
                relationOffset = literalOffset + 2 * literalSize + 1;
                relationSize = Integer.parseInt(second[relationOffset]);
                for (int i = 1; i <= relationSize; i++){
                    String relatedId = "ENG" + version + "-" + second[relationOffset + 4 * i - 2] + "-" + second[relationOffset + 4 * i - 1];
                    String relationTag = second[relationOffset + 4 * i - 3];
                    int relationFrom = Integer.parseInt(second[relationOffset + 4 * i].substring(0, 2), 16);
                    int relationTo = Integer.parseInt(second[relationOffset + 4 * i].substring(2, 4), 16);
                    if (relationTag.equals("!")){
                        relationType = SemanticRelationType.ANTONYM;
                    } else {
                        if (relationTag.equals("@")){
                            relationType = SemanticRelationType.HYPERNYM;
                        } else {
                            if (relationTag.equals("@i")){
                                relationType = SemanticRelationType.INSTANCE_HYPERNYM;
                            } else {
                                if (relationTag.equals("~")){
                                    relationType = SemanticRelationType.HYPONYM;
                                } else {
                                    if (relationTag.equals("~i")){
                                        relationType = SemanticRelationType.INSTANCE_HYPONYM;
                                    } else {
                                        if (relationTag.equals("#m")){
                                            relationType = SemanticRelationType.MEMBER_HOLONYM;
                                        } else {
                                            if (relationTag.equals("#s")){
                                                relationType = SemanticRelationType.SUBSTANCE_HOLONYM;
                                            } else {
                                                if (relationTag.equals("#p")){
                                                    relationType = SemanticRelationType.PART_HOLONYM;
                                                } else {
                                                    if (relationTag.equals("%m")){
                                                        relationType = SemanticRelationType.MEMBER_MERONYM;
                                                    } else {
                                                        if (relationTag.equals("%s")){
                                                            relationType = SemanticRelationType.SUBSTANCE_MERONYM;
                                                        } else {
                                                            if (relationTag.equals("%p")){
                                                                relationType = SemanticRelationType.PART_MERONYM;
                                                            } else {
                                                                if (relationTag.equals("=")){
                                                                    relationType = SemanticRelationType.ATTRIBUTE;
                                                                } else {
                                                                    if (relationTag.equals("+") || relationTag.equals("\\")){
                                                                        relationType = SemanticRelationType.DERIVATION_RELATED;
                                                                    } else {
                                                                        if (relationTag.equals(";c")){
                                                                            relationType = SemanticRelationType.DOMAIN_TOPIC;
                                                                        } else {
                                                                            if (relationTag.equals("-c")){
                                                                                relationType = SemanticRelationType.MEMBER_TOPIC;
                                                                            } else {
                                                                                if (relationTag.equals(";r")){
                                                                                    relationType = SemanticRelationType.DOMAIN_REGION;
                                                                                } else {
                                                                                    if (relationTag.equals("-r")){
                                                                                        relationType = SemanticRelationType.MEMBER_REGION;
                                                                                    } else {
                                                                                        if (relationTag.equals(";u")){
                                                                                            relationType = SemanticRelationType.DOMAIN_USAGE;
                                                                                        } else {
                                                                                            if (relationTag.equals("-u")){
                                                                                                relationType = SemanticRelationType.MEMBER_USAGE;
                                                                                            } else {
                                                                                                if (relationTag.equals("*")){
                                                                                                    relationType = SemanticRelationType.ENTAILMENT;
                                                                                                } else {
                                                                                                    if (relationTag.equals(">")){
                                                                                                        relationType = SemanticRelationType.CAUSE;
                                                                                                    } else {
                                                                                                        if (relationTag.equals("^")){
                                                                                                            relationType = SemanticRelationType.ALSO_SEE;
                                                                                                        } else {
                                                                                                            if (relationTag.equals("$")){
                                                                                                                relationType = SemanticRelationType.VERB_GROUP;
                                                                                                            } else {
                                                                                                                if (relationTag.equals("&")){
                                                                                                                    relationType = SemanticRelationType.SIMILAR_TO;
                                                                                                                } else {
                                                                                                                    if (relationTag.equals("<")){
                                                                                                                        relationType = SemanticRelationType.PARTICIPLE_OF_VERB;
                                                                                                                    } else {
                                                                                                                        relationType = null;
                                                                                                                        System.out.println("Semantic relation " + relationTag + " is not defined");
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
                                    }
                                }
                            }
                        }
                    }
                    if (relationFrom == 0){
                        currentSynSet.addRelation(new SemanticRelation(relatedId, relationType));
                    } else {
                        currentSynSet.getSynonym().getLiteral(relationFrom - 1).addRelation(new SemanticRelation(relatedId, relationType, relationTo));
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<SynSet> synSetList(){
        return synSetList.values();
    }

    public Collection<String> literalList(){
        return literalList.keySet();
    }

    public void addSynSet(SynSet synSet){
        synSetList.put(synSet.getId(), synSet);
    }

    public void removeSynSet(SynSet s){
        synSetList.remove(s.getId());
    }

    public SynSet getSynSetWithId(String synSetId){
        if (synSetList.containsKey(synSetId)){
            return synSetList.get(synSetId);
        }
        return null;
    }

    public SynSet getSynSetWithLiteral(String literal, int sense){
        ArrayList<Literal> literals;
        literals = literalList.get(literal);
        if (literals != null){
            for (Literal current : literals){
                if (current.getSense() == sense){
                    return getSynSetWithId(current.getSynSetId());
                }
            }
        }
        return null;
    }

    public int numberOfSynSetsWithLiteral(String literal){
        if (literalList.containsKey(literal)){
            return literalList.get(literal).size();
        } else {
            return 0;
        }
    }

    public ArrayList<SynSet> getSynSetsWithPartOfSpeech(Pos pos){
        ArrayList<SynSet> result = new ArrayList<>();
        for (SynSet synSet : synSetList.values()){
            if (synSet.getPos() != null && synSet.getPos().equals(pos)){
                result.add(synSet);
            }
        }
        return result;
    }

    public ArrayList<Literal> getLiteralsWithName(String literal){
        if (literalList.containsKey(literal)){
            return literalList.get(literal);
        } else {
            return new ArrayList<>();
        }
    }

    private void addSynSetsWithLiteralToList(ArrayList<SynSet> result, String literal, Pos pos){
        SynSet synSet;
        for (Literal current : literalList.get(literal)){
            synSet = getSynSetWithId(current.getSynSetId());
            if (synSet != null && synSet.getPos().equals(pos)){
                result.add(synSet);
            }
        }
    }

    public ArrayList<SynSet> getSynSetsWithLiteral(String literal){
        SynSet synSet;
        ArrayList<SynSet> result = new ArrayList<>();
        if (literalList.containsKey(literal)){
            for (Literal current : literalList.get(literal)){
                synSet = getSynSetWithId(current.getSynSetId());
                if (synSet != null){
                    result.add(synSet);
                }
            }
        }
        return result;
    }

    public ArrayList<String> getLiteralsWithPossibleModifiedLiteral(String literal){
        ArrayList<String> result = new ArrayList<>();
        result.add(literal);
        if (exceptionList.containsKey(literal) && literalList.containsKey(exceptionList.get(literal).getRoot())){
            result.add(exceptionList.get(literal).getRoot());
        }
        if (literal.endsWith("s") && literalList.containsKey(literal.substring(0, literal.length() - 1))){
            result.add(literal.substring(0, literal.length() - 1));
        }
        if (literal.endsWith("es") && literalList.containsKey(literal.substring(0, literal.length() - 2))){
            result.add(literal.substring(0, literal.length() - 2));
        }
        if (literal.endsWith("ed") && literalList.containsKey(literal.substring(0, literal.length() - 2))){
            result.add(literal.substring(0, literal.length() - 2));
        }
        if (literal.endsWith("ed") && literalList.containsKey(literal.substring(0, literal.length() - 2) + literal.charAt(literal.length() - 3))){
            result.add(literal.substring(0, literal.length() - 2) + literal.charAt(literal.length() - 3));
        }
        if (literal.endsWith("ed") && literalList.containsKey(literal.substring(0, literal.length() - 2) + "e")){
            result.add(literal.substring(0, literal.length() - 2) + "e");
        }
        if (literal.endsWith("er") && literalList.containsKey(literal.substring(0, literal.length() - 2))){
            result.add(literal.substring(0, literal.length() - 2));
        }
        if (literal.endsWith("er") && literalList.containsKey(literal.substring(0, literal.length() - 2) + "e")){
            result.add(literal.substring(0, literal.length() - 2) + "e");
        }
        if (literal.endsWith("ing") && literalList.containsKey(literal.substring(0, literal.length() - 3))){
            result.add(literal.substring(0, literal.length() - 3));
        }
        if (literal.endsWith("ing") && literalList.containsKey(literal.substring(0, literal.length() - 3) + literal.charAt(literal.length() - 4))){
            result.add(literal.substring(0, literal.length() - 3) + literal.charAt(literal.length() - 4));
        }
        if (literal.endsWith("ing") && literalList.containsKey(literal.substring(0, literal.length() - 3) + "e")){
            result.add(literal.substring(0, literal.length() - 3) + "e");
        }
        if (literal.endsWith("ies") && literalList.containsKey(literal.substring(0, literal.length() - 3) + "y")){
            result.add(literal.substring(0, literal.length() - 3) + "y");
        }
        if (literal.endsWith("est") && literalList.containsKey(literal.substring(0, literal.length() - 3))){
            result.add(literal.substring(0, literal.length() - 3));
        }
        if (literal.endsWith("est") && literalList.containsKey(literal.substring(0, literal.length() - 3) + "e")){
            result.add(literal.substring(0, literal.length() - 3) + "e");
        }
        return result;
    }

    public ArrayList<SynSet> getSynSetsWithPossiblyModifiedLiteral(String literal, Pos pos){
        ArrayList<SynSet> result = new ArrayList<>();
        ArrayList<String> modifiedLiterals = getLiteralsWithPossibleModifiedLiteral(literal);
        for (String modifiedLiteral : modifiedLiterals){
            if (literalList.containsKey(modifiedLiteral)){
                addSynSetsWithLiteralToList(result, modifiedLiteral, pos);
            }
        }
        return result;
    }

    public void addReverseRelation(SynSet synSet, SemanticRelation semanticRelation){
        SynSet otherSynSet = getSynSetWithId(semanticRelation.getName());
        if (otherSynSet != null && SemanticRelation.reverse(semanticRelation.getRelationType()) != null){
            Relation otherRelation = new SemanticRelation(synSet.getId(), SemanticRelation.reverse(semanticRelation.getRelationType()));
            if (!otherSynSet.containsRelation(otherRelation)){
                otherSynSet.addRelation(otherRelation);
            }
        }
    }

    public void removeReverseRelation(SynSet synSet, SemanticRelation semanticRelation){
        SynSet otherSynSet = getSynSetWithId(semanticRelation.getName());
        if (otherSynSet != null && SemanticRelation.reverse(semanticRelation.getRelationType()) != null){
            Relation otherRelation = new SemanticRelation(synSet.getId(), SemanticRelation.reverse(semanticRelation.getRelationType()));
            if (otherSynSet.containsRelation(otherRelation)){
                otherSynSet.removeRelation(otherRelation);
            }
        }
    }

    public void equalizeSemanticRelations(){
        for (SynSet synSet : synSetList.values()){
            for (int i = 0; i < synSet.relationSize(); i++){
                if (synSet.getRelation(i) instanceof SemanticRelation){
                    SemanticRelation relation = (SemanticRelation) synSet.getRelation(i);
                    addReverseRelation(synSet, relation);
                }
            }
        }
    }

    public ArrayList<Literal> constructLiterals(String word, MorphologicalParse parse, MetamorphicParse metaParse, FsmMorphologicalAnalyzer fsm){
        ArrayList<Literal> result = new ArrayList<>();
        if (parse.size() > 0){
            if (!parse.isPunctuation() && !parse.isCardinal() && !parse.isReal()){
                HashSet<String> possibleWords = fsm.getPossibleWords(parse, metaParse);
                for (String possibleWord : possibleWords){
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

    public ArrayList<SynSet> constructSynSets(String word, MorphologicalParse parse, MetamorphicParse metaParse, FsmMorphologicalAnalyzer fsm){
        ArrayList<SynSet> result = new ArrayList<>();
        if (parse.size() > 0){
            if (parse.isProperNoun()){
                result.add(getSynSetWithLiteral("(özel isim)", 1));
            }
            if (parse.isTime()){
                result.add(getSynSetWithLiteral("(zaman)", 1));
            }
            if (parse.isDate()){
                result.add(getSynSetWithLiteral("(tarih)", 1));
            }
            if (parse.isHashTag()){
                result.add(getSynSetWithLiteral("(hashtag)", 1));
            }
            if (parse.isEmail()){
                result.add(getSynSetWithLiteral("(eposta)", 1));
            }
            if (parse.isCardinal()){
                result.add(getSynSetWithLiteral("(tam sayı)", 1));
            }
            if (parse.isOrdinal()){
                result.add(getSynSetWithLiteral("(sayı sıra sıfatı)", 1));
            }
            if (parse.isPercent()){
                result.add(getSynSetWithLiteral("(yüzde)", 1));
            }
            if (parse.isFraction()){
                result.add(getSynSetWithLiteral("(kesir sayı)", 1));
            }
            if (parse.isRange()){
                result.add(getSynSetWithLiteral("(sayı aralığı)", 1));
            }
            if (parse.isReal()){
                result.add(getSynSetWithLiteral("(reel sayı)", 1));
            }
            if (!parse.isPunctuation() && !parse.isCardinal() && !parse.isReal()){
                HashSet<String> possibleWords = fsm.getPossibleWords(parse, metaParse);
                for (String possibleWord : possibleWords){
                    ArrayList<SynSet> synSets = getSynSetsWithLiteral(possibleWord);
                    if (synSets.size() > 0){
                        for (SynSet synSet : synSets){
                            if (synSet.getPos() != null && (parse.getPos().equals("NOUN") || parse.getPos().equals("ADVERB") || parse.getPos().equals("VERB") || parse.getPos().equals("ADJ") || parse.getPos().equals("CONJ"))){
                                if (synSet.getPos().equals(Pos.NOUN)){
                                    if (parse.getPos().equals("NOUN") || parse.getRootPos().equals("NOUN")){
                                        result.add(synSet);
                                    }
                                } else {
                                    if (synSet.getPos().equals(Pos.ADVERB)){
                                        if (parse.getPos().equals("ADVERB") || parse.getRootPos().equals("ADVERB")){
                                            result.add(synSet);
                                        }
                                    } else {
                                        if (synSet.getPos().equals(Pos.VERB)){
                                            if (parse.getPos().equals("VERB") || parse.getRootPos().equals("VERB")){
                                                result.add(synSet);
                                            }
                                        } else {
                                            if (synSet.getPos().equals(Pos.ADJECTIVE)){
                                                if (parse.getPos().equals("ADJ") || parse.getRootPos().equals("ADJ")){
                                                    result.add(synSet);
                                                }
                                            } else {
                                                if (synSet.getPos().equals(Pos.CONJUNCTION)){
                                                    if (parse.getPos().equals("CONJ") || parse.getRootPos().equals("CONJ")){
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
                if (result.size() == 0){
                    for (String possibleWord : possibleWords){
                        ArrayList<SynSet> synSets = getSynSetsWithLiteral(possibleWord);
                        result.addAll(synSets);
                    }
                }
            } else {
                result.addAll(getSynSetsWithLiteral(word));
            }
        } else {
            result.addAll(getSynSetsWithLiteral(word));
        }
        return result;
    }

    public ArrayList<Literal> constructIdiomLiterals(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, FsmMorphologicalAnalyzer fsm){
        ArrayList<Literal> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        for (String possibleWord1 : possibleWords1){
            for (String possibleWord2 : possibleWords2){
                for (String possibleWord3 : possibleWords3) {
                    result.addAll(getLiteralsWithName(possibleWord1 + " " + possibleWord2 + " " + possibleWord3));
                }
            }
        }
        return result;
    }

    public ArrayList<SynSet> constructIdiomSynSets(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MorphologicalParse morphologicalParse3, MetamorphicParse metaParse1, MetamorphicParse metaParse2, MetamorphicParse metaParse3, FsmMorphologicalAnalyzer fsm){
        ArrayList<SynSet> result = new ArrayList<SynSet>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        HashSet<String> possibleWords3 = fsm.getPossibleWords(morphologicalParse3, metaParse3);
        for (String possibleWord1 : possibleWords1){
            for (String possibleWord2 : possibleWords2){
                for (String possibleWord3 : possibleWords3) {
                    if (numberOfSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3) > 0) {
                        result.addAll(getSynSetsWithLiteral(possibleWord1 + " " + possibleWord2 + " " + possibleWord3));
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<Literal> constructIdiomLiterals(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MetamorphicParse metaParse1, MetamorphicParse metaParse2, FsmMorphologicalAnalyzer fsm){
        ArrayList<Literal> result = new ArrayList<Literal>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        for (String possibleWord1 : possibleWords1){
            for (String possibleWord2 : possibleWords2){
                result.addAll(getLiteralsWithName(possibleWord1 + " " + possibleWord2));
            }
        }
        return result;
    }

    public ArrayList<SynSet> constructIdiomSynSets(MorphologicalParse morphologicalParse1, MorphologicalParse morphologicalParse2, MetamorphicParse metaParse1, MetamorphicParse metaParse2, FsmMorphologicalAnalyzer fsm){
        ArrayList<SynSet> result = new ArrayList<>();
        HashSet<String> possibleWords1 = fsm.getPossibleWords(morphologicalParse1, metaParse1);
        HashSet<String> possibleWords2 = fsm.getPossibleWords(morphologicalParse2, metaParse2);
        for (String possibleWord1 : possibleWords1){
            for (String possibleWord2 : possibleWords2){
                if (numberOfSynSetsWithLiteral(possibleWord1 + " " + possibleWord2) > 0){
                    result.addAll(getSynSetsWithLiteral(possibleWord1 + " " + possibleWord2));
                }
            }
        }
        return result;
    }

    public void sortDefinitions(){
        for (SynSet synSet: synSetList()){
            synSet.sortDefinitions();
        }
    }

    public ArrayList<SynSet> getInterlingual(String synSetId){
        if (interlingualList.containsKey(synSetId)){
            return interlingualList.get(synSetId);
        } else {
            return new ArrayList<>();
        }
    }

    private void multipleInterlingualRelationCheck1(WordNet secondWordNet){
        for (SynSet synSet : synSetList()){
            ArrayList<String> interlingual = synSet.getInterlingual();
            if (interlingual.size() > 1){
                for (String s : interlingual){
                    SynSet second = secondWordNet.getSynSetWithId(s);
                    if (second != null){
                        System.out.println(synSet.getId() + "\t" + synSet.getSynonym() + "\t" + synSet.getDefinition() + "\t" + second.getId() + "\t" + second.getSynonym() + "\t" + second.getDefinition());
                    }
                }
            }
        }
    }

    private void multipleInterlingualRelationCheck2(WordNet secondWordNet){
        for (String s : interlingualList.keySet()){
            if (interlingualList.get(s).size() > 1){
                SynSet second = secondWordNet.getSynSetWithId(s);
                if (second != null){
                    for (SynSet synSet : interlingualList.get(s)){
                        System.out.println(synSet.getId() + "\t" + synSet.getSynonym() + "\t" + synSet.getDefinition() + "\t" + second.getId() + "\t" + second.getSynonym() + "\t" + second.getDefinition());
                    }
                }
            }
        }
    }

    private void sameLiteralSameSenseCheck(){
        for (String name : literalList.keySet()){
            ArrayList<Literal> literals = literalList.get(name);
            for (int i = 0; i < literals.size(); i++){
                for (int j = i + 1; j < literals.size(); j++){
                    if (literals.get(i).getSense() == literals.get(j).getSense() && literals.get(i).getName().equals(literals.get(j).getName())){
                        System.out.println("Literal " + name + " has same senses.");
                    }
                }
            }
        }
    }

    private void sameLiteralSameSynSetCheck(){
        ArrayList<SynSet> synsets = new ArrayList<>();
        for (SynSet synSet : synSetList()){
            boolean found = false;
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                Literal literal1 = synSet.getSynonym().getLiteral(i);
                for (int j = i + 1; j < synSet.getSynonym().literalSize(); j++){
                    Literal literal2 = synSet.getSynonym().getLiteral(j);
                    if (literal1.getName().equals(literal2.getName()) && synSet.getPos() != null){
                        synsets.add(synSet);
                        found = true;
                        break;
                    }
                }
                if (found){
                    break;
                }
            }
        }
        Collections.sort(synsets, new SynSetSizeComparator());
        for (SynSet synSet : synsets){
            System.out.println(synSet.getPos() + "->" + synSet.getDefinition());
        }
    }

    private void noPosCheck(){
        for (SynSet synSet : synSetList()){
            if (synSet.getPos() == null){
                System.out.println(synSet.getId() + "\t" + synSet.getSynonym().getLiteral(0).getName() + "\t" + synSet.getDefinition() + "\t" + "has no part of speech");
            }
        }
    }

    private void noDefinitionCheck(){
        for (SynSet synSet : synSetList()){
            if (synSet.getDefinition() == null){
                System.out.println("SynSet " + synSet.getId() + " has no definition " + synSet.getSynonym());
            }
        }
    }

    private void semanticRelationNoIDCheck(){
        IdMapping mapping = new IdMapping("Data/Wordnet/mapping.txt");
        for (SynSet synSet : synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                Literal literal = synSet.getSynonym().getLiteral(i);
                for (int j = 0; j < literal.relationSize(); j++){
                    Relation relation = literal.getRelation(j);
                    if (getSynSetWithId(relation.getName()) == null){
                        if (mapping.map(relation.getName()) != null){
                            relation.setName(mapping.map(relation.getName()));
                            System.out.println("Modified---->Relation " + relation.getName() + " of Synset " + synSet.getId() + " does not exists");
                        } else {
                            System.out.println("Relation " + relation.getName() + " of Synset " + synSet.getId() + " does not exists " + synSet.getSynonym());
                        }
                    }
                }
            }
            for (int j = 0; j < synSet.relationSize(); j++){
                Relation relation = synSet.getRelation(j);
                if (relation instanceof SemanticRelation && getSynSetWithId(relation.getName()) == null){
                    if (mapping.map(relation.getName()) != null){
                        relation.setName(mapping.map(relation.getName()));
                        System.out.println("Modified---->Relation " + relation.getName() + " of Synset " + synSet.getId() + " does not exists");
                    } else {
                        System.out.println("Relation " + relation.getName() + " of Synset " + synSet.getId() + " does not exists " + synSet.getSynonym());
                    }
                }
            }
        }
    }

    private void sameSemanticRelationCheck(){
        for (SynSet synSet : synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                Literal literal = synSet.getSynonym().getLiteral(i);
                for (int j = 0; j < literal.relationSize(); j++){
                    Relation relation = literal.getRelation(j);
                    Relation same = null;
                    for (int k = j + 1; k < literal.relationSize(); k++){
                        if (relation.getName().equalsIgnoreCase(literal.getRelation(k).getName())){
                            System.out.println(relation.getName() + "--" + literal.getRelation(k).getName() + " are same relation for synset " + synSet.getId());
                            same = literal.getRelation(k);
                        }
                    }
                    if (same != null){
                        literal.removeRelation(same);
                    }
                }
            }
            for (int j = 0; j < synSet.relationSize(); j++){
                Relation relation = synSet.getRelation(j);
                Relation same = null;
                for (int k = j + 1; k < synSet.relationSize(); k++){
                    if (relation.getName().equalsIgnoreCase(synSet.getRelation(k).getName())){
                        System.out.println(relation.getName() + "--" + synSet.getRelation(k).getName() + " are same relation for synset " + synSet.getId());
                        same = synSet.getRelation(k);
                    }
                }
                if (same != null){
                    synSet.removeRelation(same);
                }
            }
        }
    }

    public void check(WordNet secondWordNet){
        //multipleInterlingualRelationCheck1(secondWordNet);
        sameLiteralSameSynSetCheck();
        sameLiteralSameSenseCheck();
        noPosCheck();
        semanticRelationNoIDCheck();
        sameSemanticRelationCheck();
        noDefinitionCheck();
        //multipleInterlingualRelationCheck2(secondWordNet);
    }

    public void saveAsXml(String fileName){
        BufferedWriter outfile;
        try{
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            outfile = new BufferedWriter(writer);
            outfile.write("<SYNSETS>\n");
            for (SynSet synSet : synSetList.values()){
                synSet.saveAsXml(outfile);
            }
            outfile.write("</SYNSETS>\n");
            outfile.close();
        }
        catch (IOException ioException){
            System.out.println("Output file can not be opened");
        }
    }

    public void saveAsLmf(String fileName){
        BufferedWriter outfile;
        String wordIdString = null;
        String senseId;
        try{
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            outfile = new BufferedWriter(writer);
            outfile.write("<LexicalResource>\n");
            outfile.write("<Lexicon languageCoding=\"UTF-8\" label=\"Kenet\" language=\"tr\" version=\"10\">\n");
            int wordId = 0;
            for (String literal : literalList.keySet()){
                ArrayList<Literal> literals = literalList.get(literal);
                ArrayList<SynSet> synSets = new ArrayList<>();
                for (int i = 0; i < literals.size(); i++) {
                    if (getSynSetWithId(literals.get(i).synSetId).getPos() == null) {
                        continue;
                    }
                    synSets.add(getSynSetWithId(literals.get(i).synSetId));
                }
                Collections.sort(synSets, (o1, o2) -> o1.getPos().toString().compareTo(o2.getPos().toString()));
                SynSet previous = null;
                for (SynSet current : synSets){
                    if (previous == null || !current.getPos().equals(previous.getPos())){
                        wordIdString = "w" + wordId;
                        wordId++;
                        if (previous != null){
                            outfile.write("\t</LexicalEntry>\n");
                        }
                        outfile.write("\t<LexicalEntry id=\"" + wordIdString + "\">\n");
                        switch (current.getPos()){
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
                                if (literal.equals("\"")){
                                    outfile.write("\t\t<Lemma writtenForm=\"&quot;\" partOfSpeech=\"x\"/>\n");
                                } else {
                                    if (literal.equals("&")){
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
            for (SynSet synSet : synSetList.values()){
                synSet.saveAsLmf(outfile);
            }
            outfile.write("</Lexicon>\n");
            outfile.write("<SenseAxes>\n");
            int senseAxisId = 0;
            for (String englishId : interlingualList.keySet()){
                ArrayList<SynSet> synSets = interlingualList.get(englishId);
                for (SynSet turkishSynSet : synSets){
                    outfile.write("\t<SenseAxis id=\"sa_TUR10-" + senseAxisId + "\" relType=\"eq_synonym\">\n");
                    outfile.write("\t\t<Target ID=\"" + turkishSynSet.getId() + "\"/>\n");
                    outfile.write("\t\t<Target ID=\"" + englishId + "\"/>\n");
                    outfile.write("\t</SenseAxis>\n");
                    senseAxisId++;
                }
            }
            outfile.write("</SenseAxes>\n");
            outfile.write("</LexicalResource>\n");
            outfile.close();
        }
        catch (IOException ioException){
            System.out.println("Output file can not be opened");
        }
    }

    public int size(){
        return synSetList.size();
    }

    /*
     * Helper functions: These methods conduct common operations between similarity metrics.
     */
    public int findPathLength(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        // To find the path length, iterate over one path and check if the other path contains this node. Iterating over the longer path
        // is computationally more efficient.
        ArrayList<String> listToIterate;
        ArrayList<String> listToSearch;
        if (pathToRootOfSynSet1.size() > pathToRootOfSynSet2.size()) {
            listToIterate = pathToRootOfSynSet1;
            listToSearch = pathToRootOfSynSet2;
        } else {
            listToIterate = pathToRootOfSynSet2;
            listToSearch = pathToRootOfSynSet1;
        }
        // There might not be a path between nodes, due to missing nodes. Keep track of that as well. Break when the LCS if found.
        int i = 0, foundIndex = 0;
        boolean found = false;
        for (; i < listToIterate.size() && !found; i++) {
            foundIndex = listToSearch.indexOf(listToIterate.get(i));
            if (foundIndex != -1) {
                found = true;
            }
        }
        // Index of two lists - 1 is equal to path length. If there is not path, return -1
        if (found) {
            return i + foundIndex - 1;
        }
        return -1;
    }

    // Following two methods are wrapper of findLCS. They return the depth and ID of LCS separately
    public int findLCSdepth(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        Object[] temp = (findLCS(pathToRootOfSynSet1, pathToRootOfSynSet2));
        if (temp != null) {
            return (int) temp[1];
        }
        return -1;
    }

    public String findLCSid(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        Object[] temp = (findLCS(pathToRootOfSynSet1, pathToRootOfSynSet2));
        if (temp != null) {
            return (String) temp[0];
        }
        return null;
    }

    // This method returns depth and ID of the LCS. Though it is very similar to findPathLength, it uses HashSet, since
    // index is not necessary and search in the HashSet is cheaper.
    private Object[] findLCS(ArrayList<String> pathToRootOfSynSet1, ArrayList<String> pathToRootOfSynSet2) {
        ArrayList<String> listToIterate;
        HashSet<String> listToSearch;
        if (pathToRootOfSynSet1.size() > pathToRootOfSynSet2.size()) {
            listToIterate = pathToRootOfSynSet1;
            listToSearch = new HashSet<>(pathToRootOfSynSet2);
        } else {
            listToIterate = pathToRootOfSynSet2;
            listToSearch = new HashSet<>(pathToRootOfSynSet1);
        }
        int i = 0;
        boolean found = false;
        String LCSid = "";
        for (; i < listToIterate.size() && !found; i++) {
            LCSid = listToIterate.get(i);
            if (listToSearch.contains(LCSid)) {
                found = true;
            }
        }
        if (found) {
            return new Object[] { LCSid, listToIterate.size() - i + 1};
        }
        return null;
    }

    public ArrayList<String> findPathToRoot(SynSet synset) {
        ArrayList<String> pathToRoot = new ArrayList<>();
        while (synset != null) {
            pathToRoot.add(synset.getId());
            synset = percolateUp(synset);
        }
        return pathToRoot;
    }

    // This function finds the parent of a node. It does not move until the root, instead it goes one level up.
    public SynSet percolateUp(SynSet root) {
        for (int i = 0; i < root.relationSize(); i++) {
            Relation r = root.getRelation(i);
            if (r instanceof SemanticRelation) {
                if (((SemanticRelation) r).getRelationType().equals(SemanticRelationType.HYPERNYM)) {
                    root = getSynSetWithId(r.getName());
                    // return even if one hypernym is found.
                    return root;
                }
            }
        }
        return null;
    }

}
