package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public class Three implements intSource {
    //  intSourceNode **BEGIN** **UNIQUE SIGNATURES**
    private java.util.List _int_clients = new ArrayList(1);

    public void attach(intFilter filter) {
        _int_clients.add(filter);
    };

    public void detach(intFilter filter) {
        _int_clients.remove(filter);
    };

    static final public int data = 3;

    private static Class[] foo = new Class[]{int.class};

    //  intSourceNode **DUPE**
    public void xmit() {
        try {
            for (int ci = 0; ci < _int_clients.size(); ci++) {
                intFilter filter = (intFilter) _int_clients.get(ci);
                filter.recv(data);
            }
        }
        catch (Exception e) {
            throw new Error("more debugging needed here");
        }
        ;
    };

    final public static Class[][] _int_sources = new Class[][]{{
        int.class, //  intSourceNode **SOURCE TYPES MERGE**
    },
                                                               //add additional types as needed.
    };

    public Class[][] getSources() {
        return _int_sources;
    };
    //  intSourceNode **END**
}


