package wikinet.wiki.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
@Entity
public class Category {

    @Id
    private String name;

    @OneToOne
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
}
