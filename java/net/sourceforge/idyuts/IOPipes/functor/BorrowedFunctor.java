package net.sourceforge.idyuts.IOPipes.functor;

import net.sourceforge.idyuts.IOPipes.*;

import java.lang.reflect.*;

public class BorrowedFunctor extends StringFunctorImpl {
    Object instance;
    Method method;

    public BorrowedFunctor(Object i, String m) {
        try {
            instance = i;
            method = i.getClass().getMethod(m,
                    new Class[]{String.class});
        }
        catch (Exception e) {
            throw new Error("Borrowed Functor getMethod failure");
        }
        ;
    };

    public BorrowedFunctor(Object i, Method m) {
        instance = i;
        method = m;
    };

    public String fire(String s) {
        Object[] arr = {s};
        try {
            return (String) method.invoke(instance, arr);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e1) {
            e1.printStackTrace();
        }
        ;
        return null;
    }
}


