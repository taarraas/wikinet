/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wikinet.extending.impl;

import java.util.Map;
import junit.framework.TestCase;
import wikinet.extending.MainWord;
import wikinet.wiki.ArticleReference;

/**
 *
 * @author taras
 */
public class MainWordTest extends TestCase {

    public MainWordTest(String testName) {
        super(testName);
    }
    MainWord mainWord;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainWord = new MainWord();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetMainWordObahma() {
        String text = "Barack Hussein Obama II (Listeni /bəˈrɑːk huːˈseɪn oʊˈbɑːmə/ bə-RAHK hoo-SAYN oh-BAH-mə; born August 4, 1961) is the 44th and current President of the United States. ";
        String expResult = "the 44th and current President";
        String of="the United States";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
        //assertEquals(of, result.get(MainWord.PARTOF));
    }

    public void testGetMainWordNazi() {
        String text = "Nazi Germany and the Third Reich are the common English names for Germany under the government of Adolf Hitler and the National Socialist German Worker's Party (NSDAP), from 1933 to 1945.";
        String expResult = "the common English names";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
    }


    public void testGetMainWordWSD() {
        String text = "In computational linguistics, word sense disambiguation (WSD) is an open problem of natural language processing, which comprises the process of identifying which sense of a word (i.e. meaning) is used in any given sentence, when the word has a number of distinct senses (polysemy). Solution of this problem impacts such other tasks of computation linguistics, such as discourse, improving relevance of search engines, anaphora resolution, coherence, inference and others.";
        String expResult = "an open problem";
        String of="natural language processing";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
        //assertEquals(of, result.get(MainWord.PARTOF));

    }

    public void testGetMainWordKnownProblem() {
        String text = "In science and mathematics, an open problem or an open question is a known problem that can be accurately stated, and has not yet been solved (no solution for it is known). Notable examples of for-long open problems in mathematics, that have been solved and closed by researchers in the late twentieth century, are Fermat's Last Theorem[1] and the four color map theorem.";
        String expResult = "a known problem";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
    }

    public void testGetMainWordArchitect() {
        String text = "An architect is trained and licensed in the planning and designing of buildings, and participates in supervising the construction of a building.";
        String expResult = null;
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
    }

    public void testGetMainWordLandform() {
        String text = "In the earth sciences and geology sub-fields, a landform or physical feature comprises a geomorphological unit, and is largely defined by its surface form and location in the landscape, as part of the terrain, and as such, is typically an element of topography.";
        String expResult = null;
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        //assertEquals(expResult, result.get(MainWord.MAINWORD));
    }

    public void testGetMainWordUSA() {
        String text = "The United States of America (commonly referred to as the United States, the U.S., the USA, or America [/əm'erɪkə/]) is a federal constitutional republic comprising fifty states and a federal district.";
        String expResult = "a federal constitutional republic";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
    }

    public void testGetMainWordAre() {
        String text = "Native Americans in the United States are the indigenous peoples from North America now encompassed by the continental United States, including parts of Alaska and the island state of Hawaii.";
        String expResult = "the indigenous peoples";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
    }

    public void testGetMainWordChina() {
        String text = "China is seen variously as an ancient civilization extending over a large area in East Asia, a nation and/or a multinational entity.";
        String expResult = "an ancient civilization";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        //assertEquals(expResult, result.get(MainWord.MAINWORD));
    }

    public void testGetMainWordChech() {
        String text = "Czechoslovakia or Czecho-Slovakia[1] (Československo or Česko-Slovensko[2]) was a sovereign state in Central Europe which existed from October 1918, when it declared its independence from the Austro-Hungarian Empire, until 1992";
        String expResult = "a sovereign state";
        System.out.println(expResult);
        Map<String, String> result = mainWord.getInfo(text);
        Assert.assertEquals(expResult, result.get(MainWord.MAINWORD));
    }
}
