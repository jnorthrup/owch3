
package  net.sourceforge.idyuts.IOLayer;


public interface  GraphicsSource extends Source{	
    void attach(GraphicsFilter filter); 
    void detach(GraphicsFilter filter);  
};	

