package owch;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @version $Id: Router.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public interface Router {
    boolean hasElement(Object key);   
    boolean addElement(Map item);   
    public void remove(Object key); 
    Set getPool(); 
    void send(Map item);
    Object getDestination(Map item);  
 
    Router getNextOutbound (); 
    Router getNextInbound ();
 

}
