package WordNet.Annotation;

import Dictionary.TxtWord;

import java.util.Arrays;

public class FlagObject {
    String[] flags = null;

    /**
     * Constructor for FlagObject that creates a set of flags from word. Each word in the TxtDictionary has a
     * representation as word name and a set of flags separated with space such as
     * su CL_ISIM
     * kitap CL_ISIM ID_SD
     * @param word Word for which flags will be extracted.
     */
    public FlagObject(TxtWord word){
        if (word != null){
            String[] items = word.toString().split(" ");
            flags = Arrays.copyOfRange(items, 1, items.length);
        }
    }


}
