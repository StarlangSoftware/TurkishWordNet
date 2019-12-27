package WordNet;

import Dictionary.Pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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

    public static void transferHierarchy(WordNet source, WordNet destination){
        SynSet[] synSets = new SynSet[destination.size()];
        synSets = destination.synSetList().toArray(synSets);
        for (SynSet synSet1 : synSets){
            SynSet synSet2 = source.getSynSetWithId(synSet1.getId());
            if (synSet2 != null){
                ArrayList<String> parentList1 = destination.findPathToRoot(synSet1);
                ArrayList<String> parentList2 = source.findPathToRoot(synSet2);
                if (parentList1.size() < parentList2.size() && parentList1.size() == 1 && parentList2.get(parentList2.size() - 1).equals("TUR10-0814560")){
                    boolean isPrefix = true;
                    for (int i = 0; i < parentList1.size(); i++){
                        if (!parentList1.get(i).equals(parentList2.get(i))){
                            isPrefix = false;
                            break;
                        }
                    }
                    if (isPrefix){
                        SynSet childSynSet = destination.getSynSetWithId(parentList1.get(parentList1.size() - 1));
                        for (int i = parentList1.size(); i < parentList2.size(); i++){
                            SynSet synSet3 = source.getSynSetWithId(parentList2.get(i));
                            SynSet parentSynSet = destination.getSynSetWithId(parentList2.get(i));
                            if (parentSynSet == null){
                                parentSynSet = new SynSet(synSet3.getId());
                                parentSynSet.setPos(Pos.NOUN);
                                if (synSet3.getLongDefinition() != null){
                                    parentSynSet.setDefinition(synSet3.getLongDefinition());
                                }
                                parentSynSet.addLiteral(new Literal(synSet3.getSynonym().getLiteral(0).getName(), synSet3.getSynonym().getLiteral(0).getSense(), synSet3.getId()));
                                destination.addSynSet(parentSynSet);
                            }
                            if (!childSynSet.containsRelation(new SemanticRelation(parentSynSet.getId(), SemanticRelationType.HYPERNYM))) {
                                childSynSet.addRelation(new SemanticRelation(parentSynSet.getId(), SemanticRelationType.HYPERNYM));
                            }
                            if (!parentSynSet.containsRelation(new SemanticRelation(childSynSet.getId(), SemanticRelationType.HYPONYM))){
                                parentSynSet.addRelation(new SemanticRelation(childSynSet.getId(), SemanticRelationType.HYPONYM));
                            }
                            System.out.println(childSynSet.getId() + " (" + childSynSet.getSynonym().getLiteral(0).getName() + ")->" + parentSynSet.getId() + " (" + parentSynSet.getSynonym().getLiteral(0).getName() + ")");
                            childSynSet = parentSynSet;
                        }
                        System.out.println("---------------------------");
                    }
                }
            }
        }

    }

    public static void main(String[] args){
        WordNet turkish = new WordNet();
        turkish.saveAsXml("deneme.xml");
        //transferHierarchy(turkish, domain);
        //domain.check();
        //domain.saveAsXml("deneme.xml");
        //turkish.check(null);
        //turkish.saveAsLmf("turkish.lmf");
    }
}
