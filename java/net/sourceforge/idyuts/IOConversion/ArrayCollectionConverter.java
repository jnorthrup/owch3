package net.sourceforge.idyuts.IOConversion;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public class ArrayCollectionConverter implements ArrayFilter, CollectionSource {
    protected Collection data;

    // ArrayFilterNode  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(Object[] data) {
        System.out.println(getClass().getName() + "recv" + data);
        this.data = Arrays.asList(data);
        xmit();
    };

    //add additional interfaces as needed
    final public static Class[][] _Array_filters = new Class[][]{{
        Object[].class, // ArrayFilterNode
        // **FILTER TYPES MERGE**
    },
    };

    public Class[][] getFilters() {
        return _Array_filters;
    };

    // ArrayFilterNode  **END**
    //  CollectionSourceNode **BEGIN** **UNIQUE SIGNATURES**
    private java.util.List _Collection_clients = new ArrayList(1);

    public void attach(CollectionFilter filter) {
        _Collection_clients.add(filter);
    };

    public void detach(CollectionFilter filter) {
        _Collection_clients.remove(filter);
    };

    //  CollectionSourceNode **DUPE**
    public void xmit() {
        try {
            for (int ci = 0; ci < _Collection_clients.size(); ci++) {
                CollectionFilter filter = (CollectionFilter) _Collection_clients.get(ci);
                filter.recv(data);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Error("more debugging needed here");
        }
        ;
    };

    final public static Class[][] _Collection_sources = new Class[][]{{
        Collection.class, //
        // CollectionSourceNode **SOURCE TYPES MERGE**
    },
                                                                      //add additional types as needed.
    };

    public Class[][] getSources() {
        return _Collection_sources;
    };
    //  CollectionSourceNode **END**
}

;


