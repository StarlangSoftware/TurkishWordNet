package WordNet;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class SynSetDefinitionComparator  implements Comparator<SynSet> {

    public int compare(SynSet synSetA, SynSet synSetB) {
        Locale locale = new Locale("tr");
        Collator collator = Collator.getInstance(locale);
        return collator.compare(synSetA.getDefinition().toLowerCase(locale), synSetB.getDefinition().toLowerCase(locale));
    }

}
