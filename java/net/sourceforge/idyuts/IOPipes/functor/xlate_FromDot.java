package net.sourceforge.idyuts.IOPipes.functor;

import net.sourceforge.idyuts.IOPipes.*;

public class xlate_FromDot extends StringFunctorImpl {
    public String fire(String s) {
        if (s == null) {
            s = "";
        }
        return s.replace('.', '_');
    };
}


