/*
 * Created by IntelliJ IDEA.
 * User: root
 * Date: May 19, 2002
 * Time: 8:57:02 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.owch2.kernel;

public class StreamDesc {
    protected boolean usingInflate = false,
            usingDeflate = false,
            buffered = false;
    protected int zbuf = 0,
            bufbuf = 0;

    public StreamDesc() {
    }

    public StreamDesc(int bb) {
        buffered = bb > 128;
    }

    public StreamDesc(boolean zencrypt, boolean zdecrypt, int zbuff, int bb) {
        usingInflate = zencrypt && zbuff > 128;
        usingDeflate = zdecrypt && zbuff > 128;
        buffered = bb > 128;
        zbuf = zbuff;
        bufbuf = bb;
    }

    public boolean isBuffered() {
        return buffered;
    }

    public int getZbuf() {
        return zbuf;
    }

    public int getBufbuf() {
        return bufbuf;
    }
}
