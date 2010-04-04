package wikinet.db.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import wikinet.db.dao.ConnectionDao;
import wikinet.db.dao.SynsetDao;
import wikinet.db.dao.WordDao;
import wikinet.db.domain.Connection;
import wikinet.db.domain.ConnectionPK;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.model.ConnectionType;
import wikinet.db.model.SynsetType;

import javax.persistence.EntityExistsException;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-dao-module-test.xml"})
public class ConnectionDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WordDao wordDao;

    @Autowired
    private ConnectionDao connectionDao;

    @Test
    public void testSave() {
        Synset first = getNewSavedSynset(1L, "word1");
        Synset second = getNewSavedSynset(2L, "word2");
        Connection connection = new Connection(first, second, ConnectionType.ATTRIBUTE);
        connectionDao.save(connection);
        Connection foundConnection = connectionDao.findById(new ConnectionPK(first, second));
        assertEquals(foundConnection, connection);
    }

    @Test(expectedExceptions = {EntityExistsException.class})
    public void testExceptionOnSaveReverseDuplicate() {
        Synset first = getNewSavedSynset(1L, "word1");
        Synset second = getNewSavedSynset(2L, "word2");
        Connection connection = new Connection(first, second, ConnectionType.ATTRIBUTE);
        connectionDao.save(connection);
        Connection connection2 = new Connection(second, first, ConnectionType.ATTRIBUTE);
        connectionDao.save(connection2);
    }

    @Test
    public void testConnectedSynsetsSearch() {
        Synset first = getNewSavedSynset(1L, "word1");
        Synset second = getNewSavedSynset(2L, "word2");
        Connection connection = new Connection(first, second, ConnectionType.ATTRIBUTE);
        connectionDao.save(connection);
        Synset third = getNewSavedSynset(3L, "word3");
        Connection connection2 = new Connection(third, first, ConnectionType.ATTRIBUTE);
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
