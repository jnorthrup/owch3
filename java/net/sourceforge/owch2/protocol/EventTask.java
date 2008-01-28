package net.sourceforge.owch2.protocol;

import net.sourceforge.owch2.kernel.EventDescriptor;

import java.util.concurrent.Callable;

/**
 * Glamdring Incorporated Enterprises.  All rights reserved.
 * User: jim
 * Date: Jan 27, 2008
 * Time: 9:36:48 PM
 */
public abstract class EventTask {

    public abstract Callable getCallable(EventDescriptor event);

}
