/*
 * Created by IntelliJ IDEA.
 * User: root
 * Date: May 19, 2002
 * Time: 5:06:10 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.owch2.kernel;

public class Log {
    public final static boolean logDebug = true;
    protected static int debugLevel = 6000;
    private static DebugTimerOutputStream debugTimerOutputStream = new DebugTimerOutputStream(System.out);

    public final static DebugTimerOutputStream getDebugStream() {
        return (DebugTimerOutputStream) debugTimerOutputStream;
    }

    /**
      * sends logfile spew if the debugLevel is set high enough for an individual message.
      * @param lev an int of varying verboseness specification
      * @param s the text to log.
      */
     public static final void log(int lev, String s) {
         if (debugLevel >= lev) {
             debugTimerOutputStream.println(s);
         }
     }

    /**
     * Debug Level Accessor
     * @param i debug level
     */
    public static final void setDebugLevel(int i) {
        debugLevel = i; //
    }

    /**
     * Debug Level Accessor
     * @return the level of debug output verbosity
     */
    public final static int getDebugLevel() {
        return debugLevel; //
    }
}
