package net.sourceforge.owch2.protocol;

import java.nio.channels.Channel;
import java.util.concurrent.Callable;

/**
 * This is a Callable which returns with a "Command" input for the controller.
 * <p/>
 * Controller fires this task.  this task provides a Future during the call which can be cancelled.
 * <p/>
 * on completion the Callable returns a Command to the controller, or null, or TRUE, or FALSE, etc...
 * <p/>
 * User: jim
 * Date: Jan 27, 2008
 * Time: 9:36:48 PM
 */
public interface ControllerState<CommandType, Descriptor> {
    Callable<Channel> getCallable(final Descriptor descriptor);
}

