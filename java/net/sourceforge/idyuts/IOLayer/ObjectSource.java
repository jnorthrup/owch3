
package  net.sourceforge.idyuts.IOLayer;


public interface  ObjectSource extends Source{	
    void attach(ObjectFilter filter); 
    void detach(ObjectFilter filter);  
};	

