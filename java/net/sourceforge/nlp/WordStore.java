package net.sourceforge.nlp;

import java.io.*;
import java.util.*;

public class WordStore extends VerbKnowledge {
    private Map wordMap = new TreeMap();

    protected WordStore(String InputObjResource) throws Exception {
        read(InputObjResource);
    }

    public Map getWordMap() {
        return wordMap;
    }

    public void setWordMap(Map wordMap) {
        this.wordMap = wordMap;
    }

    public void read(String InputObjResource) throws FileNotFoundException, IOException, ClassNotFoundException {

        ObjectInputStream is = new ObjectInputStream(new FileInputStream(InputObjResource));

        Object[] w = (Object[])
                is.readObject();
        metaClause = (Map) w[0];
        clauseVerb = (Map) w[1];
        if (w.length < 3) {
            wordMap = (Map) new TreeMap();
        }
        else {
            wordMap = (Map) w[2];
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}




