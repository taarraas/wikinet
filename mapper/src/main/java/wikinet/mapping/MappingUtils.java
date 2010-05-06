package wikinet.mapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author shyiko, taras
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

    public Set<String> union(Collection<String> a, Collection<String> b) {
        Set<String> common = new HashSet<String>();
        common.addAll(a);
        common.addAll(b);
        return common;
    }

    public Set<String> intersect(Collection<String> a, Collection<String> b) {
        Set<String> common = new HashSet<String>();
        for (String s : a) {
            if (b.contains(s))
                common.add(s);
        }
        return common;
    }
    /**
     * Intersect fuzzy sets
     * @param a
     * @param b
     * @return
     */
    public Map<String, Double> intersect(Map<String, Double> a, Map<String, Double> b) {
        Map<String, Double> common = new TreeMap<String, Double>();
        for (Map.Entry<String, Double> entry : a.entrySet()) {
            if (b.containsKey(entry.getKey())) {
                common.put(entry.getKey(),
                        Math.min(entry.getValue(), b.get(entry.getKey())));
            }
        }
        return common;
    }

    public double sizeOfSet(Map<String, Double> a) {
        double size=0;
        for (Map.Entry<String, Double> entry : a.entrySet()) {
            size += entry.getValue();
        }
        return size;
    }


}
