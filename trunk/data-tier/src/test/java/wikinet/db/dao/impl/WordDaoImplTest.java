package wikinet.db.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import wikinet.db.dao.WordDao;
import wikinet.db.domain.Word;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-module-test.xml"})
public class WordDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private WordDao wordDao;

    @Test
    public void testSave() {
        Word word = new Word();
        word.setWord("word");
        wordDao.save(word);
        Word foundWord = wordDao.findById("word");
        assertEquals(foundWord, word);
    }

    @Test
    public void testMultipleSave() {
        Word word1 = new Word();
        word1.setWord("word1");
        wordDao.save(word1);
        Word word2 = new Word();
        word2.setWord("word2");
        wordDao.save(word2);
        List<Word> list = wordDao.findAll();
        assertEquals(list.size(), 2);
    }

    @Test
    public void testRemove() {
        Word word = new Word();
        word.setWord("word");
        wordDao.save(word);
        wordDao.delete(word);
        Word foundWord = wordDao.findById("word");
        assertNull(foundWord);
    }

}
