package net.sourceforge.owch2.kernel;

import java.io.*;
import java.util.*;

/**
 * Provides information of how to reference a AbstractAgent out on the network,
 * contains the most recently known network address of a listener, and it's clones.
 * @version $Id: Location.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public class Location extends TreeMap implements MetaProperties {
    static {
        try {
            Env.registerFormat("XMLSerial", (Format) Class.forName("msg.format.XMLSerialFormat").newInstance());
        }
        catch (Exception e) {
            if (Env.logDebug) Env.log(5, "XML Format not loaded");
        }
        Env.registerFormat("RFC822", new RFC822Format());
        try {
            Env.registerFormat("Serial", (Format) Class.forName("msg.format.SerialFormat").newInstance());
        }
        catch (Exception e) {
            if (Env.logDebug) Env.log(5, "Serial Format not loaded");
        }
    };

    private String format = "RFC822";

    /**
     * Inserts "URL" property from a given ServerSocket.
     * @param lr Reference to be a Location to.
     */
    public static Location create(ListenerReference lr) {
        String tstring = new String(lr.getProtocol() + ":");
        Location l = new Location();
        tstring += "//" + Env.getHostname().trim() + ":" +
                ((Env.getHostPort() == 0) ? lr.getServer().getLocalPort() : Env.getHostPort());
        l.put("URL", tstring);
        return l;
    }

    /**
     * RNODI specific Properties Serialization input.
     * @param istream Source of input.
     * @exception java.io.IOException thrown if istream throws an Exception.
     */
    public final void load(InputStream reader) throws IOException   {
        Env.getFormat(getFormat()).read(reader, this);
    }

    /** Save properties to an OutputStream. */
    public synchronized void save(OutputStream writer)  throws IOException {
        Env.getFormat(getFormat()).write(writer, this);
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    };

    public final String getURL() {
        String s = (String) get("URL");
        return s;
    }

    /** Default ctor. */
    public final String getJMSReplyTo() {
        return (String) get("JMSReplyTo");
    };

    /** Default Ctor */
    public Location() {
    };

    /**
     * Copy Constructor
     * @param p  source of copy
     */
    public Location(Map p) {
        putAll(p);
    };
}

;


