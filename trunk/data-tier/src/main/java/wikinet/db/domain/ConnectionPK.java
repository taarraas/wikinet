package wikinet.db.domain;

import wikinet.db.model.ConnectionType;

import java.io.Serializable;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
public class ConnectionPK implements Serializable {

    private long firstSynsetId;

    private long secondSynsetId;

    private ConnectionType connectionType;

    private int wordsFrom;

    private int wordsTo;

    protected ConnectionPK() {
    }

    public ConnectionPK(long firstSynsetId, long secondSynsetId, ConnectionType connectionType,
                        int wordsFrom, int wordsTo) {
        this.firstSynsetId = firstSynsetId;
        this.secondSynsetId = secondSynsetId;
        this.connectionType = connectionType;
        this.wordsFrom = wordsFrom;
        this.wordsTo = wordsTo;
    }

    public ConnectionPK(Synset firstSynset, Synset secondSynset, ConnectionType connectionType,
                        int wordsFrom, int wordsTo) {
        this.firstSynsetId = firstSynset.getId();
        this.secondSynsetId = secondSynset.getId();
        this.connectionType = connectionType;
        this.wordsFrom = wordsFrom;
        this.wordsTo = wordsTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionPK that = (ConnectionPK) o;

        if (firstSynsetId != that.firstSynsetId) return false;
        if (secondSynsetId != that.secondSynsetId) return false;
        if (wordsFrom != that.wordsFrom) return false;
        if (wordsTo != that.wordsTo) return false;
        if (connectionType != that.connectionType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (firstSynsetId ^ (firstSynsetId >>> 32));
        result = 31 * result + (int) (secondSynsetId ^ (secondSynsetId >>> 32));
        result = 31 * result + connectionType.hashCode();
        result = 31 * result + wordsFrom;
        result = 31 * result + wordsTo;
        return result;
    }
}
