
package owch;

import java.util.*;

public class NodeCache
{
    public final void addNode(Node n)
    {
        if(n==null)
        {
            Env.debug(5,"debug: NodeCache.AddNode() sent a null.");
            return;
        };

        String s=n.getNodeName();

        if(s==null)
        {
            Env.debug(5,"debug: NodeCache.AddNode() sent a null nodeName.");
            return;
        };

        Env.debug(10,"debug: NodeCache.addNode("+s+")");
        nameNodeMap.put(s,n);

        if(n.isParent()==true)
        {
            Env.setParentNode( new Location(n));
        };

        //spill the queue;
        Proxy proxy=(Proxy)Env.getProxyCache().nameProxyMap.remove("n");
        if(proxy!=null)
        {
            while(!proxy.queue.empty())
            {
                n.handleNotification((Notification)(proxy.queue.pop()));
            };
        };
    }
;

    public void remove(String n)
    {
        nameNodeMap.remove(n);
    };

    Hashtable nameNodeMap;

    NodeCache()
    {
        nameNodeMap=new Hashtable(10, 0.75f);
    }

    Node getNode(String s)
    {
        return (Node)nameNodeMap.get(s);
    };
};
