package net.sourceforge.owch2.kernel;

import java.io.*;
import java.util.*;

/**
 * @version $Id: Format.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public interface Format {
    void read(InputStream reader, Map map) throws IOException;

    void write(OutputStream writer, Map map) throws IOException;
}


