package net.sourceforge.idyuts.IOPipes.functor;

import net.sourceforge.idyuts.IOPipes.*;

import java.util.*;

public class xlateBetween extends StringFunctorImpl {
    /** xlate function used to transform the user "~BETWEEN <date1>,<date2>"into sql; */
    public String fire(String s) {
        if (!s.startsWith("~BETWEENDATES")) {
            return s;
        }
        java.util.List arr = new ArrayList(2);
        StringTokenizer st;
        st = new StringTokenizer(s.substring("~BETWEENDATES".length(), ','));
        while (st.hasMoreTokens()) {
            arr.add(st.nextToken());
        }
        String t = " BETWEEN ";
        for (int ci = 0; ci < arr.size(); ci++) {
            String ii = (String) arr.get(ci);
            if (ci > 0) {
                t = t + " AND ";
            }
            t = t + " TO_DATE('" + ii.trim() + "','YYYY-MMM-DD')";
        }
        ;
        return t;
    };
}


