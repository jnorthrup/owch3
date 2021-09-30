package net.sourceforge.owch2.protocol;

/**
 * User: jim
 * Date: Jan 28, 2008
 * Time: 4:09:36 PM
 */
public enum SetupLifecycle {
    allocate,
    open,
    configure,
    bind,
    register,
    accept,
    connect,
    select,
    initHeaderBuffers,
    react,
    createEventDescriptor
}
