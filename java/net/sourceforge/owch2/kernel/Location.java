package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * Provides information of how to reference a AbstractAgent out on the network,
 * contains the most recently known network address of a listener, and it's clones.
 *
 * @author James Northrup
 * @version $Id: Location.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class Location extends TreeMap implements MetaProperties {
    static {
        try {
            Env.getInstance().registerFormat("XMLSerial", (Format) Class.forName("msg.format.XMLSerialFormat").newInstance());
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Logger.global.info("XML Format not loaded");
        }
        Env.getInstance().registerFormat("RFC822", new RFC822Format());
        try {
            Env.getInstance().registerFormat("Serial", (Format) Class.forName("msg.format.SerialFormat").newInstance());
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Logger.global.info("Serial Format not loaded");
        }
    }

    public static final String URI_KEY = "URL";

    /**
     * Inserts "URL" property from a given ServerSocket.
     *
     * @param lr Reference to be a Location to.
     */
    public static Location create(ListenerReference lr) {
        String tstring = new String(lr.getProtocol() + ":");
        Location l = new Location();
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


    public final URI getURI() {
        return URI.create(get(Location.URI_KEY).toString());
    }

    /**
     * Default ctor.
     */
    public final String getJMSReplyTo() {
        return (String) get(Notification.REPLYTO_KEY);
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
    public Location(Map p) {
        putAll(p);
    }
}


