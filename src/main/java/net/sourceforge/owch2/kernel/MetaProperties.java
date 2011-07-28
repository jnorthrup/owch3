package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract interface MetaProperties extends MetaAgent, Map
{
  public abstract void load(InputStream paramInputStream)
    throws IOException;

  public abstract String getURL();

  public abstract void save(OutputStream paramOutputStream)
    throws IOException;

  public abstract String getFormat();

  public abstract void setFormat(String paramString);

  public abstract String getJMSReplyTo();
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.MetaProperties
 * JD-Core Version:    0.6.0
 */