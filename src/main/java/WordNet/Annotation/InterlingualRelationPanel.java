package WordNet.Annotation;

import WordNet.Literal;
import WordNet.WordNet;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class InterlingualRelationPanel extends WordNetEditorPanel {

    private final ArrayList<Literal> literals1;
    private final ArrayList<Literal> literals2;
    private final WordNet wordNet1;
    private final WordNet wordNet2;

    /**
     * Reads the parallel literal and synset list from the interlingual file and displays the first interlingual set
     * candidate. An interlingual file consists of two literals separated via {@code ->} and literals may also have
     * their  synset id's between parenthesis after them. Each line is read, and for each line the literals are stored
     * in literals1 and literals2 for two related languages respectively. An interlingual matching set consists
     * of two literals from two different wordnets, possibly having the same meaning.
     * An example file is like this:
     * <blockquote><pre>
     * su(TUR10-1000000){@code ->}water(ENG31-1234503)
     * kırmızı(TUR10-1100000){@code ->}red(ENG31-3412400)
     * </pre></blockquote>
     * @param wordNet1 First wordnet for which literal matching will be done.
     * @param wordNet2 Second wordnet for which literal matching will be done.
     * @param fileName Name of the file to be read.
     */
    public InterlingualRelationPanel(WordNet wordNet1, WordNet wordNet2, String fileName){
        super(fileName);
        literals1 = new ArrayList<>();
        literals2 = new ArrayList<>();
        this.wordNet1 = wordNet1;
        this.wordNet2 = wordNet2;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName)), StandardCharsets.UTF_8));
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
        } catch (IOException ignored) {
        }
    }

    /**
     * Displays one interlingual relation candidate. An interlingual relation candidate consists of two literals from
     * two wordnets, possibly having the same meaning. For both literals, a synset JTree will be constructed and added
     * to the panel. The synset tree displays the synset and all of its literals.
     */
    private void displayRelationCandidate(){
        removeAll();
        setLayout(new GridLayout(1, 2));
        add(createSynSetTree(literals1.get(itemIndex), wordNet1));
        add(createSynSetTree(literals2.get(itemIndex), wordNet2));
        validate();
    }

    /**
     * Saves the interlingual relations info to the interlingual file. An interlingual file consists of two literals
     * separated via {@code ->} and literals may also have their synset id's between parenthesis after them.
     * An example file is like this:
     * <blockquote><pre>
     * su(TUR10-1000000){@code ->}water(ENG31-1234503)
     * kırmızı(TUR10-1100000){@code ->}red(ENG31-3412400)
     * </pre></blockquote>
     */
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
        } catch (FileNotFoundException | UnsupportedEncodingException ignored) {
        }
    }

    /**
     * Displays next interlingual candidate.
     * @return A string showing the index of the candidate displayed.
     */
    public String nextRelationCandidate(){
        if (itemIndex < literals1.size() - 1){
            itemIndex++;
            displayRelationCandidate();
        }
        return itemIndex + 1 + "/" + literals1.size();
    }

    /**
     * Displays previous interlingual candidate.
     * @return A string showing the index of the candidate displayed.
     */
    public String previousRelationCandidate(){
        if (itemIndex > 0){
            itemIndex--;
            displayRelationCandidate();
        }
        return itemIndex + 1 + "/" + literals1.size();
    }

    /**
     * Displays random interlingual candidate.
     * @return A string showing the index of the candidate displayed.
     */
    public String randomRelationCandidate(){
        itemIndex = random.nextInt(literals1.size());
        displayRelationCandidate();
        return itemIndex + 1 + "/" + literals1.size();
    }

}
