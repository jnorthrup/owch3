package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class PCTScanner extends AbstractAgent {
    protected static final int PORT = 4050;

    protected static final String CHANNEL_NAME = "store";

    public PCTScanner(Map<String, Object> m) {
        super(m);


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
                    new PCTMessage(command.getCodes().get(Character.valueOf(type)), arg, flags, serialNo, data);
                    MetaProperties message = new Message();
                    Object value = get(Message.DESTINATION_KEY);
                    if (null != value)
                        message.put(Message.DESTINATION_KEY, String.valueOf(value));
                    Map<Character, command> cmd_type = command.getCodes();
                    command cmd = cmd_type.get(Character.valueOf(type));
                    //   if ("PCT_KEEP_ALIVE".equals(cmd.getName())) {
                    socket.getOutputStream().write(packet.getBytes()); //echo KEEPALIVES
                    //     continue;
                    //}
                    message.put("JMSType", cmd.getName());
                    message.put("PCTMessage.arg", Character.valueOf(arg));
                    message.put("PCTMessage.flags", Character.valueOf(flags));
                    message.put("PCTMessage.serial", serialNo);
                    message.put("PCTMessage.data", data);
                    send(message);
                }
            }
        } catch (IOException e) {
            // so what
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public static void main(String[] args) throws Exception {
        Map<?, ?> m = Env.getInstance().parseCommandLineArgs(args);
        final String[] ka;
        ka = new String[]{Message.REPLYTO_KEY, Message.DESTINATION_KEY, "AgentPort", "AgentHost"};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "PCTScanner Agent usage:\n\n" +
                    "-name     (String)name\n" +
                    "-AgentPort   (int)port\n" +
                    "-JMSDestination  (String) The destination agent name\n" +
                    "$Id$\n");

        }
        PCTScanner d;
        d = new PCTScanner((Map<String, Object>) m);
    }

    public void putValue(String key, Object value) {
        put(key, value);
    }
}

