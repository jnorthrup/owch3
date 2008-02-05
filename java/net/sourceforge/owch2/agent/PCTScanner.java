package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class PCTScanner extends AbstractAgent {
    protected static final int PORT = 4050;

    protected static final String CHANNEL_NAME = "store";

    public PCTScanner(Iterable<Map.Entry<CharSequence, Object>> m) {
        super(m);
        final String[] ka;
        ka = new String[]{ImmutableNotification.FROM_KEY, (String) DESTINATION_KEY, "AgentPort", "AgentHost"};

        if (!keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "PCTScanner Agent usage:\n\n" +
                    "-name     (String)name\n" +
                    "-AgentPort   (int)port\n" +
                    "-JMSDestination  (String) The destination agent name\n" +
                    "$Id$\n");
            Runtime.getRuntime().halt(1);
        }


        ServerSocket serverSocket;
        try {

            InetAddress theAddr = Env.getInstance().getHostAddress();
            try {

                String host = theAddr.getHostAddress();
                Logger.getAnonymousLogger().info("attempting to bind addr: " + host);

                theAddr = InetAddress.getByName(host);
                Logger.getAnonymousLogger().info("post-bind addr: " + theAddr);
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
                DataInput stream = new DataInputStream(inputStream);
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
                    new PCTMessage(command.getCodes().get(type), arg, flags, serialNo, data);
                    Notification message = new DefaultMapTransaction(this);
                    Object value = get(ImmutableNotification.DESTINATION_KEY);
                    if (null != value)
                        message.put(ImmutableNotification.DESTINATION_KEY, String.valueOf(value));
                    Map<Character, command> cmd_type = command.getCodes();
                    command cmd = cmd_type.get(type);
                    //   if ("PCT_KEEP_ALIVE".equals(cmd.getName())) {
                    socket.getOutputStream().write(packet.getBytes()); //echo KEEPALIVES
                    //     continue;
                    //}
                    message.put("JMSType", cmd.getName());
                    message.put("PCTMessage.arg", arg);
                    message.put("PCTMessage.flags", flags);
                    message.put("PCTMessage.serial", serialNo);
                    message.put("PCTMessage.data", data);
                    send((Transaction) message);
                }
            }
        } catch (IOException e) {
            // so what
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public static void main(String[] args) throws Exception {
        new PCTScanner((Iterable<Map.Entry<CharSequence, Object>>) Env.getInstance().parseCommandLineArgs(args));
    }

    public void putValue(String key, Object value) {
        put(key, value);
    }
}

