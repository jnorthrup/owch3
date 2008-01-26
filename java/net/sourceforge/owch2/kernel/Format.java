package net.sourceforge.owch2.kernel;

import java.io.*;
import java.util.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public interface Format {
    void read(InputStream reader, Map map) throws IOException;

    void write(OutputStream writer, Map map) throws IOException;
}


