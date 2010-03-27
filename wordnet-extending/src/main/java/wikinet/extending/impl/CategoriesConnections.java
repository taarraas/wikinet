/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.extending.impl;

import wikinet.db.domain.Synset;
import wikinet.extending.ConnectionsMaker;

/**
 * There are categories in wiki. Use them to add new connections.
 */
public class CategoriesConnections implements ConnectionsMaker{

    @Override
    public void addConnections(Synset synset, String article) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
