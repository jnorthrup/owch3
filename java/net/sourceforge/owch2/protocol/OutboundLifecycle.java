package net.sourceforge.owch2.protocol;

/**
 * This defines the steps and order of delivering an Notification to
 * a destination.  it is not necessary to apply all steps to all transports
 * <p/>
 * User: jim
 * Date: Jan 26, 2008
 * Time: 3:53:16 PM
 */
public enum OutboundLifecycle {
    /**
     * proto or message specific work is inserted
     */
    preRoute,
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
     * proto or message specific work is inserted
     */
    preSend,

    /**
     * deliver a message to a transport for delivery
     */
    send,

    /**
     * proto or message specific work is inserted
     */
    postSend,

    /**
     * convert and/or decorate the notification with transaction and JMS info.
     */
    marshal,

    /**
     * async delivery into the protocol message queue
     */
    queue,

    /**
     * async connection specified including any credentials handshake
     */
    connect,

    /**
     * buffers are delivered to the low-level system api's
     */
    push,

    /**
     * message state-changes are reported such as Select() and open/connect/route/close/failure events.
     */
    monitor,

    /**
     * message may also experience policy-based expire.
     */
    expire,

    /**
     * Domain-specific hooks such as timing
     */
    close,

    /**
     * resources are reclaimed and or additional reporting
     */
    cleanup
}

