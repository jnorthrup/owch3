
package  net.sourceforge.idyuts.IOLayer;


public interface  MapSource extends Source{	
    void attach(MapFilter filter); 
    void detach(MapFilter filter);  
};	

