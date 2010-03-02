package wikinet.persistence.domain;

import wikinet.persistence.model.ConnectionType;

import javax.persistence.*;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
@Entity
@IdClass(ConnectionPK.class)
public class Connection {

    @Id
    @Column(name = "fsid", insertable = false, updatable = false)
    private long firstSynsetId;

    @Id
    @Column(name = "ssid", insertable = false, updatable = false)
    private long secondSynsetId;

    @ManyToOne
    @JoinColumn(name = "fsid")
    private Synset firstSynset;

    @ManyToOne
    @JoinColumn(name = "ssid")
    private Synset secondSynset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionType connectionType;

    private Integer wordsFrom;
    private Integer wordsTo;

    public Synset getFirstSynset() {
        return firstSynset;
    }

    public void setFirstSynset(Synset firstSynset) {
        this.firstSynset = firstSynset;
        this.firstSynsetId = firstSynset.getId();
    }

    public Synset getSecondSynset() {
        return secondSynset;
    }

    public void setSecondSynset(Synset secondSynset) {
        this.secondSynset = secondSynset;
        this.secondSynsetId = secondSynset.getId();
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public Integer getWordsFrom() {
        return wordsFrom;
    }

    public void setWordsFrom(Integer wordsFrom) {
        this.wordsFrom = wordsFrom;
    }

    public Integer getWordsTo() {
        return wordsTo;
    }

    public void setWordsTo(Integer wordsTo) {
        this.wordsTo = wordsTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (firstSynsetId != that.firstSynsetId) return false;
        if (secondSynsetId != that.secondSynsetId) return false;
        if (connectionType != that.connectionType) return false;
        if (wordsFrom != null ? !wordsFrom.equals(that.wordsFrom) : that.wordsFrom != null) return false;
        if (wordsTo != null ? !wordsTo.equals(that.wordsTo) : that.wordsTo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (firstSynsetId ^ (firstSynsetId >>> 32));
        result = 31 * result + (int) (secondSynsetId ^ (secondSynsetId >>> 32));
        result = 31 * result + connectionType.hashCode();
        result = 31 * result + (wordsFrom != null ? wordsFrom.hashCode() : 0);
        result = 31 * result + (wordsTo != null ? wordsTo.hashCode() : 0);
        return result;
    }
}
