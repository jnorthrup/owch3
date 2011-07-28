package net.sourceforge.idyuts.IOLayer;

import java.io.Reader;

public abstract interface ReaderFilter extends Filter
{
  public abstract void recv(Reader paramReader);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.ReaderFilter
 * JD-Core Version:    0.6.0
 */