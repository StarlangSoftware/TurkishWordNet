package WordNet;

import DataStructure.CounterHashMap;
import Dictionary.*;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

import java.io.*;
import java.util.*;

public class TestWordNet {

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

    public static void main(String[] args){
        WordNet turkish = new WordNet();
        //turkish.saveAsXml("deneme.xml");
        //turkish.check(null);
        //turkish.saveAsLmf("turkish.lmf");
    }
}
