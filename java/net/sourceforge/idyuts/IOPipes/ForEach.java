package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public abstract class ForEach implements ArrayFilter, ArraySource {
    int index = 0;
    Object[] arr;
    private java.util.List _Array_clients = new ArrayList(1);

    public void attach(ArrayFilter filter) {
        _Array_clients.add(filter);
    };

    public void detach(ArrayFilter filter) {
        _Array_clients.remove(filter);
    };

    private static Class[] foo = new Class[]{Object[].class};

    //  ArraySourceNode **DUPE**
    public void xmit() {
        try {
            Object data = arr;
            for (int ci = 0; ci < _Array_clients.size(); ci++) {
                ArrayFilter filter = (ArrayFilter) _Array_clients.get(ci);
                filter.getClass().getMethod("recv", foo).invoke(filter,
                        new Object[]{data});
            }
        }
        catch (Exception e) {
            throw new Error("more debugging needed here");
        }
        ;
    };
    // ArrayFilterNode  **END**
}


