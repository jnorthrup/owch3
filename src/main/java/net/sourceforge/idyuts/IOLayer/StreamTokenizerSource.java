package net.sourceforge.idyuts.IOLayer;

public abstract interface StreamTokenizerSource extends Source
{
  public abstract void attach(StreamTokenizerFilter paramStreamTokenizerFilter);

  public abstract void detach(StreamTokenizerFilter paramStreamTokenizerFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.StreamTokenizerSource
 * JD-Core Version:    0.6.0
 */