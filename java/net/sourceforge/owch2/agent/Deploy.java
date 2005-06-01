/**
 *Deploy.java
 *@author   Jim Northrup
 */

package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.AbstractAgent;
import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.MetaAgent;
import net.sourceforge.owch2.kernel.MetaProperties;
import net.sourceforge.owch2.router.Router;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

public class Deploy extends AbstractAgent {
    static int uniq = 0;

    public static void main(String[] args) throws Exception {
        Map<? extends Object, ? extends Object> m = Env.getInstance().parseCommandLineArgs(args);
        {
            final String[] ka = {"JMSReplyTo",};

            if (!m.keySet().containsAll(Arrays.asList(ka))) {
                Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                        "Deploy Agent usage:\n\n" +
                        "-name name\n" +
                        "$Id: Deploy.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $\n");
            }
        }
        Deploy d = new Deploy(m);
        Thread t = new Thread();
        t.start();
        while (!d.killFlag) t.sleep(60 * 60 * 3);

    }

    ;

    /*
    *  Client Constructor
    *
    *  Initializes communication
    */

    public Deploy(Map<? extends Object, ? extends Object> map) {
        super(map);
    }

    /**
     * <B>Message:</B> Deploy Fields:<UL> <LI>Class - class to be constructed
     * <LI>Path  - Array of URL Strings for Classpath, or "default" for native classloader
     * <LI> Parameters  -  array of normalized Strings to pass into our  new object
     */
    public void handle_Deploy(MetaProperties n) {
        String _class = (String) n.get("Class");
        if (Env.getInstance().logDebug) Env.getInstance().log(45, "Deplying::Class " + _class);
        String path = (String) n.get("Path");
        if (Env.getInstance().logDebug) Env.getInstance().log(45, "Deplying::Path " + path);
        String parm = (String) n.get("Parameters");
        if (Env.getInstance().logDebug) Env.getInstance().log(45, "Deplying::Parameters " + parm);
        String signature = (String) n.get("Signature");
        if (Env.getInstance().logDebug) Env.getInstance().log(45, "Deplying::Signature " + signature);
        int i;
        java.util.List tokens;
        //we use a classloader based on our reletive origin..
        ClassLoader loader = getClass().getClassLoader();
        String[] tok_arr = new String[]{path, parm, signature};
        Object[][] res_arr = new Object[ tok_arr.length ][];
        StringTokenizer st;
        for (i = 0; i < tok_arr.length; i++) {
            String temp_str = tok_arr[i];
            tokens = new ArrayList();
            if (temp_str == null) {
                if (Env.getInstance().logDebug) Env.getInstance().log(500, "Deploy tokenizing nullinating  " + i);
                temp_str = "";
            }
            st = new StringTokenizer(temp_str);
            while (st.hasMoreElements()) {
                tokens.add(st.nextElement());
            }
            res_arr[i] = tokens.toArray();
            if (Env.getInstance().logDebug)
                Env.getInstance().log(500, "Deploy arr" + i + " found " + res_arr[i].length + " tokens");
        }
        URL[] path_arr = new URL[ res_arr[0].length ];
        Object[] parm_arr = new Object[ res_arr[1].length ];
        Class[] sig_arr = new Class[ res_arr[2].length ];
        try {
            //path is URL's, gotta do a loop to instantiate URL's...
            for (i = 0; i < res_arr[0].length; i++) {
                path_arr[i] = new URL((String) res_arr[0][i]);
            }
            //parms are strings, easy copy there...
            System.arraycopy(res_arr[1], 0, parm_arr, 0, res_arr[1].length);
            //determine if our loader is based on URLS....
            if (path_arr.length != 0) //if we have URL's we need
            // to make a new loader that adds these URLS to our path
            {
                loader = new URLClassLoader(path_arr, loader);
            }
            //user our loader to populate sig_arr with a Class[]
            for (i = 0; i < res_arr[2].length; i++) {
                sig_arr[i] = loader.loadClass((String) res_arr[2][i]);
            }

            /* this creates a new Object */

            loader.loadClass(_class).getConstructor(sig_arr).newInstance(parm_arr);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle_DeployNode(MetaProperties n) {
        String _class = (String) n.get("Class");
        if (Env.getInstance().logDebug) Env.getInstance().log(45, "Deplying::Class " + _class);
        String path = (String) n.get("Path");
        if (Env.getInstance().logDebug) Env.getInstance().log(45, "Deplying::Path " + path);
        int i;
        java.util.List tokens;
        //we use a classloader based on our reletive origin..
        ClassLoader loader = getClass().getClassLoader();
        String[] tok_arr = new String[]{path,};
        URL[] path_arr = new URL[]{};
        Object[][] res_arr = new Object[ tok_arr.length ][];
        StringTokenizer st;
        for (i = 0; i < tok_arr.length; i++) {
            String temp_str = tok_arr[i];
            tokens = new ArrayList();
            if (temp_str == null) {
                if (Env.getInstance().logDebug) Env.getInstance().log(500, "Deploy tokenizing nullinating  " + i);
                temp_str = "";
            }
            st = new StringTokenizer(temp_str);
            while (st.hasMoreElements()) {
                tokens.add(st.nextElement());
            }
            res_arr[i] = tokens.toArray();
            if (Env.getInstance().logDebug)
                Env.getInstance().log(500, "Deploy arr" + i + " found " + res_arr[i].length + " tokens");
        }
        try {
            if (!n.containsKey("Singleton")) {
                n.put("JMSReplyTo", n.getJMSReplyTo() + "." + uniq++ + "." + getJMSReplyTo());
            } else {
                n.put("JMSReplyTo", n.getJMSReplyTo());

            }
            //path is URL's, gotta do a loop to instantiate URL's...
            for (i = 0; i < res_arr[0].length; i++) {
                path_arr[i] = new URL((String) res_arr[0][i]);
            }

            /* this creates a new Object */
            Object o =
                    loader.loadClass(_class).getConstructor(
                            new Class[]{Map.class}).newInstance(
                            new Object[]{n});
            //use our Notification as a bootstrap of parms
            if (o instanceof MetaAgent) {
                Env.getInstance().getRouter("owch").remove(((MetaAgent) o).getJMSReplyTo());
                Router r = Env.getInstance().getRouter("IPC");
                r.addElement((Map) o);
            }
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


