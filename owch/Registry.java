
package owch ;
import java.util.*; 
import java.io.*;
import java.lang.ref.*;

import java.net.*;

/**
 * gatekeeper registers a prefix of an Item such as "/cgi-bin/foo.cgi"
 * The algorithm to locate the Item works in 2 phases;<OL>
 *
 * <LI> The weakHashMap is checked for an exact match.
 *
 * <LI> The arraycache is then checked from top to bottom to see if
 * Item startswith (element <n>) </OL>
 *
 * The when an Item is located -- registering the Item "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a
 * waiting pipeline
 */
public abstract  class  Registry 
{
    /**
     * references key ->content 
     */
    
    abstract  protected Map getWeakMap(); 

    protected Reference refGet(Object key){
	return (Reference) getWeakMap().get(key);
    };

    protected Object weakGet(Object key){
	if(key==null)
	    return null;
	Reference r=refGet(key);
	if(r==null)
	    return null;
	Object o=r.get();
	return o;
    };

    /**    exported frequently after a change.
     */
    abstract protected Object[] getCache();

    abstract protected  void setCache(  Object[] arr);
    
    /** set when Set has been modified cacheArray
     */
    abstract public boolean cacheDirty();
    
    abstract public void setDirty(boolean dirt);
    
    /** this is used to store the items in custom? order  */
    abstract public  Set getSet();
    
    /** used to define ordering 
     */
    abstract  protected Comparator getComparator();
    
    abstract public String displayKey( Comparable  key);

    abstract public String displayValue(Reference o);
  
    abstract public Reference  referenceValue(Object o);
    
    protected ReferenceQueue itemQ=new ReferenceQueue();

   /** register the beginning of a tree
     */
    protected ReferenceQueue refQ()
    {
	return itemQ;
    }

    public  void registerItem (Comparable key , Object val){
	registerItem( key,referenceValue(val));
    }

    protected void registerItem ( Comparable key , Reference val)
    {
     	synchronized(getSet()){ 
	    getSet().add(key);
	    setDirty(true);
	};
        getWeakMap().put(key, val);
	Env.debug(15,getClass().getName()+":::Item Registration:" +"@"+displayKey(key)+" -- "+ displayValue(val));
 
    };
    /** unregister the tree item */
    public void unregisterItem(Comparable key ) {    
 
	synchronized(getSet()){      
	    getSet().remove(key); 
	    setDirty(true);
			};
	Env.debug(15,getClass().getName()+":::Item DeRegistration:" + displayKey(key) );
	
    };
    
    /** this renews our cache for specific custom ordered results.
     */
    protected void reCache(){
	Env.debug(150,getClass().getName()+" recache starting..");
	synchronized(getSet()){
	    setCache(getSet().toArray());
	    setDirty(false);
	    Env.debug(150,getClass().getName()+" recache done..");
	};
	Env.debug(150,getClass().getName()+" - recache fin..");	
    };
};
