package WordNet.Annotation;

import WordNet.Literal;
import WordNet.SynSet;
import WordNet.WordNet;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class LiteralMatcherPanel extends WordNetEditorPanel {

    private ArrayList<SynSet> synSets;
    private WordNet wordNet;

    public LiteralMatcherPanel(WordNet wordNet, String fileName){
        super(fileName);
        synSets = new ArrayList<>();
        this.wordNet = wordNet;
        this.fileName = fileName;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
            String line = br.readLine();
            int i = 0;
            while (line != null){
                String[] candidates = line.split("->");
                SynSet synSet = new SynSet("" + i);
                for (String candidate: candidates){
                    if (candidate.contains("(")){
                        String[] items = candidate.split("[()]");
                        synSet.addLiteral(new Literal(items[0], -1, items[1]));
                    } else {
                        synSet.addLiteral(new Literal(candidate, -1, ""));
                    }
                }
                line = br.readLine();
                i++;
                synSets.add(synSet);
            }
            br.close();
            displaySynSetCandidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displaySynSetCandidate(){
        removeAll();
        SynSet synSet = synSets.get(itemIndex);
        setLayout(new GridLayout(1, synSet.getSynonym().literalSize()));
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            add(createSynSetTree(synSet.getSynonym().getLiteral(i), wordNet));
        }
        validate();
    }

    public void save(){
        PrintWriter writer;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
            for (SynSet synSet:synSets){
                if (synSet.getSynonym().getLiteral(0).getSynSetId().isEmpty()){
                    writer.print(synSet.representative());
                } else {
                    writer.print(synSet.representative() + "(" + synSet.getSynonym().getLiteral(0).getSynSetId() + ")");
                }
                for (int i = 1; i < synSet.getSynonym().literalSize(); i++){
                    if (synSet.getSynonym().getLiteral(i).getSynSetId().isEmpty()){
                        writer.print("->" + synSet.getSynonym().getLiteral(i).getName());
                    } else {
                        writer.print("->" + synSet.getSynonym().getLiteral(i).getName() + "(" + synSet.getSynonym().getLiteral(i).getSynSetId() + ")");
                    }
                }
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String nextSynSetCandidate(){
        if (itemIndex < synSets.size() - 1){
            itemIndex++;
            displaySynSetCandidate();
        }
        return itemIndex + 1 + "/" + synSets.size();
    }

    public String previousSynSetCandidate(){
        if (itemIndex > 0){
            itemIndex--;
            displaySynSetCandidate();
        }
        return itemIndex + 1 + "/" + synSets.size();
    }

    public String randomSynSetCandidate(){
        itemIndex = random.nextInt(synSets.size());
        displaySynSetCandidate();
        return itemIndex + 1 + "/" + synSets.size();
    }

}
