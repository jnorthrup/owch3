package owch;

import java.io.*;

/**
 * @version $Id: DebugTimerOutputStream.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
class DebugTimerOutputStream extends PrintStream
{
    private static long benchmark=System.currentTimeMillis();
    private static long time;
    private static long diff;

    DebugTimerOutputStream(PrintStream os)
    {
        super(os);
    };

    public void println(String s)
    {
        time=System.currentTimeMillis();
        diff=time-benchmark;
        super.println(time+"\t "+diff+"\t "+s);
        benchmark=time;
    };
 };
