package net.sourceforge.idyuts.IOPipes;

import net.sourceforge.idyuts.IOLayer.*;

public interface StringFunctor extends StringSource, StringFilter {
    public String fire(String s);
}


