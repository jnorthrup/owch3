
package  net.sourceforge.idyuts.IOLayer;


public interface  NumberSource extends Source{	
    void attach(NumberFilter filter); 
    void detach(NumberFilter filter);  
};	

