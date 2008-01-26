package net.sourceforge.nlp;

import java.util.*;

public class SentenceParser extends WordStore {
    public SentenceParser(String InputObjResource) throws Exception {
        super(InputObjResource);
    }

    public List<Report> tokenize(String line) {

        List<Report> ret = new ArrayList<Report>();
        StringTokenizer st = new StringTokenizer(line, ":,;'/ ");

        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            Map<String, Occurence> wm = getWordMap();
            Occurence w = wm.get(t);

            if (w == null) {
                wm.put(t, w = new Occurence());
            } else {
                w.incr();
            }
            ret.add(new Report(t, w));
        }
        return ret;
    }
}




