package net.sourceforge.idyuts.IOPipes.functor;

import net.sourceforge.idyuts.IOPipes.*;

import java.util.*;

public class xlateSecondDotToken extends StringFunctorImpl {
    /** xlate function used to grab the columnname from col0 */
    public String fire(String s) {
        boolean t = true;
        String ts;
        StringTokenizer st = new StringTokenizer(s, ".");
        while (st.hasMoreTokens()) {
            ts = st.nextToken();
            if (t) {
                t = !t;
            }
            else {
                return ts;
            }
        }
        ;
        return s;
    };
}


