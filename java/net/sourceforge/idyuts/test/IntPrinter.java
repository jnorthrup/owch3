package net.sourceforge.idyuts.test;

import net.sourceforge.idyuts.IOLayer.*;

public class IntPrinter implements intFilter {
    //IMPORT Several Filter Interfaces for complex problem solving needs
    // IntPrinter  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(int evt) { //
        // System.out.println(getClass().getName()+"recv"+data);
        //YOUR CODE GOES HERE
        //you probably want to end with an xmit() call
        //if this is also a Source interface.
        System.out.println("" + evt);
    };

    //add additional interfaces as needed
    final public static Class[][] _Int_filters = new Class[][]{{
        int.class, // IntPrinter  **FILTER TYPES MERGE**
    },
    };

    public Class[][] getFilters() {
        return _Int_filters;
    };
    // IntPrinter  **END**
}

;


