package owch;

import java.net.*;
import java.io.*;
import java.util.*;


/**
   this class came about by accident one day and seemed to fit..  null
   is all-seeing all forgiving and 100% black hole.
 */
public class nullRouter implements Router{
    public boolean hasElement(Object key){
	return true;
    };   
    public boolean addElement(Map item){
	return true;
    };
    public void remove(Object key){
    }; 

    public Set getPool(){
	return new HashSet(1);
    }; 
    public void send(Map item){
    };
    public Object getDestination(Map item){
	return null;
    };  
 
    public Router getNextOutbound (){
	return null;
    }; 
    public Router getNextInbound (){
	return null;
    };
 

}
