
package  net.sourceforge.idyuts.IOLayer;


public interface  WriterSource extends Source{	
    void attach(WriterFilter filter); 
    void detach(WriterFilter filter);  
};	

