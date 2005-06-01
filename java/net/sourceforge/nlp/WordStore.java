package net.sourceforge.nlp;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class WordStore extends VerbKnowledge {
    private Map<String, Occurence> wordMap = new TreeMap<String, Occurence>();

    protected WordStore(String InputObjResource) throws Exception {
        read(InputObjResource);
    }

    public Map<String, Occurence> getWordMap() {
        return wordMap;
    }

    public void setWordMap(Map<String, Occurence> wordMap) {
        this.wordMap = wordMap;
    }

    public void read(String InputObjResource) throws FileNotFoundException, IOException, ClassNotFoundException {

        ObjectInputStream is = new ObjectInputStream(new FileInputStream(InputObjResource));

        Object[] w = (Object[])
                is.readObject();
        metaClause = (Map<String, String>) w[0];
        clauseVerb = (Map<String, Object[]>) w[1];
        if (w.length < 3) {
            wordMap = (Map<String, Occurence>) new TreeMap<String, Occurence>();
        } else {
            wordMap = (Map<String, Occurence>) w[2];
        }

    }

    public void write(String argv) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(argv));

            Object[] w = new Object[]{
                metaClause,
                clauseVerb,
                wordMap
            };

            os.writeObject(w);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




