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

    protected ConnectionPK() {
    }

    public ConnectionPK(long firstSynsetId, long secondSynsetId, ConnectionType connectionType) {
        this.firstSynsetId = firstSynsetId;
        this.secondSynsetId = secondSynsetId;
        this.connectionType = connectionType;
    }

    public ConnectionPK(Synset firstSynset, Synset secondSynset, ConnectionType connectionType) {
        this.firstSynsetId = firstSynset.getId();
        this.secondSynsetId = secondSynset.getId();
        this.connectionType = connectionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionPK that = (ConnectionPK) o;

        if (firstSynsetId != that.firstSynsetId) return false;
        if (secondSynsetId != that.secondSynsetId) return false;
        if (connectionType != that.connectionType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (firstSynsetId ^ (firstSynsetId >>> 32));
        result = 31 * result + (int) (secondSynsetId ^ (secondSynsetId >>> 32));
        result = 31 * result + connectionType.hashCode();
        return result;
    }
}
