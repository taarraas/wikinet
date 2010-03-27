/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.extending.impl;

import wikinet.db.domain.Synset;
import wikinet.extending.ConnectionsMaker;

/**
 *
 * @author taras
 */


/**
 * First sentence of article says, that * %title% is a %something% *
 * Find what means %something%, synset for it, and add connection.
 */
public class DescriptionIsAConnection implements ConnectionsMaker{

    @Override
    public void addConnections(Synset synset, String article) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
