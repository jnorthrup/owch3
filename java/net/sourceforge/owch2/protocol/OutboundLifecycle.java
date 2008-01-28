package net.sourceforge.owch2.protocol;

/**
 * Glamdring Incorporated Enterprises.  All rights reserved.
 * User: jim
 * Date: Jan 26, 2008
 * Time: 3:53:16 PM
 */
public enum OutboundLifecycle {
    /**
     * proto or message specific work is inserted
     */
    preSend,
    /**
     * deliver a message to a router for delivery
     */
    send,
    /**
     * convert and/or decorate the notification with transaction and JMS info.
     */
    marshal,
    /**
     * async delivery into the protocol message queue
     */
    queue,
    /**
     * locate a path and perform path housekeeping defined by this event
     * via the outbound resolver
     */
    route,
    /**
     * post-route Domain specifics
     */
    postRoute,
    /**
     * async connection specified including any credentials handshake
     */
    connect,
    /**
     * underlying Stack state is managed
     */
    transport,
    /**
     * message state-changes are reported such as Select() and open/connect/route/close/failure events.
     */
    monitor,
    /**
     * Domain-specific hooks such as timing
     */
    postSend,
    /**
     * message may also experience policy-based expire.  domain specific resource-cleanup
     */
    expire,
    /**
     * resources are reclaimed and or additional reporting
     */
    cleanup
}

