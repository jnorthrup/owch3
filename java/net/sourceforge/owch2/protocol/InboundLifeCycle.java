package net.sourceforge.owch2.protocol;

public enum InboundLifeCycle {

    /**
     * stack events to open underlying network relying on stack/worker internal queueing
     */
    accept,
    postAccept,
    /**
     *
     */
    initHeaderBuffers,
    /**
     *
     */
    decodeHeader,
    /**
     * determine applicable ACL fit
     */
    authenticate,
    /**
     * message cracking and marshal the artifacts into a EventDescriptor
     */
    marshal,
    /**
     * optional response via a message or by domain specific workflow
     */
    sendAck,
    /**
     * domain specific stuff performed prior to agent completion
     */
    preRoute,
    /**
     * use inbound resolver to deliver to agent or outbound gateway or pathway hops
     */
    route,
    /**
     * insert into transport queue and/or agent queue
     */
    queue,
}
