package wikinet.persistence.domain;

import java.io.Serializable;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
public class ConnectionPK implements Serializable {

    private long firstSynsetId;

    private long secondSynsetId;

    public ConnectionPK() {
    }

    public ConnectionPK(long firstSynsetId, long secondSynsetId) {
        this.firstSynsetId = firstSynsetId;
        this.secondSynsetId = secondSynsetId;
    }

    public ConnectionPK(Synset firstSynset, Synset secondSynset) {
        this.firstSynsetId = firstSynset.getId();
        this.secondSynsetId = secondSynset.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionPK that = (ConnectionPK) o;

        if ((firstSynsetId == that.firstSynsetId && secondSynsetId == that.secondSynsetId) ||
            (firstSynsetId == that.secondSynsetId && secondSynsetId == that.firstSynsetId))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        long _firstSynsetId = Math.min(this.firstSynsetId, this.secondSynsetId);
        long _secondSynsetId = Math.max(this.firstSynsetId, this.secondSynsetId);;
        int result = (int) (_firstSynsetId ^ (_firstSynsetId >>> 32));
        result = 31 * result + (int) (_secondSynsetId ^ (_secondSynsetId >>> 32));
        return result;
    }
}
