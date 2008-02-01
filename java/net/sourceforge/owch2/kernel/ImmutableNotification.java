package net.sourceforge.owch2.kernel;

import java.net.*;
import java.util.*;

/**
 * A Notification passed from agent to agent.
 * <p/>
 * Messages are intended to be commands, or references to resources,
 * but not to exceed reasoonable realtime traffic of a 1-2k at most,
 * excepting streaming delivery or local IPC.  no java serialization
 * is yet implied if ever...
 *
 * @author James Northrup
 * @version $Id$
 */
public abstract class ImmutableNotification implements Notification {
    private CharSequence from;
    private URI URI;
    private Entry<CharSequence, Object>[] message;

    public ImmutableNotification(CharSequence from, URI URI, Entry<CharSequence, Object>... message) {
        this.from = from;
        this.URI = URI;
        this.message = message;
    }

    public CharSequence getFrom() {
        return from;
    }

    public URI getURI() {
        return this.URI;
    }


    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<Entry<CharSequence, Object>> iterator() {
        return Arrays.asList(message).iterator();
    }
}

