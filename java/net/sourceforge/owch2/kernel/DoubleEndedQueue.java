/**
 * A First-In-First-Out(FIFO) Queue of objects.
 * @version     1.12.2112.nc, 19 Sep 1996
 */

package net.sourceforge.owch2.kernel;


/**
 * @author James Northrup
 * @version $Id: DoubleEndedQueue.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public final class DoubleEndedQueue {
    DoubleEndedQueueNode head;
    DoubleEndedQueueNode tail;

    /**
     * Pushes an item onto the deq.
     *
     * @param item the item to be pushed on.
     */
    synchronized public final void push(Object item) {
        head = new DoubleEndedQueueNode(head, item);
        if (tail == null) {
            tail = head;
        }
    }

    /**
     * Pops an item off the deq.
     */
    public final Object pop() {
        if (tail != null) {
            DoubleEndedQueueNode td = tail;
            tail = tail.next;
            if (td == head) {
                head = null;
            }
            return td.getObject();
        }
        ;
        return null;
    }

    /**
     * peeks at the next object on the out side.
     */
    public final Object peek() {
        DoubleEndedQueueNode de = tail;
        if (de == null) {
            return null;
        }
        return de.getObject();
    }

    /**
     * Returns true if the deq is empty.
     */
    public final boolean empty() {
        return head == null;
    }

    final class DoubleEndedQueueNode {
        DoubleEndedQueueNode(DoubleEndedQueue.DoubleEndedQueueNode previous, Object o) {
            object = o;
            next = previous;
        }

        ;

        DoubleEndedQueue.DoubleEndedQueueNode next = null;

        final Object getObject() {
            return object;
        }

        ;

        Object object;
    }

    ;
}


