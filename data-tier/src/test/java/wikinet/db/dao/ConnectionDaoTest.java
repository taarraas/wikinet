package wikinet.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.db.domain.Connection;
import wikinet.db.domain.ConnectionPK;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.model.ConnectionType;
import wikinet.db.model.SynsetType;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-dao-module-test.xml"})
public class ConnectionDaoTest extends SpringDaoTest {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WordDao wordDao;

    @Autowired
    private ConnectionDao connectionDao;

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        sessionFactory.getCurrentSession().beginTransaction();
        for (Connection connection : connectionDao.findAll()) {
            connectionDao.delete(connection);
        }
        for (Synset synset : synsetDao.findAll()) {
            synsetDao.delete(synset);
        }
        for (Word word : wordDao.findAll()) {
            wordDao.delete(word);
        }
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Test
    public void testSave() {
        Synset first = getNewSavedSynset(1L, "word1");
        Synset second = getNewSavedSynset(2L, "word2");
        Connection connection = new Connection(first, second, ConnectionType.ATTRIBUTE, 1, 2);
        connectionDao.save(connection);
        Connection foundConnection = connectionDao.findById(new ConnectionPK(first, second, ConnectionType.ATTRIBUTE, 1, 2));
        assertEquals(foundConnection, connection);
    }

    @Test
    public void testConnectedSynsetsSearch() {
        Synset first = getNewSavedSynset(1L, "word1");
        Synset second = getNewSavedSynset(2L, "word2");
        Connection connection = new Connection(first, second, ConnectionType.ATTRIBUTE, 1, 2);
        connectionDao.save(connection);
        Synset third = getNewSavedSynset(3L, "word3");
        Connection connection2 = new Connection(third, first, ConnectionType.ATTRIBUTE, 2, 3);
        connectionDao.save(connection2);
        List<Synset> connectedSynsets = synsetDao.getConnected(first);
        assertEquals(connectedSynsets.size(), 1);
    }

    private Synset getNewSavedSynset(long id, String sword) {
        Word word = new Word(sword);
        wordDao.save(word);
        Synset synset = new Synset(id, "desc " + sword, SynsetType.NOUN);
        synset.addWord(word);
        synsetDao.save(synset);
        return synset;
    }

}