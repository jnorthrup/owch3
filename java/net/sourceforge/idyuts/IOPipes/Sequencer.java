package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

/** this class receives intSource data and stores the int for use in sequncing against a library or array */
public abstract class Sequencer implements intFilter {
    int index = 0;

    public void recv(int data) {
        index = data;
    };

    //add additional interfaces as needed
    final public static Class[][] Int_filters = new Class[][]{{int.class}, // IntFilterNode  **FILTER TYPES MERGE**
    };

    public Class[][] getFilters() {
        return Int_filters;
    };
    // IntFilterNode  **END**
}


