
package  net.sourceforge.idyuts.IOLayer;


public interface  BoolSource extends Source{	
    void attach(BoolFilter filter); 
    void detach(BoolFilter filter);  
};	

