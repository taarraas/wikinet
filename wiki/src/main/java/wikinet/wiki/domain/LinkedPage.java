package wikinet.wiki.domain;

import javax.persistence.*;

/**
 * @author shyiko
 * @since Apr 5, 2010
 */
@Entity
public class LinkedPage {

    @Id
    @GeneratedValue
    private long id;

    private int startPos;

    private int length;

    @ManyToOne(optional = false)
    private Page page;

    protected LinkedPage() {
    }

    public LinkedPage(int startPos, int length, Page page) {
        this.startPos = startPos;
        this.length = length;
        this.page = page;
    }

    public long getId() {
        return id;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getLength() {
        return length;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkedPage that = (LinkedPage) o;

        if (id != that.id) return false;
        if (length != that.length) return false;
        if (startPos != that.startPos) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + startPos;
        result = 31 * result + length;
        return result;
    }
}
