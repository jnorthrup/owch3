package owch;

/*
 *
 * ListenerFactory
 *
 */
public interface ListenerFactory
{

	ListenerReference create(int port,int threads
	/*0==Parent Server Default*/);
	//params
	//port - requested port number 0=random
	//threads - the number of threads to monitor the listener.  0=default
};


