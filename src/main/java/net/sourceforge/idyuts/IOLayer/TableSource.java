package net.sourceforge.idyuts.IOLayer;

public abstract interface TableSource extends Source
{
  public abstract void attach(TableFilter paramTableFilter);

  public abstract void detach(TableFilter paramTableFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.TableSource
 * JD-Core Version:    0.6.0
 */