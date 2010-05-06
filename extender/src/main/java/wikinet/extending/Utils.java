package wikinet.extending;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author taras
 */
public class Utils {

    private static final String[] TOBE1 = {"was", "is", "are", "were"};
    public static final Set<String> TOBE = new TreeSet<String>();

    static {
        for (String string : TOBE1) {
            TOBE.add(string);
        }
    }
}
