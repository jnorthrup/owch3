package net.sourceforge.nlp;

import java.util.*;

public class SentenceParser extends WordStore {
    public SentenceParser(String InputObjResource) throws Exception {
        super(InputObjResource);
    }

    public List tokenize(String line) {

        List ret = new ArrayList();
        StringTokenizer st = new StringTokenizer(line, ":,;'/ ");

        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            Map wm = getWordMap();
            Occurence w = (Occurence) wm.get(t);

            if (w == null) {
                wm.put(t, w = new Occurence());
            }
            else {
                w.incr();
            }
            ret.add(new Report(t, w));
        }
        return ret;
    };
}




