package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.StringFilter;
import net.sourceforge.idyuts.IOLayer.StringSource;

public abstract interface StringFunctor extends StringSource, StringFilter
{
  public abstract String fire(String paramString);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.StringFunctor
 * JD-Core Version:    0.6.0
 */