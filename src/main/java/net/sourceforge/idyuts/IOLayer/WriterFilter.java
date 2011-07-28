package net.sourceforge.idyuts.IOLayer;

import java.io.Writer;

public abstract interface WriterFilter extends Filter
{
  public abstract void recv(Writer paramWriter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.WriterFilter
 * JD-Core Version:    0.6.0
 */