/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.w2wmapping.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import wikinet.w2wmapping.SynsetMapper;

/**
 *
 * @author taras
 */
public class FirstSynsetMapper extends SynsetMapper{

    public FirstSynsetMapper() {
        setMinimalTrust(0.6, 0.1);
    }
}
