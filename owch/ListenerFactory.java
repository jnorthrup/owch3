package owch;

/*
 *
 * ListenerFactory
 *
 */

/**
 * @version $Id: ListenerFactory.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public interface ListenerFactory
{

	ListenerReference create(int port,int threads
	/*0==Parent Server Default*/);
	//params
	//port - requested port number 0=random
	//threads - the number of threads to monitor the listener.  0=default
};


