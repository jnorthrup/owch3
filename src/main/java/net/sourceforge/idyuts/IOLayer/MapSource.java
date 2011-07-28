package net.sourceforge.idyuts.IOLayer;

public abstract interface MapSource extends Source
{
  public abstract void attach(MapFilter paramMapFilter);

  public abstract void detach(MapFilter paramMapFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.MapSource
 * JD-Core Version:    0.6.0
 */