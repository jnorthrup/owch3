  package net.sourceforge.owch2.router;

  import java.util.Map;
  import java.util.Set;
  import java.util.TreeSet;
  import net.sourceforge.owch2.kernel.Env;

  public class defaultRouter
    implements Router
  {
    public Set getPool()
    {
      return new TreeSet();
    }

    public void send(Map item) {
      boolean hasParent = Env.getParentNode() != null;
      if (!hasParent) {
        Env.log(10, "dropping item" + item.toString());
        return;
      }
      item.put("URL", Env.getDefaultURL());
      Env.getRouter("owch").send(item);
    }

    public void remove(Object key) {
    }

    public Object getDestination(Map item) {
      return null;
    }

    public boolean addElement(Map item) {
      return false;
    }

    public boolean hasElement(Object key) {
      return Env.getParentNode() != null;
    }
  }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.defaultRouter
 * JD-Core Version:    0.6.0
 */