package WordNet.Annotation;

import Dictionary.Pos;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import Util.DrawingButton;
import WordNet.Literal;
import WordNet.SynSet;
import WordNet.WordNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Properties;

public abstract class DomainEditorFrame extends JFrame implements ActionListener {
    protected WordNet turkish, domainWordNet;
    protected TxtDictionary dictionary;
    protected JToolBar toolBar;

    protected static final String SAVE = "save";

    private String domainWordNetFileName;
    private String domainDictionaryFileName;
    protected String domainPrefix = "turkish";
    protected String wordNetPrefix = "TUR10-";
    protected int finalId;
    protected boolean modified = false;
    abstract void loadContents();

    /**
     * If the user clicks save button, the domain wordnet and the domain dictionary will be saved.
     * @param e Action event to be handled
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (SAVE.equals(e.getActionCommand())) {
            domainWordNet.saveAsXml(domainWordNetFileName);
            dictionary.saveAsTxt(domainDictionaryFileName);
            modified = false;
        }
    }

    /**
     * Checks if the new synset from the general wordnet exists in the domain wordnet or not. If it does not
     * exist, the function creates a new synset with the same pos and definition and also adds root as the only
     * existing literal. It also adds the new synset to the domain wordnet.
     * @param addedSynSet New synset candidate from the general wordnet
     * @param root Root of the new word
     * @return Created synset if newly created in the function, otherwise existing synset.
     */
    protected SynSet addSynSet(SynSet addedSynSet, String root){
        boolean newOne = false;
        SynSet newSynSet = domainWordNet.getSynSetWithId(addedSynSet.getId());
        if (newSynSet == null){
            newOne = true;
            newSynSet = new SynSet(addedSynSet.getId());
            newSynSet.setPos(addedSynSet.getPos());
            newSynSet.setDefinition(addedSynSet.getLongDefinition());
        }
        boolean found = false;
        String rootLower = root.toLowerCase(new Locale("tr"));
        for (int i = 0; i < addedSynSet.getSynonym().literalSize(); i++){
            String literalLower = addedSynSet.getSynonym().getLiteral(i).getName().toLowerCase(new Locale("tr"));
            if (literalLower.equals(rootLower) || (addedSynSet.getPos().equals(Pos.VERB) && literalLower.startsWith(rootLower) && literalLower.length() == rootLower.length() + 3)){
                domainWordNet.addLiteralToLiteralList(addedSynSet.getSynonym().getLiteral(i));
                newSynSet.addLiteral(addedSynSet.getSynonym().getLiteral(i));
                found = true;
                break;
            }
        }
        if (!found){
            newSynSet.addLiteral(new Literal(root, 1, newSynSet.getId()));
        }
        if (newOne){
            domainWordNet.addSynSet(newSynSet);
        }
        return newSynSet;
    }

    /**
     * Finds the last existing id in the domain wordnet. In the domain wordnet, all id's start from 0000000 and
     * increment by 10 such as 0000010, 0000020, 0000030, etc.
     * @return The largest synset id in the domain wordnet.
     */
    private int getFinalId(){
        int max = 0;
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getId().startsWith(wordNetPrefix)){
                int id = Integer.parseInt(synSet.getId().substring(wordNetPrefix.length()));
                if (id > max){
                    max = id;
                }
            }
        }
        return max;
    }

    /**
     * Creates and adds save button to the toolbar.
     */
    private void addButtons() {
        JButton save = new DrawingButton(WordNetEditorFrame.class, this, "save", SAVE, "Save");
        toolBar.add(save);
    }

    /**
     * Abstract constructor for the domain editor. It loads the prefixes of the general and domain wordnets, and also
     * the wordnets themselves. Adds buttons to the toolbar.
     */
    public DomainEditorFrame(){
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(new File("config.properties").toPath()));
            wordNetPrefix = properties.getProperty("wordNetPrefix");
            domainPrefix = properties.getProperty("domainPrefix");
            domainWordNetFileName = domainPrefix + "_wordnet.xml";
            domainDictionaryFileName = domainPrefix + "_dictionary.txt";
        } catch (IOException ignored) {
        }
        dictionary = new TxtDictionary(domainDictionaryFileName, new TurkishWordComparator());
        domainWordNet = new WordNet(domainWordNetFileName, new Locale("tr"));
        finalId = getFinalId();
        turkish = new WordNet();
        toolBar = new JToolBar("ToolBox");
        addButtons();
        add(toolBar, BorderLayout.PAGE_START);
        toolBar.setVisible(true);
        loadContents();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                JFrame frame = (JFrame) e.getSource();
                if (modified){
                    int result = JOptionPane.showConfirmDialog(frame,
                            "Are you sure you want to exit the application without saving?",
                            frame.getName(),
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION){
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    }
                } else {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });
        setVisible(true);
    }

}
