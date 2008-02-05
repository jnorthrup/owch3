/**
 *Deploy.java
 *@author Jim Northrup
 */

package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.net.*;
import java.util.*;

public class Deploy extends AbstractAgent {

    static int uniq = 0;
    private boolean killFlag;

    public Deploy(Map.Entry<CharSequence, Object>... p) {
        super(p);
    }

    public Deploy() {
    }

    public Deploy(Iterable<Map.Entry<CharSequence, Object>> entryIterable) {
        super(entryIterable.iterator());
    }

    public Deploy(Iterator<Map.Entry<CharSequence, Object>> entryIterator) {
        super(entryIterator);
    }

    public static void main(String[] args) throws Exception {
//        Map<CharSequence, Object> m = getMap(Env.getInstance().parseCommandLineArgs(args));
//        {
//            final String[] ka = {ImmutableNotification.FROM_KEY,};
//
//            if (!m.keySet().containsAll(Arrays.asList(ka))) {
//                Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
//                        "Deploy Agent usage:\n\n" +
//                        "-name name\n" +
//                        "$Id$\n");
//            }
//        }
//        Deploy d = new Deploy(m);
//        Thread t = new Thread();
//        t.start();
//        while (!d.killFlag) t.sleep(1000 * 60 * 3);
//

        Deploy deploy = new Deploy(Env.getInstance().parseCommandLineArgs());
    }

    /*
    *  Client Constructor
    *
    *  Initializes communication
    */

    public Deploy(Map<CharSequence, Object> map) {
        super(map.entrySet());
    }

    /**
     * <B>Notification:</B> Deploy Fields:<UL> <LI>Class - class to be constructed
     * <LI>Path  - Array of URL Strings for Classpath, or "default" for native classloader
     * <LI> Parameters  -  array of normalized Strings to pass into our  new object
     */
    public void handle_Deploy(Notification n) {

        final Map<CharSequence, Object> hashMap = getMap(n);
        String _class = (String) hashMap.get("Class");
        String path = (String) hashMap.get("Path");
        String parm = (String) hashMap.get("Parameters");
        String signature = (String) hashMap.get("Signature");
        int i;
        List<Object> tokens;
        //we use a classloader based on our reletive origin..
        ClassLoader loader = getClass().getClassLoader();
        String[] tok_arr = new String[]{path, parm, signature};
        Object[][] res_arr = new Object[tok_arr.length][];
        StringTokenizer st;
        for (i = 0; i < tok_arr.length; i++) {
            String temp_str = tok_arr[i];
            tokens = new ArrayList<Object>();
            if (temp_str == null) {
                temp_str = "";
            }
            st = new StringTokenizer(temp_str);
            while (st.hasMoreElements()) {
                tokens.add(st.nextElement());
            }
            res_arr[i] = tokens.toArray();
        }
        URL[] path_arr = new URL[res_arr[0].length];
        Object[] parm_arr = new Object[res_arr[1].length];
        Class[] sig_arr = new Class[res_arr[2].length];
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
            for (i = 0; i < res_arr[2].length; i++)
                sig_arr[i] = loader.loadClass((String) res_arr[2][i]);
            /* this creates a new Object */

            loader.loadClass(_class).getConstructor(sig_arr).newInstance(parm_arr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: repair
    //    public void handle_DeployNode(Notification notification) {
    //        HashMap n = getMap(notification);
    //        String _class = (String) n.get("Class");
    //        String path = (String) n.get("Path");
    //        int i;
    //        List<Object> tokens;
    //        //we use a classloader based on our reletive origin..
    //        ClassLoader loader = getClass().getClassLoader();
    //        String[] tok_arr = new String[]{path,};
    //        URL[] path_arr = new URL[]{};
    //        Object[][] res_arr = new Object[tok_arr.length][];
    //        StringTokenizer st;
    //        for (i = 0; i < tok_arr.length; i++) {
    //            String temp_str = tok_arr[i];
    //            tokens = new ArrayList<Object>();
    //            if (temp_str == null) {
    //                temp_str = "";
    //            }
    //            st = new StringTokenizer(temp_str);
    //            while (st.hasMoreElements()) {
    //                tokens.add(st.nextElement());
    //            }
    //            res_arr[i] = tokens.toArray();
    //        }
    //        try {
    //            if (!n.containsKey("Singleton")) {
    //                n.put(ImmutableNotification.FROM_KEY, notification.getFrom()) + "." + uniq + "." + getFrom());
    //                uniq++;
    //            } else {
    //                n.put(FROM_KEY, notif.getFrom());
    //
    //            }
    //            //path is URL's, gotta do a loop to instantiate URL's...
    //            for (i = 0; i < res_arr[0].length; i++) {
    //                path_arr[i] = new URL((String) res_arr[0][i]);
    //            }
    //
    //            /* this creates a new Object */
    //            Object Location = loader.loadClass(_class).getConstructor(new Class[]{Map.class}).newInstance(new Object[]{n});
    //            //use our Notification as a bootstrap of parms
    //            if (Location instanceof ImmutableNotification) {
    //                throw new Error("return to fix this...");
    ////                owch.remove(((Notification) Notification).getFrom());
    ////                ipc.hasPath((Map) Notification);
    //            }
    //            return;
    //        }
    //        catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

    public Object getValue(CharSequence key) {
        return get(key);  //To change body of implemented methods use File | Settings | File Templates.
    }


}


