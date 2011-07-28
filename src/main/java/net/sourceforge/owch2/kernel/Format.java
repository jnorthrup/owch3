package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract interface Format
{
  public abstract void read(InputStream paramInputStream, Map paramMap)
    throws IOException;

  public abstract void write(OutputStream paramOutputStream, Map paramMap)
    throws IOException;
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.Format
 * JD-Core Version:    0.6.0
 */