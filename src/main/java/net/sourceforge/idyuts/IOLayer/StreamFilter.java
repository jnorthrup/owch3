package net.sourceforge.idyuts.IOLayer;

import java.io.InputStream;

public abstract interface StreamFilter extends Filter
{
  public abstract void recv(InputStream paramInputStream);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.StreamFilter
 * JD-Core Version:    0.6.0
 */