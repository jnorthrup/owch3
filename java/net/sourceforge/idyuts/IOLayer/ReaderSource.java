
package  net.sourceforge.idyuts.IOLayer;


public interface  ReaderSource extends Source{	
    void attach(ReaderFilter filter); 
    void detach(ReaderFilter filter);  
};	

