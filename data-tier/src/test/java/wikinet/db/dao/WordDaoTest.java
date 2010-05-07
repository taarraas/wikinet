package wikinet.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.db.domain.Category;
import wikinet.db.domain.LocalizedPage;
import wikinet.db.domain.Page;
import wikinet.db.domain.Word;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-dao-module-test.xml"})
public class WordDaoTest extends SpringDaoTest {

    @Autowired
    private WordDao wordDao;

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        sessionFactory.getCurrentSession().beginTransaction();
        for (Word word : wordDao.findAll()) {
            wordDao.delete(word);
        }
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Test
    public void testSave() {
        Word word = new Word("word");
        wordDao.save(word);
        Word foundWord = wordDao.findById("word");
        assertEquals(foundWord, word);
    }

    @Test
    public void testMultipleSave() {
        Word word1 = new Word("word1");
        wordDao.save(word1);
        Word word2 = new Word("word2");
        wordDao.save(word2);
        List<Word> list = wordDao.findAll();
        assertEquals(list.size(), 2);
    }

    @Test
    public void testRemove() {
        Word word = new Word("word");
        wordDao.save(word);
        wordDao.delete(word);
        Word foundWord = wordDao.findById("word");
        assertNull(foundWord);
    }

}
