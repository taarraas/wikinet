package wikinet.persistence.domain;

import java.io.Serializable;

/**
 * @author shyiko
 * @since Feb 27, 2010
 */
class ConnectionPK implements Serializable {

    private Synset firstSynset;
    private Synset secondSynset;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionPK that = (ConnectionPK) o;

        if ((firstSynset.equals(that.firstSynset) && secondSynset.equals(that.secondSynset)) ||
            (firstSynset.equals(that.secondSynset) && secondSynset.equals(that.firstSynset)))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        int result = firstSynset.hashCode();
        result = 31 * result + secondSynset.hashCode();
        return result;
    }
}
