package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.AbstractAgent;
import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.Notification;
import net.sourceforge.owch2.kernel.MetaProperties;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Map;

public class PCTScanner extends AbstractAgent {
    protected static final int PORT = 4050;

    protected static final String CHANNEL_NAME = "store";
     ;
    protected static final String MSG_DEST = "JMSDestination";

    public PCTScanner(Map m) {
        super(m);


        ServerSocket serverSocket;
        try {

            InetAddress theAddr = Env.getHostAddress();
            try {

                String host     =theAddr.getHostAddress();
                Env.log(33, "attempting to bind addr: " + host);

                theAddr = InetAddress.getByName(host);
                Env.log(33, "post-bind addr: " + theAddr);
            } catch (Exception e) {
                // e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
            int port = PORT;
            try {
                port = Integer.parseInt((String) get("AgentPort"));
            } catch (Exception e) {
                //aborted
            }

            serverSocket = new ServerSocket(port, 16, theAddr);
            while (!killFlag) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                DataInputStream stream = new DataInputStream(inputStream);
                while (!socket.isInputShutdown()) {
                    String packet;
                    packet = stream.readLine();
                    int first;
                    first = packet.indexOf('{');
                    int last;
                    last = packet.lastIndexOf('}');
                    String line;
                    line = packet.substring(first + 1, last);
                    char type;
                    type = line.charAt(0);
                    char arg;
                    arg = line.charAt(1);
                    char flags;
                    flags = line.charAt(2);
                    String serialNo;
                    serialNo = line.substring(3, 3 + 14);
                    String data = "";
                    try {

                        data = line.substring(3 + 14 + 1);
                    } catch (StringIndexOutOfBoundsException e) {
                        //no data    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    new PCTMessage((command) command.getCodes().get(new Character(type)), arg, flags, serialNo, data);
                    Notification notification = new Notification();
                    Object value = get(MSG_DEST);
                    if (null != value)
                        notification.put(MSG_DEST, value);
                    Map cmd_type = command.getCodes();
                    command cmd = (command) cmd_type.get(new Character(type));
                 //   if ("PCT_KEEP_ALIVE".equals(cmd.getName())) {
                        socket.getOutputStream().write(packet.getBytes()); //echo KEEPALIVES
                   //     continue;
                    //}
                    notification.put("JMSType", cmd.getName());
                    notification.put("PCTMessage.arg", new Character(arg));
                    notification.put("PCTMessage.flags", new Character(flags));
                    notification.put("PCTMessage.serial", serialNo);
                    notification.put("PCTMessage.data", data);
                    send(notification);
                    if (Env.logDebug) notification.save(System.out);
                }
            }
        } catch (IOException e) {
            // so what
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public static void main(String[] args) throws Exception {
        Map m = Env.parseCommandLineArgs(args);
        final String[] ka;
        ka = new String[]{"JMSReplyTo", "JMSDestination", "AgentPort", "AgentHost"};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "PCTScanner Agent usage:\n\n" +
                    "-name     (String)name\n" +
                    "-AgentPort   (int)port\n" +
                    "-JMSDestination  (String) The destination agent name\n" +
                    "$Id: PCTScanner.java,v 1.1 2002/12/08 16:05:49 grrrrr Exp $\n");

        }
        PCTScanner d;
        d = new PCTScanner(m);
    }
}

