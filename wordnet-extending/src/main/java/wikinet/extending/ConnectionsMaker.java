/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.extending;

import wikinet.db.domain.Synset;

/**
 *
 * @author taras
 */
public interface ConnectionsMaker {
    public void addConnections(Synset synset, String article);
}
