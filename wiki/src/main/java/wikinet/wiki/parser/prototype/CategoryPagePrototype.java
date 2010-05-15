package wikinet.wiki.parser.prototype;

import java.util.HashSet;
import java.util.Set;

/**
 * @author shyiko
 * @since Apr 16, 2010
 */
public class CategoryPagePrototype extends PagePrototype {

    private Set<String> parentCategories = new HashSet<String>();

    public CategoryPagePrototype(String title) {
        super(title);
    }

    public void addParentCategory(String category) {
        parentCategories.add(category);
    }

    public Set<String> getParentCategories() {
        return parentCategories;
    }
    
}