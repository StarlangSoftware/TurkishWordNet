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
                    if (synSets1.size() != 0) {
                        if (synSets2.size() == 0) {
                            return 1;
                        } else {
                            return synSets1.get(0).getId().compareTo(synSets2.get(0).getId());
                        }
                    } else {
                        if (synSets2.size() == 0) {
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

    public class FlagObject{
        String[] flags = null;

        private FlagObject(TxtWord word){
            if (word != null){
                String[] items = word.toString().split(" ");
                flags = Arrays.copyOfRange(items, 1, items.length);
            }
        }
    }

    public class SynSetObject{
        ArrayList<SynSet> synSets;
        ArrayList<SynSet> extraSynSets = new ArrayList<>();

        private SynSetObject(String root, TxtWord word){
            Transition verbTransition = new Transition("mAk");
            if (word != null){
                String verbForm = verbTransition.makeTransition(word, word.getName());
                synSets = domainWordNet.getSynSetsWithLiteral(word.getName());
                synSets.addAll(domainWordNet.getSynSetsWithLiteral(verbForm));
                ArrayList<SynSet> candidates = turkish.getSynSetsWithLiteral(word.getName());
                for (SynSet synSet : candidates){
                    if (!synSets.contains(synSet)){
                        extraSynSets.add(synSet);
                    }
                }
                candidates = turkish.getSynSetsWithLiteral(verbForm);
                for (SynSet synSet : candidates){
                    if (!synSets.contains(synSet)){
                        extraSynSets.add(synSet);
                    }
                }
            } else {
                synSets = domainWordNet.getSynSetsWithLiteral(root);
                ArrayList<SynSet> candidates = turkish.getSynSetsWithLiteral(root);
                for (SynSet synSet : candidates){
                    if (!synSets.contains(synSet)){
                        extraSynSets.add(synSet);
                    }
                }
            }
        }
    }

    private class PanelObject{
        private FlagObject flagObject;
        private SynSetObject synSetObject;
        private TxtWord word;
        private String root;
        private JPanel flagPanel;
        private JPanel synSetIdPanel;
        private JPanel synSetPosPanel;
        private JPanel synSetEditPanel;

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
            JButton add = new JButton();
            add.setIcon(addIcon);
            c.gridx = 0;
            add.addActionListener(e -> {
                if (!root.contains(" ")){
                    dictionary.addWithFlag(root, (String) flagComboBox.getSelectedItem());
                    modified = true;
                    PanelObject panelObject = new PanelObject(root, row);
                    display.put(root, panelObject);
                }
            });
            flagPanel.add(add, c);
            c.gridx = 1;
            flagPanel.add(flagComboBox, c);
        }

        private void createSynSetPanel(int column, int row){
            JPanel newPanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = 0;
            c.gridx = 0;
            for (SynSet synSet : synSetObject.synSets){
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
                            text.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (!text.getText().equalsIgnoreCase("No Definition")){
                                        synSet.setDefinition(text.getText());
                                    }
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
                for (SynSet synSet : synSetObject.extraSynSets){
                    int definitionLength = 0, exampleLength = 0;
                    if (synSet.getDefinition() != null){
                        definitionLength = synSet.getDefinition().length();
                    }
                    if (synSet.getExample() != null){
                        exampleLength = synSet.getExample().length();
                        if (definitionLength + exampleLength < 150){
                            synSetChooser.addItem(synSet.getDefinition() + " [" + synSet.getExample() + "]");
                        } else {
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
                if (synSetChooser.getItemCount() > 0){
                    JButton add = new JButton();
                    add.setIcon(addIcon);
                    add.addActionListener(e -> {
                        if (synSetChooser.getSelectedIndex() != -1){
                            int extraRows = 0;
                            for (int i = 0; i < synSetChooser.getItemCount(); i++){
                                if (((String) synSetChooser.getItemAt(i)).startsWith("New SynSet")){
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
                                String newSynSetId = wordNetPrefix + "" + String.format("%07d", finalId);
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
                                addedSynSet = synSetObject.extraSynSets.get(synSetChooser.getSelectedIndex() - extraRows);
                                if (addedSynSet.getPos().equals(Pos.VERB)){
                                    Transition verbTransition = new Transition("mAk");
                                    String verbForm = verbTransition.makeTransition(word, word.getName());
                                    addSynSet(addedSynSet, verbForm);
                                } else {
                                    addSynSet(addedSynSet, root);
                                }
                            }
                            modified = true;
                            PanelObject panelObject = new PanelObject(root, row);
                            display.put(root, panelObject);
                        }
                    });
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

        private PanelObject(String root, int row){
            this.root = root;
            word = (TxtWord) dictionary.getWord(root);
            flagObject = new FlagObject(word);
            synSetObject = new SynSetObject(root, word);
            createFlagPanel(row);
            createSynSetPanel(1, row);
            createSynSetPanel(2, row);
            createSynSetPanel(4, row);
        }
    }

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

        public int getColumnCount() {
            return 6;
        }

        public int getRowCount() {
            return data.size();
        }

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

        public Class getColumnClass(int col){
            switch (col){
                case 1:
                case 2:
                case 4:
                    return SynSetObject.class;
                case 5:
                    return FlagObject.class;
            }
            return Object.class;
        }

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
                    newHeight = (panelObject.synSetObject.synSets.size() + 1) * 35;
                    if (newHeight > currentHeight){
                        dataTable.setRowHeight(row, newHeight);
                    }
                    return panelObject.synSetObject;
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

    public void loadContents(){
        FsmMorphologicalAnalyzer fsm;
        FsmParseList fsmParseList;
        HashMap<String, String> rootList = new HashMap<>();
        String root;
        JButton idSort = new DrawingButton(DictionaryEditorFrame.class, this, "sortnumbers", ID_SORT, "Sort by WordNet Id");
        toolBar.add(idSort);
        JButton textSort = new DrawingButton(DictionaryEditorFrame.class, this, "sorttext", TEXT_SORT, "Sort by Word");
        toolBar.add(textSort);
        JButton deleteWord = new DrawingButton(DictionaryEditorFrame.class, this, "delete", DELETE, "Delete Word");
        toolBar.add(deleteWord);
        setName("Dictionary Editor");
        display = new HashMap<>();
        data = new ArrayList<>();
        mappedSentences = new HashMap<>();
        for (int i = 0; i < dictionary.size(); i++){
            TxtWord word = (TxtWord) dictionary.getWord(i);
            if (word.isNominal() || word.isAdjective() || word.isAdverb() || word.isVerb()){
                data.add(word.getName());
                mappedSentences.put(word.getName(), new ArrayList<Sentence>());
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
        String imgLocation = "/icons/addparent.png";
        URL imageURL = this.getClass().getResource(imgLocation);
        addIcon = new ImageIcon(imageURL);
        imgLocation = "/icons/delete.png";
        imageURL = this.getClass().getResource(imgLocation);
        deleteIcon = new ImageIcon(imageURL);
        dataTable = new JTable(new TableDataModel());
        dataTable.getColumnModel().getColumn(0).setMinWidth(60);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(1).setMinWidth(120);
        dataTable.getColumnModel().getColumn(1).setMaxWidth(120);
        dataTable.getColumnModel().getColumn(2).setMinWidth(90);
        dataTable.getColumnModel().getColumn(2).setMaxWidth(90);
        dataTable.getColumnModel().getColumn(3).setMinWidth(120);
        dataTable.getColumnModel().getColumn(3).setMaxWidth(120);
        dataTable.getColumnModel().getColumn(5).setMinWidth(200);
        dataTable.getColumnModel().getColumn(5).setMaxWidth(200);
        FlagCell flagCell = new FlagCell();
        dataTable.setDefaultRenderer(FlagObject.class, flagCell);
        dataTable.setDefaultEditor(FlagObject.class, flagCell);
        SynSetCell synSetCell = new SynSetCell();
        dataTable.setDefaultRenderer(SynSetObject.class, synSetCell);
        dataTable.setDefaultEditor(SynSetObject.class, synSetCell);
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
