
package  net.sourceforge.idyuts.IOLayer;


public interface  MatrixSource extends Source{	
    void attach(MatrixFilter filter); 
    void detach(MatrixFilter filter);  
};	

