package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

import java.util.*;

public class StringFunctorChain implements StringFunctor {
    StringFunctor[] filters;

    /** ctor takes an array of virgin StringFunctors and attaches them end to end. */
    public StringFunctorChain(StringFunctor[] f) {
        for (int i = 0; i < filters.length; i++) {
            if (i > 0) {
                filters[i - 1].attach(filters[i]);
            }
        }
    }

    public void recv(String data) {
        filters[0].recv(data);
    };

    //add additional interfaces as needed
    final public static Class[][] String_filters = new Class[][]{{String.class},
                                                                 //add additional types as needed.
    };

    public Class[][] getFilters() {
        return String_filters;
    };

    private java.util.List _Stringclients = new ArrayList(1);

    public void attach(StringFilter filter) {
        filters[filters.length - 1].attach(filter);
    };

    public void detach(StringFilter filter) {
        filters[filters.length - 1].detach(filter);
    };

    //add additional interfaces as needed
    final public static Class[][] String_sources = new Class[][]{{String.class},
                                                                 //add additional types as needed.
    };

    public Class[][] getSources() {
        return String_sources;
    };

    /**
     * This method allows us to look like a single Functor.  This does
     * not imply that recv/xmit processing will occur however. it is an optional second means of chainning Functor results.
     */
    public String fire(String s) {
        for (int ci = 0; ci < filters.length; ci++) {
            s = filters[ci].fire(s);
        }
        return s;
    };

    public void xmit() {
        filters[0].xmit();
    };
}


