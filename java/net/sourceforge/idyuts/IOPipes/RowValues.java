package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public class RowValues implements TableFilter, ArraySource {
    //  ArraySourceNode **BEGIN** **UNIQUE SIGNATURES**
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
            Object data = null; //YOUR OBJECT
            for (int ci = 0; ci < _Array_clients.size(); ci++) {
                ArrayFilter filter = (ArrayFilter) _Array_clients.get(ci);
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

    final public static Class[][] _Array_sources = new Class[][]{{
        Object[].class, //  ArraySourceNode **SOURCE TYPES MERGE**
    },
                                                                 //add additional types as needed.
    };

    public Class[][] getSources() {
        return _Array_sources;
    };

    //  ArraySourceNode **END**
    // TableFilterNode  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(Object[][] data) {
        //YOUR CODE GOES HERE
        //you probably want to end with an xmit() call
        //if this is also a Source interface.
    };

    //add additional interfaces as needed
    final public static Class[][] _Table_filters = new Class[][]{{
        Object[][].class, // TableFilterNode
        // **FILTER TYPES MERGE**
    },
    };

    public Class[][] getFilters() {
        return _Table_filters;
    };
    // TableFilterNode  **END**
}


