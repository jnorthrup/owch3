package net.sourceforge.owch2.kernel;

import java.net.*;
import java.util.*;


public class DefaultMapNotification extends LinkedHashMap<CharSequence, Object>
        implements Notification {
    public DefaultMapNotification(CharSequence from) {
        put(FROM_KEY, from);
    }


    public DefaultMapNotification(Map.Entry<CharSequence, Object>... entryIterable) {
        for (Map.Entry<CharSequence, Object> charSequenceObjectEntry : entryIterable) {
            put(charSequenceObjectEntry.getKey(), charSequenceObjectEntry.getValue());
        }
    }


    public DefaultMapNotification(Iterable<Map.Entry<CharSequence, Object>> entryIterable) {
        for (Map.Entry<CharSequence, Object> charSequenceObjectEntry : entryIterable) {
            put(charSequenceObjectEntry.getKey(), charSequenceObjectEntry.getValue());
        }
    }

    public DefaultMapNotification(CharSequence from, URI uri) {
        super();
        put(FROM_KEY, from);
        put(URI_KEY, uri);

    }


    /**
     * sender's semantic name, which supercedes
     * URI in terms of router precedence.
     * <p/>
     * e.g.  the Semantic name 'GateKeeper' is used in a
     * mobile web server facade. there may be multiple
     * nodes claiming the name "GateKeeper"  which have the effect
     * of re-asserting alternating routing based on load factors
     * or timing issues or network subnets, etc.
     * the Gatekeeper assigns a set of URI's to name:URI pairs, these mappings
     * are also equally re-assertable allowing multiple registrations competing
     * for the same http introductions.
     * <p/>
     * AKA <b>Subject</b>
     *
     * @return a name
     */
    public CharSequence getFrom() {
        return (CharSequence) get(FROM_KEY);
    }

    /**
     * The sender's resource URL or URI, a hint how to reach the sender's context
     * <p/>
     * AKA <b>verb</b> or <b>via</b>
     *
     * @return a URI
     */
    public URI getURI() {
        return (URI) get(URI_KEY);
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<Map.Entry<CharSequence, Object>> iterator() {
        return (Iterator<Map.Entry<CharSequence, Object>>) super.entrySet();
    }
}
