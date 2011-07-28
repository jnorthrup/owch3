package net.sourceforge.idyuts.IOLayer;

public abstract interface MatrixSource extends Source
{
  public abstract void attach(MatrixFilter paramMatrixFilter);

  public abstract void detach(MatrixFilter paramMatrixFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOLayer.MatrixSource
 * JD-Core Version:    0.6.0
 */