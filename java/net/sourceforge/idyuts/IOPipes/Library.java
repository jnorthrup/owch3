package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

/** Library is used to select from an array of functors by plugging the IntFilter iface. */
public abstract class Library extends net.sourceforge.idyuts.IOPipes.Sequencer implements Source {
    Library(Filter[] l) {
        library = l;
    };

    Filter[] library;
    Object data;

    private static Class[] foo = new Class[]{Object.class};

    synchronized public void xmit() {
        try {
            Filter filter = library[index % library.length];
            if (filter == null) {
                return;
            }
            filter.getClass().getMethod("recv", foo).invoke(filter,
                    new Object[]{data});
        }
        catch (Exception e) {
            throw new Error("need more debugging here");
        }
    };
}


