package wikinet.wiki.domain;

import javax.persistence.*;
import java.util.List;

@NamedQueries(value = {
        @NamedQuery(name = "Category.findByName",
        query = "select c from Category c where c.name = :name")
})
/**
 * @author shyiko
 * @since Mar 30, 2010
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Category {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    @ManyToOne
    @JoinTable(name = "Category_Parent",
        joinColumns = @JoinColumn(name = "category_name", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "parent_category_name", referencedColumnName = "id"))
    private Category parent;

    @ManyToMany(mappedBy = "categories")
    private List<Page> pages;

    protected Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
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
