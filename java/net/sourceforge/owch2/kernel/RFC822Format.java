package net.sourceforge.owch2.kernel;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * @author James Northrup
 * @version $Id: RFC822Format.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class RFC822Format implements Format {
    public RFC822Format() {
    }

    ;

    public void read(InputStream inputStream, Map map) throws IOException {
        String line, key, val;
        int col;
        DataInputStream ins = (inputStream instanceof DataInputStream) ? ((DataInputStream) inputStream) : new DataInputStream(inputStream);
        do {
            line = ins.readLine();
            if (line == null) {
                return;
            }
            col = line.indexOf(':');
            if (col < 1) {
                return;
            }
            key = line.substring(0, col).trim();
            val = line.substring(col + 1).trim();
            map.put(key, val);
        } while (true);
    }

    public void write(OutputStream writer, Map map) throws IOException {
        String line, key;
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            line = key.toString() + ": " + map.get(key) + "\n";
            writer.write(line.getBytes());
            if (Env.getInstance().logDebug) Env.getInstance().log(200, "RFC822Format line saved:" + line);
        }
        writer.write('\n');
        writer.flush();
    }
}