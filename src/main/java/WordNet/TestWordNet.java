package WordNet;

import Dictionary.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

import java.io.*;
import java.util.*;

public class TestWordNet {

    private static void allVerbs(){
        WordNet turkish = new WordNet();
        ArrayList<SynSet> synSets = turkish.getSynSetsWithPartOfSpeech(Pos.VERB);
        for (SynSet synSet : synSets){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                System.out.println(synSet.getSynonym().getLiteral(i).getName());
            }
        }
    }

    public static void mergeWordNets(){
        try {
            PrintWriter pw = new PrintWriter(new File("deneme.xml"), "utf-8");
            Scanner s = new Scanner(new File("turkish_wordnet.xml"));
            Scanner s1 = new Scanner(new File("turkish_wordnet1.xml"));
            Scanner s2 = new Scanner(new File("turkish_wordnet2.xml"));
            while (s.hasNext()){
                String line = s.nextLine();
                String line1 = s1.nextLine();
                String line2 = s2.nextLine();
                if (!line1.equalsIgnoreCase(line2)){
                    if (!line1.equalsIgnoreCase(line)){
                        pw.println(line1);
                    } else {
                        pw.println(line2);
                    }
                } else {
                    pw.println(line1);
                }
            }
            pw.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void balkanetVsWordnet(){
        WordNet turkish = new WordNet();
        WordNet balkanet = new WordNet("Data/Wordnet/balkanet.xml", new Locale("tr"));
        for (SynSet synSet : balkanet.synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                if (turkish.getSynSetsWithLiteral(synSet.getSynonym().getLiteral(i).getName()).size() == 0){
                    System.out.println(synSet.getDefinition() + "->" + synSet.getSynonym().getLiteral(i).getName());
                }
            }
        }
    }

    public static void addSynSets(){
        Literal newLiteral;
        int lastId = 124230;
        WordNet turkish = new WordNet();
        try {
            Scanner input = new Scanner(new File("data.txt"));
            while (input.hasNext()){
                String line = input.nextLine();
                String[] items = line.split("\\t");
                if (turkish.getLiteralsWithName(items[0]).size() == 0){
                    newLiteral = new Literal(items[0], 1, "");
                } else {
                    int maxIndex = 0;
                    for (Literal literal : turkish.getLiteralsWithName(items[0])){
                        if (literal.getSense() > maxIndex){
                            maxIndex = literal.getSense();
                        }
                    }
                    newLiteral = new Literal(items[0], maxIndex + 1, "");
                }
                lastId++;
                SynSet newSynSet = new SynSet("TUR10-" + lastId + "0");
                newLiteral.setSynSetId(newSynSet.getId());
                newSynSet.getSynonym().addLiteral(newLiteral);
                newSynSet.setDefinition(items[1]);
                turkish.addSynSet(newSynSet);
                turkish.addLiteralToLiteralList(newLiteral);
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        turkish.saveAsXml("deneme.xml");
    }

    public static void wordNotExists(){
        WordNet turkish = new WordNet();
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        for (String literal : turkish.literalList()){
            if (literal.endsWith("mek") || literal.endsWith("mak")){
                if (!literal.contains(" ") && turkish.getSynSetsWithLiteral(literal).size() > 1 && fsm.getDictionary().getWord(literal.substring(0, literal.length() - 3)) == null){
                    System.out.println(literal.substring(0, literal.length() - 3).toLowerCase(new Locale("tr")) + " CL_FIIL");
                }
            } else {
                if (!literal.contains(" ") && turkish.getSynSetsWithLiteral(literal).size() > 1 && fsm.getDictionary().getWord(literal) == null){
                    boolean isAdjective = false, isNoun = false, isAdverb = false;
                    for (SynSet synSet : turkish.getSynSetsWithLiteral(literal)){
                        if (synSet.getPos().equals(Pos.NOUN)){
                            isNoun = true;
                        }
                        if (synSet.getPos().equals(Pos.ADJECTIVE)){
                            isAdjective = true;
                        }
                        if (synSet.getPos().equals(Pos.ADVERB)){
                            isAdverb = true;
                        }
                    }
                    if (isAdjective){
                        if (isNoun){
                            System.out.println(literal.toLowerCase(new Locale("tr")) + " IS_ADJ CL_ISIM");
                        } else {
                            if (isAdverb){
                                System.out.println(literal.toLowerCase(new Locale("tr")) + " IS_ADJ IS_ADVERB");
                            } else {
                                System.out.println(literal.toLowerCase(new Locale("tr")) + " IS_ADJ");
                            }
                        }
                    } else {
                        if (isNoun){
                            if (literal.endsWith("lık") || (literal.endsWith("lik"))){
                                System.out.println(literal.toLowerCase(new Locale("tr")) + " CL_ISIM IS_SD");
                            } else {
                                System.out.println(literal.toLowerCase(new Locale("tr")) + " CL_ISIM");
                            }
                        } else {
                            if (isAdverb){
                                System.out.println(literal.toLowerCase(new Locale("tr")) + " IS_ADVERB");
                            }
                        }
                    }
                }
            }
        }
    }

    public static void splitCandidates(){
        SynSet candidate;
        WordNet turkish = new WordNet();
        WordNet turkish00 = new WordNet("Data/Wordnet/turkish_wordnet_version_00.xml", new Locale("tr"));
        WordNet turkish01 = new WordNet("Data/Wordnet/turkish_wordnet_version_01.xml", new Locale("tr"));
        try {
            Scanner s = new Scanner(new File("split.txt"));
            while (s.hasNext()){
                String id = s.nextLine();
                SynSet synSet = turkish.getSynSetWithId(id);
                for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                    Literal literal = synSet.getSynonym().getLiteral(i);
                    candidate = turkish00.getSynSetWithLiteral(literal.getName(), literal.getSense());
                    if (candidate == null){
                        candidate = turkish01.getSynSetWithLiteral(literal.getName(), literal.getSense());
                    }
                    if (candidate == null){
                        System.out.println(id + "\t" + literal.getName() + "\t" + literal.getSense() + "\t" + "TUR10-0000000" + "\t" + synSet.getLongDefinition());
                    } else {
                        System.out.println(id + "\t" + literal.getName() + "\t" + literal.getSense() + "\t" + candidate.getId() + "\t" + candidate.getDefinition());
                    }
                }
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void test(){
    }

    public static void derivationRelated(){
        WordNet turkish = new WordNet();
        for (SynSet synSet : turkish.synSetList()){
            for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                Literal literal = synSet.getSynonym().getLiteral(i);
                for (int j = 0; j < literal.relationSize(); j++){
                    Relation relation = literal.getRelation(j);
                    if (relation instanceof SemanticRelation){
                        SemanticRelation semanticRelation = (SemanticRelation) relation;
                        if (semanticRelation.getRelationType().equals(SemanticRelationType.DERIVATION_RELATED) && !semanticRelation.getName().startsWith("TUR10")){
                            ArrayList<SynSet> synSets = turkish.getSynSetsWithLiteral(semanticRelation.getName());
                            if (synSets.size() > 0){
                                String s = synSet.getId() + "\t" + literal.getName() + "\t" + synSet.getDefinition() + "\t" + semanticRelation.getName();
                                for (SynSet synSet1 : synSets){
                                    s = s + "\t" + synSet1.getId() + "\t" + synSet1.getDefinition();
                                }
                                System.out.println(s);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void candidateRelation(SemanticRelationType semanticRelationType) throws FileNotFoundException {
        WordNet turkish = new WordNet();
        WordNet english = new WordNet("Data/Wordnet/english_wordnet_version_31.xml");
        PrintWriter printWriter = new PrintWriter(new File(semanticRelationType + ".txt"));
        for (SynSet synSet : english.synSetList()){
            ArrayList<SynSet> list1 = turkish.getInterlingual(synSet.getId());
            if (list1.size() > 0){
                for (int i = 0; i < synSet.relationSize(); i++){
                    Relation relation = synSet.getRelation(i);
                    if (relation instanceof SemanticRelation){
                        SemanticRelation semanticRelation = (SemanticRelation) relation;
                        if (semanticRelation.getRelationType().equals(semanticRelationType)){
                            SynSet relatedSynSet = english.getSynSetWithId(semanticRelation.getName());
                            if (relatedSynSet != null){
                                ArrayList<SynSet> list2 = turkish.getInterlingual(relatedSynSet.getId());
                                if (list2.size() > 0){
                                    for (SynSet synSet1 : list1){
                                        for (SynSet synSet2 : list2){
                                            printWriter.println(synSet1.getId() + "\t" + synSet1.getSynonym() + "\t" + synSet1.getDefinition() + "\t" + synSet2.getId() + "\t" + synSet2.getSynonym() + "\t" + synSet2.getDefinition() + "\t" + synSet.getId() + "\t" + synSet.getSynonym() + "\t" + relatedSynSet.getId() + "\t" + relatedSynSet.getSynonym());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < synSet.getSynonym().literalSize(); j++){
                    Literal literal = synSet.getSynonym().getLiteral(j);
                    for (int i = 0; i < literal.relationSize(); i++){
                        Relation relation = literal.getRelation(i);
                        if (relation instanceof SemanticRelation){
                            SemanticRelation semanticRelation = (SemanticRelation) relation;
                            if (semanticRelation.getRelationType().equals(semanticRelationType)){
                                SynSet relatedSynSet = english.getSynSetWithId(semanticRelation.getName());
                                if (relatedSynSet != null){
                                    ArrayList<SynSet> list2 = turkish.getInterlingual(relatedSynSet.getId());
                                    if (list2.size() > 0){
                                        for (SynSet synSet1 : list1){
                                            for (SynSet synSet2 : list2){
                                                printWriter.println(synSet1.getId() + "\t" + synSet1.getSynonym() + "\t" + synSet1.getDefinition() + "\t" + synSet2.getId() + "\t" + synSet2.getSynonym() + "\t" + synSet2.getDefinition() + "\t" + synSet.getId() + "\t" + synSet.getSynonym() + "\t" + relatedSynSet.getId() + "\t" + relatedSynSet.getSynonym());
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
        printWriter.close();
    }

    public static void englishInstanceHypernyms(){
        WordNet english = new WordNet("Data/Wordnet/english_wordnet_version_31.xml");
        for (SynSet synSet : english.synSetList()){
            for (int i = 0; i < synSet.relationSize(); i++){
                Relation relation = synSet.getRelation(i);
                if (relation instanceof SemanticRelation){
                    SemanticRelation semanticRelation = (SemanticRelation) relation;
                    if (semanticRelation.getRelationType().equals(SemanticRelationType.INSTANCE_HYPERNYM)){
                        SynSet relatedSynSet = english.getSynSetWithId(semanticRelation.getName());
                        System.out.println(synSet.getId() + "\t" + synSet.representative() + "\t" + relatedSynSet.representative());
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        wordNotExists();
    }
}
