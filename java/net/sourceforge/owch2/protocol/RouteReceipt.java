package net.sourceforge.owch2.protocol;

import net.sourceforge.owch2.kernel.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Jan 28, 2008
 * Time: 11:07:08 PM
 * To change this template use File | Settings | File Templates.
 */
class RouteReceipt implements Receipt {
    final Date date;
    private final EventDescriptor[] events;
    private Transport transport;

    public RouteReceipt(Transport transport, EventDescriptor... events) {
        this.transport = transport;
        this.events = events;
        date = new Date();
    }

    public Transport getTransport() {
        return transport;
    }

    public Date getTimestamp() {
        return date;
    }

    public Iterator<EventDescriptor> iterator() {
        return Arrays.asList(events).iterator();
    }
}
