package net.sourceforge.owch2.router;

import java.util.Map;
import java.util.Set;

public abstract interface Router
{
  public abstract void remove(Object paramObject);

  public abstract Set getPool();

  public abstract void send(Map paramMap);

  public abstract Object getDestination(Map paramMap);

  public abstract boolean addElement(Map paramMap);

  public abstract boolean hasElement(Object paramObject);
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.Router
 * JD-Core Version:    0.6.0
 */