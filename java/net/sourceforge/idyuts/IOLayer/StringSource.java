
package  net.sourceforge.idyuts.IOLayer;


public interface  StringSource extends Source{	
    void attach(StringFilter filter); 
    void detach(StringFilter filter);  
};	

