package owch;

import java.net.*;
import java.io.*;
import java.util.*;

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
