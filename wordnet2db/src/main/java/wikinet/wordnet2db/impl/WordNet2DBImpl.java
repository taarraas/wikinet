/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.wordnet2db.impl;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.dao.WordDao;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.model.SynsetType;
import wikinet.wordnet2db.WordNet2DB;

/**
 *
 * @author taras
 */
public class WordNet2DBImpl implements WordNet2DB {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WordDao wordDao;

    Word saveWord(String word) {
        Word w;
        if ((w=wordDao.findById(word))==null) {
            w=new Word();
            w.setWord(word);
            wordDao.save(w);
        };
        return w;
    }
    void saveSynset(Synset synset, List<String> words) {
        Synset syns=synsetDao.findById(synset.getId());
        synset.setId(syns.getId());
        List<Word> wordlist=new LinkedList<Word>();
        for (String string : words) {
            wordlist.add(saveWord(string));
        }
        //synset.setWords(wordlist); //uncoment after realizing
        synsetDao.save(synset);
    }

    /**
     * add connection between from and to. toWord must be null and will be processed later.
     * if there is no synset with id idTo, then create it with no data
     * @param from
     * @param idTo
     * @param pointer_symbol
     * @param fromWordNo
     * @param toWordNo
     * @param fromWord
     */
    void addConnection(Synset from, long idTo, String pointer_symbol, int fromWordNo, int toWordNo, String fromWord) {
        //TODO
    }
    SynsetType getTypeByLetter(String letter) {
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
                default : {
                    return null;
                }
            }
    }
    void parse(String pathToData) throws IOException{
        BufferedReader br=new BufferedReader(new FileReader(pathToData));
        String str;
        while ((str=br.readLine())!=null) {
            if (str.startsWith("  ")) {
                continue;
            }
            String[] el=str.split(" ");
            Synset synset = new Synset();
            synset.setId(Integer.parseInt(el[0], 10));
            synset.setLexFileNum(el[1]);
            synset.setType(getTypeByLetter(el[2]));

            int w_cnt=Integer.getInteger(el[3], 16);
            List<String> words=new LinkedList<String>();
            for (int i = 0; i < w_cnt; i++) {
                words.add(el[4+i*2]);
            }
            synset.setDescription(str.split("|")[1]);
            saveSynset(synset, words);

            int basep=4+w_cnt*2;
            int p_cnt=Integer.getInteger(el[basep], 10);
            for (int i = 0; i < p_cnt; i++) {
                String pointer_symbol=el[basep+1+i*4],
                        synset_offset_tar=el[basep+2+i*4],
                        pos=el[basep+3+i*4],
                        source_target=el[basep+4+i*4];
                int nosource=Integer.valueOf(source_target.substring(0, 2), 16),
                        nodestination=Integer.valueOf(source_target.substring(2), 16);

                addConnection(synset, Integer.parseInt(synset_offset_tar,10),
                        pointer_symbol, nosource, nodestination, nosource==0?null:words.get(nosource-1));
            }
        }

    }
    @Override
    public void importIt(String pathToWordnet) throws IOException{
        parse(pathToWordnet+"data.adj");
        parse(pathToWordnet+"data.adv");
        parse(pathToWordnet+"data.noun");
        parse(pathToWordnet+"data.verb");
    }

}
