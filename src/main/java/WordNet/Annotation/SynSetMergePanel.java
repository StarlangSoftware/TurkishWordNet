package WordNet.Annotation;

import WordNet.Literal;
import WordNet.SynSet;
import WordNet.WordNet;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SynSetMergePanel extends WordNetEditorPanel {

    private final ArrayList<SynSet> synSets;
    private final WordNet wordNet;

    /**
     * Reads the literal list from the literal file and displays the first literal set candidate. A literal file
     * consists of literals separated via {@code ->} and some literals may also have their synset id's between parenthesis
     * after them. Each line is read, and for each line a new synset is constructed. A literal matching synset consists
     * of multiple literals, possibly all of which have the same meaning.
     * An example file is like this:
     * <blockquote><pre>
     * ab{@code ->}su(TUR10-1000000){@code ->}âb
     * kırmızı(TUR10-1100000){@code ->}al
     * </pre></blockquote>
     * @param wordNet Wordnet for which literal matching will be done.
     * @param fileName Name of the file to be read.
     */
    public SynSetMergePanel(WordNet wordNet, String fileName){
        super(fileName);
        synSets = new ArrayList<>();
        this.wordNet = wordNet;
        this.fileName = fileName;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName)), StandardCharsets.UTF_8));
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
        } catch (IOException ignored) {
        }
    }

    /**
     * Displays one single merge candidate. A synset merge candidate consists of multiple literals, possibly
     * all of which have the same meaning. For every literal, a synset JTree will be constructed and added to the panel.
     * The synset tree  displays the synset and all of its literals.
     */
    private void displaySynSetCandidate(){
        removeAll();
        SynSet synSet = synSets.get(itemIndex);
        setLayout(new GridLayout(1, synSet.getSynonym().literalSize()));
        for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
            add(createSynSetTree(synSet.getSynonym().getLiteral(i), wordNet));
        }
        validate();
    }

    /**
     * Saves the matched synsets info in the synsets list to the file. A literal file
     * consists of literals separated via {@code ->} and some literals may also have their synset id's between parenthesis
     * after them. Each line is read, and for each line a new synset is constructed. A literal matching synset consists
     * of multiple literals, possibly all of which have the same meaning.
     * An example file is like this:
     * <blockquote><pre>
     * ab{@code ->}su(TUR10-1000000){@code ->}âb
     * kırmızı(TUR10-1100000){@code ->}al
     * </pre></blockquote>
     */
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
        } catch (FileNotFoundException | UnsupportedEncodingException ignored) {
        }
    }

    /**
     * Displays next synset merge candidate in the synsets list.
     * @return A string showing the index of the candidate displayed.
     */
    public String nextSynSetCandidate(){
        if (itemIndex < synSets.size() - 1){
            itemIndex++;
            displaySynSetCandidate();
        }
        return itemIndex + 1 + "/" + synSets.size();
    }

    /**
     * Displays previous synset merge candidate in the synsets list.
     * @return A string showing the index of the candidate displayed.
     */
    public String previousSynSetCandidate(){
        if (itemIndex > 0){
            itemIndex--;
            displaySynSetCandidate();
        }
        return itemIndex + 1 + "/" + synSets.size();
    }

    /**
     * Displays random synset merge candidate in the synsets list.
     * @return A string showing the index of the candidate displayed.
     */
    public String randomSynSetCandidate(){
        itemIndex = random.nextInt(synSets.size());
        displaySynSetCandidate();
        return itemIndex + 1 + "/" + synSets.size();
    }

}
