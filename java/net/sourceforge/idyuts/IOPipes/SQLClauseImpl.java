package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

/** simple class to give a prefix and a seperator to an obiquitous array (strings) */
public abstract class SQLClauseImpl implements ArrayFilter, StringSource {
    protected java.util.List clients = new ArrayList(1);
    protected String stage;

    public void attach(StringFilter filter) {
        clients.add(filter);
    };

    public void detach(StringFilter filter) {
        clients.remove(filter);
    };

    //add additional interfaces as needed
    final public static Class[][] String_sources = new Class[][]{{String.class},
                                                                 //add additional types as needed.
    };

    public Class[][] getSources() {
        return String_sources;
    };

    public void xmit() {
        try {
            for (int ci = 0; ci < clients.size(); ci++) {
                StringFilter filter = (StringFilter) clients.get(ci);
                filter.recv(stage);
            }
        }
        catch (Exception e) {
            throw new Error("more debugging needed here");
        }
        ;
    };

    public void recv(Object[] data) {
        //YOUR CODE GOES HERE
        //you probably want to end with an xmit() call
        //if this is also a Source interface.
    };

    protected String f_loop(Object[] s, String pref, String sep) {
        String a, t, sql = "";
        for (int ti = 0; ti < s.length; ti++) {
            t = "" + s[ti];
            if (ti == 0) {
                a = pref;
            }
            else {
                a = sep;
            }
            sql = sql + a + t;
        }
        ;
        return sql;
    }

    //add additional interfaces as needed
    final public static Class[][] Array_filters = new Class[][]{{Object[].class},
                                                                //add additional types as needed.
    };

    public Class[][] getFilters() {
        return Array_filters;
    };
}


