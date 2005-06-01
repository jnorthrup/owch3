package net.sourceforge.owch2.kernel;

import java.net.DatagramPacket;

/**
 * @version $Id: DatagramPacketFilter.java,v 1.1 2005/06/01 06:43:11 grrrrr Exp $
 * @Author James Northrup
 * @copyright All Rights Reserved Glamdring Inc.
 */

/* $Log: DatagramPacketFilter.java,v $
/* Revision 1.1  2005/06/01 06:43:11  grrrrr
/* no message
/*  
*/

public interface DatagramPacketFilter {
    void recv(DatagramPacket data);
}
