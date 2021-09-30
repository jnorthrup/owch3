package net.sourceforge.owch2.kernel;

import java.net.URI;
import java.util.Map;

public abstract class ImmutableTransaction extends ImmutableNotification {
    protected ImmutableTransaction(CharSequence from, URI URI, CharSequence destination, Map.Entry<CharSequence, Object>... message) {
        super(from, URI, message);
        this.to = destination;
    }

    private CharSequence to;

    public CharSequence getTo() {
        return to;
    }
}
