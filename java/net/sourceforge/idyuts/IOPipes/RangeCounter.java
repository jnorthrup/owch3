package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public class RangeCounter implements intSource {
    int start;
    int finish;

    public RangeCounter(int finish) {
        this(0, finish);
    };

    public RangeCounter(int start, int finish) {
        this.finish = finish;
        this.start = start;
    };

    private java.util.List _Int_clients = new ArrayList(1);

    public void attach(intFilter filter) {
        _Int_clients.add(filter);
    };

    public void detach(intFilter filter) {
        _Int_clients.remove(filter);
    };

    //add additional interfaces as needed
    final public static Class[][] Int_sources = new Class[][]{{int.class},
                                                              //add additional types as needed.
    };

    public Class[][] getSources() {
        return Int_sources;
    };

    public synchronized void xmit() {
        try {
            for (int x = start; x <= finish; x++) {
                int ii = x;
                for (int ci = 0; ci < _Int_clients.size(); ci++) {
                    intFilter filter = (intFilter) _Int_clients.get(ci);
                    filter.recv(ii);
                }
            }
        }
        catch (Exception e) {
            throw new Error("more debugging needed here");
        }
        ;
    };
}


