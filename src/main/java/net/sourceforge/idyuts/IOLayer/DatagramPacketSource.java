package net.sourceforge.idyuts.IOLayer;

public abstract interface DatagramPacketSource extends Source
{
  public abstract void attach(DatagramPacketFilter paramDatagramPacketFilter);

  public abstract void detach(DatagramPacketFilter paramDatagramPacketFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.DatagramPacketSource
 * JD-Core Version:    0.6.0
 */