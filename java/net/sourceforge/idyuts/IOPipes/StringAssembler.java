package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

public class StringAssembler extends Assembler implements StringFilter {
    public StringAssembler(int len) {
        super(len);
    };

    // StringFilterNode  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(String data) {
        //System.out.println(getClass().getName()+"recv"+data);
        arr[index] = data;
    };

    //add additional interfaces as needed
    final public static Class[][] _String_filters = new Class[][]{{
        String.class, // StringFilterNode
        // **FILTER TYPES MERGE**
    },
    };

    public Class[][] getFilters() {
        return _String_filters;
    };
    // StringFilterNode  **END**
}

;


