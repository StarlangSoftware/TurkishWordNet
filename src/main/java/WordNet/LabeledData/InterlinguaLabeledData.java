package WordNet.LabeledData;

import DataStructure.CounterHashMap;
import DataStructure.Graph.Graph;
import DataStructure.Graph.Node;
import WordNet.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InterlinguaLabeledData extends LabeledData{
    private WordNet leftWordNet, rightWordNet;

    public InterlinguaLabeledData(WordNet leftWordNet, WordNet rightWordNet){
        this.leftWordNet = leftWordNet;
        this.rightWordNet = rightWordNet;
    }

    public HashMap<SynSet, ArrayList<SynSet>> extractSynSetMap() {
        HashMap<SynSet, ArrayList<SynSet>> synSetMap = new HashMap<>();
        ArrayList<SynSet> mappedSynSets;
        IdMapping idMapping = new IdMapping();
        for (Map.Entry<WordPair, CounterHashMap<WordPair>> entry : data.entrySet()) {
            CounterHashMap<WordPair> labeledRow = entry.getValue();
            for (WordPair label : labeledRow.keySet()) {
                if (labeledRow.count(label) > 1 && label.leftWord != null && label.rightWord != null) {
                    SynSet left = leftWordNet.getSynSetWithId(label.leftWord);
                    SynSet right = rightWordNet.getSynSetWithId(label.rightWord);
                    if (right == null && idMapping.map(label.rightWord) != null) {
                        right = rightWordNet.getSynSetWithId(idMapping.map(label.rightWord));
                    }
                    if (left != null && right != null && left.getPos() == right.getPos()) {
                        if (synSetMap.containsKey(left)) {
                            if (!synSetMap.get(left).contains(right)) {
                                mappedSynSets = synSetMap.get(left);
                                mappedSynSets.add(right);
                            }
                        } else {
                            mappedSynSets = new ArrayList<>();
                            mappedSynSets.add(right);
                            synSetMap.put(left, mappedSynSets);
                        }
                    }
                }
            }
        }
        return synSetMap;
    }

    public Graph<SynSet> createGraph(SemanticRelationType semanticRelationType){
        Graph<SynSet> synSetGraph;
        HashMap<SynSet, ArrayList<SynSet>> synSetMap = extractSynSetMap();
        synSetGraph = new Graph<SynSet>(true);
        for (SynSet synSet : synSetMap.keySet()){
            ArrayList<SynSet> fromList = synSetMap.get(synSet);
            for (int i = 0; i < synSet.relationSize(); i++){
                if (synSet.getRelation(i) instanceof SemanticRelation){
                    SemanticRelation relation = (SemanticRelation) synSet.getRelation(i);
                    if (relation.getRelationType().equals(semanticRelationType)){
                        SynSet to = leftWordNet.getSynSetWithId(relation.getName());
                        if (to != null && synSetMap.containsKey(to)){
                            ArrayList<SynSet> toList = synSetMap.get(to);
                            for (SynSet fromSynSet : fromList){
                                Node<SynSet> fromNode = new Node<SynSet>(fromSynSet);
                                for (SynSet toSynSet : toList){
                                    if (!fromSynSet.equals(toSynSet)){
                                        Node<SynSet> toNode = new Node<SynSet>(toSynSet);
                                        if (!synSetGraph.containsEdge(fromNode, toNode) && !synSetGraph.containsEdge(toNode, fromNode)){
                                            switch (semanticRelationType){
                                                case INSTANCE_HYPONYM:
                                                case HYPONYM:
                                                case PART_MERONYM:
                                                case SUBSTANCE_MERONYM:
                                                case MEMBER_MERONYM:
                                                    synSetGraph.addEdge(fromNode, toNode, 1);
                                                    break;
                                                case HYPERNYM:
                                                case INSTANCE_HYPERNYM:
                                                case PART_HOLONYM:
                                                case SUBSTANCE_HOLONYM:
                                                case MEMBER_HOLONYM:
                                                    synSetGraph.addEdge(toNode, fromNode, 1);
                                                    break;
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
        return synSetGraph;
    }


}
