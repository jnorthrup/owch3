package net.sourceforge.idyuts.test;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public class CollectionPrinter implements CollectionFilter
//IMPORT Several Filter Interfaces for complex problem solving needs
{
    // CollectionPrinter  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(Collection data) {
        System.out.println(getClass().getName() + "recv" + data);
        System.out.println(data.toString());
    };

    //add additional interfaces as needed
    final public static Class[][] _Collection_filters = new Class[][]{{
        Collection.class, // CollectionPrinter
        // **FILTER TYPES MERGE**
    },
    };

    public Class[][] getFilters() {
        return _Collection_filters;
    };
    // CollectionPrinter  **END**
}

;


