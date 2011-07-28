package net.sourceforge.idyuts.IOLayer;

public abstract interface ReaderSource extends Source
{
  public abstract void attach(ReaderFilter paramReaderFilter);

  public abstract void detach(ReaderFilter paramReaderFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.ReaderSource
 * JD-Core Version:    0.6.0
 */