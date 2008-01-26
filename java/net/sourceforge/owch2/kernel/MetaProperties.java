package net.sourceforge.owch2.kernel;

import java.io.*;
import java.util.*;

/**
 * Acts like a Properties Class, with plug-in serilization;
 *
 * @author James Northrup
 * @version $Id$
 */

public interface MetaProperties<V>
        extends MetaAgent, Map<String, V> {
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
    public void load(InputStream reader) throws IOException;


    /**
     * Save properties to an OutputStream.
     *
     * @param writer todo: replace with serialization
     * @throws java.io.IOException e
     */
    public void save(OutputStream writer) throws IOException;
}


