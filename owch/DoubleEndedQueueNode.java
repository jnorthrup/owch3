package owch;

    /*
     Double Ended Queue Node

     Singly linked list nodes by any other name.

     */

final class DoubleEndedQueueNode
{
    DoubleEndedQueueNode(DoubleEndedQueueNode previous,Object o)
    {
        object=o;
        next=previous;
    };

    DoubleEndedQueueNode next=null;

    final Object getObject()
    {
        return object;
    };

    Object object;
};




