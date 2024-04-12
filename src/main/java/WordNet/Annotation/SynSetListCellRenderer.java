package WordNet.Annotation;

import WordNet.SynSet;

import javax.swing.*;
import java.awt.*;

public class SynSetListCellRenderer extends DefaultListCellRenderer {


    /**
     * Sets the tooltip for a list item that contains a synset. The tooltip will show all the literals of the synset
     * separated via '|'. For example for the synset kırmızı, it will display 'kırmızı|al'.
     * @param list The JList we're painting.
     * @param value The value returned by list.getModel().getElementAt(index).
     * @param index The cells index.
     * @param isSelected True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return New component with tooltip text.
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof SynSet){
            StringBuilder literals = new StringBuilder(((SynSet) value).representative());
            for (int i = 1; i < ((SynSet) value).getSynonym().literalSize(); i++){
                literals.append("|").append(((SynSet) value).getSynonym().getLiteral(i).getName());
            }
            ((JComponent) cell).setToolTipText(literals.toString());
        }
        return this;
    }
}
