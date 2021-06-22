package WordNet.Annotation;

import WordNet.Literal;
import WordNet.WordNet;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class InterlingualRelationPanel extends WordNetEditorPanel {

    private ArrayList<Literal> literals1;
    private ArrayList<Literal> literals2;
    private WordNet wordNet1;
    private WordNet wordNet2;

    public InterlingualRelationPanel(WordNet wordNet1, WordNet wordNet2, String fileName){
        super(fileName);
        literals1 = new ArrayList<>();
        literals2 = new ArrayList<>();
        this.wordNet1 = wordNet1;
        this.wordNet2 = wordNet2;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
            String line = br.readLine();
            while (line != null){
                Literal literal1, literal2;
                String[] words = line.split("->");
                if (words[0].contains("(")){
                    literal1 = new Literal(words[0].split("[()]")[0], -1, words[0].split("[()]")[1]);
                } else {
                    literal1 = new Literal(words[0], -1, "");
                }
                if (words[1].contains("(")){
                    literal2 = new Literal(words[1].split("[()]")[0], -1, words[1].split("[()]")[1]);
                } else {
                    literal2 = new Literal(words[1], -1, "");
                }
                line = br.readLine();
                literals1.add(literal1);
                literals2.add(literal2);
            }
            br.close();
            displayRelationCandidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayRelationCandidate(){
        removeAll();
        setLayout(new GridLayout(1, 2));
        add(createSynSetTree(literals1.get(itemIndex), wordNet1));
        add(createSynSetTree(literals2.get(itemIndex), wordNet2));
        validate();
    }

    public void save(){
        PrintWriter writer;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
            for (int i = 0; i < literals1.size(); i++){
                if (literals1.get(i).getSynSetId().isEmpty()){
                    writer.print(literals1.get(i).getName());
                } else {
                    writer.print(literals1.get(i).getName() + "(" + literals1.get(i).getSynSetId() + ")");
                }
                if (literals2.get(i).getSynSetId().isEmpty()){
                    writer.print("->" + literals2.get(i).getName());
                } else {
                    writer.print("->" + literals2.get(i).getName() + "(" + literals2.get(i).getSynSetId() + ")");
                }
                writer.println();
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String nextRelationCandidate(){
        if (itemIndex < literals1.size() - 1){
            itemIndex++;
            displayRelationCandidate();
        }
        return itemIndex + 1 + "/" + literals1.size();
    }

    public String previousRelationCandidate(){
        if (itemIndex > 0){
            itemIndex--;
            displayRelationCandidate();
        }
        return itemIndex + 1 + "/" + literals1.size();
    }

    public String randomRelationCandidate(){
        itemIndex = random.nextInt(literals1.size());
        displayRelationCandidate();
        return itemIndex + 1 + "/" + literals1.size();
    }

}
