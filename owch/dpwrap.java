package owch;
import java.io.*;
import java.net.*;
import java.util.*;

//would be nested class in C++

final class dpwrap
{
    final static byte hot=0;
    final static byte cold=1;
    final static byte frozen=2;
    final static byte dead=3;

    final static String[] age=
    {
        "hot",
        "cold",
        "frozen",
        "dead"
    };

    final static int lifespan=3;
    final static int mortality=lifespan*lifespan;

    DatagramPacket p;

    int count=0;
    dpwrap(DatagramPacket p_)
    {
        p=p_;
    };

    final byte[] getData()
    {
        return p.getData();
    };

    final InetAddress getAddress()
    {
        return p.getAddress();
    };

    final int getPort()
    {
        return p.getPort() ;
    };


    //TODO:
    //      Set up interface for fire() to use function objects
    //      and build a heterogenous collection of function objects to determine fireing interval, destination, lifespan, and hops.
    //
    //

    public byte fire()
    throws IOException
    {
        count++;

        DatagramSocket ds=(DatagramSocket)Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();

        if(count<lifespan)  {
          ds.send(p);
          return hot;
        }
        else
	    if((count%lifespan)==0) //try 1/n
        {
            ds.send(p);
            return cold;
        }
        else
        if(count>mortality)
        {
            owch.Env.debug(30,"debug:  dpwrap timeout");
            return dead;
        };
        return frozen;//don't try
    };
};
