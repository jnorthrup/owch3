package net.sourceforge.owch2.kernel;

public abstract interface ListenerReference
{
  public abstract void expire();

  public abstract ServerWrapper getServer();

  public abstract String getProtocol();

  public abstract int getThreads();

  public abstract long getExpiration();
}

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.ListenerReference
 * JD-Core Version:    0.6.0
 */