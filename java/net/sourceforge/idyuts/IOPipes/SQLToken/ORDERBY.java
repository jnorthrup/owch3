/* Generated by Together */

package net.sourceforge.idyuts.IOPipes.SQLToken;

import net.sourceforge.idyuts.IOPipes.*;

public class ORDERBY extends SQLClauseImpl {
    public void recv(Object[] s) {
        synchronized (stage) {
            stage = f_loop(s, " ORDER BY ", ", ");
            xmit();
        }
        ;
    }
}


