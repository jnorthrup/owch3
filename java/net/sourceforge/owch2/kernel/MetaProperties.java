package net.sourceforge.owch2.kernel;

import java.io.*;
import java.util.*;

/**
 *     Acts like a Properties Class, with plug-in serilization;
 *
 * @version $Id: MetaProperties.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */

public interface MetaProperties extends MetaAgent, Map {
    /*
    int key=0;
    int token=1;
    int data=2;
    int comment=3;
    int newline=4;
    int empty=5;
    int statelen=6;
    */

    /**
     * RNODI specific Properties Serialization input.
     * @param istream Source of input.
     * @exception java.io.IOException thrown if istream throws an Exception.
     */
    public void load(InputStream reader) throws IOException ;

    public String getURL();

    /** Save properties to an OutputStream. */
    public void save(OutputStream writer) throws IOException;

    public String getFormat();

    public void setFormat(String format);

    public String getJMSReplyTo();
}

;


