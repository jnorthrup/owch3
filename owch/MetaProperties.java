package owch;

import java.io.*;
import java.util.*;

/**
 * RNODI Serializable Properties class. java.util.Properties.load() requires EOF, the intent of MetaProperties is to keep
 * the Properties Interface and insert indirection to circumvent closing streams to receive input.
 * (early tests proved impossible to stream multiple Properties classes on the same stream.)
 * This enables a socket connection stream caching mechanism and allows us to gain state info clues
 * on remote nodes via watching tcp exceptions popping.  We thus avoid using udp to write our own
 * reliable connection state while keeping the fairly expedient response of an open socket.
 * @version $Id: MetaProperties.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup*/
public interface MetaProperties extends MetaNode, Map {
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
    public   void load(InputStream istream) throws IOException;

    public   String getURL();

    /** Save properties to an OutputStream. */
    public  void save(OutputStream os) throws IOException;

    public   String getFormat(); 
 
    public   void  setFormat(String format); 
 
    public  String getJMSReplyTo();
};
