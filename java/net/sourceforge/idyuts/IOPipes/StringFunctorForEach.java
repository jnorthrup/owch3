package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

public class StringFunctorForEach extends ForEach implements ArrayFilter, StringFilter, intFilter {
    StringFunctor chain;
    Object[] data; //really assumed String[] but java has bad manners

    // intFilterNode  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(int i) {
        //Second Called
        //inbound message from a intSource
        this.index = i;
        chain.recv((String) data[index]);
    };

    // intFilterNode  **END**
    StringFunctorForEach(StringFunctor c) {
        chain = c;
        chain.attach(this);
    }

    // StringFilterNode  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(String d) //LAST CALLED
    {
        arr[index] = d;
    };

    // ArrayFilterNode  **BEGIN** **UNIQUE SIGNATURES**
    public void recv(Object[] d) {
        synchronized (this) { //first called
            data = d;
            arr = new Object[data.length];
            RangeCounter range = new RangeCounter(data.length - 1);
            range.attach(this);
            range.xmit();
            xmit();
        }
        ;
    };

    //add additional interfaces as needed
    final public static Class[][] _String_filters = new Class[][]{{
        String.class, // StringFilterNode
        // **FILTER TYPES MERGE**
    }, {Object[].class, }, {int.class, },
    };

    public Class[][] getFilters() {
        return _String_filters;
    };
    // StringFilterNode  **END**
}


