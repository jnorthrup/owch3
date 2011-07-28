package net.sourceforge.owch2.kernel;

import net.sourceforge.idyuts.IOLayer.Filter;

public abstract interface MetaPropertiesFilter extends Filter
{
  public abstract void recv(MetaProperties paramMetaProperties);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.MetaPropertiesFilter
 * JD-Core Version:    0.6.0
 */