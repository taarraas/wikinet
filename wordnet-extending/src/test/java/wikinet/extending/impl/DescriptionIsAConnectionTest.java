/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.extending.impl;

import junit.framework.TestCase;
import wikinet.db.domain.Synset;
import wikinet.wiki.ArticleReference;

/**
 *
 * @author taras
 */
public class DescriptionIsAConnectionTest extends TestCase {
    
    public DescriptionIsAConnectionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getMainWord method, of class DescriptionIsAConnection.
     */
    public void testGetMainWordWSD() {
        String text = "In computational linguistics, word sense disambiguation (WSD) is an open problem of natural language processing, which comprises the process of identifying which sense of a word (i.e. meaning) is used in any given sentence, when the word has a number of distinct senses (polysemy). Solution of this problem impacts such other tasks of computation linguistics, such as discourse, improving relevance of search engines, anaphora resolution, coherence, inference and others.";
        String expResult = "an open problem";
        String result = DescriptionIsAConnection.getMainWord(text);
        assertEquals(expResult, result);       
    }
    public void testGetMainWordKnownProblem() {
        String text = "In science and mathematics, an open problem or an open question is a known problem that can be accurately stated, and has not yet been solved (no solution for it is known). Notable examples of for-long open problems in mathematics, that have been solved and closed by researchers in the late twentieth century, are Fermat's Last Theorem[1] and the four color map theorem.";
        String expResult = "a known problem";
        String result = DescriptionIsAConnection.getMainWord(text);
        assertEquals(expResult, result);
    }
    public void testGetMainWordArchitect() {
        String text = "An architect is trained and licensed in the planning and designing of buildings, and participates in supervising the construction of a building.";
        String expResult = "";
        String result = DescriptionIsAConnection.getMainWord(text);
        assertEquals(expResult, result);
    }
}
