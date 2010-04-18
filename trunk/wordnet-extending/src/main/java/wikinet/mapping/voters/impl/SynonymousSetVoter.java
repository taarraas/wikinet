/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping.voters.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Word;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author taras
 */
public class SynonymousSetVoter implements SynsetArticleVoter{
    private static final int MINIMALWORDSCOUNT = 3;

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private MappingUtils mappingUtils;

    @Override
    public double getVote(long synsetId, PagePrototype page) {
        List<Word> fromWordNet = synsetDao.findById(synsetId).getWords();
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
        int cnt = mappingUtils.intersect(a, b).size();
        if (cnt >= MINIMALWORDSCOUNT
                || fromWordNet.size()<MINIMALWORDSCOUNT
                || fromWiki.size() < MINIMALWORDSCOUNT) {
            return 1;
        } else {
            return 0;
        }
    }
    
}
