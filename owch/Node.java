package owch;

import java.net.*;
import java.io.*;
import java.util.*;

/*
  http://www.iconcomp.com/papers/comp/comp_41.html
*/

/** 
$Log: Node.java,v $
Revision 1.1  2001/04/14 20:35:26  grrrrr
Initial revision

 
*/
abstract public class Node extends TreeMap implements MetaNode 
{
    boolean virgin;
	LinkRegistry acl = null;
  



    public boolean isParent()
    {
        return false;
    }
;

    public void receive(MetaProperties n)
    {

        if(n==null)
        {
    	    Env.debug(5,"debug: Node.receive(Notification) has been sent a null notification");
    	    return;
        };

		String Dest=(String)n.get("JMSDestination");
		if(Dest==null)
		    return;
		    //worthless to us

		String JMSType=(String)n.get("JMSType");
		if(JMSType==null)JMSType="hi there";

		if(JMSType.equals("Link"))
		{
			String name=n.getJMSReplyTo();
    		if(name!=null)
    		{
    		    update(name);
		    }else
		    {
		        Env.debug(5,"debug: Node.Handler(JMSType=Link) has been sent to link a null JMSReplyTo");
		    };
		    return;
		};
    }
    /**
     *  Sends an update to another Node
     *
     *  @param dest JMSReplyTo
     */

    public void update(String dest)
    {
        MetaProperties n=new Notification();
        n.put("JMSType","Update");
   		n.put("JMSDestination",dest);
        String s=null;
        String u=null;
        String mu=Env.getLocation("owch").getURL();

        Map npm=Env.getProxyCache().nameProxyMap;
        for (Iterator e = npm.keySet().iterator() ; e.hasNext() ;)
        {
            s=(String)e.next();

            u=((String)((Proxy)npm.get(s)).getURL());

            if(u!=null)
            if(!u.equals(mu))
            {
                if(!u.equals(Env.getParentNode().getURL()))
                {
                    n.put(s,u);
                };
            };
        };

        send(n);
        Env.debug(15,"debug: Node.update() sent for "+dest);
    }
;

	String getAclEntry(String Node)
	{
		//in the case where a proxy offers an
		//expiration time, there exists a mutally
		//known hashCode on both sides to infer an accurate
		//Expiration time.

		return (String)acl.get(Node);
	};

	void setAcl(MetaProperties m)
	{
		//in the case where a proxy offers an
		//expiration time, there exists a mutally
		//known hashCode on both sides to infer an accurate
		//Expiration time.

		acl=new LinkRegistry(m);
	};

    public Node()
    { 
        put("Created","Node()"); 
    }
 
    public Node(Map p)
    { 
	super(p);
        put("Created","Node(map)"); 
    }
    /**
     *
     * Sends a Link notification other node(s)
     *
     * intended to establish direct socket communication.
     *
     * @param lk node(s) to link to
     *
     */


    public void linkTo(String lk)
    {
        if(lk==null)
        {
	    Env.debug(5, getClass().getName()+"::"+this.getJMSReplyTo()+".link invoked. routing to default"); 
            lk=Env.getParentNode().getJMSReplyTo();
        };
        MetaProperties n=new Notification();
		n.put("JMSDestination",lk);
        n.put("JMSType","Link"); 
        send(n);
    }
 
    /**
     *
     * Sends a Link notification other node(s)
     *
     * intended to establish direct socket communication.
     *
     * @param lk node(s) to link to
     *
     */


    public void unlink(String lk)
    {
        if(lk==null)
        {
            Env.debug(5, getClass().getName()+"::"+this.getJMSReplyTo()+".unlink invoked. routing to default");
	    lk=Env.getParentNode().getJMSReplyTo();
        };
        MetaProperties n=new Notification();
		n.put("JMSDestination",lk);
        n.put("JMSType","UnLink"); 
        send(n);
    }
 


    /***
     *
     *  Node level Notification insignia creation and inter-process notification routing.
     *
     * @param n  Notification destined for somewhere else
     */

    public void send(MetaProperties n)
    {
        String d=null;
        String w=null;

        Node node=null;

        //see if this works out

        if(n.getJMSReplyTo()==null)
        {
            n.put("JMSReplyTo",this.getJMSReplyTo());
        };

        d=(String)n.get("JMSDestination");

        if(d==null)
        {
            Env.debug(8,"debug: Node.Send(Notification) dropping unsendd Notification from "+this.getJMSReplyTo());
            return;
        };

        StringTokenizer st = new StringTokenizer(d);
        while (st.hasMoreTokens())
        {
            w=(String)st.nextToken();
            Env.debug(15,"debug: Node.Send(Notification) routing from "+n.getJMSReplyTo()+" to "+w+" type "+n.get("JMSType"));

            node=(Node)(Env.getNodeCache().get(w));

            if(node==null)
            {
                if(n.getJMSReplyTo()==null)
                  n.put("JMSReplyTo",getJMSReplyTo());

                Location l=new Location();
                l.put("Created","send()");
                l.put("JMSReplyTo",w);

                Proxy proxy=(Proxy)Env.getProxyCache().getProxy(l);
                proxy.addQueue(n);

            }else
            {
                node.receive(n);
            };
        };
    };

    public final String getURL()
    {
        String s=(String)get("URL");
        return s;
    }

 
    public final String getJMSReplyTo()
    {
        return (String)get("JMSReplyTo");
    };
};

