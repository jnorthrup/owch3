package net.sourceforge.owch2.kernel;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: Feb 1, 2008
 * Time: 12:16:06 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HasDestination {
    CharSequence DESTINATION_KEY = "JMSDestination";

    /**
     * The destination's semantic name,
     * <p/>
     * AKA <b>Object</b>
     *
     * @return a name
     */
    CharSequence getDestination();
}
