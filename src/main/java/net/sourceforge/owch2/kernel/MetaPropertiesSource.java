package net.sourceforge.owch2.kernel;

import net.sourceforge.idyuts.IOLayer.Source;

public abstract interface MetaPropertiesSource extends Source
{
  public abstract void attach(MetaPropertiesFilter paramMetaPropertiesFilter);

  public abstract void detach(MetaPropertiesFilter paramMetaPropertiesFilter);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.MetaPropertiesSource
 * JD-Core Version:    0.6.0
 */