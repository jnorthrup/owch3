package owch;

import java.net.*;
import java.io.*;
import java.util.*;

/*
http://www.iconcomp.com/papers/comp/comp_41.html
*/

/*
<HTML>
<HEAD>
<TITLE> Design Pattern [17]: Recursive Composite</TITLE>
</HEAD>
<BODY BGCOLOR=#ffffff>
<A NAME=HEADING40>
<A HREF="comp_40.html"><IMG SRC="prev_1.gif" ALIGN=TOP></A><A HREF="comp_1.html"><IMG SRC="top_1.gif" ALIGN=TOP></A><A HREF="comp_42.html"><IMG SRC="next_1.gif" ALIGN=TOP></A><P>
Section 2:  A Framework<P>
<H1>Design Pattern [17]: Recursive Composite<P></H1>
<HR>
 Problem:<P>
<UL>
<LI>An object may consist of, or contain, other very similar objects<P>
<LI>This containment is sometimes limited artificially to just 2 or 3 levels<P>
<LI>In any case, modeling and designing based on this could be too restrictive<BR><IMG SRC="comp_AFrame_103.gif"><BR>
<P>
</UL>
 Solution:<P>
<UL>
<LI>Model and design using a Recursive Composite<P>
<LI>Minimize interface differences between atomic and composite cases<P>
<LI>Consider Constructor-Enforced Semantics [41] for different composites<P>
</UL>

<HR>
<ADDRESS>A Comparison of OOA and OOD Methods - Copyright 1995 <A HREF="http://www.iconcomp.com">ICON Computing, Inc.</A></ADDRESS>
<HR>
<A HREF="comp_40.html"><IMG SRC="prev_1.gif" ALIGN=TOP></A><A HREF="comp_1.html"><IMG SRC="top_1.gif" ALIGN=TOP></A><A HREF="comp_42.html"><IMG SRC="next_1.gif" ALIGN=TOP></A><P>
</BODY>
</HTML>
*/
abstract public class Node extends MetaProperties
{
    boolean virgin;
	LinkRegistry acl = null;
  



    public boolean isParent()
    {
        return false;
    }
;

    public void handleNotification(Notification n)
    {

        if(n==null)
        {
    	    Env.debug(5,"debug: Node.handleNotification(Notification) has been sent a null notification");
    	    return;
        };

		String Dest=(String)n.get("DestNode");
		if(Dest==null)
		    return;
		    //worthless to us

		String Type=(String)n.get("Type");
		if(Type==null)Type="hi there";

		if(Type.equals("Link"))
		{
			String name=n.getNodeName();
    		if(name!=null)
    		{
    		    update(name);
		    }else
		    {
		        Env.debug(5,"debug: Node.Handler(Type=Link) has been sent to link a null NodeName");
		    };
		    return;
		};
    }
    /**
     *  Sends an update to another Node
     *
     *  @param dest NodeName
     */

    public void update(String dest)
    {
        Notification n=new Notification();
        n.put("Type","Update");
   		n.put("DestNode",dest);
        String s=null;
        String u=null;
        String mu=Env.getLocation("owch").getURL();

        Hashtable npm=Env.getProxyCache().nameProxyMap;
        for (Enumeration e = npm.keys() ; e.hasMoreElements() ;)
        {
            s=(String)e.nextElement();

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

        route(n);
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
            Env.debug(5,"debug: (Node)"+this.getNodeName()+".linkTo(null) invoked.  routing to default");
            lk="default";
        };
        Notification n=new Notification();
		n.put("DestNode",lk);
        n.put("Type","Link");

        route(n);
    }
 


    /***
     *
     *  Node level Notification insignia creation and inter-process notification routing.
     *
     * @param n  Notification destined for somewhere else
     */

    public void route(Notification n)
    {
        String d=null;
        String w=null;

        Node node=null;

        //see if this works out

        if(n.getNodeName()==null)
        {
            n.put("NodeName",this.getNodeName());
        };

        d=(String)n.get("DestNode");

        if(d==null)
        {
            Env.debug(8,"debug: Node.Route(Notification) dropping unrouted Notification from "+this.getNodeName());
            return;
        };

        StringTokenizer st = new StringTokenizer(d);
        while (st.hasMoreTokens())
        {
            w=(String)st.nextToken();
            Env.debug(15,"debug: Node.Route(Notification) routing from "+n.getNodeName()+" to "+w+" type "+n.get("Type"));

            node=(Node)(Env.getNodeCache().nameNodeMap.get(w));

            if(node==null)
            {
                if(n.getNodeName()==null)
                  n.put("NodeName",getNodeName());

                Location l=new Location();
                l.put("Created","route()");
                l.put("NodeName",w);

                Proxy proxy=(Proxy)Env.getProxyCache().getProxy(l);
                proxy.addQueue(n);

            }else
            {
                node.handleNotification(n);
            };
        };
    };
};

