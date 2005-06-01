package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author James Northrup
 * @version $Id: Format.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public interface Format {
    void read(InputStream reader, Map map) throws IOException;

    void write(OutputStream writer, Map map) throws IOException;
}


