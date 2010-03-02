package wikinet.persistence.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import wikinet.persistence.dao.ConnectionDao;
import wikinet.persistence.dao.SynsetDao;
import wikinet.persistence.dao.WordDao;
import wikinet.persistence.domain.Connection;
import wikinet.persistence.domain.ConnectionPK;
import wikinet.persistence.domain.Synset;
import wikinet.persistence.domain.Word;
import wikinet.persistence.model.ConnectionType;
import wikinet.persistence.model.SynsetType;

import javax.persistence.EntityExistsException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-module-test.xml"})
public class ConnectionDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WordDao wordDao;

    @Autowired
    private ConnectionDao connectionDao;

    @Test
    public void testSave() {
        Synset first = getNewSavedSynset("word1");
        Synset second = getNewSavedSynset("word2");
        Connection connection = new Connection();
        connection.setConnectionType(ConnectionType.DEFAULT);
        connection.setFirstSynset(first);
        connection.setSecondSynset(second);
        connectionDao.save(connection);
        Connection foundConnection = connectionDao.findById(new ConnectionPK(first, second));
        assertEquals(foundConnection, connection);
    }

    @Test(expectedExceptions = {EntityExistsException.class})
    public void testExceptionOnSaveReverseDuplicate() {
        Synset first = getNewSavedSynset("word1");
        Synset second = getNewSavedSynset("word2");
        Connection connection = new Connection();
        connection.setConnectionType(ConnectionType.DEFAULT);
        connection.setFirstSynset(first);
        connection.setSecondSynset(second);
        connectionDao.save(connection);
        Connection connection2 = new Connection();
        connection2.setConnectionType(ConnectionType.DEFAULT);
        connection2.setFirstSynset(second);
        connection2.setSecondSynset(first);
        connectionDao.save(connection2);
    }

    @Test
    public void testConnectedSynsetsSearch() {
        Synset first = getNewSavedSynset("word1");
        Synset second = getNewSavedSynset("word2");
        Connection connection = new Connection();
        connection.setConnectionType(ConnectionType.DEFAULT);
        connection.setFirstSynset(first);
        connection.setSecondSynset(second);
        connectionDao.save(connection);
        Synset third = getNewSavedSynset("word3");
        Connection connection2 = new Connection();
        connection2.setConnectionType(ConnectionType.DEFAULT);
        connection2.setFirstSynset(third);
        connection2.setSecondSynset(first);
        connectionDao.save(connection2);
        System.out.println(Arrays.toString(connectionDao.findAll().toArray()));
        System.out.println(first);
        List<Synset> connectedSynsets = connectionDao.getConnectedSynsets(first);
        System.out.println(Arrays.toString(connectedSynsets.toArray()));
        assertEquals(connectedSynsets.size(), 2);
    }

    private Synset getNewSavedSynset(String sword) {
        Word word = new Word();
        word.setWord(sword);
        wordDao.save(word);
        Synset synset = new Synset();
        synset.setDescription("synset description " + sword);
        synset.setType(SynsetType.NOUN);
        synset.addWord(word);
        synsetDao.save(synset);
        return synset;
    }

}
