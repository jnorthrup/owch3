package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.*;
import net.sourceforge.owch2.protocol.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Glamdring Incorporated Enterprises.  All rights reserved.
 * User: jim
 * Date: Jan 27, 2008
 * Time: 6:12:57 AM
 */
public class owchRouter extends AbstractRouterImpl {
    private static final Transport OWCH = Transport.owch;

    /**
     * this router is "sticky".
     * <p/>
     * all messages via owch pass through the same router for either direction, including ACK notes.  we record each name/URI pair and update them if they already exist.
     * <p/>
     * Routing resource usage is "Discardable"
     *
     * @param eventDescriptor name of sendee
     * @return bool: do we have the path for the Destination?
     */
    @Override
    public boolean hasPath(EventDescriptor eventDescriptor) {
        return false;
    }

    public Future<Receipt> send(final EventDescriptor... async) throws Exception {
        final Callable<Receipt> callable = new Callable<Receipt>() {
            public int seq;

            public Receipt call() throws Exception {

                for (EventDescriptor event : async) {
                    if (event.containsKey(EventDescriptor.MESSAGE_ID_KEY)) {

                        //we think it's inbound...
                        Logger.getAnonymousLogger().finest("recpt: " + event.get(EventDescriptor.MESSAGE_ID_KEY));
                        for (InboundLifeCycle inboundLifeCycle : InboundLifeCycle.values()) {
                            Callable callable = OWCH.getEventAgendaTask(inboundLifeCycle, event);
                            if (null != callable) {
                                final Future<Receipt> recieptFuture = Reactor.submit(callable);
                                final Receipt receipt = recieptFuture.get(3, TimeUnit.MINUTES);
                                OWCH.addReceipt(receipt);
                            }
                        }
                    } else {
                        for (OutboundLifecycle s : OutboundLifecycle.values()) {


                        }
                    }
                }

                final Date date = new Date();

                return new Receipt() {

                    public Transport getTransport() {
                        return null;  //Todo: verify for a purpose
                    }

                    public Date getTimestamp() {
                        return date;
                    }

                    public Iterator<EventDescriptor> iterator() {
                        return Arrays.asList(async).iterator();

                    }
                };
            }


        };
        return Reactor.submit(callable);
    }


}


