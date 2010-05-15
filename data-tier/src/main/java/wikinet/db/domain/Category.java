package wikinet.db.domain;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    @Index(name = "CategoryNameIDX")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Category_Subcategory",
        joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "subcategory_id", referencedColumnName = "id"))
    private List<Category> subcategories = new LinkedList<Category>();

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<Page> pages = new LinkedList<Page>();

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

    public List<Category> getSubcategories() {
        return subcategories;
    }

    public List<Page> getPages() {
        return pages;
    }
}
