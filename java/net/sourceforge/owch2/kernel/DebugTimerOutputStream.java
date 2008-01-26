package net.sourceforge.owch2.kernel;

import java.io.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public class DebugTimerOutputStream extends PrintStream {
    private static long benchmark = System.currentTimeMillis();
    private static long time;
    private static long diff;

    DebugTimerOutputStream(OutputStream os) {
        super(os);
    }

    public void println(String s) {
        time = System.currentTimeMillis();
        diff = time - benchmark;
        super.println(time + "\t " + diff + "\t " + s);
        benchmark = time;
    }

}


