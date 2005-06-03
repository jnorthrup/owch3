package net.sourceforge.owch2.kernel;

import java.io.*;
import java.util.*;

/**
 * @author James Northrup
 * @version $Id: Format.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public interface Format {
    void read(InputStream reader, Map map) throws IOException;

    void write(OutputStream writer, Map map) throws IOException;
}


