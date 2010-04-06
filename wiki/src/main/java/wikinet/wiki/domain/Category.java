package wikinet.wiki.domain;

import javax.persistence.*;
import java.util.List;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
@Entity
public class Category {

    @Id
    private String name;

    @ManyToOne
    @JoinTable(name = "Category_Parent",
        joinColumns = @JoinColumn(name = "category_name", referencedColumnName = "name"),
        inverseJoinColumns = @JoinColumn(name = "parent_category_name", referencedColumnName = "name"))
    private Category parent;

    @ManyToMany(mappedBy = "categories")
    private List<Page> pages;

    protected Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }
    
}
