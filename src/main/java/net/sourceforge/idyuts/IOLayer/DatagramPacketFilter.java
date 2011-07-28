package net.sourceforge.idyuts.IOLayer;

import java.net.DatagramPacket;

public abstract interface DatagramPacketFilter extends Filter
{
  public abstract void recv(DatagramPacket paramDatagramPacket);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.DatagramPacketFilter
 * JD-Core Version:    0.6.0
 */