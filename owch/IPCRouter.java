package owch;

import java.net.*;
import java.io.*;
import java.util.*;

public class  IPCRouter implements  Router {

    private Map elements=new WeakHashMap();
    public void remove(Object key){ 
	Node n=(Node)	elements.get(key);
	n.dissolve(); 
	elements.remove(key); 
    };
    public void send(Map item){
	Env.debug(500,getClass().getName()+" sending item to"+getDestination(item));
	Node node=(Node)elements.get(getDestination(item));
	node.receive(new Notification(item));	
    }

    public Object getDestination(Map item){
	return item.get("JMSDestination");
    };

    public Set getPool(){
	return elements.keySet();
    };

    public Router getNextOutbound (){
	return Env.getRouter ("owch");
    }

    public Router getNextInbound (){
	return null;
    };

    public boolean hasElement(Object key){
	return elements.containsKey(key);
    }; 

    public void put(Node node){
	elements.put(node.getJMSReplyTo(),node);
    }

    public boolean addElement(Map item){ 
	if(item instanceof Node)
	    {
		//check for a previous element of same name... dissolve it..
		Node n=(Node)elements.get("JMSReplyTo");
		if(n!=null)
		    n.dissolve();
		elements.put(item.get("JMSReplyTo"),item);
		Env.debug(500,getClass().getName()+" adding item "+ item.get("JMSReplyTo"));
		Env.debug(500,getClass().getName()+" adding item "+ item.get("JMSReplyTo"));	
		Env.debug(500,getClass().getName()+" adding item "+ item.get("JMSReplyTo"));	
		Env.debug(500,getClass().getName()+" adding item "+ item.get("JMSReplyTo"));
		return true; 
	    }
	return false;
    };
};
