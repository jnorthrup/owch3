package net.sourceforge.owch2.kernel;

import java.net.InetAddress;

public abstract class ListenerFactory
{
  abstract ListenerReference create(InetAddress paramInetAddress, int paramInt1, int paramInt2);

  public abstract MetaProperties getLocation();
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.ListenerFactory
 * JD-Core Version:    0.6.0
 */