package wikinet.wordnet2db.impl;

import com.mysql.jdbc.Clob;
import com.mysql.jdbc.JDBC4NClob;
import wikinet.db.dao.ConnectionDao;
import wikinet.db.dao.SynsetDao;
import wikinet.db.dao.WordDao;
import wikinet.db.domain.Connection;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.model.ConnectionType;
import wikinet.db.model.SynsetType;
import wikinet.wordnet2db.WordNet2DB;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.lob.ClobImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author taras, shyiko
 */
public class WordNet2DBImpl implements WordNet2DB {

    private static final Logger logger = Logger.getLogger(Class.class);
    private static final String[] FILELIST={"data.noun", "data.adj", "data.adv", "data.verb"};

    private SynsetDao synsetDao;

    private WordDao wordDao;
    
    private ConnectionDao connectionDao;

    SessionFactory sessionFactory;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setWordDao(WordDao wordDao) {
        this.wordDao = wordDao;
    }

    public void setConnectionDao(ConnectionDao connectionDao) {
        this.connectionDao = connectionDao;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void importDirectory(String pathToWordnet) throws IOException {
    System.out.println("3");
    File path = new File(pathToWordnet);
        if (!path.exists())
            throw new FileNotFoundException(pathToWordnet);
        logger.info("Starting parsing");
    System.out.println("4");
        for (String fileName : FILELIST) {
    System.out.println("5");
                parseSynsets(new File(path, fileName));
            logger.info("Synsets type "+fileName+" parsed");
        }
        for (String fileName : FILELIST) {
            parseConnections(new File(path, fileName));
            logger.info("Connections for synsets type "+fileName+" parsed");
        }
    }

    private String normaliseWord(String word) {
        return word.replace('_', ' ');
    }
    private Word saveWord(String word) {
        word = normaliseWord(word);
        Word w;
        if ((w = wordDao.findById(word)) == null) {
            w = new Word(word);
            wordDao.save(w);
        }
        return w;
    }

    private void saveSynset(Synset synset, List<String> words) {
       Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        try {
            List<Word> wordlist = new LinkedList<Word>();
            for (String string : words) {
                wordlist.add(saveWord(string));
            }
            synset.setWords(wordlist);
            synsetDao.save(synset);
            session.getTransaction().commit();
        } catch (Exception ex) {
            logger.error(synset, ex);
            session.getTransaction().rollback();
        }
    }

    private void addConnection(long from, long to, String pointer_symbol, int fromWordNo, int toWordNo) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        try {
            Connection connection = new Connection(synsetDao.findById(from),
                    synsetDao.findById(to),
                    ConnectionType.parse(pointer_symbol),
                    fromWordNo,
                    toWordNo);
            connectionDao.save(connection);
            session.getTransaction().commit();
        } catch (Exception ex) {
            logger.error(from+"->"+to, ex);
            session.getTransaction().rollback();
        }
    }

    private SynsetType getTypeByLetter(String letter) {
        switch (letter.charAt(0)) {
            case 'n': {
                return SynsetType.NOUN;
            }
            case 'v': { 
                return SynsetType.VERB;
            }
            case 'a': {
                return SynsetType.ADJ;
            }
            case 's': {
                return SynsetType.ADJ_SATELITE;
            }
            case 'r': {
                return SynsetType.ADV;
            }
            default: {
                return null;
            }
        }
    }
    private int getSegByLetter(String letter) {
        switch (letter.charAt(0)) {
            case 'n': {
                return 0;
            }
            case 'v': {
                return 1;
            }
            case 'a': {
                return 2;
            }
            case 's': {
                return 2;
            }
            case 'r': {
                return 3;
            }
            default: {
                return -1;
            }
        }
    }

    private void parseSynsets(File pathToData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathToData));
        String str;
        int cnt=0;
        while ((str = br.readLine()) != null) {
            if (str.startsWith("  ")) {
                continue;
            }
            cnt++;
            if (cnt%1000 == 0) {
                logger.info(cnt+" synsets of file "+pathToData+" parsed");
            }
            String[] el = str.split(" ");
            long key=Long.parseLong(el[0], 10) + 100000000 * getSegByLetter(el[2]);
            Synset synset = new Synset(key, str.split("\\|")[1], getTypeByLetter(el[2]));
            synset.setLexFileNum(el[1]);

            int w_cnt = Integer.parseInt(el[3], 16);
            List<String> words = new LinkedList<String>();
            for (int i = 0; i < w_cnt; i++) {
                words.add(el[4 + i * 2]);
            }
            saveSynset(synset, words);
        }
    }

    private void parseConnections(File pathToData) throws IOException {
             BufferedReader br = new BufferedReader(new FileReader(pathToData));
        String str;
        int cnt=0;
        while ((str = br.readLine()) != null) {
            if (str.startsWith("  ")) {
                continue;
            }
            cnt++;
            if (cnt%1000 == 0) {
                logger.info(cnt+" synsets of file "+pathToData+" parsed");
            }
            String[] el = str.split(" ");
            long key=Long.parseLong(el[0], 10) + 100000000 * getSegByLetter(el[2]);
            int w_cnt = Integer.parseInt(el[3], 16);
            int basep = 4 + w_cnt * 2;
            int p_cnt = Integer.parseInt(el[basep], 10);
            for (int i = 0; i < p_cnt; i++) {
                String pointer_symbol = el[basep + 1 + i * 4],
                        synset_offset_tar = el[basep + 2 + i * 4],
                        pos = el[basep + 3 + i * 4],
                        source_target = el[basep + 4 + i * 4];
                int nosource = Integer.parseInt(source_target.substring(0, 2), 16),
                        nodestination = Integer.parseInt(source_target.substring(2), 16);
                long keyTo=Long.parseLong(synset_offset_tar, 10)
                        + 100000000 * getSegByLetter(pos);
                addConnection(key, keyTo, pointer_symbol, nosource, nodestination);
            }
        }
    }

}
