 package net.sourceforge.idyuts.IOPipes;

 import java.util.ArrayList;
 import java.util.List;
 import net.sourceforge.idyuts.IOLayer.ArrayFilter;
 import net.sourceforge.idyuts.IOLayer.ArraySource;

 public abstract class ForEach
   implements ArrayFilter, ArraySource
 {
   int index;
   Object[] arr;
   private List<ArrayFilter> _Array_clients;
   private static Class[] foo = {Object[].class};

   public ForEach()
   {
     index = 0;

     _Array_clients = new ArrayList<ArrayFilter>(1);
   }
   public void attach(ArrayFilter filter) {
     _Array_clients.add(filter);
   }

   public void detach(ArrayFilter filter) {
     _Array_clients.remove(filter);
   }

   public void xmit()
   {
     try
     {
       Object data = arr;

    for (Object filter : _Array_clients) {
      filter.getClass().getMethod("recv", foo).invoke(filter, data);

    }
     }
     catch (Exception e)
     {
       throw new Error("more debugging needed here");
     }
   }
 }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.ForEach
 * JD-Core Version:    0.6.0
 */