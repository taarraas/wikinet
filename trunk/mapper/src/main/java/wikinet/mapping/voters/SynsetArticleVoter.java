package wikinet.mapping.voters;

import wikinet.wiki.parser.prototype.PagePrototype;

/**
 * @author taras, shyiko
 */
public interface SynsetArticleVoter {

    /**
     * @return similarity level from 0..1 including, where 1 means strongest relation
     */
    public double getVote(long synsetId, PagePrototype pagePrototype);
}
