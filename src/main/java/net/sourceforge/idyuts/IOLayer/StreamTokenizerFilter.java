package net.sourceforge.idyuts.IOLayer;

import java.io.StreamTokenizer;

public abstract interface StreamTokenizerFilter extends Filter
{
  public abstract void recv(StreamTokenizer paramStreamTokenizer);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.StreamTokenizerFilter
 * JD-Core Version:    0.6.0
 */