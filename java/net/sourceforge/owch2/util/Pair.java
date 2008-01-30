package net.sourceforge.owch2.util;

/**
 * User: jim
 * Date: Jan 28, 2008
 * Time: 6:02:07 PM
 */
public class Pair<First, Second> {
    public First first;
    public Second second;

    Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }
}
