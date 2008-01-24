/*
* Room.java
*************************************************************************************
Created by Jim Northrup
Modified by   EDS CSTS
@version     2.0 12/15/96
Backend implemenation of Room.
*************************************************************************************/

package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id: Room.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class Room extends AbstractAgent implements Runnable {
    //String dir=".";
    public Map usersList; //Contains users in the room

    public static void main(String[] args) {
        Map<? extends Object, ? extends Object> m = Env.getInstance().parseCommandLineArgs(args);
        if (!(m.containsKey("JMSReplyTo"))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" + "Room Agent usage:\n\n" + "-name name\n" +
                    "$Id: Room.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $\n");
        }
        Room d = new Room(m);
        Thread t = new Thread();
        try {
            t.start();
            while (true) {
                t.sleep(60000);
            }
        }
        catch (Exception e) {
        }
    }

    ;

    /**
     * Constructor
     */
    public Room(Map<? extends Object, ? extends Object> m) {
        super(m);
        usersList = new LinkRegistry();
        Thread t = new Thread(this, "Room: " + getJMSReplyTo());
        t.start();
    }

    ;

    synchronized void addUser(MetaProperties clientIn) {
        //TODO: make MetaPropertiesects
        String userNode = clientIn.getJMSReplyTo();
        String userKey = (String) usersList.get(userNode);
        if (Env.getInstance().logDebug) Logger.global.info("Room - addUser = " + userNode + "key = " + userKey);
        if (userKey == null) {
            usersList.put(userNode, clientIn);
        }
    }

    ;

    /**
     * This method handles the notifications based on their "JMSType" property.
     *
     * @param nIn a MetaProperties
     */
    public void handle_Test(MetaProperties nIn) {
        //if (Env.logDebug) Env.log(50, "Env.getLocation - " + Protocol);

        MetaProperties l = ProtocolType.Http.getLocation();
        MetaProperties n = new Notification(l);
        n.put("JMSDestination", nIn.get("JMSReplyTo"));
        n.put("JMSType", "Test");
        String url = n.get("URL") + "/test.jar";
        n.put("Path", url);
        n.put("Class", "owch.agent.TestWindow");
        n.put("args", "");
        send(n);
        return;
    }

    ;

    /**
     * publishs MetaPropertiess to all users in the room
     */
    public void publish(MetaProperties nIn) {
        String tmpJMSReplyTo;
        String roomServerName = getJMSReplyTo();
        nIn.put("ResentFrom", roomServerName);
        nIn.remove("JMSMessageID");
        //for each user in room
        for (Iterator e = usersList.keySet().iterator(); e.hasNext();) {
            tmpJMSReplyTo = (String) e.next();
            nIn.put("JMSDestination", tmpJMSReplyTo);
            if (Env.getInstance().logDebug)
                Logger.global.info("Room.Publish (" + roomServerName + ")(" + nIn.getJMSReplyTo() + ") user is " +
                        nIn.get("JMSDestination"));
            send(new Notification(nIn));
        }
        ; // end for
    } // end publish method

    /**
     * Updates the status of a user, and the user's view of users in the same room.
     *
     * @param clientIn notification to publish
     */
    synchronized public void userUpdate(MetaProperties clientIn) {
        addUser(clientIn);
        syncUsers(clientIn.get("JMSReplyTo").toString());
    }

    ;

    synchronized public void syncUsers(String nodeNameIn) {
        String tmpJMSReplyTo = null;
        MetaProperties notificationOut = new Notification();
        if (Env.getInstance().logDebug) Logger.global.info("Room - syncUsers  ");
        notificationOut.put("JMSType", "UserUpdate");
        notificationOut.put("SubJMSType", "Add");
        notificationOut.put("JMSDestination", nodeNameIn);
        String roomServerName = getJMSReplyTo();
        notificationOut.put("ResentFrom", roomServerName);
        //for each user in room
        for (Iterator e = usersList.keySet().iterator(); e.hasNext();) {
            tmpJMSReplyTo = (String) e.next();
            notificationOut.put("JMSReplyTo", tmpJMSReplyTo);
            if (Env.getInstance().logDebug)
                Logger.global.info("Room.Publish (" + roomServerName + ")(" + notificationOut.getJMSReplyTo() + ") user is" +
                        notificationOut.get("JMSDestination"));
            send(new Notification(notificationOut));
        }
    }

    /**
     * typical threadspawn code.
     */
    public void run() {
        linkTo(Env.getInstance().getParentNode().getJMSReplyTo());
        StringBuffer s;
        while (!killFlag) {
            wait120();
            // commented out - causes relink - much network traffic
            // linkTo(Env.getInstance().getParentNode().getJMSReplyTo());
            //linkTo(Env.getInstance().getProxyCache().enumProxies());
        }
    }

    ;

    /**
     * waits an interval averageing 120 seconds.
     */
    synchronized final private static void wait120() {
        //Random
        try {
            long tim = (long) (Math.random() * 180.0 * 1000.0) + 60000;
            if (Env.getInstance().logDebug) Logger.global.info("debug: wait120 Waiting for " + tim + " ms.");
            Thread.currentThread().sleep(tim, 0);
        }
        catch (Exception e) {
        }
        if (Env.getInstance().logDebug) Logger.global.info("debug: wait120 end");
    }
}


