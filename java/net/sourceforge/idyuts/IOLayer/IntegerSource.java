
package  net.sourceforge.idyuts.IOLayer;


public interface  IntegerSource extends Source{	
    void attach(IntegerFilter filter); 
    void detach(IntegerFilter filter);  
};	

