package net.sourceforge.owch2.kernel;

import java.io.PrintStream;

/**
 * @author James Northrup
 * @version $Id: DebugTimerOutputStream.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class DebugTimerOutputStream extends PrintStream {
    private static long benchmark = System.currentTimeMillis();
    private static long time;
    private static long diff;

    DebugTimerOutputStream(PrintStream os) {
        super(os);
    }

    ;

    public void println(String s) {
        time = System.currentTimeMillis();
        diff = time - benchmark;
        super.println(time + "\t " + diff + "\t " + s);
        benchmark = time;
    }

    ;
}

;


