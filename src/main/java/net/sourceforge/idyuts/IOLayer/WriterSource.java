package net.sourceforge.idyuts.IOLayer;

public abstract interface WriterSource extends Source
{
  public abstract void attach(WriterFilter paramWriterFilter);

  public abstract void detach(WriterFilter paramWriterFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.WriterSource
 * JD-Core Version:    0.6.0
 */