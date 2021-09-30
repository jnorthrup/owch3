package net.sourceforge.owch2.kernel;

import java.net.*;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Feb 1, 2008
 * Time: 12:15:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HasOrigin {
    String FROM_KEY = "From";
    String URI_KEY = "URI";

    /**
     * sender's semantic name, which is more important than the URI
     * p/>
     * AKA <b>Subject</b>
     *
     * @return a name
     */
    CharSequence getFrom();

    /**
     * The sender's resource URL or URI, a hint how to reach the sender's context
     * <p/>
     * * AKA <b>verb</b> or <b>via</b>
     *
     * @return a URI
     */
    URI getURI();
}
