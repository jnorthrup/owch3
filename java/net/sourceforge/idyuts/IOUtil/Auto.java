package net.sourceforge.idyuts.IOUtil;

import net.sourceforge.owch2.kernel.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Auto static util class. binds a Source to a Filter, by interrogating Reflection 1) first attempts to fit perfectly by
 * Source.attach(Filter.getInterfaces) 2) then brute forces all the attach app
 */
final public class Auto {
    private static final boolean dbg_output = false;

    /** internal recursive parental descent method */
    private static void walkParents(Class c, Collection col) {
        Collection clt = new ArrayList();
        clt.add(c.getSuperclass());
        clt.addAll(Arrays.asList(c.getInterfaces()));
        for (Iterator i = clt.iterator(); i.hasNext();) {
            Class t = (Class) i.next();
            if (t == null) {
                continue;
            }
            col.add(t);
            //	System.out.println("walkParents +="+t.getName());
            walkParents(t, col);
        }
        //System.out.println("walkParents +="+clt.toString());
        col.addAll(clt);
    }

    /** recursive method which will go through all parent interfaces until one matches the source attach app */
    private final static Class[] examplar = new Class[]{};

    public static boolean attach(Object source, Object filter) {
        try {
            List l = new ArrayList();
            Class[] ifs;
            {
                HashSet hs = new HashSet();
                walkParents(filter.getClass(), hs);
                ifs = (Class[]) hs.toArray(examplar);
            }
            ;
            // System.out.println("ifaces: "+ifs.length);
            Method[] app = source.getClass().getMethods();
            Method the1 = null;
            for (int i = 0; i < app.length; i++) {
                if ("attach".intern() == app[i].getName().intern()) {
                    Class[] cl = app[i].getParameterTypes();
                    for (int j = 0; j < ifs.length; j++) {
                        //System.out.println("comparing
                        // "+cl[0].getName()+":"+ifs[j]);
                        if (cl[0] == ifs[j]) {
                            if (dbg_output) {
                                if (Env.logDebug) Env.log(499, "Auto.attach:" + cl[0]);
                            }
                            the1 = app[i];
                            break;
                        }
                        ;
                    }
                    ;
                }
                ;
                if (the1 != null) {
                    the1.invoke(source,
                            new Object[]{filter});
                    return true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}


