
package  net.sourceforge.idyuts.IOLayer;


public interface  CollectionSource extends Source{	
    void attach(CollectionFilter filter); 
    void detach(CollectionFilter filter);  
};	

