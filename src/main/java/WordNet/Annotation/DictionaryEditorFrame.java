package WordNet.Annotation;

import Corpus.Sentence;
import Dictionary.Pos;
import Dictionary.TxtWord;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import MorphologicalAnalysis.Transition;
import Util.DrawingButton;
import WordNet.Literal;
import WordNet.SynSet;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.Collator;
import java.util.*;

/**
 * The Dictionary Editor is distinct from the previous components in that it is an interface designed to create
 * domain-specific dictionaries, whereas the former components are for building and maintaining natural language
 * dictionaries. With the Dictionary Editor, synsets inside a WordNet can be added or removed and sense inputs of
 * synsets can be edited in order to obtain a domain-specific dictionary. Whichever sense of a synset in the WordNet
 * is used in that domain can be selected and transferred to the new dictionary or synsets can be transferred
 * automatically from an existing WordNet to the domain-specific dictionary. Finally, if the sought sense is lacking,
 * it can simply be added to the dictionary with this editor.
 * This interface also makes sure that the dictionary and the WordNet are in accord: When an entry is added to the
 * dictionary, it will be added to the WordNet too, and vice versa. The editor can also sort synsets numerically or
 * alphabetically.
 */
public class DictionaryEditorFrame extends DomainEditorFrame implements ActionListener {
    private ArrayList<String> data;
    private JTable dataTable;
    private ImageIcon addIcon, deleteIcon;
    private HashMap<String, PanelObject> display;
    private static final String ID_SORT = "sortnumbers";
    private static final String TEXT_SORT = "sorttext";
    private static final String DELETE = "delete";
    private JList exampleList;
    private HashMap<String, ArrayList<Sentence>> mappedSentences;

    /**
     * According to the button pushed, the following operations are implemented:
     *
     * <p>ID_SORT: The data is sorted with respect to the synset id's of the words. If one word is not
     * assigned a synset id, then that word is larger than the other.</p>
     * <p>TEXT_SORT: The data is sorted with respect to the words themselves.</p>
     * <p>DELETE: Selected row will be deleted both from the dictionary and the wordnet.<p/>
     * @param e Action event to be handled
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        switch (e.getActionCommand()) {
            case ID_SORT:
                Transition verbTransition = new Transition("mAk");
                data.sort((o1, o2) -> {
                    TxtWord word1 = (TxtWord) dictionary.getWord(o1);
                    TxtWord word2 = (TxtWord) dictionary.getWord(o2);
                    ArrayList<SynSet> synSets1 = domainWordNet.getSynSetsWithLiteral(o1);
                    ArrayList<SynSet> synSets2 = domainWordNet.getSynSetsWithLiteral(o2);
                    if (word1 != null) {
                        String verbForm1 = verbTransition.makeTransition(word1, word1.getName());
                        synSets1.addAll(domainWordNet.getSynSetsWithLiteral(verbForm1));
                    }
                    if (word2 != null) {
                        String verbForm2 = verbTransition.makeTransition(word2, word2.getName());
                        synSets2.addAll(domainWordNet.getSynSetsWithLiteral(verbForm2));
                    }
                    if (!synSets1.isEmpty()) {
                        if (synSets2.isEmpty()) {
                            return 1;
                        } else {
                            return synSets1.get(0).getId().compareTo(synSets2.get(0).getId());
                        }
                    } else {
                        if (synSets2.isEmpty()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case TEXT_SORT:
                Locale locale = new Locale("tr");
                Collator collator = Collator.getInstance(locale);
                data.sort(collator::compare);
                JOptionPane.showMessageDialog(this, "Words Sorted!", "Sorting Complete", JOptionPane.INFORMATION_MESSAGE);
                break;
            case DELETE:
                int rowNo = dataTable.getSelectedRow();
                dictionary.removeWord(data.get(rowNo));
                data.remove(rowNo);
                break;
        }
        dataTable.invalidate();
    }

    private class PanelObject{
        private JPanel flagPanel;
        private JPanel synSetIdPanel;
        private final TxtWord word;
        private final FlagObject flagObject;
        private final SynSetListObject synSetListObject;
        private final String root;
        private JPanel synSetPosPanel;
        private JPanel synSetEditPanel;

        /**
         * Constructs a flag panel for a given word. The flag panel consists of JLabel's for each flag of the word
         * added. There is also a delete button for every flag JLabel, so that the user can delete any flag the user
         * wants. There is also one add button with a JCombobox consisting of all possible flags, so that the user
         * add any flag he/she wants.
         * @param row The index of the word in the list.
         */
        private void createFlagPanel(int row){
            flagPanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            if (flagObject.flags != null){
                for (String flag : flagObject.flags){
                    JButton delete = new JButton();
                    delete.setIcon(deleteIcon);
                    c.gridx = 0;
                    flagPanel.add(delete, c);
                    c.gridx = 1;
                    flagPanel.add(new JLabel(flag), c);
                    c.gridy++;
                    delete.addActionListener(e -> {
                        word.removeFlag(flag);
                        PanelObject panelObject = new PanelObject(root, row);
                        display.put(root, panelObject);
                    });
                }
                dataTable.setRowHeight(row, 35 * (flagObject.flags.length + 1));
            } else {
                dataTable.setRowHeight(row, 35);
            }
            JComboBox<String> flagComboBox = getComboBox();
            JButton add = new JButton();
            add.setIcon(addIcon);
            c.gridx = 0;
            add.addActionListener(e -> {
                if (!root.contains(" ")){
                    dictionary.addWithFlag(root, (String) flagComboBox.getSelectedItem());
                    modified = true;
                    DictionaryEditorFrame.PanelObject panelObject = new DictionaryEditorFrame.PanelObject(root, row);
                    display.put(root, panelObject);
                }
            });
            flagPanel.add(add, c);
            c.gridx = 1;
            flagPanel.add(flagComboBox, c);
        }

        /**
         * Constructs a combobox consisting of all possible flags for a word.
         * @return Combobox consisting of all possible flags for a word.
         */
        private JComboBox<String> getComboBox() {
            JComboBox<String> flagComboBox = new JComboBox<>();
            flagComboBox.addItem("CL_ISIM");
            flagComboBox.addItem("IS_OA");
            flagComboBox.addItem("IS_HM");
            flagComboBox.addItem("IS_ADJ");
            flagComboBox.addItem("IS_ADVERB");
            flagComboBox.addItem("CL_FIIL");
            flagComboBox.addItem("IS_SAYI");
            flagComboBox.addItem("IS_ZM");
            flagComboBox.addItem("IS_CONJ");
            flagComboBox.addItem("IS_QUES");
            flagComboBox.addItem("IS_INTERJ");
            flagComboBox.addItem("IS_SD");
            flagComboBox.addItem("IS_UD");
            flagComboBox.addItem("IS_KG");
            flagComboBox.addItem("IS_ST");
            flagComboBox.addItem("F_SD");
            flagComboBox.addItem("F_UD");
            flagComboBox.addItem("F_GUD");
            flagComboBox.addItem("IS_KIS");
            flagComboBox.addItem("IS_DUP");
            flagComboBox.addItem("IS_BILEŞ");
            flagComboBox.addItem("IS_POSTP");
            flagComboBox.addItem("IS_CA");
            return flagComboBox;
        }

        /**
         * Creates synset panel for the given column. For column
         * <ul>
         *     <li>1: Synset id will be displayed in a JLabel</li>
         *     <li>2: Synset pos tag will be displayed in a JLabel</li>
         *     <li>4: A delete and an add button is display in a grid.</li>
         * </ul>
         * @param column Column of the panel
         * @param row Row number of the panel.
         */
        private void createSynSetPanel(int column, int row){
            JPanel newPanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.gridx = 0;
            for (SynSet synSet : synSetListObject.synSets){
                switch (column){
                    case 1:
                        newPanel.add(new JLabel(synSet.getId()), c);
                        c.gridy++;
                        break;
                    case 2:
                        newPanel.add(new JLabel(synSet.getPos().toString()), c);
                        c.gridy++;
                        break;
                    case 4:
                        JButton delete = new JButton();
                        delete.setIcon(deleteIcon);
                        delete.addActionListener(e -> {
                            domainWordNet.removeSynSet(synSet);
                            modified = true;
                            PanelObject panelObject = new PanelObject(root, row);
                            display.put(root, panelObject);
                        });
                        c.gridx = 0;
                        newPanel.add(delete, c);
                        c.gridx = 1;
                        if (synSet.getDefinition() == null || synSet.getDefinition().equals(" ")){
                            JTextField text = new JTextField("No Definition");
                            text.setMinimumSize(new Dimension(500, 30));
                            text.addActionListener(e -> {
                                if (!text.getText().equalsIgnoreCase("No Definition")){
                                    synSet.setDefinition(text.getText());
                                }
                            });
                            newPanel.add(text, c);
                        } else {
                            if (synSet.getDefinition() != null && synSet.getDefinition().length() > 150){
                                newPanel.add(new JLabel(synSet.getDefinition().substring(0, 150) + "..."), c);
                            } else {
                                newPanel.add(new JLabel(synSet.getDefinition()), c);
                            }
                        }
                        c.gridy++;
                        break;
                }
            }
            if (column == 4){
                c.gridx = 0;
                JComboBox<String> synSetChooser = getSynSetChooser();
                if (synSetChooser.getItemCount() > 0){
                    JButton add = getAddButton(row, synSetChooser);
                    newPanel.add(add, c);
                    c.gridx = 1;
                    newPanel.add(synSetChooser, c);
                }
            }
            switch (column){
                case 1:
                    synSetIdPanel = newPanel;
                    break;
                case 2:
                    synSetPosPanel = newPanel;
                    break;
                case 4:
                    synSetEditPanel = newPanel;
                    break;
            }
        }

        private JButton getAddButton(int row, JComboBox<String> synSetChooser) {
            JButton add = new JButton();
            add.setIcon(addIcon);
            add.addActionListener(e -> {
                if (synSetChooser.getSelectedIndex() != -1){
                    int extraRows = 0;
                    for (int i = 0; i < synSetChooser.getItemCount(); i++){
                        if (synSetChooser.getItemAt(i).startsWith("New SynSet")){
                            extraRows++;
                        } else {
                            break;
                        }
                    }
                    SynSet addedSynSet;
                    Literal addedLiteral;
                    Pos pos;
                    if (synSetChooser.getSelectedIndex() < extraRows){
                        finalId += 10;
                        String newSynSetId = wordNetPrefix + String.format("%07d", finalId);
                        String selectedText = (String) synSetChooser.getSelectedItem();
                        if (selectedText.contains("NOUN")){
                            addedLiteral = new Literal(root, 1, newSynSetId);
                            pos = Pos.NOUN;
                        } else {
                            if (selectedText.contains("ADJECTIVE")){
                                addedLiteral = new Literal(root, 1, newSynSetId);
                                pos = Pos.ADJECTIVE;
                            } else {
                                if (selectedText.contains("ADVERB")){
                                    addedLiteral = new Literal(root, 1, newSynSetId);
                                    pos = Pos.ADVERB;
                                } else {
                                    Transition verbTransition = new Transition("mAk");
                                    String verbForm = verbTransition.makeTransition(word, word.getName());
                                    addedLiteral = new Literal(verbForm, 1, newSynSetId);
                                    pos = Pos.VERB;
                                }
                            }
                        }
                        addedSynSet = new SynSet(newSynSetId);
                        addedSynSet.addLiteral(addedLiteral);
                        addedSynSet.setPos(pos);
                        domainWordNet.addSynSet(addedSynSet);
                        domainWordNet.addLiteralToLiteralList(addedLiteral);
                    } else {
                        addedSynSet = synSetListObject.extraSynSets.get(synSetChooser.getSelectedIndex() - extraRows);
                        if (addedSynSet.getPos().equals(Pos.VERB)){
                            Transition verbTransition = new Transition("mAk");
                            String verbForm = verbTransition.makeTransition(word, word.getName());
                            addSynSet(addedSynSet, verbForm);
                        } else {
                            addSynSet(addedSynSet, root);
                        }
                    }
                    modified = true;
                    DictionaryEditorFrame.PanelObject panelObject = new DictionaryEditorFrame.PanelObject(root, row);
                    display.put(root, panelObject);
                }
            });
            return add;
        }

        /**
         * Construct a combobox which contains either new synset items or an existing synset item. The new synset can be
         * (i) either a new noun, adjective, verb, or adverb synset, (ii) or a synset already existing in the
         * general Turkish wordnet.
         * @return A combobox of options.
         */
        private JComboBox<String> getSynSetChooser() {
            JComboBox<String> synSetChooser = new JComboBox<>();
            if (word != null){
                if (word.isNominal()){
                    synSetChooser.addItem("New SynSet (NOUN)");
                }
                if (word.isAdjective()){
                    synSetChooser.addItem("New SynSet (ADJECTIVE)");
                }
                if (word.isVerb()){
                    synSetChooser.addItem("New SynSet (VERB)");
                }
                if (word.isAdverb()){
                    synSetChooser.addItem("New SynSet (ADVERB)");
                }
            } else {
                if (root.contains(" ")){
                    synSetChooser.addItem("New SynSet (NOUN)");
                }
            }
            for (SynSet synSet : synSetListObject.extraSynSets){
                int definitionLength = 0, exampleLength;
                if (synSet.getDefinition() != null){
                    definitionLength = synSet.getDefinition().length();
                }
                if (synSet.getExample() != null){
                    exampleLength = synSet.getExample().length();
                    if (definitionLength + exampleLength < 150){
                        synSetChooser.addItem(synSet.getDefinition() + " [" + synSet.getExample() + "]");
                    } else {
                        String text = extractTextForSynSet(synSet, definitionLength, exampleLength);
                        synSetChooser.addItem(text);
                    }
                } else {
                    if (definitionLength < 150){
                        synSetChooser.addItem(synSet.getDefinition());
                    } else {
                        synSetChooser.addItem(synSet.getDefinition().substring(0, 150) + "...");
                    }
                }
            }
            return synSetChooser;
        }

        /**
         * Constructs example text displayed for a given synset. Returns concatenation of the first 75 characters of the
         * definition and the first 75 characters of the example. If the length of the definition or the example have
         * less than 75 characters, all characters are concatenated.
         * @param synSet SynSet whose example text will be displayed.
         * @param definitionLength Maximum length for the definition text.
         * @param exampleLength Maximum length for the example text.
         * @return Concatenation of the first 75 characters of the  definition and the first 75 characters of the
         * example.
         */
        private String extractTextForSynSet(SynSet synSet, int definitionLength, int exampleLength) {
            String text = "";
            if (definitionLength < 75){
                text = text + synSet.getDefinition();
            } else {
                text = text + synSet.getDefinition().substring(0, 75) + "...";
            }
            if (exampleLength < 75){
                text = text + " [" + synSet.getExample() + "]";
            } else {
                text = text + " [" + synSet.getExample().substring(0, 75) + "...]";
            }
            return text;
        }

        /**
         * Constructor for a panel. Creates a flag object consisting of flags for the root word. Creates a synset list
         * object. Creates synset panel for the first, second and fourth columns.
         * @param root Root word for which panel will be constructed.
         * @param row Row index for the word.
         */
        private PanelObject(String root, int row){
            this.root = root;
            word = (TxtWord) dictionary.getWord(root);
            flagObject = new FlagObject(word);
            synSetListObject = new SynSetListObject(domainWordNet, turkish, root, word);
            createFlagPanel(row);
            createSynSetPanel(1, row);
            createSynSetPanel(2, row);
            createSynSetPanel(4, row);
        }

    }

    /**
     * If the root word is in the display, the panel dedicated to that word is returned. Otherwise, a new panel is
     * constructed  for that root word and returned.
     * @param root Root word
     * @param row Row number for the new panel.
     * @return Panel dedicated to the root word (either it exists or created).
     */
    private PanelObject addIfNotExists(String root, int row){
        if (display.containsKey(root)){
            return display.get(root);
        } else {
            PanelObject panelObject = new PanelObject(root, row);
            display.put(root, panelObject);
            return panelObject;
        }
    }

    public class SynSetCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

        private JPanel getPanel(int row, int column){
            PanelObject panelObject = addIfNotExists(data.get(row), row);
            switch (column){
                case 1:
                    return panelObject.synSetIdPanel;
                case 2:
                    return panelObject.synSetPosPanel;
                case 4:
                    return panelObject.synSetEditPanel;
                default:
                    return null;
            }
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return getPanel(row, column);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return getPanel(row, column);
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    public class FlagCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            PanelObject panelObject = addIfNotExists(data.get(row), row);
            return panelObject.flagPanel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            PanelObject panelObject = addIfNotExists(data.get(row), row);
            return panelObject.flagPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    public class TableDataModel extends AbstractTableModel {

        /**
         * Returns number of columns.
         * @return Number of columns.
         */
        public int getColumnCount() {
            return 6;
        }

        /**
         * Retuns number of rows.
         * @return Number of rows.
         */
        public int getRowCount() {
            return data.size();
        }

        /**
         * Sets the column name of the table
         * @param col  the column being queried
         * @return The column name
         */
        public String getColumnName(int col) {
            switch (col){
                case 0:
                    return "No";
                case 1:
                    return "WordNet ID";
                case 2:
                    return "Pos";
                case 3:
                    return "Root";
                case 4:
                    return "Meaning";
                case 5:
                    return "Flags";
                default:
                    return "";
            }
        }

        /**
         * Returns the object at a given column.
         * @param col  the column being queried
         * @return The object at a given column.
         */
        public Class getColumnClass(int col){
            switch (col){
                case 1:
                case 2:
                case 4:
                    return SynSetListObject.class;
                case 5:
                    return FlagObject.class;
            }
            return Object.class;
        }

        /**
         * Returns the object at a given row and column.
         * @param row   row of cell
         * @param col  column of cell
         */
        public Object getValueAt(int row, int col) {
            PanelObject panelObject;
            int currentHeight, newHeight;
            switch (col){
                case 0:
                    return row + 1;
                case 1:
                case 2:
                case 4:
                    panelObject = addIfNotExists(data.get(row), row);
                    currentHeight = dataTable.getRowHeight(row);
                    newHeight = (panelObject.synSetListObject.synSets.size() + 1) * 35;
                    if (newHeight > currentHeight){
                        dataTable.setRowHeight(row, newHeight);
                    }
                    return panelObject.synSetListObject;
                case 3:
                    return data.get(row);
                case 5:
                    panelObject = addIfNotExists(data.get(row), row);
                    currentHeight = dataTable.getRowHeight(row);
                    if (panelObject.flagObject.flags != null){
                        newHeight = (panelObject.flagObject.flags.length + 1) * 35;
                        if (newHeight > currentHeight){
                            dataTable.setRowHeight(row, newHeight);
                        }
                    }
                    return panelObject.flagObject;
                default:
                    return "";
            }
        }

        /**
         * Returns true if the cell is editable, false otherwise.
         * @param row  the row being queried
         * @param col the column being queried
         * @return True if the cell is editable, false otherwise.
         */
        public boolean isCellEditable(int row, int col) {
            return col >= 3;
        }

        public void setValueAt(Object value, int row, int col) {
            if (col == 3) {
                data.set(row, (String) value);
            }
            fireTableCellUpdated(row, col);
        }
    }

    /**
     * Constructs the data list and example sentences hash map. Example sentences in the 'examples.txt' will be
     * inserted to the hash map. Each sentence is morphologically analyzed and disambiguated with the longest word
     * disambiguator to get the root words in the sentence. Then for each root word, the example sentence is inserted
     * for that root word's array list
     */
    private void constructExampleSentenceList(){
        FsmMorphologicalAnalyzer fsm;
        FsmParseList fsmParseList;
        HashMap<String, String> rootList = new HashMap<>();
        String root;
        data = new ArrayList<>();
        mappedSentences = new HashMap<>();
        for (int i = 0; i < dictionary.size(); i++){
            TxtWord word = (TxtWord) dictionary.getWord(i);
            if (word.isNominal() || word.isAdjective() || word.isAdverb() || word.isVerb()){
                data.add(word.getName());
                mappedSentences.put(word.getName(), new ArrayList<>());
            }
        }
        fsm = new FsmMorphologicalAnalyzer(dictionary);
        try {
            Scanner input = new Scanner(new File("examples.txt"), "utf-8");
            while (input.hasNextLine()){
                String line = input.nextLine();
                Sentence sentence = new Sentence(line);
                String[] words = line.split(" ");
                for (String word : words){
                    root = null;
                    if (rootList.containsKey(word)){
                        root = rootList.get(word);
                    } else {
                        fsmParseList = fsm.morphologicalAnalysis(word);
                        if (fsmParseList.size() > 0){
                            root = fsmParseList.getParseWithLongestRootWord().getWord().getName();
                            rootList.put(word, root);
                        }
                    }
                    if (root != null && mappedSentences.containsKey(root)){
                        ArrayList<Sentence> sentences = mappedSentences.get(root);
                        sentences.add(sentence);
                        mappedSentences.put(root, sentences);
                    }
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs the user interface of the frame. The following tasks are done:
     * <ul>
     *     <li>Sort and delete buttons are added.</li>
     *     <li>Example sentences and the words in the dictionary are inserted into mappedSentences and data fields
     *     respectively</li>
     *     <li>Data table is constructed, its column width are arranged and the renderer classes are attached</li>
     * </ul>
     */
    public void loadContents(){
        JButton idSort = new DrawingButton(DictionaryEditorFrame.class, this, "sortnumbers", ID_SORT, "Sort by WordNet Id");
        toolBar.add(idSort);
        JButton textSort = new DrawingButton(DictionaryEditorFrame.class, this, "sorttext", TEXT_SORT, "Sort by Word");
        toolBar.add(textSort);
        JButton deleteWord = new DrawingButton(DictionaryEditorFrame.class, this, "delete", DELETE, "Delete Word");
        toolBar.add(deleteWord);
        setName("Dictionary Editor");
        constructExampleSentenceList();
        display = new HashMap<>();
        String imgLocation = "/icons/addparent.png";
        URL imageURL = this.getClass().getResource(imgLocation);
        addIcon = new ImageIcon(imageURL);
        imgLocation = "/icons/delete.png";
        imageURL = this.getClass().getResource(imgLocation);
        deleteIcon = new ImageIcon(imageURL);
        dataTable = new JTable(new TableDataModel());
        dataTable.getColumnModel().getColumn(0).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(1).setMaxWidth(120);
        dataTable.getColumnModel().getColumn(2).setMaxWidth(90);
        dataTable.getColumnModel().getColumn(3).setMaxWidth(120);
        dataTable.getColumnModel().getColumn(5).setMaxWidth(200);
        FlagCell flagCell = new FlagCell();
        dataTable.setDefaultRenderer(FlagObject.class, flagCell);
        dataTable.setDefaultEditor(FlagObject.class, flagCell);
        SynSetCell synSetCell = new SynSetCell();
        dataTable.setDefaultRenderer(SynSetListObject.class, synSetCell);
        dataTable.setDefaultEditor(SynSetListObject.class, synSetCell);
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
        dataTable.getSelectionModel().addListSelectionListener(event -> {
            String word = data.get(dataTable.getSelectedRow());
            DefaultListModel<Sentence> listModel;
            if (mappedSentences.containsKey(word)){
                ArrayList<Sentence> sentences = mappedSentences.get(word);
                listModel = new DefaultListModel<>();
                for (Sentence sentence : sentences){
                    listModel.addElement(sentence);
                }
            } else {
                listModel = new DefaultListModel<>();
            }
            exampleList.setModel(listModel);
        });
        JPanel examplePanel = new JPanel(new BorderLayout());
        exampleList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(exampleList);
        examplePanel.add(scrollPane);
        add(examplePanel, BorderLayout.SOUTH);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        scrollPane.setVisible(true);
    }
}
