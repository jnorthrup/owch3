 package net.sourceforge.owch2.agent;



 import java.net.URL;
 import java.net.URLClassLoader;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Map;

 import java.util.StringTokenizer;
 import net.sourceforge.owch2.kernel.AbstractAgent;
 import net.sourceforge.owch2.kernel.Env;
 import net.sourceforge.owch2.kernel.MetaAgent;
 import net.sourceforge.owch2.kernel.MetaProperties;
 import net.sourceforge.owch2.router.Router;

 public class Deploy extends AbstractAgent
 {
   static int uniq = 0;



 public static void main(String[] args) throws Exception {
   Map m = Env.parseCommandLineArgs(args);

   String[] ka = { "JMSReplyTo" };

   if (!m.keySet().containsAll(Arrays.asList(ka))) {
     Env.cmdLineHelp("\n\n******************** cmdline syntax error\nDeploy Agent usage:\n\n-name name\n$Id: Deploy.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
   }

   Deploy d = new Deploy(m);
   Thread t = new Thread();
   t.start();
   while (!d.killFlag)
     Thread.sleep(10800L);
 }

 public Deploy(Map map)
 {
   super(map);
 }

 public void handle_Deploy(MetaProperties n)
 {
   String _class = (String)n.get("Class");
   Env.log(45, "Deplying::Class " + _class);
   String path = (String)n.get("Path");
   Env.log(45, "Deplying::Path " + path);
   String parm = (String)n.get("Parameters");
   Env.log(45, "Deplying::Parameters " + parm);
   String signature = (String)n.get("Signature");
   Env.log(45, "Deplying::Signature " + signature);

   ClassLoader loader = getClass().getClassLoader();
   String[] tok_arr = { path, parm, signature };
   Object[][] res_arr = new Object[tok_arr.length][];

   for (int i = 0; i < tok_arr.length; i++) {
     String temp_str = tok_arr[i];
     List<Object> tokens = new ArrayList<Object>();
     if (temp_str == null) {
       Env.log(500, "Deploy tokenizing nullinating  " + i);
       temp_str = "";
     }
     StringTokenizer st = new StringTokenizer(temp_str);
     while (st.hasMoreElements()) {
       tokens.add(st.nextElement());
     }
     res_arr[i] = tokens.toArray();
     Env.log(500, "Deploy arr" + i + " found " + res_arr[i].length + " tokens");
   }
   URL[] path_arr = new URL[res_arr[0].length];
   Object[] parm_arr = new Object[res_arr[1].length];
   Class[] sig_arr = new Class[res_arr[2].length];
   try
   {


    for (int i = 0; i < res_arr[0].length; i++) {
          path_arr[i] = new URL((String)res_arr[0][i]);
        }

        System.arraycopy(res_arr[1], 0, parm_arr, 0, res_arr[1].length);

        if (path_arr.length != 0)
        {
          loader = new URLClassLoader(path_arr, loader);
        }

        for (int i = 0; i < res_arr[2].length; i++) {
          sig_arr[i] = loader.loadClass((String)res_arr[2][i]);
        }

        loader.loadClass(_class).getConstructor(sig_arr).newInstance(parm_arr);
        return;
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void handle_DeployNode(MetaProperties n) {
      String _class = (String)n.get("Class");
      Env.log(45, "Deplying::Class " + _class);
      String path = (String)n.get("Path");
      Env.log(45, "Deplying::Path " + path);

      ClassLoader loader = getClass().getClassLoader();
      String[] tok_arr = { path };
      URL[] path_arr = new URL[0];
      Object[][] res_arr = new Object[tok_arr.length][];

      for (int i = 0; i < tok_arr.length; i++) {
        String temp_str = tok_arr[i];
        List<Object> tokens = new ArrayList<Object>();
        if (temp_str == null) {
          Env.log(500, "Deploy tokenizing nullinating  " + i);
          temp_str = "";
        }
        StringTokenizer st = new StringTokenizer(temp_str);
        while (st.hasMoreElements()) {
          tokens.add(st.nextElement());
        }
        res_arr[i] = tokens.toArray();
        Env.log(500, "Deploy arr" + i + " found " + res_arr[i].length + " tokens");
      }
      try {
        if (!n.containsKey("Singleton")) {
          n.put("JMSReplyTo", n.getJMSReplyTo() + "." + uniq++ + "." + getJMSReplyTo());
        }
        else {
          n.put("JMSReplyTo", n.getJMSReplyTo());
        }

        for (int i = 0; i < res_arr[0].length; i++) {
          path_arr[i] = new URL((String)res_arr[0][i]);
        }

        Object o = loader.loadClass(_class).getConstructor(new Class[] { Map.class }).newInstance(n);

        if ((o instanceof MetaAgent)) {
          Env.getRouter("owch").remove(((MetaAgent)o).getJMSReplyTo());
          Router r = Env.getRouter("IPC");
          r.addElement((Map)o);
        }
        return;
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.Deploy
 * JD-Core Version:    0.6.0
 */