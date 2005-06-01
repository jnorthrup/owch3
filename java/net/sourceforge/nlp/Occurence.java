package net.sourceforge.nlp;

import java.io.Serializable;

public class Occurence implements Serializable {
    private int count = 1;

    public Occurence() {
    }

    public final void incr() {
        count++;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}




