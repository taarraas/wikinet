package wikinet.persistence.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import wikinet.persistence.dao.SynsetDao;
import wikinet.persistence.dao.WordDao;
import wikinet.persistence.domain.Synset;
import wikinet.persistence.domain.Word;
import wikinet.persistence.model.SynsetType;

import static org.testng.Assert.*;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-module-test.xml"})
public class SynsetDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WordDao wordDao;

    @Test
    public void testSave() {
        Word word = new Word();
        word.setWord("word");
        wordDao.save(word);
        Word word2 = new Word();
        word2.setWord("word2");
        wordDao.save(word2);
        Synset synset = new Synset();
        synset.setId(1);
        synset.setDescription("synset description");
        synset.setType(SynsetType.NOUN);
        synset.addWord(word);
        synset.addWord(word2);
        synsetDao.save(synset);
        Synset foundSynset = synsetDao.findById(1L);
        assertNotNull(foundSynset);
        assertEquals(foundSynset.getWords().size(), 2);
        foundSynset.getWords().remove(word2);
        synsetDao.save(foundSynset);
        Synset foundSynsetAfterWordRemove = synsetDao.findById(1L);
        assertEquals(foundSynsetAfterWordRemove.getWords().size(), 1);
    }

    @Test
    public void testMultipleSave() {
        Word word1 = new Word();
        word1.setWord("word1");
        wordDao.save(word1);
        Word word2 = new Word();
        word2.setWord("word2");
        wordDao.save(word2);
        Synset synset1 = new Synset();
        synset1.setDescription("synset1 description");
        synset1.setType(SynsetType.NOUN);
        synset1.addWord(word1);
        synsetDao.save(synset1);
        Synset synset2 = new Synset();
        synset2.setDescription("synset2 description");
        synset2.setType(SynsetType.NOUN);
        synset2.addWord(word2);
        synsetDao.save(synset2);
        assertEquals(synsetDao.findAll().size(), 2);
    }

    @Test
    public void testRemove() {
        Word word = new Word();
        word.setWord("word");
        wordDao.save(word);
        Synset synset = new Synset();
        synset.setId(1L);
        synset.setDescription("synset description");
        synset.setType(SynsetType.NOUN);
        synset.addWord(word);
        synsetDao.save(synset);
        synsetDao.delete(synset);
        Synset foundSynset = synsetDao.findById(1L);
        assertNull(foundSynset);
    }


}