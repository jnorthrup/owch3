package net.sourceforge.owch2.kernel;

import java.net.*;
import java.util.*;

public class DefaultMapTransaction extends DefaultMapNotification implements Transaction {
    private CharSequence to;
    private static final CharSequence DESTINATION_KEY = HasDestination.DESTINATION_KEY;
    private Map.Entry<CharSequence, Object>[] e;


    public DefaultMapTransaction(CharSequence from, URI uri, CharSequence to, Iterator<Map.Entry<CharSequence, Object>> entryIterator) {
        this(from, to);


    }

    public DefaultMapTransaction(CharSequence from, CharSequence to) {
        super(from);
        this.to = to;
    }

    public DefaultMapTransaction(Map.Entry<CharSequence, Object>... e) {
        super(e);

    }

    public DefaultMapTransaction(HasProperties n) {
        super(n);        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * The destination's semantic name,
     * <p/>
     * AKA <b>Object</b>
     *
     * @return a name
     */
    public CharSequence getDestination() {
        return (CharSequence) get(DESTINATION_KEY);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
