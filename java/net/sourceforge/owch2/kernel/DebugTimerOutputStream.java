package net.sourceforge.owch2.kernel;

import java.io.*;

/**
 * @version $Id: DebugTimerOutputStream.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public class DebugTimerOutputStream extends PrintStream {
    private static long benchmark = System.currentTimeMillis();
    private static long time;
    private static long diff;

    DebugTimerOutputStream(PrintStream os) {
        super(os);
    };

    public void println(String s) {
        time = System.currentTimeMillis();
        diff = time - benchmark;
        super.println(time + "\t " + diff + "\t " + s);
        benchmark = time;
    };
}

;


