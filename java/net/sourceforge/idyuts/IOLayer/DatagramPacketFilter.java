package net.sourceforge.idyuts.IOLayer;
 
public interface DatagramPacketFilter extends Filter {
	public void recv(java.net.DatagramPacket data );
 	
}
