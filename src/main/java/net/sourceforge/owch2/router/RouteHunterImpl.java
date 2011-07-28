  package net.sourceforge.owch2.router;

  import java.util.Collection;
  import java.util.Iterator;
  import java.util.Map;
  import net.sourceforge.owch2.kernel.Env;

  public abstract class RouteHunterImpl
    implements RouteHunter
  {
    public void send(Map item)
    {
      if (item.get("JMSReplyTo") == null) {
        Env.log(500, "*** dropping nameless message");
        return;
      }
      if (item.get("JMSReplyTo") == item.get("JMSDestination")) {
        Env.log(500, "*** dropping routeless");
        return;
      }
      boolean sated = false;

  for (Object o1 : getOutbound()) {

    Router router = (Router) o1;

    Env.log(500, "*** " + router.getClass().getName() + " testing " + item.toString()  );

    Object dest = router.getDestination(item);

    sated = router.hasElement(dest);

    if (sated) {

      router.send(item);

      break;

    }

  }


  for (Object o : getOutbound()) {

    Router router = (Router) o;

    if (router.addElement(item))
  break;

  }
    }

    public void remove(Object key)
    {
      for (Object o : getInbound()) {
        Router r = (Router) o;
        r.remove(key);
      }
    }
  }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.RouteHunterImpl
 * JD-Core Version:    0.6.0
 */