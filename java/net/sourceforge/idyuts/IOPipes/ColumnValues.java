package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

class ColumnValues implements ArraySource, TableFilter, intFilter {
    int column = 0;

    public void recv(int data) {
        column = data;
        xmit();
    }

    ColumnValues(int column) {
        this.column = column;
    }

    ColumnValues() {
    }

    public void collect() {
        list = new ArrayList(1);
        for (int i = 0; i < data.length; i++) {
            list.add(data[i][column]);
        }
        ;
    };

    java.util.List list;
    Object[][] data;

    public void recv(Object[][] data) {
        synchronized (data) {
            this.data = data;
            xmit();
        }
    };

    //add additional interfaces as needed
    final public static Class[][] Table_filters = new Class[][]{{Object[][].class}, {int.class, }
    };

    public Class[][] getFilters() {
        return Table_filters;
    };

    private java.util.List _Arrayclients = new ArrayList(1);

    public void attach(ArrayFilter filter) {
        _Arrayclients.add(filter);
    };

    public void detach(ArrayFilter filter) {
        _Arrayclients.remove(filter);
    };

    //add additional interfaces as needed
    final public static Class[][] Array_sources = new Class[][]{{Object[].class, }, {Boolean.class, }
                                                                //add additional types as needed.
    };

    public Class[][] getSources() {
        return Array_sources;
    };

    public synchronized void xmit() {
        try {
            if (data == null) {
                return;
            }
            collect();
            Object[] arr = list.toArray();
            for (int ci = 0; ci < _Arrayclients.size(); ci++) {
                ArrayFilter filter = (ArrayFilter) _Arrayclients.get(ci);
                filter.recv(arr);
            }
        }
        catch (Exception e) {
            throw new Error("more debugging needed here");
        }
        ;
    };
}


