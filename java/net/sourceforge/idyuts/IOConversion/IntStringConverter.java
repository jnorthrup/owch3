package net.sourceforge.idyuts.IOConversion;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public class IntStringConverter implements intFilter, StringSource
//IMPORT Several Filter Interfaces for complex problem solving needs
{
    protected String data;

    // IntFilterNode  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(int evt) {
        //System.out.println(getClass().getName()+"recv "+data);
        data = "" + evt + "L";
        xmit();
    };

    //add additional interfaces as needed
    final public static Class[][] _Int_filters = new Class[][]{{
        int.class, // IntFilterNode  **FILTER TYPES MERGE**
    },
    };

    public Class[][] getFilters() {
        return _Int_filters;
    };

    // IntFilterNode  **END**
    //  StringSourceNode **BEGIN** **UNIQUE SIGNATURES**
    private java.util.List _String_clients = new ArrayList(1);

    public void attach(StringFilter filter) {
        _String_clients.add(filter);
    };

    public void detach(StringFilter filter) {
        _String_clients.remove(filter);
    };

    // StringSourceNode **DUPE**
    public void xmit() {
        try {
            for (int ci = 0; ci < _String_clients.size(); ci++) {
                StringFilter filter = (StringFilter) _String_clients.get(ci);
                filter.recv(data);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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


