package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

/**
 * this does 2 things: <UL> <LI>channels input to one of /n/ waiting
 * values <LI>Fires off an Array event if index is  set higher than the library count. </UL>
 */
public abstract class Assembler extends net.sourceforge.idyuts.IOPipes.Sequencer implements ArraySource, intFilter {
    Object[] arr;

    public Assembler() {
        arr = new Object[0];
    }

    public Assembler(int len) {
        arr = new Object[len];
    }

    public void recv(int data) {
        if (data >= arr.length) {
            xmit();
        }
        else {
            /* meager error prevention.. instead of ArrayOOB
            exception we just
            assume the lifespan of arr[] is fulfilled and start
            broadcasting. */

            super.recv(data); //sets index
        }
        ;
    }

    //  ArraySourceNode **BEGIN** **UNIQUE SIGNATURES**
    private java.util.List _Array_clients = new ArrayList(1);

    public void attach(ArrayFilter filter) {
        _Array_clients.add(filter);
    };

    //  ArraySourceNode **DUPE**
    public void detach(ArrayFilter filter) {
        _Array_clients.remove(filter);
    };

    public void xmit() {
        try {
            for (int ci = 0; ci < _Array_clients.size(); ci++) {
                ArrayFilter filter = (ArrayFilter) _Array_clients.get(ci);
                filter.recv(arr);
            }
        }
        catch (Exception e) {
            throw new Error("more debugging needed here");
        }
        ;
    };

    final public static Class[][] _Array_sources = new Class[][]{{Object[].class}, //  ArraySourceNode **SOURCE TYPES MERGE**
                                                                 //add additional types as needed.
    };

    public Class[][] getSources() {
        return _Array_sources;
    };
    //  ArraySourceNode **END**
}


