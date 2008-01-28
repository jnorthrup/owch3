package net.sourceforge.owch2.protocol;

import net.sourceforge.owch2.kernel.*;

import java.util.*;

/**
 * Glamdring Incorporated Enterprises.  All rights reserved.
 * User: jim
 * Date: Jan 26, 2008
 * Time: 3:15:45 PM
 */
public interface Reciept extends Iterable<EventDescriptor> {
    Transport getTransport();

    Iterator<EventDescriptor> iterator();
}
