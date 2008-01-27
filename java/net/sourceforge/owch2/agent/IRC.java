package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.protocol.Transport.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class IRC extends AbstractAgent implements Runnable {
    private BufferedReader is;
    private PrintWriter os;
    private int nickname_ctr = 3;
    static int seq_nr = 0;

    private Socket socket;
    private static final int DEFAULT_IRC_PORT = 6667;
    private static final String IRCREPLYTO_KEY = "IRCReplyTo";
    private static final String IRCAGENT_KEY = "IRCAgent";
    public static final String IRCPRVMSG_TYPE = "PRIVMSG";
    public static final Object IRCCHANNEL_KEY = "IRCChannel";
    public static final String VALUE_KEY = "Value";
    public static final String MSG_TYPE = "MSG";
    public static final Object IRCHOST_KEY = "IRCHost";


    public IRC(Map m) {
        super(m);
        relocate();
        new Thread(this).start();
    }

    public Map getChannels() {
        return channels;
    }

    public void setChannels(Map channels) {
        this.channels = channels;
    }

    public void setIRCPort(int IRCPort) {
    }

    private int getIRCPort() {
        if (this.containsKey("IRCPort")) {
            return Integer.decode((String) get("IRCPort"));
        } else {
            return DEFAULT_IRC_PORT;
        }
    }


    public static void main(String[] args) {
        Map<?, ?> bootstrap = Env.getInstance().parseCommandLineArgs(args);

        final List requiredList = Arrays.asList(new Object[]{Message.REPLYTO_KEY, IRCHOST_KEY, "IRCNickname",});
        if (!bootstrap.keySet().containsAll(requiredList)) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "IRC Agent usage:\n\n" +
                    "-name (String)name\n" +
                    "-IRCHost (String)hostname/IP\n" +
                    "-IRCNickname (String)nickname\n" +
                    "[-IRCJoin (String)channel1 ... channeln]\n" +
                    "[-IRCPort (int)port]\n" +
                    "[-Persist t]\n" +
                    "[-Clone 'host1[ ..hostn]']\n" +
                    "[-Deploy 'host1[ ..hostn]']\n" +
                    "$Id$\n");
        }
        IRC d = new IRC(bootstrap);
    }


    protected void removeLocationFromChannel(Object channel, Object l) {
        Collection<Location> s = (Collection<Location>) getChannels().get(channel);
        if (s == null) {
            return;
        }
        s.remove(l);
        if (s.isEmpty()) {
            getChannels().remove(channel);
        }
    }

    private void acknowledgePublicMsg(MetaProperties m) {
        String backto = m.get(Message.REPLYTO_KEY).toString();
//an irc nick
        String response = backto + ", noted";
        MetaProperties n = new Message(m);
        n.put(Message.DESTINATION_KEY, getJMSReplyTo());
        n.put(Message.TYPE_KEY, MSG_TYPE);
        n.put(VALUE_KEY, response);
        handle_MSG(n);
    }

    protected void addLocationToChannel(Object channel, Location l) {
        Collection<Location> s = (Collection<Location>) getChannels().get(channel);
        if (s == null) {
            s = new HashSet<Location>();
        }
        s.add(l);
    }

    public void run() {
        do {
            try {
                socket = new Socket(InetAddress.getByName(get(IRCHOST_KEY).toString()), this.getIRCPort());
                //        socket.setTcpNoDelay(true);
                //        socket.setSoTimeout(20 * 60 * 1000); //20 minutes
                //        socket.setReceiveBufferSize(16 * 1024);
                //        socket.setSendBufferSize(32);
                setIs(new BufferedReader(new InputStreamReader(socket.getInputStream())));
                setOs(new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true));
                //if (E

                MetaProperties l = owch.getLocation();
                String out = "USER" + " owch " + " " + " owch " + " " + " owch " + " :" + l.getURI() +
                        "/" + getJMSReplyTo();
                getOs().println(out);
                handle_NICK(new Message(this));
                String line;
                while (!this.killFlag) {
                    //				if( getIs().ready())
                    line = getIs().readLine();
                    if (line.startsWith("PING")) {
                        String pong = "PONG" + line.substring(line.lastIndexOf(":"), line.length());
                        getOs().println(pong);
                    } else if (line.startsWith("NOTICE")) {
                    } else {
                        try {
                            MetaProperties n = new Message();
                            String prefix = "IRC_";
                            parseLine(line, n, prefix);
                            send(n);
                        }
                        catch (NoSuchElementException e) {
                        }
                    }
                }
            }
            catch (IOException e) {

            }
        } while (containsKey("Persist"));

    }

    public void handle_Dissolve(MetaProperties m) {
        super.handle_Dissolve(m);
        try {
            m.put(VALUE_KEY, "Dissolve Message from" + m.getJMSReplyTo());
            socket.close();
        }
        catch (Exception e) {
        }
    }

    public void handle_PART(MetaProperties m) {
        getOs().println("PART" + m.get(VALUE_KEY));
    }

    public void handle_QUIT(MetaProperties m) {
        getOs().println("QUIT" + m.get(VALUE_KEY));
    }

    public void handle_MSG(MetaProperties m) {
        try {
            String out = IRCPRVMSG_TYPE + (m.containsKey(IRCCHANNEL_KEY) ? m.get(IRCCHANNEL_KEY).toString() : m.get(Message.DESTINATION_KEY).toString()) + " :" + m.get(VALUE_KEY);
            getOs().println(out);
        }
        catch (Exception e) {
            handle_Dissolve(new Message(this));
        }
    }

    public void handle_NICK(MetaProperties m) {
        String nick;
        if (m.containsKey(VALUE_KEY)) {
            nick = m.get(VALUE_KEY).toString();
        } else {
            nick = m.get("IRCNickname").toString();
        }
        getOs().println("NICK" + nick);
    }

    /**
     * 376 end of motd
     */
    public void handle_IRC_RPL_ENDOFMOTD(MetaProperties p) {
        if (containsKey("IRCJoin")) {
            handle_IRC_INVITE(new Message(this));
        }
    }

    public void handle_IRC_INVITE(MetaProperties m) {
        if (!containsKey("NoInvite")) {
            handle_JOIN(m);
        }
    }

    public void handle_JOIN(MetaProperties m) {
        String t = "flood";
        String chans = m.containsKey("IRCJoin") ? m.get("IRCJoin").toString() : m.get(VALUE_KEY).toString();
        StringTokenizer s = new StringTokenizer(chans);
        while (s.hasMoreTokens()) {
            t = s.nextToken();
            String out = "JOIN" + (t.startsWith("#") ? "" : "#") + t;
            getOs().println(out);
        }
    }

    public void handle_IRC_PRIVMSG(MetaProperties p) {
        MetaProperties n = new Message(p);
        String prefix = "";
        parseLine(p.get(VALUE_KEY).toString().substring(1), n, prefix);
        String dest = n.get(Message.DESTINATION_KEY).toString();
        if (n.get(Message.DESTINATION_KEY).equals(get("IRCNickname"))) {
            n.put("IRCDestination", dest);
            n.put(Message.DESTINATION_KEY, getJMSReplyTo());
        }
        n.put(IRCREPLYTO_KEY, p.getJMSReplyTo()); //skips the user's input replyto value
        n.put(Message.REPLYTO_KEY, getJMSReplyTo());
        send(n);
    }

    /**
     * 433 nickname invalid
     */
    public void handle_IRC_ERR_NICKNAMEINUSE(MetaProperties p) {
        nickname_ctr++;
        String t;
        if (containsKey("IRCBasename")) {
            t = get("IRCBasename").toString();
        } else {
            t = get("IRCNickname").toString();
            put("IRCBasename", t);
        }
        t += nickname_ctr;
        put("IRCNickname", t);
        handle_NICK(new Message(this));
    }

    private Map channels = new HashMap();

    public void handle_AGENT_REMOVE(MetaProperties m) {
        Object l = new Location(m);
        StringTokenizer t = new StringTokenizer(m.get(VALUE_KEY).toString());
        while (t.hasMoreTokens()) {
            String token = t.nextToken();
            removeLocationFromChannel(token, l);
        }
    }

    public void handle_AGENT_JOIN(MetaProperties m) {
        Location l = new Location(m);
        StringTokenizer t = new StringTokenizer(m.get(VALUE_KEY).toString());
        while (t.hasMoreTokens()) {
            String token = t.nextToken();
            addLocationToChannel(token, l);
        }
    }

    /**
     * Generated by Together on May 2, 2002
     */
    private void xmitChannelToLocations(MetaProperties m) {
        if (getChannels().containsKey(m.get(IRCCHANNEL_KEY))) {
            Collection<Location> c = (Collection<Location>) getChannels().get(m.get(IRCCHANNEL_KEY));
            Iterator<Location> i = c.iterator();
            while (i.hasNext()) {
                Location l = i.next();
                MetaProperties n = new Message(m);
                n.put(Message.DESTINATION_KEY, l.getJMSReplyTo());
                send(n);
            }
        }
    }

    /**
     * <P>Populates a Message with message semantic values. <P> Produces the fields in order<OL><LI> JMSReplyTo<LI>JMSType
     * <LI>JMSDestination<LI>Value
     *
     * @param sourceLine - sourceLine of text
     * @param messageIn  -  to be filled with values
     * @param prefix     - modifies JMSType
     */
    private void parseLine(String sourceLine, Map messageIn, String prefix) throws NoSuchElementException {
        StringTokenizer tokenizer;
        String IRCDestination, IRCChannel, JMSDestination = getJMSReplyTo();
        tokenizer = new StringTokenizer(sourceLine, ":", false);
        String cmd = tokenizer.nextToken(),
                value = tokenizer.nextToken("\0").substring(1);
        messageIn.put(VALUE_KEY, value);
        tokenizer = new StringTokenizer(cmd, "", false);
        String IRCReplyTo = tokenizer.nextToken(),
                JMSType = tokenizer.nextToken().trim(),
                parameters = tokenizer.nextToken("\0").trim();
        JMSType = RFCConversion(JMSType);
        tokenizer = new StringTokenizer(IRCReplyTo, "!", false);
        String nick = tokenizer.nextToken();
        messageIn.put(IRCREPLYTO_KEY, IRCReplyTo);
        messageIn.put(IRCAGENT_KEY, getJMSReplyTo());
        messageIn.put(Message.REPLYTO_KEY, nick);
        if (JMSType.equals(IRCPRVMSG_TYPE)) {
            {
                IRCDestination = parameters;
                JMSDestination = IRCDestination.equals(get("IRCNickName")) ? get(Message.REPLYTO_KEY).toString() : IRCDestination;
                messageIn.put("IRCDestination", IRCDestination.trim());
            }
            messageIn.put(Message.DESTINATION_KEY, JMSDestination);
        } else if (JMSType.equals("RPL_NAMREPLY") || JMSType.equals("RPL_ENDOFNAMES")) {
            tokenizer = new StringTokenizer(parameters, "=");
            IRCDestination = tokenizer.nextToken().trim();
            JMSDestination = tokenizer.nextToken().trim();
            messageIn.put("IRCDestination", IRCDestination.trim());
        }
        messageIn.put(Message.DESTINATION_KEY, JMSDestination.trim().toLowerCase());
        messageIn.put(Message.TYPE_KEY, prefix + JMSType);
        Logger.getAnonymousLogger().info(messageIn.toString());
    }

    /**
     * Generated by Together on May 5, 2002
     */
    private static final String RFCConversion(String JMSType) {
        String JMSType1 = JMSType;
        if (RFCTags.containsKey(JMSType1)) {
            JMSType1 = RFCTags.get(JMSType1).toString();
        }
        return JMSType1;
    }

    /**
     * gets socket
     *
     * @return socket
     */
    protected Socket getSocket() {
        return socket;
    }

    /**
     * sets socket
     *
     * @param socket socket
     */
    protected void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getIs() {
        return is;
    }

    protected void setIs(BufferedReader is) {
        this.is = is;
    }

    protected PrintWriter getOs() {
        return os;
    }

    protected void setOs(PrintWriter os) {
        this.os = os;
    }


    private static Map<String, RFC_IRC> RFCTags;

    static {
        RFCTags = new HashMap<String, RFC_IRC>();
        for (RFC_IRC irc : RFC_IRC.values()) RFCTags.put(String.format("%03d", irc.code), irc);


    }
//    /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/bin/java -Dfile.encoding=MacRoman -classpath /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/deploy.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/dt.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/jce.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/plugin.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/charsets.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/classes.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/dt.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/jce.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/jconsole.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/jsse.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/laf.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/ui.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/ext/apple_provider.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/ext/localedata.jar:/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/lib/ext/sunjce_provider.jar:/Users/jamesnorthrup/IdeaProjects/owch2/classes:/Users/jamesnorthrup/IdeaProjects/owch2/lib/xstream-1.1.2.jar:/Users/jamesnorthrup/IdeaProjects/owch2/lib/picocontainer-1.2-beta-1.jar:/Users/jamesnorthrup/Projects/prev/jars/hibernate-deps/junit-3.8.1.jar net.sourceforge.owch2.agent.IRC

//    Process finished with exit code 0

/*
    static {
        RFCTags = new HashMap<String, String[]>();
        RFCTags.put("001".trim(), new String[]{"RPL_WELCOME".trim(), "	Welcome to the Internet Relay Network   <nick>!<user>@<host>".trim(),});
        RFCTags.put("002".trim(), new String[]{"RPL_YOURHOST".trim(), "	Your host is <servername>, running version <ver>".trim(),});
        RFCTags.put("003".trim(), new String[]{"RPL_CREATED".trim(), "	This server was created <date>".trim(),});
        RFCTags.put("004".trim(), new String[]{"RPL_MYINFO".trim(), "	<servername> <version> <available user modes>\n<available channel modes>".trim(),});
        RFCTags.put("005".trim(), new String[]{"RPL_BOUNCE".trim(), "	Try server <server name>, port <port number>".trim(),});
        RFCTags.put("200".trim(), new String[]{"RPL_TRACELINK".trim(), "	Link <version & debug level> <destination>               <next server> V<protocol version>               <link uptime in seconds> <backstream sendq>               <upstream sendq>".trim(),});
        RFCTags.put("201".trim(), new String[]{"RPL_TRACECONNECTING".trim(), "	Try. <class> <server>".trim(),});
        RFCTags.put("202".trim(), new String[]{"RPL_TRACEHANDSHAKE".trim(), "	H.S. <class> <server>".trim(),});
        RFCTags.put("203".trim(), new String[]{"RPL_TRACEUNKNOWN".trim(), "	???? <class> [<client IP address in dot form>]".trim(),});
        RFCTags.put("204".trim(), new String[]{"RPL_TRACEOPERATOR".trim(), "	Oper <class> <nick>".trim(),});
        RFCTags.put("205".trim(), new String[]{"RPL_TRACEUSER".trim(), "	User <class> <nick>".trim(),});
        RFCTags.put("206".trim(), new String[]{"RPL_TRACESERVER".trim(), "	Serv <class> <int>S <int>C <server>               <nick!user|*!*>@<host|server> V<protocol version>".trim(),});
        RFCTags.put("207".trim(), new String[]{"RPL_TRACESERVICE".trim(), "	Service <class> <name> <type> <active type>".trim(),});
        RFCTags.put("208".trim(), new String[]{"RPL_TRACENEWTYPE".trim(), "	<newtype> 0 <client name>".trim(),});
        RFCTags.put("209".trim(), new String[]{"RPL_TRACECLASS".trim(), "	Class <class> <count>".trim(),});
        RFCTags.put("210".trim(), new String[]{"RPL_TRACERECONNECT".trim(), "	Unused.".trim(),});
        RFCTags.put("211".trim(), new String[]{"RPL_STATSLINKINFO".trim(), "	<linkname> <sendq> <sent messages>               <sent Kbytes> <received messages>               <received Kbytes> <time open>".trim(),});
        RFCTags.put("212".trim(), new String[]{"RPL_STATSCOMMANDS".trim(), "	<command> <count> <byte count> <remote count>".trim(),});
        RFCTags.put("219".trim(), new String[]{"RPL_ENDOFSTATS".trim(), "	<stats letter> :End of STATS report".trim(),});
        RFCTags.put("221".trim(), new String[]{"RPL_UMODEIS".trim(), "	<user mode string>".trim(),});
        RFCTags.put("234".trim(), new String[]{"RPL_SERVLIST".trim(), "	<name> <server> <mask> <type> <hopcount> <info>".trim(),});
        RFCTags.put("235".trim(), new String[]{"RPL_SERVLISTEND".trim(), "	<mask> <type> :End of service listing".trim(),});
        RFCTags.put("242".trim(), new String[]{"RPL_STATSUPTIME".trim(), "	:Server Up %d days %d:%02d:%02d".trim(),});
        RFCTags.put("243".trim(), new String[]{"RPL_STATSOLINE".trim(), "	O <hostmask> * <name>".trim(),});
        RFCTags.put("251".trim(), new String[]{"RPL_LUSERCLIENT".trim(), "	:There are <integer> users and <integer> services on <integer> servers".trim(),});
        RFCTags.put("252".trim(), new String[]{"RPL_LUSEROP".trim(), "	<integer> :operator(s) online".trim(),});
        RFCTags.put("253".trim(), new String[]{"RPL_LUSERUNKNOWN".trim(), "	<integer> :unknown connection(s)".trim(),});
        RFCTags.put("254".trim(), new String[]{"RPL_LUSERCHANNELS".trim(), "	<integer> :channels formed".trim(),});
        RFCTags.put("255".trim(), new String[]{"RPL_LUSERME".trim(), "	:I have <integer> clients and <integer> servers".trim(),});
        RFCTags.put("256".trim(), new String[]{"RPL_ADMINME".trim(), "	<server> :Administrative info".trim(),});
        RFCTags.put("257".trim(), new String[]{"RPL_ADMINLOC1".trim(), "	:<admin info>".trim(),});
        RFCTags.put("258".trim(), new String[]{"RPL_ADMINLOC2".trim(), "	:<admin info>".trim(),});
        RFCTags.put("259".trim(), new String[]{"RPL_ADMINEMAIL".trim(), "	:<admin info>".trim(),});
        RFCTags.put("261".trim(), new String[]{"RPL_TRACELOG".trim(), "	File <logfile> <debug level>".trim(),});
        RFCTags.put("262".trim(), new String[]{"RPL_TRACEEND".trim(), "	<server name> <version & debug level> :End of TRACE".trim(),});
        RFCTags.put("263".trim(), new String[]{"RPL_TRYAGAIN".trim(), "	<command> :Please wait a while and try again.".trim(),});
        RFCTags.put("301".trim(), new String[]{"RPL_AWAY".trim(), "	<nick> :<away message>".trim(),});
        RFCTags.put("302".trim(), new String[]{"RPL_USERHOST".trim(), "	:*1<reply> *(".trim(),});
        RFCTags.put("305".trim(), new String[]{"RPL_UNAWAY".trim(), "	:You are no longer marked as being away".trim(),});
        RFCTags.put("306".trim(), new String[]{"RPL_NOWAWAY".trim(), "	:You have been marked as being away".trim(),});
        RFCTags.put("311".trim(), new String[]{"RPL_WHOISUSER".trim(), "	<nick> <user> <host> * :<real name>".trim(),});
        RFCTags.put("312".trim(), new String[]{"RPL_WHOISSERVER".trim(), "	<nick> <server> :<server info>".trim(),});
        RFCTags.put("313".trim(), new String[]{"RPL_WHOISOPERATOR".trim(), "	<nick> :is an IRC operator".trim(),});
        RFCTags.put("314".trim(), new String[]{"RPL_WHOWASUSER".trim(), "	<nick> <user> <host> * :<real name>".trim(),});
        RFCTags.put("315".trim(), new String[]{"RPL_ENDOFWHO".trim(), "	<name> :End of WHO list".trim(),});
        RFCTags.put("317".trim(), new String[]{"RPL_WHOISIDLE".trim(), "	<nick> <integer> :seconds idle".trim(),});
        RFCTags.put("318".trim(), new String[]{"RPL_ENDOFWHOIS".trim(), "	<nick> :End of WHOIS list".trim(),});
        RFCTags.put("319".trim(), new String[]{"RPL_WHOISCHANNELS".trim(), "	<nick> :*( ( @".trim(),});
        RFCTags.put("321".trim(), new String[]{"RPL_LISTSTART".trim(), "	Obsolete.".trim(),});
        RFCTags.put("322".trim(), new String[]{"RPL_LIST".trim(), "	<channel> <# visible> :<topic>".trim(),});
        RFCTags.put("323".trim(), new String[]{"RPL_LISTEND".trim(), "	:End of LIST".trim(),});
        RFCTags.put("324".trim(), new String[]{"RPL_CHANNELMODEIS".trim(), "	<channel> <mode> <mode params>".trim(),});
        RFCTags.put("325".trim(), new String[]{"RPL_UNIQOPIS".trim(), "	<channel> <nickname>".trim(),});
        RFCTags.put("331".trim(), new String[]{"RPL_NOTOPIC".trim(), "	<channel> :No topic is set".trim(),});
        RFCTags.put("332".trim(), new String[]{"RPL_TOPIC".trim(), "	<channel> :<topic>".trim(),});
        RFCTags.put("341".trim(), new String[]{"RPL_INVITING".trim(), "	<channel> <nick>".trim(),});
        RFCTags.put("342".trim(), new String[]{"RPL_SUMMONING".trim(), "	<user> :Summoning user to IRC".trim(),});
        RFCTags.put("346".trim(), new String[]{"RPL_INVITELIST".trim(), "	<channel> <invitemask>".trim(),});
        RFCTags.put("347".trim(), new String[]{"RPL_ENDOFINVITELIST".trim(), "	<channel> :End of channel invite list".trim(),});
        RFCTags.put("348".trim(), new String[]{"RPL_EXCEPTLIST".trim(), "	<channel> <exceptionmask>".trim(),});
        RFCTags.put("349".trim(), new String[]{"RPL_ENDOFEXCEPTLIST".trim(), "	<channel> :End of channel exception list".trim(),});
        RFCTags.put("351".trim(), new String[]{"RPL_VERSION".trim(), "	<version>.<debuglevel> <server> :<comments>".trim(),});
        RFCTags.put("352".trim(), new String[]{"RPL_WHOREPLY".trim(), "	<channel> <user> <host> <server> <nick>".trim(),});
        RFCTags.put("353".trim(), new String[]{"RPL_NAMREPLY".trim(), "	".trim(),});
        RFCTags.put("364".trim(), new String[]{"RPL_LINKS".trim(), "	<mask> <server> :<hopcount> <server info>".trim(),});
        RFCTags.put("365".trim(), new String[]{"RPL_ENDOFLINKS".trim(), "	<mask> :End of LINKS list".trim(),});
        RFCTags.put("366".trim(), new String[]{"RPL_ENDOFNAMES".trim(), "	<channel> :End of NAMES list".trim(),});
        RFCTags.put("367".trim(), new String[]{"RPL_BANLIST".trim(), "	<channel> <banmask>".trim(),});
        RFCTags.put("368".trim(), new String[]{"RPL_ENDOFBANLIST".trim(), "	<channel> :End of channel ban list".trim(),});
        RFCTags.put("369".trim(), new String[]{"RPL_ENDOFWHOWAS".trim(), "	<nick> :End of WHOWAS".trim(),});
        RFCTags.put("371".trim(), new String[]{"RPL_INFO".trim(), "	:<string>".trim(),});
        RFCTags.put("372".trim(), new String[]{"RPL_MOTD".trim(), "	:- <text>".trim(),});
        RFCTags.put("374".trim(), new String[]{"RPL_ENDOFINFO".trim(), "	:End of INFO list".trim(),});
        RFCTags.put("375".trim(), new String[]{"RPL_MOTDSTART".trim(), "	:- <server> Message of the day -".trim(),});
        RFCTags.put("376".trim(), new String[]{"RPL_ENDOFMOTD".trim(), "	:End of MOTD command".trim(),});
        RFCTags.put("381".trim(), new String[]{"RPL_YOUREOPER".trim(), "	:You are now an IRC operator".trim(),});
        RFCTags.put("382".trim(), new String[]{"RPL_REHASHING".trim(), "	<config file> :Rehashing".trim(),});
        RFCTags.put("383".trim(), new String[]{"RPL_YOURESERVICE".trim(), "	You are service <servicename>".trim(),});
        RFCTags.put("391".trim(), new String[]{"RPL_TIME".trim(), "	<server> :<string showing server's local time>".trim(),});
        RFCTags.put("392".trim(), new String[]{"RPL_USERSSTART".trim(), "	:UserID   Terminal  Host".trim(),});
        RFCTags.put("393".trim(), new String[]{"RPL_USERS".trim(), "	:<username> <ttyline> <hostname>".trim(),});
        RFCTags.put("394".trim(), new String[]{"RPL_ENDOFUSERS".trim(), "	:End of users".trim(),});
        RFCTags.put("395".trim(), new String[]{"RPL_NOUSERS".trim(), "	:Nobody logged in".trim(),});
        RFCTags.put("401".trim(), new String[]{"ERR_NOSUCHNICK".trim(), "	<nickname> :No such nick/channel".trim(),});
        RFCTags.put("402".trim(), new String[]{"ERR_NOSUCHSERVER".trim(), "	<server name> :No such server".trim(),});
        RFCTags.put("403".trim(), new String[]{"ERR_NOSUCHCHANNEL".trim(), "	<channel name> :No such channel".trim(),});
        RFCTags.put("404".trim(), new String[]{"ERR_CANNOTSENDTOCHAN".trim(), "	<channel name> :Cannot send to channel".trim(),});
        RFCTags.put("405".trim(), new String[]{"ERR_TOOMANYCHANNELS".trim(), "	<channel name> :You have joined too many channels".trim(),});
        RFCTags.put("406".trim(), new String[]{"ERR_WASNOSUCHNICK".trim(), "	<nickname> :There was no such nickname".trim(),});
        RFCTags.put("407".trim(), new String[]{"ERR_TOOMANYTARGETS".trim(), "	<target> :<error code> recipients. <abort message>".trim(),});
        RFCTags.put("408".trim(), new String[]{"ERR_NOSUCHSERVICE".trim(), "	<service name> :No such service".trim(),});
        RFCTags.put("409".trim(), new String[]{"ERR_NOORIGIN".trim(), "	:No origin specified".trim(),});
        RFCTags.put("411".trim(), new String[]{"ERR_NORECIPIENT".trim(), "	:No recipient given (<command>)".trim(),});
        RFCTags.put("412".trim(), new String[]{"ERR_NOTEXTTOSEND".trim(), "	:No text to send".trim(),});
        RFCTags.put("413".trim(), new String[]{"ERR_NOTOPLEVEL".trim(), "	<mask> :No toplevel domain specified".trim(),});
        RFCTags.put("414".trim(), new String[]{"ERR_WILDTOPLEVEL".trim(), "	<mask> :Wildcard in toplevel domain".trim(),});
        RFCTags.put("415".trim(), new String[]{"ERR_BADMASK".trim(), "	<mask> :Bad Server/host mask".trim(),});
        RFCTags.put("421".trim(), new String[]{"ERR_UNKNOWNCOMMAND".trim(), "	<command> :Unknown command".trim(),});
        RFCTags.put("422".trim(), new String[]{"ERR_NOMOTD".trim(), "	:MOTD File is missing".trim(),});
        RFCTags.put("423".trim(), new String[]{"ERR_NOADMININFO".trim(), "	<server> :No administrative info available".trim(),});
        RFCTags.put("424".trim(), new String[]{"ERR_FILEERROR".trim(), "	:File error doing <file op> on <file>".trim(),});
        RFCTags.put("431".trim(), new String[]{"ERR_NONICKNAMEGIVEN".trim(), "	:No nickname given".trim(),});
        RFCTags.put("432".trim(), new String[]{"ERR_ERRONEUSNICKNAME".trim(), "	<nick> :Erroneous nickname".trim(),});
        RFCTags.put("433".trim(), new String[]{"ERR_NICKNAMEINUSE".trim(), "	<nick> :Nickname is already in use".trim(),});
        RFCTags.put("436".trim(), new String[]{"ERR_NICKCOLLISION".trim(), "	<nick> :Nickname collision KILL from <user>@<host>".trim(),});
        RFCTags.put("437".trim(), new String[]{"ERR_UNAVAILRESOURCE".trim(), "	<nick/channel> :Nick/channel is temporarily unavailable".trim(),});
        RFCTags.put("441".trim(), new String[]{"ERR_USERNOTINCHANNEL".trim(), "	<nick> <channel> :They aren't on that channel".trim(),});
        RFCTags.put("442".trim(), new String[]{"ERR_NOTONCHANNEL".trim(), "	<channel> :You're not on that channel".trim(),});
        RFCTags.put("443".trim(), new String[]{"ERR_USERONCHANNEL".trim(), "	<user> <channel> :is already on channel".trim(),});
        RFCTags.put("444".trim(), new String[]{"ERR_NOLOGIN".trim(), "	<user> :User not logged in".trim(),});
        RFCTags.put("445".trim(), new String[]{"ERR_SUMMONDISABLED".trim(), "	:SUMMON has been disabled".trim(),});
        RFCTags.put("446".trim(), new String[]{"ERR_USERSDISABLED".trim(), "	:USERS has been disabled".trim(),});
        RFCTags.put("451".trim(), new String[]{"ERR_NOTREGISTERED".trim(), "	:You have not registered".trim(),});
        RFCTags.put("461".trim(), new String[]{"ERR_NEEDMOREPARAMS".trim(), "	<command> :Not enough parameters".trim(),});
        RFCTags.put("462".trim(), new String[]{"ERR_ALREADYREGISTRED".trim(), "	:Unauthorized command (already registered)".trim(),});
        RFCTags.put("463".trim(), new String[]{"ERR_NOPERMFORHOST".trim(), "	:Your host isn't among the privileged".trim(),});
        RFCTags.put("464".trim(), new String[]{"ERR_PASSWDMISMATCH".trim(), "	:Password incorrect".trim(),});
        RFCTags.put("465".trim(), new String[]{"ERR_YOUREBANNEDCREEP".trim(), "	:You are banned from this server".trim(),});
        RFCTags.put("466".trim(), new String[]{"ERR_YOUWILLBEBANNED".trim(), "".trim(),});
        RFCTags.put("467".trim(), new String[]{"ERR_KEYSET".trim(), "	<channel> :Channel key already set".trim(),});
        RFCTags.put("471".trim(), new String[]{"ERR_CHANNELISFULL".trim(), "	<channel> :Cannot join channel (+l)".trim(),});
        RFCTags.put("472".trim(), new String[]{"ERR_UNKNOWNMODE".trim(), "	<char> :is unknown mode char to me for <channel>".trim(),});
        RFCTags.put("473".trim(), new String[]{"ERR_INVITEONLYCHAN".trim(), "	<channel> :Cannot join channel (+i)".trim(),});
        RFCTags.put("474".trim(), new String[]{"ERR_BANNEDFROMCHAN".trim(), "	<channel> :Cannot join channel (+b)".trim(),});
        RFCTags.put("475".trim(), new String[]{"ERR_BADCHANNELKEY".trim(), "	<channel> :Cannot join channel (+k)".trim(),});
        RFCTags.put("476".trim(), new String[]{"ERR_BADCHANMASK".trim(), "	<channel> :Bad Channel Mask".trim(),});
        RFCTags.put("477".trim(), new String[]{"ERR_NOCHANMODES".trim(), "	<channel> :Channel doesn't support modes".trim(),});
        RFCTags.put("478".trim(), new String[]{"ERR_BANLISTFULL".trim(), "	<channel> <char> :Channel list is full".trim(),});
        RFCTags.put("481".trim(), new String[]{"ERR_NOPRIVILEGES".trim(), "	:Permission Denied- You're not an IRC operator".trim(),});
        RFCTags.put("482".trim(), new String[]{"ERR_CHANOPRIVSNEEDED".trim(), "	<channel> :You're not channel operator".trim(),});
        RFCTags.put("483".trim(), new String[]{"ERR_CANTKILLSERVER".trim(), "	:You can't kill a server!".trim(),});
        RFCTags.put("484".trim(), new String[]{"ERR_RESTRICTED".trim(), "	:Your connection is restricted!".trim(),});
        RFCTags.put("485".trim(), new String[]{"ERR_UNIQOPPRIVSNEEDED".trim(), "	:You're not the original channel operator".trim(),});
        RFCTags.put("491".trim(), new String[]{"ERR_NOOPERHOST".trim(), "	:No O-lines for your host".trim(),});
        RFCTags.put("501".trim(), new String[]{"ERR_UMODEUNKNOWNFLAG".trim(), "	:Unknown MODE flag".trim(),});
        RFCTags.put("502".trim(), new String[]{"ERR_USERSDONTMATCH".trim(), "	:Cannot change mode for other users".trim(),});
    }*/
}
