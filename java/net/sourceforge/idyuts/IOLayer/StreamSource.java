
package  net.sourceforge.idyuts.IOLayer;


public interface  StreamSource extends Source{	
    void attach(StreamFilter filter); 
    void detach(StreamFilter filter);  
};	

