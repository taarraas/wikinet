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

    @OneToOne
    private Category parent;

    @ManyToMany(mappedBy = "categories")
    private List<Page> pages;

    public Category(String name) {
        this.name = name;
    }
}
