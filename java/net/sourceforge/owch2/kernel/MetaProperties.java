package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Acts like a Properties Class, with plug-in serilization;
 *
 * @author James Northrup
 * @version $Id: MetaProperties.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
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
     *
     * @param reader Source of input.
     * @throws java.io.IOException thrown if istream throws an Exception.
     */
    public void load(InputStream reader) throws IOException ;

    public String getURL();

    /**
     * Save properties to an OutputStream.
     */
    public void save(OutputStream writer) throws IOException;

    public String getFormat();

    public void setFormat(String format);

    public String getJMSReplyTo();
}

;


