/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package linguistics;

import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author taras
 */
public class Utils {
    private static final String[] TOBE1={"was", "is", "are", "were"};
    public static final Set<String> TOBE = new TreeSet<String>();
    static {
        for (String string : TOBE1) {
            TOBE.add(string);
        }
    }
}
