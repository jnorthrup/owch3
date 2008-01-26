package net.sourceforge.owch2.kernel;

import java.io.*;
import java.util.*;

/**
 * Provides information of how to reference a AbstractAgent out on the network,
 * contains the most recently known network address of a listener, and it's clones.
 *
 * @author James Northrup
 * @version $Id: Location.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class Location<V>
        extends TreeMap<String, V>
        implements MetaProperties<V> {
    static {
        try {
            Env.getInstance().registerFormat("XMLSerial", (Format) Class.forName("msg.format.XMLSerialFormat").newInstance());
        }
        catch (Exception e) {
        }
        Env.getInstance().registerFormat("RFC822", new RFC822Format());
        try {
            Env.getInstance().registerFormat("Serial", (Format) Class.forName("msg.format.SerialFormat").newInstance());
        }
        catch (Exception e) {
        }
    }

    public static final String URI_KEY = "URL";

    /**
     * Inserts "URL" property from a given ServerSocket.
     *
     * @param lr Reference to be a Location to.
     */
    public static Location<String> create(ListenerReference lr) {
        String tstring = lr.getProtocol() + ":";
        Location<String> l = new Location<String>();
        final int owchPort = Env.getInstance().getOwchPort();
        tstring += "//" + Env.getInstance().getHostname().trim() + ":" +
                ((owchPort == 0) ? lr.getServer().getLocalPort() : owchPort);
        l.put("URL", tstring);
        return l;
    }

    /**
     * RNODI specific Properties Serialization input.
     *
     * @param reader Source of input.
     * @throws java.io.IOException thrown if istream throws an Exception.
     */
    public final void load(InputStream reader) throws IOException {
        Env.getInstance().getFormat("RFC822").read(reader, this);
    }

    /**
     * Save properties to an OutputStream.
     */
    public synchronized void save(OutputStream writer) throws IOException {
        Env.getInstance().getFormat("RFC822").write(writer, this);
    }


    /**
     * this method has undergone the move from string to url to
     * string to uri to string again...
     *
     * @return a string as a URI
     */
    public final String getURI() {
        return String.valueOf(get(Location.URI_KEY));
    }

    /**
     * Default ctor.
     */
    public final String getJMSReplyTo() {
        return String.valueOf(get(Message.REPLYTO_KEY));
    }

    /**
     * Default Ctor
     */
    public Location() {
    }

    /**
     * Copy Constructor
     *
     * @param p source of copy
     */
    public Location(Map<String, ? extends V> p) {
        putAll(p);
    }
}


