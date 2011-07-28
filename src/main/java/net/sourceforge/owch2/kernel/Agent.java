package net.sourceforge.owch2.kernel;

public abstract interface Agent extends MetaAgent, MetaPropertiesFilter
{
  public abstract Object getValue(String paramString);

  public abstract void putValue(String paramString, Object paramObject);

  public abstract boolean isParent();

  public abstract void linkTo(String paramString);

  public abstract void send(MetaProperties paramMetaProperties);

  public abstract void recv(MetaProperties paramMetaProperties);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.Agent
 * JD-Core Version:    0.6.0
 */