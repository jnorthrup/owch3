package net.sourceforge.idyuts.IOLayer;

public abstract interface StreamSource extends Source
{
  public abstract void attach(StreamFilter paramStreamFilter);

  public abstract void detach(StreamFilter paramStreamFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.StreamSource
 * JD-Core Version:    0.6.0
 */