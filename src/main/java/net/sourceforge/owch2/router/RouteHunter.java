package net.sourceforge.owch2.router;

import java.util.Collection;
import java.util.Map;

public abstract interface RouteHunter
{
  public abstract void remove(Object paramObject);

  public abstract void send(Map paramMap);

  public abstract Collection getOutbound();

  public abstract void setOutbound(Collection paramCollection);

  public abstract Collection getInbound();

  public abstract void setInbound(Collection paramCollection);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.RouteHunter
 * JD-Core Version:    0.6.0
 */