
package  net.sourceforge.idyuts.IOLayer;


public interface  ArraySource extends Source{	
    void attach(ArrayFilter filter); 
    void detach(ArrayFilter filter);  
};	

