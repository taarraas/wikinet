package wikinet.db.domain;

import wikinet.db.model.ConnectionType;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = "Connection.getConnections",
                    query = "select c from Connection c where c.firstSynset = :firstSynsetId and c.secondSynset = :secondSynsetId")
})

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

    protected Connection() {
    }

    public Connection(Synset firstSynset, Synset secondSynset, ConnectionType connectionType) {
        this.firstSynset = firstSynset;
        this.firstSynsetId = firstSynset.getId();
        this.secondSynset = secondSynset;
        this.secondSynsetId = secondSynset.getId();
        this.connectionType = connectionType;
    }

    public Synset getFirstSynset() {
        return firstSynset;
    }

    public Synset getSecondSynset() {
        return secondSynset;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
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

    @Override
    public String toString() {
        return "Connection{" +
                "firstSynsetId=" + firstSynsetId +
                ", secondSynsetId=" + secondSynsetId +
                ", connectionType=" + connectionType +
                ", wordsFrom=" + wordsFrom +
                ", wordsTo=" + wordsTo +
                '}';
    }
}
