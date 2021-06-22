package WordNet.Annotation;

import Dictionary.Pos;
import Dictionary.TxtWord;
import Dictionary.Word;
import MorphologicalAnalysis.Transition;
import Util.DrawingButton;
import WordNet.Literal;
import WordNet.SemanticRelation;
import WordNet.SemanticRelationType;
import WordNet.SynSet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.*;

public class WordNetEditorFrame extends DomainEditorFrame implements ActionListener {
    private PartOfSpeechTree noun, adjective, verb, adverb;
    private PartOfSpeechTree selectedPartOfSpeechTree;
    private JTextField leftSearch, id, literal, sense, definition;
    private DefaultMutableTreeNode selectedTreeNode = null;
    private SynSet selectedSynSet = null;
    private JComboBox<SynSet> leftSearchAlternatives;
    private JComboBox<SynSet> alternatives;
    private JCheckBox showMoved, automaticSelection;
    private JComboBox<String> fontSizeSelection;
    private JList dictionaryList, wordNetList;
    private boolean completed = false;

    private static final String EDIT = "edit";
    private static final String ADD_NEW = "add new";
    private static final String INSERT_FROM_WORDNET = "insert from wordnet";
    private static final String INSERT_CHILD = "insert child";
    private static final String REMOVE_FROM_PARENT = "remove from parent";
    private static final String DELETE = "delete";
    private static final String REPLACE = "replace with new synset";
    private static final String MERGE = "merge two synsets";
    private static final String ADD_WORDNET = "add to wordnet";
    private static final String INSERT_INTO_WORDNET = "insert into wordnet";
    private static final String ADD_DICTIONARY = "add to dictionary";
    private static final String EXPAND_ALL = "expand all";
    private static final String EXPAND_EVERYTHING = "expand everything";
    private static final String COLLAPSE_ALL = "collapse all";

    public class PartOfSpeechTree{
        private JTree tree;
        private HashMap<SynSet, DefaultMutableTreeNode> nodeList;
        private DefaultTreeModel treeModel;

        public JTree getTree() {
            return tree;
        }

        public DefaultTreeModel getTreeModel() {
            return treeModel;
        }

        PartOfSpeechTree(JTree tree, HashMap<SynSet, DefaultMutableTreeNode> nodeList, DefaultTreeModel treeModel) {
            this.tree = tree;
            this.nodeList = nodeList;
            this.treeModel = treeModel;
        }
    }

    public class SynSetObject{
        private SynSet synSet;

        SynSetObject(SynSet synSet){
            this.synSet = synSet;
        }

        public String toString(){
            String color = "black";
            int count = 0;
            for (int i = 0; i < synSet.relationSize(); i++){
                if (synSet.getRelation(i) instanceof SemanticRelation && (((SemanticRelation) synSet.getRelation(i)).getRelationType().equals(SemanticRelationType.HYPERNYM) || ((SemanticRelation) synSet.getRelation(i)).getRelationType().equals(SemanticRelationType.INSTANCE_HYPERNYM))){
                    count++;
                }
            }
            if (count > 1){
                color = "red";
            }
            String literal = "<html><font color=\"" + color + "\">" + synSet.getSynonym().getLiteral(0).getName();
            for (int i = 1; i < synSet.getSynonym().literalSize(); i++){
                literal += "::" + synSet.getSynonym().getLiteral(i).getName();
            }
            return literal + " (" + synSet.getDefinition() + ")" + "</font></html>";
        }

        public SynSet getSynSet(){
            return synSet;
        }
    }

    public class LiteralObject{
        private Literal literal;
        private Pos pos;

        LiteralObject(Literal literal, Pos pos){
            this.literal = literal;
            this.pos = pos;
        }

        public String toString(){
            return literal.getName() + " (" + pos + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LiteralObject that = (LiteralObject) o;
            return literal.getName().equals(that.literal.getName()) && pos == that.pos;
        }

    }

    public class WordObject{
        private Word word;
        private Pos pos;

        WordObject(Word word, Pos pos){
            this.word = word;
            this.pos = pos;
        }

        public String toString(){
            return word.getName() + " (" + pos + ")";
        }

    }

    private void addButtons() {
        JButton edit = new DrawingButton(WordNetEditorFrame.class, this, "edit", EDIT, "Edit");
        toolBar.add(edit);
        toolBar.addSeparator();
        JButton addNew = new DrawingButton(WordNetEditorFrame.class, this, "addparent", ADD_NEW, "Add New Noun SynSet");
        toolBar.add(addNew);
        JButton insertFromWordNet = new DrawingButton(WordNetEditorFrame.class, this, "merge", INSERT_FROM_WORDNET, "Insert Noun From Turkish WordNet");
        toolBar.add(insertFromWordNet);
        toolBar.addSeparator();
        JButton insertChild = new DrawingButton(WordNetEditorFrame.class, this, "semanticrelation", INSERT_CHILD, "Insert Child");
        toolBar.add(insertChild);
        JButton breakLink = new DrawingButton(WordNetEditorFrame.class, this, "split", REMOVE_FROM_PARENT, "Remove From Parent");
        toolBar.add(breakLink);
        toolBar.addSeparator();
        JButton replace = new DrawingButton(WordNetEditorFrame.class, this, "interlingual", REPLACE, "Replace With New Synset");
        toolBar.add(replace);
        toolBar.addSeparator();
        JButton delete = new DrawingButton(WordNetEditorFrame.class, this, "delete", DELETE, "Delete");
        toolBar.add(delete);
        toolBar.addSeparator();
        JButton merge = new DrawingButton(WordNetEditorFrame.class, this, "random", MERGE, "Merge Two SynSets");
        toolBar.add(merge);
        toolBar.addSeparator();
        JButton addWordnet = new DrawingButton(WordNetEditorFrame.class, this, "moveleft", ADD_WORDNET, "Add to WordNet from Dictionary");
        toolBar.add(addWordnet);
        JButton insertIntoWordnet = new DrawingButton(WordNetEditorFrame.class, this, "insertinto", INSERT_INTO_WORDNET, "Insert into WordNet from Dictionary");
        toolBar.add(insertIntoWordnet);
        JButton addDictionary = new DrawingButton(WordNetEditorFrame.class, this, "moveright", ADD_DICTIONARY, "Add to Dictionary from WordNet");
        toolBar.add(addDictionary);
        toolBar.addSeparator();
        JButton expandAll = new DrawingButton(WordNetEditorFrame.class, this, "fastforward", EXPAND_ALL, "Expand All");
        toolBar.add(expandAll);
        JButton collapseAll = new DrawingButton(WordNetEditorFrame.class, this, "fastbackward", COLLAPSE_ALL, "Collapse All");
        toolBar.add(collapseAll);
        toolBar.addSeparator();
        JButton expandEverything = new DrawingButton(WordNetEditorFrame.class, this, "fastfastforward", EXPAND_EVERYTHING, "Expand Everything");
        toolBar.add(expandEverything);
        showMoved = new JCheckBox("Show Moved");
        toolBar.add(showMoved);
        automaticSelection = new JCheckBox("Automatic Selection");
        toolBar.add(automaticSelection);
        toolBar.addSeparator();
        Label fontSize = new Label("Font Size:");
        fontSize.setMaximumSize(new Dimension(80, 30));
        toolBar.add(fontSize);
        fontSizeSelection = new JComboBox<String>(new String[]{"11", "12", "13", "14", "15", "16", "17", "18", "19", "20"});
        fontSizeSelection.setMaximumSize(new Dimension(70, 30));
        fontSizeSelection.addActionListener(e -> {
            final Font currentFont = noun.getTree().getFont();
            final Font bigFont = new Font(currentFont.getName(), currentFont.getStyle(), Integer.parseInt((String) fontSizeSelection.getSelectedItem()));
            noun.getTree().setFont(bigFont);
            adjective.getTree().setFont(bigFont);
            adverb.getTree().setFont(bigFont);
            verb.getTree().setFont(bigFont);
            dictionaryList.setFont(bigFont);
            wordNetList.setFont(bigFont);
        });
        toolBar.add(fontSizeSelection);
    }

    private void showPath(PartOfSpeechTree partOfSpeechTree, DefaultMutableTreeNode treeNode){
        TreePath treePath = new TreePath(partOfSpeechTree.treeModel.getPathToRoot(treeNode));
        partOfSpeechTree.tree.setSelectionPath(treePath);
        partOfSpeechTree.tree.scrollPathToVisible(treePath);
    }

    private void replaceAllRelationsWithNewSynSet(String oldId, String newId){
        for (SynSet synSet : domainWordNet.synSetList()){
            for (int i = 0; i < synSet.relationSize(); i++){
                if (synSet.getRelation(i).getName().equals(oldId)){
                    synSet.getRelation(i).setName(newId);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        String synsetId, newSynSetId;
        SynSet synSet;
        switch (e.getActionCommand()){
            case DELETE:
                if (selectedSynSet != null){
                    domainWordNet.removeSynSet(selectedSynSet);
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                    parent.remove(selectedTreeNode);
                    selectedPartOfSpeechTree.treeModel.reload(parent);
                    selectedSynSet = null;
                    modified = true;
                } else {
                    if (!dictionaryList.isSelectionEmpty()){
                        WordObject selectedWord = (WordObject) dictionaryList.getSelectedValue();
                        TxtWord word = (TxtWord) dictionary.getWord(selectedWord.word.getName());
                        dictionary.removeWord(word.getName());
                        modified = true;
                        ((DefaultListModel) dictionaryList.getModel()).removeElement(dictionaryList.getSelectedValue());
                    } else {
                        JOptionPane.showMessageDialog(this, "No Synset or Word Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case EDIT:
                if (selectedSynSet != null){
                    selectedSynSet.getSynonym().getLiteral(0).setName(literal.getText());
                    selectedSynSet.getSynonym().getLiteral(0).setSense(Integer.parseInt(sense.getText()));
                    if (definition.getText().length() > 0){
                        selectedSynSet.setDefinition(definition.getText());
                    } else {
                        selectedSynSet.setDefinition(" ");
                    }
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedTreeNode.getParent();
                    selectedPartOfSpeechTree.treeModel.reload(parent);
                    selectedSynSet = null;
                    modified = true;
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case REPLACE:
                if (selectedSynSet != null){
                    finalId += 10;
                    newSynSetId = wordNetPrefix + "" + finalId;
                    DefaultMutableTreeNode node = noun.nodeList.get(selectedSynSet);
                    noun.nodeList.remove(selectedSynSet);
                    replaceAllRelationsWithNewSynSet(selectedSynSet.getId(), newSynSetId);
                    domainWordNet.changeSynSetId(selectedSynSet, newSynSetId);
                    selectedSynSet.setDefinition(" ");
                    noun.nodeList.put(selectedSynSet, node);
                    node.setUserObject(new SynSetObject(selectedSynSet));
                    noun.treeModel.reload(node);
                    modified = true;
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case ADD_NEW:
                finalId += 10;
                newSynSetId = wordNetPrefix + "" + finalId;
                id.setText(newSynSetId);
                definition.setText(" ");
                SynSet newSynSet = new SynSet(id.getText());
                newSynSet.addLiteral(new Literal(literal.getText(), Integer.parseInt(sense.getText()) + 1, id.getText()));
                if (definition.getText().length() > 0){
                    newSynSet.setDefinition(definition.getText());
                } else {
                    newSynSet.setDefinition(" ");
                }
                addNewSynSet(newSynSet, Pos.NOUN, noun);
                modified = true;
                break;
            case INSERT_FROM_WORDNET:
                synsetId = JOptionPane.showInputDialog("Enter synset id");
                synSet = turkish.getSynSetWithId(synsetId);
                if (synSet != null){
                    if (synSet.getPos() == Pos.NOUN){
                        if (domainWordNet.getSynSetWithId(synsetId) == null){
                            id.setText(synsetId);
                            literal.setText(synSet.getSynonym().getLiteral(0).getName());
                            sense.setText("" + synSet.getSynonym().getLiteral(0).getSense());
                            definition.setText(synSet.getLongDefinition());
                            modified = true;
                        } else {
                            DefaultMutableTreeNode node = noun.nodeList.get(domainWordNet.getSynSetWithId(synsetId));
                            showPath(noun, node);
                            JOptionPane.showMessageDialog(this, "Synset Does Exist In Domain WordNet!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Synset Is Not a Noun!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Synset Does Not Exist In Turkish WordNet!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case INSERT_CHILD:
                if (selectedSynSet != null){
                    synsetId = JOptionPane.showInputDialog("Enter parent synset id");
                    synSet = domainWordNet.getSynSetWithId(synsetId);
                    if (synSet != null){
                        if (synSet.getPos() == Pos.NOUN){
                            DefaultMutableTreeNode parentNode = noun.nodeList.get(synSet);
                            insertIntoCorrectPosition(parentNode, selectedTreeNode);
                            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)noun.tree.getModel().getRoot();
                            noun.treeModel.reload(rootNode);
                            if (showMoved.isSelected()){
                                showPath(noun, selectedTreeNode);
                            }
                            selectedSynSet.addRelation(new SemanticRelation(synSet.getId(), SemanticRelationType.HYPERNYM));
                            synSet.addRelation(new SemanticRelation(selectedSynSet.getId(), SemanticRelationType.HYPONYM));
                            modified = true;
                        } else {
                            JOptionPane.showMessageDialog(this, "Parent Synset Is Not a Noun!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Parent Synset Does Not Exist!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case REMOVE_FROM_PARENT:
                if (selectedSynSet != null) {
                    for (int i = 0; i < selectedSynSet.relationSize(); i++){
                        if (selectedSynSet.getRelation(i) instanceof SemanticRelation){
                            if (((SemanticRelation) selectedSynSet.getRelation(i)).getRelationType() == SemanticRelationType.HYPERNYM || ((SemanticRelation) selectedSynSet.getRelation(i)).getRelationType() == SemanticRelationType.INSTANCE_HYPERNYM){
                                synsetId = selectedSynSet.getRelation(i).getName();
                                synSet = domainWordNet.getSynSetWithId(synsetId);
                                selectedSynSet.removeRelation(selectedSynSet.getRelation(i));
                                modified = true;
                                if (synSet != null){
                                    for (int j = 0; j < synSet.relationSize(); j++){
                                        if (synSet.getRelation(j) instanceof SemanticRelation){
                                            if ((((SemanticRelation) synSet.getRelation(j)).getRelationType() == SemanticRelationType.HYPONYM || ((SemanticRelation) synSet.getRelation(j)).getRelationType() == SemanticRelationType.INSTANCE_HYPONYM) && synSet.getRelation(j).getName().equals(selectedSynSet.getId())){
                                                synSet.removeRelation(synSet.getRelation(j));
                                                break;
                                            }
                                        }
                                    }
                                    DefaultMutableTreeNode parentNode = noun.nodeList.get(synSet);
                                    parentNode.remove(selectedTreeNode);
                                    SynSet newParent = null;
                                    for (int j = 0; j < selectedSynSet.relationSize(); j++){
                                        if (selectedSynSet.getRelation(j) instanceof SemanticRelation){
                                            if ((((SemanticRelation) selectedSynSet.getRelation(j)).getRelationType() == SemanticRelationType.HYPERNYM || ((SemanticRelation) selectedSynSet.getRelation(j)).getRelationType() == SemanticRelationType.INSTANCE_HYPERNYM)){
                                                newParent = domainWordNet.getSynSetWithId(selectedSynSet.getRelation(j).getName());
                                                break;
                                            }
                                        }
                                    }
                                    if (newParent == null){
                                        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)noun.tree.getModel().getRoot();
                                        insertIntoCorrectPosition(rootNode, selectedTreeNode);
                                        noun.treeModel.reload(rootNode);
                                    } else {
                                        DefaultMutableTreeNode newParentNode = noun.nodeList.get(newParent);
                                        insertIntoCorrectPosition(newParentNode, selectedTreeNode);
                                        noun.treeModel.reload(newParentNode);
                                    }
                                    if (showMoved.isSelected()){
                                        showPath(noun, selectedTreeNode);
                                    } else {
                                        showPath(noun, parentNode);
                                    }
                                }
                                break;
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case MERGE:
                if (noun.tree.getSelectionPaths() != null){
                    if (noun.tree.getSelectionPaths().length == 2){
                        DefaultMutableTreeNode selectedTreeNode1 = (DefaultMutableTreeNode) noun.tree.getSelectionPaths()[1].getLastPathComponent();
                        DefaultMutableTreeNode selectedTreeNode2 = (DefaultMutableTreeNode) noun.tree.getSelectionPaths()[0].getLastPathComponent();
                        SynSetObject synSetObject1 = (SynSetObject) selectedTreeNode1.getUserObject();
                        SynSetObject synSetObject2 = (SynSetObject) selectedTreeNode2.getUserObject();
                        SynSet synSet1 = synSetObject1.synSet;
                        SynSet synSet2 = synSetObject2.synSet;
                        synSet1.mergeSynSet(synSet2);
                        domainWordNet.removeSynSet(synSet2);
                        modified = true;
                        replaceAllRelationsWithNewSynSet(synSet2.getId(), synSet1.getId());
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedTreeNode2.getParent();
                        for (int i = 0; i < selectedTreeNode2.getChildCount(); i++){
                            insertIntoCorrectPosition(selectedTreeNode1, (DefaultMutableTreeNode) selectedTreeNode2.getChildAt(i));
                        }
                        parentNode.remove(selectedTreeNode2);
                        noun.treeModel.reload(parentNode);
                    } else {
                        JOptionPane.showMessageDialog(this, "More Than 2 Synsets Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case ADD_WORDNET:
                if (!dictionaryList.isSelectionEmpty()){
                    WordObject selectedWord = (WordObject) dictionaryList.getSelectedValue();
                    finalId += 10;
                    newSynSetId = wordNetPrefix + "" + finalId;
                    newSynSet = new SynSet(newSynSetId);
                    String wordForm = selectedWord.word.getName();
                    if (selectedWord.pos.equals(Pos.VERB)){
                        Transition verbTransition = new Transition("mAk");
                        TxtWord word = (TxtWord) dictionary.getWord(wordForm);
                        String verbForm = verbTransition.makeTransition(word, word.getName());
                        newSynSet.addLiteral(new Literal(verbForm, 1, newSynSetId));
                    } else {
                        newSynSet.addLiteral(new Literal(wordForm, 1, newSynSetId));
                    }
                    newSynSet.setDefinition(" ");
                    switch (selectedWord.pos){
                        case NOUN:
                            addNewSynSet(newSynSet, selectedWord.pos, noun);
                            break;
                        case ADJECTIVE:
                            addNewSynSet(newSynSet, selectedWord.pos, adjective);
                            break;
                        case VERB:
                            addNewSynSet(newSynSet, selectedWord.pos, verb);
                            break;
                        case ADVERB:
                            addNewSynSet(newSynSet, selectedWord.pos, adverb);
                            break;
                    }
                    modified = true;
                    ((DefaultListModel) dictionaryList.getModel()).removeElement(dictionaryList.getSelectedValue());
                } else {
                    JOptionPane.showMessageDialog(this, "No Word Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case INSERT_INTO_WORDNET:
                if (!dictionaryList.isSelectionEmpty()){
                    if (selectedSynSet != null){
                        if (selectedSynSet.getPos().equals(((WordObject) dictionaryList.getSelectedValue()).pos)){
                            WordObject selectedWord = (WordObject) dictionaryList.getSelectedValue();
                            String wordForm = selectedWord.word.getName();
                            if (selectedWord.pos.equals(Pos.VERB)){
                                Transition verbTransition = new Transition("mAk");
                                TxtWord word = (TxtWord) dictionary.getWord(wordForm);
                                String verbForm = verbTransition.makeTransition(word, word.getName());
                                selectedSynSet.addLiteral(new Literal(verbForm, 1, selectedSynSet.getId()));
                            } else {
                                selectedSynSet.addLiteral(new Literal(wordForm, 1, selectedSynSet.getId()));
                            }
                            modified = true;
                            ((DefaultListModel) dictionaryList.getModel()).removeElement(dictionaryList.getSelectedValue());
                            selectedPartOfSpeechTree.treeModel.reload(selectedTreeNode);
                            selectedSynSet = null;
                        } else {
                            JOptionPane.showMessageDialog(this, "Selected SynSet Pos is not equal to word pos!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No SynSet Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Word Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case ADD_DICTIONARY:
                if (!wordNetList.isSelectionEmpty()){
                    for (Object object : wordNetList.getSelectedValuesList()){
                        LiteralObject selectedLiteral = (LiteralObject) object;
                        String word = selectedLiteral.literal.getName();
                        switch (selectedLiteral.pos){
                            case NOUN:
                                dictionary.addWithFlag(word, "CL_ISIM");
                                break;
                            case ADJECTIVE:
                                dictionary.addWithFlag(word, "IS_ADJ");
                                break;
                            case VERB:
                                dictionary.addWithFlag(word.substring(0, word.length() - 3), "CL_FIIL");
                                break;
                            case ADVERB:
                                dictionary.addWithFlag(word, "IS_ADVERB");
                                break;
                        }
                        modified = true;
                        ((DefaultListModel) wordNetList.getModel()).removeElement(object);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No Literal Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case EXPAND_ALL:
                if (selectedSynSet != null){
                    expandAll(selectedTreeNode);
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case EXPAND_EVERYTHING:
                expandAll((DefaultMutableTreeNode)noun.tree.getModel().getRoot());
                break;
            case COLLAPSE_ALL:
                if (selectedSynSet != null){
                    collapseAll(selectedTreeNode);
                } else {
                    JOptionPane.showMessageDialog(this, "No Synset Selected!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
        }
    }

    private void expandAll(DefaultMutableTreeNode node) {
        ArrayList<DefaultMutableTreeNode> list = Collections.list(node.children());
        for (DefaultMutableTreeNode treeNode : list) {
            expandAll(treeNode);
        }
        TreePath path = new TreePath(node.getPath());
        noun.tree.expandPath(path);
    }

    private void collapseAll(DefaultMutableTreeNode node) {
        ArrayList<DefaultMutableTreeNode> list = Collections.list(node.children());
        for (DefaultMutableTreeNode treeNode : list) {
            collapseAll(treeNode);
        }
        TreePath path = new TreePath(node.getPath());
        noun.tree.collapsePath(path);
    }

    private void addNewSynSet(SynSet newSynSet, Pos pos, PartOfSpeechTree partOfSpeechTree){
        newSynSet.setPos(pos);
        domainWordNet.addSynSet(newSynSet);
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(new SynSetObject(newSynSet));
        partOfSpeechTree.nodeList.put(newSynSet, newChild);
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)partOfSpeechTree.tree.getModel().getRoot();
        insertIntoCorrectPosition(rootNode, newChild);
        partOfSpeechTree.treeModel.reload(rootNode);
        showPath(partOfSpeechTree, newChild);
    }

    private void insertIntoCorrectPosition(DefaultMutableTreeNode parent, DefaultMutableTreeNode newChild) {
        if (parent.getChildCount() == 0){
            parent.add(newChild);
            return;
        }
        Locale locale = new Locale("tr");
        Collator collator = Collator.getInstance(locale);
        int lower = 0, upper = parent.getChildCount(), middle = (lower + upper) / 2;
        while (lower < upper - 1){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(middle);
            if (collator.compare(child.getUserObject().toString(), newChild.getUserObject().toString()) > 0){
                upper = middle;
            } else {
                lower = middle;
            }
            middle = (lower + upper) / 2;
        }
        if (collator.compare(((DefaultMutableTreeNode) parent.getChildAt(lower)).getUserObject().toString(), newChild.getUserObject().toString()) > 0) {
            parent.insert(newChild, lower);
        } else {
            parent.insert(newChild, lower + 1);
        }
    }

    private PartOfSpeechTree constructTree(Pos partOfSpeech, boolean hypernym){
        DefaultMutableTreeNode parent, child, rootNode = new DefaultMutableTreeNode(partOfSpeech);
        HashMap<SynSet, DefaultMutableTreeNode> nodeList = new HashMap<>();
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getPos() == partOfSpeech){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SynSetObject(synSet));
                nodeList.put(synSet, node);
            }
        }
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getPos() == partOfSpeech){
                child = nodeList.get(synSet);
                if (hypernym){
                    boolean parentFound = false;
                    for (int j = 0; j < synSet.relationSize(); j++){
                        if ((synSet.getRelation(j) instanceof SemanticRelation)){
                            SemanticRelation relation = (SemanticRelation) synSet.getRelation(j);
                            if (relation.getRelationType().equals(SemanticRelationType.INSTANCE_HYPERNYM) || relation.getRelationType().equals(SemanticRelationType.HYPERNYM)){
                                SynSet parentSynSet = domainWordNet.getSynSetWithId(relation.getName());
                                if (parentSynSet != null){
                                    parent = nodeList.get(parentSynSet);
                                    if (parent != null){
                                        try{
                                            insertIntoCorrectPosition(parent, child);
                                        } catch (IllegalArgumentException e){
                                            System.out.println(synSet.getSynonym());
                                        }
                                        parentFound = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (!parentFound){
                        insertIntoCorrectPosition(rootNode, child);
                    }
                } else {
                    insertIntoCorrectPosition(rootNode, child);
                }
            }
        }
        JTree tree = new JTree(treeModel);
        PartOfSpeechTree partOfSpeechTree = new PartOfSpeechTree(tree, nodeList, treeModel);
        tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode){
                if (((DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent()).getUserObject() instanceof SynSetObject){
                    selectedTreeNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
                    selectedSynSet = ((SynSetObject) selectedTreeNode.getUserObject()).synSet;
                    selectedPartOfSpeechTree = partOfSpeechTree;
                    id.setText(selectedSynSet.getId());
                    literal.setText(selectedSynSet.getSynonym().getLiteral(0).getName());
                    sense.setText("" + selectedSynSet.getSynonym().getLiteral(0).getSense());
                    definition.setText(selectedSynSet.getDefinition());
                    ArrayList<SynSet> alternativeList = turkish.getSynSetsWithLiteral(selectedSynSet.getSynonym().getLiteral(0).getName());
                    completed = false;
                    alternatives.removeAllItems();
                    alternatives.setSelectedIndex(-1);
                    for (SynSet synSet : alternativeList){
                        if (synSet.getPos().equals(partOfSpeech)){
                            alternatives.addItem(synSet);
                            if (synSet.equals(selectedSynSet)){
                                alternatives.setSelectedItem(synSet);
                            }
                        }
                    }
                    if (alternatives.getItemCount() == 0){
                        alternatives.setEnabled(false);
                    } else {
                        if (selectedSynSet.getId().startsWith(wordNetPrefix) && selectedSynSet.getDefinition() != null){
                            alternatives.setEnabled(false);
                        } else {
                            if (selectedSynSet.getId().startsWith(wordNetPrefix) && selectedSynSet.getDefinition() == null){
                                alternatives.setEnabled(true);
                            } else {
                                if (alternatives.getItemCount() > 1 && !selectedSynSet.getId().startsWith(wordNetPrefix)){
                                    alternatives.setEnabled(true);
                                } else {
                                    alternatives.setEnabled(false);
                                }
                            }
                        }
                    }
                    completed = true;
                } else {
                    selectedTreeNode = null;
                    selectedSynSet = null;
                }
            }
        });
        if (partOfSpeech == Pos.NOUN){
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        } else {
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        ToolTipManager.sharedInstance().registerComponent(tree);
        return partOfSpeechTree;
    }

    private void replaceWithNewSynSet(SynSet synSetInTurkishWordNet){
        if (synSetInTurkishWordNet != null && selectedSynSet != null){
            DefaultMutableTreeNode node = selectedPartOfSpeechTree.nodeList.get(selectedSynSet);
            selectedPartOfSpeechTree.nodeList.remove(selectedSynSet);
            for (SynSet synSet1 : domainWordNet.synSetList()){
                for (int i = 0; i < synSet1.relationSize(); i++){
                    if (synSet1.getRelation(i).getName().equals(selectedSynSet.getId())){
                        synSet1.getRelation(i).setName(synSetInTurkishWordNet.getId());
                    }
                }
            }
            domainWordNet.removeSynSet(selectedSynSet);
            SynSet addedSynSet = addSynSet(synSetInTurkishWordNet, literal.getText());
            selectedPartOfSpeechTree.nodeList.put(addedSynSet, node);
            node.setUserObject(new SynSetObject(addedSynSet));
            selectedPartOfSpeechTree.treeModel.reload(node);
        }
    }


    private void addToListModel(DefaultListModel<WordObject> listModel, Word word, Pos pos, ArrayList<SynSet> synSets){
        boolean found = false;
        for (SynSet synSet : synSets){
            if (synSet.getPos() != null && synSet.getPos().equals(pos)){
                found = true;
                break;
            }
        }
        if (!found){
            listModel.addElement(new WordObject(word, pos));
        }
    }

    private JList<WordObject> createDictionaryList(){
        Transition verbTransition = new Transition("mAk");
        JList<WordObject> list = new JList<>();
        DefaultListModel<WordObject> listModel = new DefaultListModel<>();
        for (int i = 0; i < dictionary.size(); i++){
            TxtWord word = (TxtWord) dictionary.getWord(i);
            String verbForm = verbTransition.makeTransition(word, word.getName());
            ArrayList<SynSet> synSets = domainWordNet.getSynSetsWithLiteral(word.getName());
            synSets.addAll(domainWordNet.getSynSetsWithLiteral(verbForm));
            if (word.isNominal()){
                addToListModel(listModel, word, Pos.NOUN, synSets);
            }
            if (word.isAdjective()){
                addToListModel(listModel, word, Pos.ADJECTIVE, synSets);
            }
            if (word.isVerb()){
                addToListModel(listModel, word, Pos.VERB, synSets);
            }
            if (word.isAdverb()){
                addToListModel(listModel, word, Pos.ADVERB, synSets);
            }
        }
        list.setModel(listModel);
        return list;
    }

    private JList<LiteralObject> createWordNetList(){
        JList<LiteralObject> list = new JList<>();
        DefaultListModel<LiteralObject> listModel = new DefaultListModel<>();
        for (SynSet synSet : domainWordNet.synSetList()){
            if (synSet.getPos() != null){
                for (int i = 0; i < synSet.getSynonym().literalSize(); i++){
                    String word = synSet.getSynonym().getLiteral(i).getName().toLowerCase(new Locale("tr"));
                    if (!word.contains(" ") && !word.startsWith(".") && !word.startsWith(",") && !word.startsWith("(") && !word.matches(".*[0-9]+.*")){
                        if (synSet.getPos().equals(Pos.VERB)){
                            if (dictionary.getWord(word.substring(0, word.length() - 3)) == null && !listModel.contains(new LiteralObject(synSet.getSynonym().getLiteral(i), synSet.getPos()))){
                                listModel.addElement(new LiteralObject(synSet.getSynonym().getLiteral(i), synSet.getPos()));
                                break;
                            }
                        } else {
                            if (dictionary.getWord(word) == null && !listModel.contains(new LiteralObject(synSet.getSynonym().getLiteral(i), synSet.getPos()))){
                                listModel.addElement(new LiteralObject(synSet.getSynonym().getLiteral(i), synSet.getPos()));
                                break;
                            }
                        }
                    }
                }
            }
        }
        list.setModel(listModel);
        return list;
    }

    private void selectPossibleCandidate(ArrayList<SynSet> candidates, Pos selectedPos){
        for (SynSet candidate : candidates){
            if (candidate.getPos().equals(selectedPos)){
                selectedSynSet = candidate;
                switch (selectedSynSet.getPos()){
                    case NOUN:
                        showPath(noun, noun.nodeList.get(selectedSynSet));
                        break;
                    case ADJECTIVE:
                        showPath(adjective, adjective.nodeList.get(selectedSynSet));
                        break;
                    case ADVERB:
                        showPath(adverb, adverb.nodeList.get(selectedSynSet));
                        break;
                }
            }
        }
    }

    public void loadContents(){
        addButtons();
        JPanel topPanel = new JPanel(new GridLayout(4, 4));
        topPanel.add(new JLabel("Id"));
        id = new JTextField();
        topPanel.add(id);
        topPanel.add(new JLabel("Literal"));
        literal = new JTextField();
        topPanel.add(literal);
        topPanel.add(new JLabel("Sense"));
        sense = new JTextField();
        topPanel.add(sense);
        topPanel.add(new JLabel("Definition"));
        definition = new JTextField();
        topPanel.add(definition);
        leftSearch = new JTextField();
        leftSearch.addActionListener(e -> selectTree(leftSearch.getText()));
        topPanel.add(leftSearch);
        alternatives = new JComboBox<SynSet>();
        topPanel.add(alternatives);
        alternatives.addActionListener (e -> {
            if (completed){
                replaceWithNewSynSet((SynSet) alternatives.getSelectedItem());
            }
        });
        JLabel dummy1 = new JLabel("");
        topPanel.add(dummy1);
        dummy1.setVisible(false);
        JLabel dummy2 = new JLabel("");
        topPanel.add(dummy2);
        dummy2.setVisible(false);
        leftSearchAlternatives = new JComboBox<SynSet>();
        leftSearchAlternatives.addActionListener(e -> {
            if (leftSearchAlternatives.getSelectedIndex() != -1){
                TreePath treePath = new TreePath(noun.treeModel.getPathToRoot(noun.nodeList.get(leftSearchAlternatives.getSelectedItem())));
                noun.tree.setSelectionPath(treePath);
                noun.tree.scrollPathToVisible(treePath);
            }
        });
        leftSearchAlternatives.setVisible(false);
        topPanel.add(leftSearchAlternatives);
        JPanel nounAdjectivePanel = new JPanel(new BorderLayout());
        noun = constructTree(Pos.NOUN, true);
        JScrollPane nounPane = new JScrollPane(noun.tree);
        nounPane.setMinimumSize(new Dimension(100, 100));
        adjective = constructTree(Pos.ADJECTIVE, false);
        JScrollPane adjectivePane = new JScrollPane(adjective.tree);
        adjectivePane.setMinimumSize(new Dimension(100, 100));
        JSplitPane nounAdjectivePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, nounPane, adjectivePane);
        nounAdjectivePanel.add(nounAdjectivePane, BorderLayout.CENTER);
        verb = constructTree(Pos.VERB, true);
        JScrollPane verbPane = new JScrollPane(verb.tree);
        verbPane.setMinimumSize(new Dimension(100, 100));
        JPanel verbAdverbPanel = new JPanel(new BorderLayout());
        adverb = constructTree(Pos.ADVERB, false);
        JScrollPane adverbPane = new JScrollPane(adverb.tree);
        adverbPane.setMinimumSize(new Dimension(100, 100));
        JSplitPane verbAdverbPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, verbPane, adverbPane);
        verbAdverbPanel.add(verbAdverbPane, BorderLayout.CENTER);
        JSplitPane posPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nounAdjectivePanel, verbAdverbPanel);
        JPanel dictionaryPanel = new JPanel(new BorderLayout());
        dictionaryPanel.setMinimumSize(new Dimension(50, 100));
        dictionaryList = createDictionaryList();
        dictionaryList.addListSelectionListener(e -> {
            if (!dictionaryList.isSelectionEmpty()){
                WordObject selectedWord = (WordObject) dictionaryList.getSelectedValue();
                String wordForm = selectedWord.word.getName();
                if (wordForm.contains("â") || wordForm.contains("û") || wordForm.contains("î")){
                    wordForm = wordForm.replaceAll("â", "a").replaceAll("û", "ü").replaceAll("î", "i");
                    ArrayList<SynSet> candidates = domainWordNet.getSynSetsWithLiteral(wordForm);
                    selectPossibleCandidate(candidates, selectedWord.pos);
                } else {
                    for (int i = 2; i < wordForm.length() - 2; i++){
                        String form1 = wordForm.substring(0, i);
                        String form2 = wordForm.substring(i);
                        ArrayList<SynSet> candidates = domainWordNet.getSynSetsWithLiteral(form1 + " " + form2);
                        selectPossibleCandidate(candidates, selectedWord.pos);
                    }
                }
            }
        });
        JScrollPane dictionaryPane = new JScrollPane(dictionaryList);
        JLabel dictionaryHeader = new JLabel("Words that are in the dictionary but not in the wordnet");
        dictionaryHeader.setHorizontalAlignment(SwingConstants.CENTER);
        dictionaryHeader.setForeground(Color.BLUE);
        dictionaryPane.setColumnHeaderView(dictionaryHeader);
        wordNetList = createWordNetList();
        JScrollPane wordNetPane = new JScrollPane(wordNetList);
        JLabel wordNetHeader = new JLabel("Words that are in the wordnet but not in the dictionary");
        wordNetHeader.setHorizontalAlignment(SwingConstants.CENTER);
        wordNetHeader.setForeground(Color.BLUE);
        wordNetPane.setColumnHeaderView(wordNetHeader);
        JSplitPane dictionaryWordNetPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dictionaryPane, wordNetPane);
        dictionaryPanel.add(dictionaryWordNetPane, BorderLayout.CENTER);
        JSplitPane bottomPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, posPane, dictionaryPanel);
        JSplitPane allPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPane);
        add(allPane, BorderLayout.CENTER);
        setName("WordNet Editor");
    }

    private void selectTree(String searchKey){
        if (!automaticSelection.isSelected()){
            noun.tree.clearSelection();
        }
        leftSearchAlternatives.removeAllItems();
        for (Map.Entry<SynSet, DefaultMutableTreeNode> entry : noun.nodeList.entrySet()){
            if (entry.getKey().getSynonym().containsLiteral(searchKey)){
                leftSearchAlternatives.addItem(entry.getKey());
            }
        }
        if (leftSearchAlternatives.getItemCount() > 1){
            leftSearchAlternatives.setVisible(true);
        } else {
            leftSearchAlternatives.setVisible(false);
        }
        for (Map.Entry<SynSet, DefaultMutableTreeNode> entry : noun.nodeList.entrySet()){
            if (entry.getKey().getSynonym().containsLiteral(searchKey)){
                TreePath treePath = new TreePath(noun.treeModel.getPathToRoot(entry.getValue()));
                noun.tree.addSelectionPath(treePath);
                noun.tree.scrollPathToVisible(treePath);
                break;
            }
        }
    }

}
