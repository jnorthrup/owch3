
package  net.sourceforge.idyuts.IOLayer;


public interface  DatagramPacketSource extends Source{	
    void attach(DatagramPacketFilter filter); 
    void detach(DatagramPacketFilter filter);  
};	

