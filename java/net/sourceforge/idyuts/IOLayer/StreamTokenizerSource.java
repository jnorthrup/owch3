
package  net.sourceforge.idyuts.IOLayer;


public interface  StreamTokenizerSource extends Source{	
    void attach(StreamTokenizerFilter filter); 
    void detach(StreamTokenizerFilter filter);  
};	

