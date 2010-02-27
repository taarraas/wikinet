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
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="firstSynset", referencedColumnName="id"),
        @JoinColumn(name="secondSynset", referencedColumnName="id")
    })
    private Synset firstSynset;
    @Id
    private Synset secondSynset;
    @Column(nullable = false)
    private ConnectionType connectionType;

    public Synset getFirstSynset() {
        return firstSynset;
    }

    public void setFirstSynset(Synset firstSynset) {
        this.firstSynset = firstSynset;
    }

    public Synset getSecondSynset() {
        return secondSynset;
    }

    public void setSecondSynset(Synset secondSynset) {
        this.secondSynset = secondSynset;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }
}
