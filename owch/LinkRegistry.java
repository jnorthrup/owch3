package owch;

import java.util.*;

   /**
    * Class is used to track the connections a proxy is entertaining.
    * "Update" notifications are simply LinkRegistries, with routing
    * info that gets stripped out.  This is implicitly a proxy
    * aggregate.
    *
    * @author Jim Northrup
    * @version 0.5 22 Aug 96
    */

//TODO:remove LinkRegistry from owch
public class LinkRegistry extends TreeMap
{
    /**
     * Default constructor 
     */

    public LinkRegistry()
    {
        super();
    }

    /**
     * C'tor with copy.
     *
     * @param m The source,  generally an Update Notification.
     */

    public LinkRegistry(MetaProperties m)
    {
        super(m);
        prune();
    };

    /**
     * Strips a Notification-derived LinkRegistry of transport tags.
     *
     */

    final void prune()
    {
        for (int i =0;i<reserved.length;i++)
	    {
		String t=reserved[i];
		if(containsKey(t))
		    {
			remove(t);
		    };
	    };
    };

    /**
     * Holds reserved Keywords that should be stripped from "Update" notifications
     *
     */

    static String[] reserved=
    {
        "ACK".intern(),
        "Created".intern(),
        "JMSDestination".intern(),
        "MessageText".intern(),
        "JMSReplyTo".intern(),
        "ResentFrom".intern(),
        "JMSMessageID".intern(),
        "JMSType".intern(),
        "URL".intern(),
        "retry".intern()
    };

 
}

