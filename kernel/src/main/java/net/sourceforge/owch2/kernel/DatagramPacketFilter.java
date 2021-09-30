package net.sourceforge.owch2.kernel;

import java.net.DatagramPacket;

/**
 * @author James Northrup
 * @version $Id$
 * @copyright All Rights Reserved Glamdring Inc.
 */

/* $Log: DatagramPacketFilter.java,v $
/* Revision 1.2  2005/06/03 18:27:47  grrrrr
/* no message
/*
/* Revision 1.1  2005/06/01 06:43:11  grrrrr
/* no message
/*
*/

public interface DatagramPacketFilter {
    void recv(DatagramPacket data);
}
