package net.sourceforge.gui;

import javax.swing.*;
import java.util.*;

public class ScrollingListModel extends AbstractListModel {
    List list = new LinkedList();

    public void addElement(Object o) {
        list.add(o);
        super.fireIntervalAdded(this, list.size(), list.size());
    }

    public ScrollingListModel() {
        super();
    }

    public Object getElementAt(int par1) {
        // Write your code here
        return list.get(par1);
    }

    public int getSize() {
        // Write your code here
        return list.size();

    }

    public void remove(int i) {
        list.remove(i);
        super.fireIntervalRemoved(this, i, i);
    };

    public boolean remove(Object o) {

        super.fireIntervalRemoved(this, 0, 0);
        return list.remove(o);
    };
}




