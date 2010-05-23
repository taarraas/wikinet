/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping.voter;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.PageDao;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.Utils;
import wikinet.mapping.Voter;
import wikinet.db.domain.Page;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author taras
 */
public class SynonymousSetVoter implements Voter {
    private static final int MINIMALWORDSCOUNT = 3;

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private Utils utils;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setMappingUtils(Utils utils) {
        this.utils = utils;
    }

    @Override
    public double getVote(Synset synset, Page page) {
        List<Word> fromWordNet = synset.getWords();
        Page p = pageDao.findByWordAndDisambiguation(page.getWord(), page.getDisambiguation());
        List<String> a = new LinkedList<String>();
        for (Word word : fromWordNet) {
            a.add(word.getWord());
        }
        Set<Page> fromWiki = p.getRedirects();
        List<String> b = new LinkedList<String>();
        for (Page pg : fromWiki) {
            b.add(pg.getWord());
        }
        int cnt = utils.intersect(a, b).size();
        if (cnt >= MINIMALWORDSCOUNT
                || fromWordNet.size()<MINIMALWORDSCOUNT
                || fromWiki.size() < MINIMALWORDSCOUNT) {
            return 1;
        } else {
            return 0;
        }
    }
    
}
