package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public abstract class StringFunctorImpl implements StringFunctor {
    public void recv(String data) {
        synchronized (this) {
            stage = fire(data);
            xmit();
        }
        ;
    };

    private List clients = new ArrayList(1);
    private String stage;

    //add additional interfaces as needed
    final public static Class[][] _String_filters = new Class[][]{{
        String.class, // StringFilterNode  **FILTER TYPES MERGE**
    },
    };

    public Class[][] getFilters() {
        return _String_filters;
    };

    // StringFilterNode  **END**
    //  StringSourceNode **BEGIN** **UNIQUE SIGNATURES**
    private List _String_clients = new ArrayList(1);

    public void attach(StringFilter filter) {
        _String_clients.add(filter);
    };

    public void detach(StringFilter filter) {
        _String_clients.remove(filter);
    };

    private static Class[] foo = new Class[]{Object[].class};

    //  StringSourceNode **DUPE**
    public void xmit() {
        try {
            Object data = null; //YOUR OBJECT
            for (int ci = 0; ci < _String_clients.size(); ci++) {
                StringFilter filter = (StringFilter) _String_clients.get(ci);
                filter.getClass().getMethod("recv", foo).invoke(filter,
                        new Object[]{data});
                //YOUR CODE GOES HERE
                throw new Error("unfinished code");
            }
        }
        catch (Exception e) {
            throw new Error("more debugging needed here");
        }
        ;
    };

    final public static Class[][] _String_sources = new Class[][]{{
        String.class, //  StringSourceNode **SOURCE TYPES MERGE**
    },
                                                                  //add additional types as needed.
    };

    public Class[][] getSources() {
        return _String_sources;
    };
    //  StringSourceNode **END**
}

;


