 
package owch ;
import java.util.*; 
import java.io.*;

import java.lang.ref.*;
import java.net.*;

/**
 * gatekeeper registers a prefix of an URL such as "/cgi-bin/foo.cgi"
 * The algorithm to locate the URL works in 2 phases;<OL>
 *
 * <LI> The weakHashMap is checked for an exact match.
 *
 * <LI> The arraycache is then checked from top to bottom to see if
 * URL startswith (element <n>) </OL>
 *
 * The when an URL is located -- registering the URL "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a
 * waiting pipeline
 */
 public class httpRegistry extends Registry
{
    
    public   String  displayKey(Comparable key) {
	return key.toString();
    };

    public   String  displayValue(Reference o) {
	Map m=(Map)o.get();
	if(m==null)
	    return "*something utterly unimportant*";
	return m.get("JMSReplyTo").toString();
    };

    /**
     * references key ->content 
     */
    
    protected Map getWeakMap(){
	return URLNodeMap;
    };
    
    /** references URL prefix-> NodeName */
    private WeakHashMap URLNodeMap = new WeakHashMap(384);

    /**    exported frequently after a change.
     */
    protected Object[] getCache(){
	return arrayCache;
    };

    protected  void setCache(  Object[] arr){
	arrayCache=arr;
    }
    
    /** URLSet is exported frequently after a change. */
    private Object[] arrayCache;

     
    /** set when Set has been modified cacheArray
     */
      public boolean cacheDirty(){
	  return cacheInvalid;
      };
    
    public Reference  referenceValue(Object o){
	return new SoftReference (o,refQ());
    };
    
    public void setDirty(boolean dirt){
	cacheInvalid=dirt;
    }; 
    /** when URLSet next used will rewrite
     * cacheArray
     */
    private boolean cacheInvalid = true;
 
    /** this is used to store the items in custom? order  */
    public  Set getSet(){
	return URLSet;
    };
      
    /** used to define ordering 
     */
    protected Comparator getComparator(){
	return /** sorts strings based on length */
	    new Comparator() {
		    /** Compares its two arguments for order.  In this
		     * case order is defined by strlen and then by
		     * content sorting.*/
		    public int compare(Object o1, Object o2) {
			int res= o1.toString().length() - o2.toString().length();
			if(res==0)
			    res=o1.toString().compareTo(o2.toString());//equal length objects are then copmred as strings
			return res;
		    };
		    /** Indicates whether some other object is "equal
		     * to" this Comparator. */
		    public boolean equals(Object obj) {
			return true;
		    };
		};
    };
    
    /** this is used to store the URLS in order of length */
    private  SortedSet URLSet =
	new TreeSet(getComparator() ); //holds the reference to url strings 
     
    /**
       dispatchRequest    public boolean dispatchRequest(Socket s,MetaProperties n)

       @return whether the request was fulfilled yet.
    */
    public boolean dispatchRequest(Socket s,MetaProperties n)
    {
	String resource=n.get ("Resource").toString();
	String method  =n.get ("Method"  ).toString();
	n.put("Proxy-Request",n.get("Request"));
		
	MetaNode l;

	//it is important to recache before we look up any weak keys
	//from URLNodeMap since cacheArray is holding registrations alive.

	if(cacheInvalid)
	    reCache();
					
	//1 check resource for an exact match in our WeakRefMap
		 		
 
	l=(MetaNode)weakGet(resource);

	if(l==null) {
		int len=resource.length();   
			 
	
		for(int i=arrayCache.length-1 ;i>=0;i--)
		    {
			String temp=arrayCache[i].toString();
			Env.debug(500,"Pattern test on "+resource+":"+temp);
			if(temp.length()>len)
			    continue;
			if(resource.startsWith(temp)){
			    l=(MetaNode)weakGet(temp);
			    Env.debug(500,"Pattern match on "+resource+":"+temp);
			}
		    } 
	    }
	if(l!=null) {
		String lname=l.getJMSReplyTo();
	 	//check to see if the Node that registered this resource is actually present
		if(Env.getRouter("IPC").hasElement(lname))
		    {
			//yes?  experimental...  just dump the
			//inbound Socket right into a
			//Notification... since we're certain
			//a node exists by this name
			n.put("_Socket",s);
			n.put("JMSDestination",lname );
			n.put("JMSType","httpd");
			n.put("JMSReplyTo","nobody");//apparently we *MUST* give ourselves a name..
			Env.send(n);
			return true; 	
		    } 
		//3 create PipeConnection to registered location
		Env.debug(15, getClass().getName()+" creating PipeSocket for "+n.get("Resource").toString());
		PipeSocket p=new httpPipeSocket (s,l,n);
		return true;
	    }; 
	//4 else super.sendFile 
	return false;
    }
  
};
