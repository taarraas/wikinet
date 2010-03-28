package wikinet.mapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shyiko
 * @since Mar 28, 2010
 */
public class MappingUtils {

    public Set<String> getWords(String text) {
        Set<String> words = new HashSet<String>();
        if (text == null)
            return words;
        words.addAll(Arrays.asList(text.split("\\w+")));
        return words;
    }

    public Set<String> union(Set<String> a, Set<String> b) {
        Set<String> common = new HashSet<String>();
        common.addAll(a);
        common.addAll(b);
        return common;
    }

    public Set<String> intersect(Set<String> a, Set<String> b) {
        Set<String> common = new HashSet<String>();
        for (String s : a) {
            if (b.contains(s))
                common.add(s);
        }
        return common;
    }


}
