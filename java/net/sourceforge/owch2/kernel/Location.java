package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides information of how to reference a AbstractAgent out on the network,
 * contains the most recently known network address of a listener, and it's clones.
 *
 * @author James Northrup
 * @version $Id: Location.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class Location extends TreeMap implements MetaProperties {
    static {
        try {
            Env.getInstance().registerFormat("XMLSerial", (Format) Class.forName("msg.format.XMLSerialFormat").newInstance());
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Env.getInstance().log(5, "XML Format not loaded");
        }
        Env.getInstance().registerFormat("RFC822", new RFC822Format());
        try {
            Env.getInstance().registerFormat("Serial", (Format) Class.forName("msg.format.SerialFormat").newInstance());
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Env.getInstance().log(5, "Serial Format not loaded");
        }
    }

    ;

    private String format = "RFC822";

    /**
     * Inserts "URL" property from a given ServerSocket.
     *
     * @param lr Reference to be a Location to.
     */
    public static Location create(ListenerReference lr) {
        String tstring = new String(lr.getProtocol() + ":");
        Location l = new Location();
        tstring += "//" + Env.getInstance().getHostname().trim() + ":" +
                ((Env.getInstance().getHostPort() == 0) ? lr.getServer().getLocalPort() : Env.getInstance().getHostPort());
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
        Env.getInstance().getFormat(getFormat()).read(reader, this);
    }

    /**
     * Save properties to an OutputStream.
     */
    public synchronized void save(OutputStream writer) throws IOException {
        Env.getInstance().getFormat(getFormat()).write(writer, this);
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    ;

    public final String getURL() {
        String s = (String) get("URL");
        return s;
    }

    /**
     * Default ctor.
     */
    public final String getJMSReplyTo() {
        return (String) get("JMSReplyTo");
    }

    ;

    /**
     * Default Ctor
     */
    public Location() {
    }

    ;

    /**
     * Copy Constructor
     *
     * @param p source of copy
     */
    public Location(Map p) {
        putAll(p);
    }

    ;
}

;


