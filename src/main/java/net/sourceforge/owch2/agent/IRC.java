 package net.sourceforge.owch2.agent;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStreamWriter;
 import java.io.PrintWriter;
 import java.net.Socket;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.NoSuchElementException;
 import java.util.StringTokenizer;
 import net.sourceforge.owch2.kernel.AbstractAgent;
 import net.sourceforge.owch2.kernel.Env;
 import net.sourceforge.owch2.kernel.Location;
 import net.sourceforge.owch2.kernel.MetaProperties;
 import net.sourceforge.owch2.kernel.Notification;

 public class IRC extends AbstractAgent
   implements Runnable
 {
   private BufferedReader is;
   private PrintWriter os;
   private int nickname_ctr = 3;
   static int seq_nr = 0;
   private Socket socket;
   private Map channels = new HashMap();

   private static HashMap<String, String[]> RFCTags = new HashMap<String, String[]>();

   public IRC(Map m)
   {
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
     if (containsKey("IRCPort")) {
       return Integer.decode((String) get("IRCPort"));
     }

     return 6667;
   }

   public static void main(String[] args)
   {
     Map bootstrap = Env.parseCommandLineArgs(args);

     List requiredList = Arrays.asList("JMSReplyTo", "IRCHost", "IRCNickname");
     if (!bootstrap.keySet().containsAll(requiredList)) {
       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nIRC Agent usage:\n\n-name (String)name\n-IRCHost (String)hostname/IP\n-IRCNickname (String)nickname\n[-IRCJoin (String)channel1 ... channeln]\n[-IRCPort (int)port]\n[-Persist t]\n[-Clone 'host1[ ..hostn]']\n[-Deploy 'host1[ ..hostn]']\n$Id: IRC.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
     }

     IRC d = new IRC(bootstrap);
   }

   protected void removeLocationFromChannel(String channel, Location l)
   {
     Collection<Location> s = (Collection<Location>) getChannels().get(channel);
     if (s == null) {
       return;
     }
     s.remove(l);
     if (s.isEmpty())
       getChannels().remove(channel);
   }

   private void acknowledgePublicMsg(MetaProperties m)
   {
     String backto = m.get("JMSReplyTo").toString();

     String response = backto + ", noted";
     Notification n = new Notification(m);
     n.put("JMSDestination", getJMSReplyTo());
     n.put("JMSType", "MSG");
     n.put("Value", response);
     handle_MSG(n);
   }

   protected void addLocationToChannel(String channel, Location l) {
     Collection<Location> s = (Collection<Location>) getChannels().get(channel);
     if (s == null) {
       s = new HashSet<Location>();
     }
     s.add(l);
   }

   public void run() {
     do
       try {
         socket = new Socket(get("IRCHost").toString(), getIRCPort());

         setIs(new BufferedReader(new InputStreamReader(socket.getInputStream())));
         setOs(new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true));
         Env.log(50, "IRC::USER");
         String out = "USER owch owch owch :" + Env.getLocation("owch").getURL() + "/" + getJMSReplyTo();

         getOs().println(out);
         Env.log(50, "IRC::connect string " + out);
         handle_NICK(new Notification(this));

         while (!killFlag)
         {
           String line = getIs().readLine();
           Env.log(50, "IRC::" + line);
           if (line.startsWith("PING")) {
             String pong = "PONG " + line.substring(line.lastIndexOf(":"), line.length());
             getOs().println(pong);
             Env.log(50, "IRC::" + pong);
           } else {
             if (line.startsWith("NOTICE"))
               continue;
             try
             {
               Notification n = new Notification();
               String prefix = "IRC_";
               parseLine(line, n, prefix);
               send(n);
             }
             catch (NoSuchElementException e)
             {
             }
           }
         }
       }
       catch (IOException e) {
       }
     while (containsKey("Persist"));
   }

   public void handle_Dissolve(MetaProperties m)
   {
     super.handle_Dissolve(m);
     try {
       m.put("Value", "Dissolve Message from " + m.getJMSReplyTo());
       socket.close();
     }
     catch (Exception e) {
     }
   }

   public void handle_PART(MetaProperties m) {
     getOs().println("PART " + m.get("Value"));
   }

   public void handle_QUIT(MetaProperties m) {
     getOs().println("QUIT " + m.get("Value"));
   }

   public void handle_MSG(MetaProperties m) {
     try {
       String out = "PRIVMSG " + (m.containsKey("IRCChannel") ? m.get("IRCChannel").toString() : m.get("JMSDestination").toString()) + " :" + m.get("Value");

       getOs().println(out);
       Env.log(50, "IRC<< " + out);
       Env.log(50, "debug: " + m.toString());
     }
     catch (Exception e) {
       handle_Dissolve(new Notification(this));
     }
   }

   public void handle_NICK(MetaProperties m)
   {
     String nick;
     if (m.containsKey("Value")) {
       nick = m.get("Value").toString();
     }
     else {
       nick = m.get("IRCNickname").toString();
     }
     getOs().println("NICK " + nick);
   }

   public void handle_IRC_RPL_ENDOFMOTD(MetaProperties p)
   {
     if (containsKey("IRCJoin"))
       handle_IRC_INVITE(new Notification(this));
   }

   public void handle_IRC_INVITE(MetaProperties m)
   {
     if (!containsKey("NoInvite"))
       handle_JOIN(m);
   }

   public void handle_JOIN(MetaProperties m)
   {
     String t = "flood";
     String chans = m.containsKey("IRCJoin") ? m.get("IRCJoin").toString() : m.get("Value").toString();
     StringTokenizer s = new StringTokenizer(chans);
     while (s.hasMoreTokens()) {
       t = s.nextToken().toString();
       String out = "JOIN " + (t.startsWith("#") ? "" : "#") + t;
       getOs().println(out);
     }
   }

   public void handle_IRC_PRIVMSG(MetaProperties p)
   {
     Notification n = new Notification(p);
     String prefix = "";
     parseLine(p.get("Value").toString().substring(1), n, prefix);
     String dest = n.get("JMSDestination").toString();
     if (n.get("JMSDestination").equals(get("IRCNickname"))) {
       n.put("IRCDestination", dest);
       n.put("JMSDestination", getJMSReplyTo());
     }
     n.put("IRCReplyTo", p.getJMSReplyTo());
     n.put("JMSReplyTo", getJMSReplyTo());
     send(n);
   }

   public void handle_IRC_ERR_NICKNAMEINUSE(MetaProperties p)
   {
     nickname_ctr += 1;

     String t;
     if (containsKey("IRCBasename")) {
       t = get("IRCBasename").toString();
     }
     else {
       t = get("IRCNickname").toString();
       put("IRCBasename", t);
     }


     t += nickname_ctr;
     put("IRCNickname", t);
     handle_NICK(new Notification(this));
   }

   public void handle_AGENT_REMOVE(MetaProperties m)
   {
     Location l = new Location(m);
     StringTokenizer t = new StringTokenizer(m.get("Value").toString());
     while (t.hasMoreTokens()) {
       String token = t.nextToken();
       removeLocationFromChannel(token, l);
     }
   }

   public void handle_AGENT_JOIN(MetaProperties m) {
     Location l = new Location(m);
     StringTokenizer t = new StringTokenizer(m.get("Value").toString());
     while (t.hasMoreTokens()) {
       String token = t.nextToken();
       addLocationToChannel(token, l);
     }
   }

   private void xmitChannelToLocations(MetaProperties m)
   {
     if (getChannels().containsKey(m.get("IRCChannel"))) {
       Collection<Location> c =  (Collection<Location>) getChannels().get(m.get("IRCChannel"));
       Iterator<Location> i = c.iterator();
       while (i.hasNext()) {
         Location l = i.next();
         Notification n = new Notification(m);
         n.put("JMSDestination", l.getJMSReplyTo());
         send(n);
       }
     }
   }

   private void parseLine(String sourceLine, Notification notificationIn, String prefix)
     throws NoSuchElementException
   {
     String JMSDestination = getJMSReplyTo();
     notificationIn.put("Source", sourceLine);
     StringTokenizer tokenizer = new StringTokenizer(sourceLine, ":", false);
     String cmd = tokenizer.nextToken();
     String value = tokenizer.nextToken("").substring(1);
     notificationIn.put("Value", value);
     tokenizer = new StringTokenizer(cmd, " ", false);
     String IRCReplyTo = tokenizer.nextToken();
     String JMSType = tokenizer.nextToken().trim();
     String parameters = tokenizer.nextToken("").trim();
     JMSType = RFCConversion(JMSType);
     tokenizer = new StringTokenizer(IRCReplyTo, "!", false);
     String nick = tokenizer.nextToken();
     notificationIn.put("IRCReplyTo", IRCReplyTo);
     notificationIn.put("IRCAgent", getJMSReplyTo());
     notificationIn.put("JMSReplyTo", nick);
     String IRCDestination;
     if (JMSType.equals("PRIVMSG"))
     {
       IRCDestination = parameters;
       JMSDestination = IRCDestination.equals(get("IRCNickName")) ? get("JMSReplyTo").toString() : IRCDestination;
       notificationIn.put("IRCDestination", IRCDestination.trim());

       notificationIn.put("JMSDestination", JMSDestination);
     }
     else if ((JMSType.equals("RPL_NAMREPLY")) || (JMSType.equals("RPL_ENDOFNAMES"))) {
       tokenizer = new StringTokenizer(parameters, "=");
       IRCDestination = tokenizer.nextToken().trim();
       JMSDestination = tokenizer.nextToken().trim();
       notificationIn.put("IRCDestination", IRCDestination.trim());
     }
     notificationIn.put("JMSDestination", JMSDestination.trim().toLowerCase());
     notificationIn.put("JMSType", prefix + JMSType);
     Env.log(500, notificationIn.toString());
   }

   private static final String RFCConversion(String JMSType)
   {
     if (RFCTags.containsKey(JMSType)) {
       JMSType = (RFCTags.get(JMSType))[0];
     }
     return JMSType;
   }

   protected Socket getSocket()
   {
     return socket;
   }

   protected void setSocket(Socket socket)
   {
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

   static
   {
     RFCTags.put("001".trim(), new String[] { "RPL_WELCOME\t".trim(), "\tWelcome to the Internet Relay Network   <nick>!<user>@<host>\t".trim() });

     RFCTags.put("002".trim(), new String[] { "RPL_YOURHOST\t".trim(), "\tYour host is <servername>, running version <ver>\t".trim() });

     RFCTags.put("003".trim(), new String[] { "RPL_CREATED\t".trim(), "\tThis server was created <date>\t".trim() });

     RFCTags.put("004".trim(), new String[] { "RPL_MYINFO\t".trim(), "\t<servername> <version> <available user modes>\n<available channel modes>\t".trim() });

     RFCTags.put("005".trim(), new String[] { "RPL_BOUNCE\t".trim(), "\tTry server <server name>, port <port number>\t".trim() });

     RFCTags.put("200".trim(), new String[] { "RPL_TRACELINK\t".trim(), "\tLink <version & debug level> <destination>               <next server> V<protocol version>               <link uptime in seconds> <backstream sendq>               <upstream sendq>\t".trim() });

     RFCTags.put("201".trim(), new String[] { "RPL_TRACECONNECTING\t".trim(), "\tTry. <class> <server>\t".trim() });

     RFCTags.put("202".trim(), new String[] { "RPL_TRACEHANDSHAKE\t".trim(), "\tH.S. <class> <server>\t".trim() });

     RFCTags.put("203".trim(), new String[] { "RPL_TRACEUNKNOWN\t".trim(), "\t???? <class> [<client IP address in dot form>]\t".trim() });

     RFCTags.put("204".trim(), new String[] { "RPL_TRACEOPERATOR\t".trim(), "\tOper <class> <nick>\t".trim() });

     RFCTags.put("205".trim(), new String[] { "RPL_TRACEUSER\t".trim(), "\tUser <class> <nick>\t".trim() });

     RFCTags.put("206".trim(), new String[] { "RPL_TRACESERVER\t".trim(), "\tServ <class> <int>S <int>C <server>               <nick!user|*!*>@<host|server> V<protocol version>\t".trim() });

     RFCTags.put("207".trim(), new String[] { "RPL_TRACESERVICE\t".trim(), "\tService <class> <name> <type> <active type>\t".trim() });

     RFCTags.put("208".trim(), new String[] { "RPL_TRACENEWTYPE\t".trim(), "\t<newtype> 0 <client name>\t".trim() });

     RFCTags.put("209".trim(), new String[] { "RPL_TRACECLASS\t".trim(), "\tClass <class> <count>\t".trim() });

     RFCTags.put("210".trim(), new String[] { "RPL_TRACERECONNECT\t".trim(), "\tUnused.\t".trim() });

     RFCTags.put("211".trim(), new String[] { "RPL_STATSLINKINFO\t".trim(), "\t<linkname> <sendq> <sent messages>               <sent Kbytes> <received messages>               <received Kbytes> <time open>\t".trim() });

     RFCTags.put("212".trim(), new String[] { "RPL_STATSCOMMANDS\t".trim(), "\t<command> <count> <byte count> <remote count>\t".trim() });

     RFCTags.put("219".trim(), new String[] { "RPL_ENDOFSTATS\t".trim(), "\t<stats letter> :End of STATS report\t".trim() });

     RFCTags.put("221".trim(), new String[] { "RPL_UMODEIS\t".trim(), "\t<user mode string>\t".trim() });

     RFCTags.put("234".trim(), new String[] { "RPL_SERVLIST\t".trim(), "\t<name> <server> <mask> <type> <hopcount> <info>\t".trim() });

     RFCTags.put("235".trim(), new String[] { "RPL_SERVLISTEND\t".trim(), "\t<mask> <type> :End of service listing\t".trim() });

     RFCTags.put("242".trim(), new String[] { "RPL_STATSUPTIME\t".trim(), "\t:Server Up %d days %d:%02d:%02d\t".trim() });

     RFCTags.put("243".trim(), new String[] { "RPL_STATSOLINE\t".trim(), "\tO <hostmask> * <name>\t".trim() });

     RFCTags.put("251".trim(), new String[] { "RPL_LUSERCLIENT\t".trim(), "\t:There are <integer> users and <integer>               services on <integer> servers\t".trim() });

     RFCTags.put("252".trim(), new String[] { "RPL_LUSEROP\t".trim(), "\t<integer> :operator(s) online\t".trim() });

     RFCTags.put("253".trim(), new String[] { "RPL_LUSERUNKNOWN\t".trim(), "\t<integer> :unknown connection(s)\t".trim() });

     RFCTags.put("254".trim(), new String[] { "RPL_LUSERCHANNELS\t".trim(), "\t<integer> :channels formed\t".trim() });

     RFCTags.put("255".trim(), new String[] { "RPL_LUSERME\t".trim(), "\t:I have <integer> clients and <integer>                servers\t".trim() });

     RFCTags.put("256".trim(), new String[] { "RPL_ADMINME\t".trim(), "\t<server> :Administrative info\t".trim() });

     RFCTags.put("257".trim(), new String[] { "RPL_ADMINLOC1\t".trim(), "\t:<admin info>\t".trim() });

     RFCTags.put("258".trim(), new String[] { "RPL_ADMINLOC2\t".trim(), "\t:<admin info>\t".trim() });

     RFCTags.put("259".trim(), new String[] { "RPL_ADMINEMAIL\t".trim(), "\t:<admin info>\t".trim() });

     RFCTags.put("261".trim(), new String[] { "RPL_TRACELOG\t".trim(), "\tFile <logfile> <debug level>\t".trim() });

     RFCTags.put("262".trim(), new String[] { "RPL_TRACEEND\t".trim(), "\t<server name> <version & debug level> :End of TRACE\t".trim() });

     RFCTags.put("263".trim(), new String[] { "RPL_TRYAGAIN\t".trim(), "\t<command> :Please wait a while and try again.\t".trim() });

     RFCTags.put("301".trim(), new String[] { "RPL_AWAY\t".trim(), "\t<nick> :<away message>\t".trim() });

     RFCTags.put("302".trim(), new String[] { "RPL_USERHOST\t".trim(), "\t:*1<reply> *( \t".trim() });

     RFCTags.put("305".trim(), new String[] { "RPL_UNAWAY\t".trim(), "\t:You are no longer marked as being away\t".trim() });

     RFCTags.put("306".trim(), new String[] { "RPL_NOWAWAY\t".trim(), "\t:You have been marked as being away\t".trim() });

     RFCTags.put("311".trim(), new String[] { "RPL_WHOISUSER\t".trim(), "\t<nick> <user> <host> * :<real name>\t".trim() });

     RFCTags.put("312".trim(), new String[] { "RPL_WHOISSERVER\t".trim(), "\t<nick> <server> :<server info>\t".trim() });

     RFCTags.put("313".trim(), new String[] { "RPL_WHOISOPERATOR\t".trim(), "\t<nick> :is an IRC operator\t".trim() });

     RFCTags.put("314".trim(), new String[] { "RPL_WHOWASUSER\t".trim(), "\t<nick> <user> <host> * :<real name>\t".trim() });

     RFCTags.put("315".trim(), new String[] { "RPL_ENDOFWHO\t".trim(), "\t<name> :End of WHO list\t".trim() });

     RFCTags.put("317".trim(), new String[] { "RPL_WHOISIDLE\t".trim(), "\t<nick> <integer> :seconds idle\t".trim() });

     RFCTags.put("318".trim(), new String[] { "RPL_ENDOFWHOIS\t".trim(), "\t<nick> :End of WHOIS list\t".trim() });

     RFCTags.put("319".trim(), new String[] { "RPL_WHOISCHANNELS\t".trim(), "\t<nick> :*( ( @".trim() });

     RFCTags.put("321".trim(), new String[] { "RPL_LISTSTART\t".trim(), "\tObsolete.\t".trim() });

     RFCTags.put("322".trim(), new String[] { "RPL_LIST\t".trim(), "\t<channel> <# visible> :<topic>\t".trim() });

     RFCTags.put("323".trim(), new String[] { "RPL_LISTEND\t".trim(), "\t:End of LIST\t".trim() });

     RFCTags.put("324".trim(), new String[] { "RPL_CHANNELMODEIS\t".trim(), "\t<channel> <mode> <mode params>\t".trim() });

     RFCTags.put("325".trim(), new String[] { "RPL_UNIQOPIS\t".trim(), "\t<channel> <nickname>\t".trim() });

     RFCTags.put("331".trim(), new String[] { "RPL_NOTOPIC\t".trim(), "\t<channel> :No topic is set\t".trim() });

     RFCTags.put("332".trim(), new String[] { "RPL_TOPIC\t".trim(), "\t<channel> :<topic>\t".trim() });

     RFCTags.put("341".trim(), new String[] { "RPL_INVITING\t".trim(), "\t<channel> <nick>\t".trim() });

     RFCTags.put("342".trim(), new String[] { "RPL_SUMMONING\t".trim(), "\t<user> :Summoning user to IRC\t".trim() });

     RFCTags.put("346".trim(), new String[] { "RPL_INVITELIST\t".trim(), "\t<channel> <invitemask>\t".trim() });

     RFCTags.put("347".trim(), new String[] { "RPL_ENDOFINVITELIST\t".trim(), "\t<channel> :End of channel invite list\t".trim() });

     RFCTags.put("348".trim(), new String[] { "RPL_EXCEPTLIST\t".trim(), "\t<channel> <exceptionmask>\t".trim() });

     RFCTags.put("349".trim(), new String[] { "RPL_ENDOFEXCEPTLIST\t".trim(), "\t<channel> :End of channel exception list\t".trim() });

     RFCTags.put("351".trim(), new String[] { "RPL_VERSION\t".trim(), "\t<version>.<debuglevel> <server> :<comments>\t".trim() });

     RFCTags.put("352".trim(), new String[] { "RPL_WHOREPLY\t".trim(), "\t<channel> <user> <host> <server> <nick>           \t".trim() });

     RFCTags.put("353".trim(), new String[] { "RPL_NAMREPLY\t".trim(), "\t \t".trim() });

     RFCTags.put("364".trim(), new String[] { "RPL_LINKS\t".trim(), "\t<mask> <server> :<hopcount> <server info>\t".trim() });

     RFCTags.put("365".trim(), new String[] { "RPL_ENDOFLINKS\t".trim(), "\t<mask> :End of LINKS list\t".trim() });

     RFCTags.put("366".trim(), new String[] { "RPL_ENDOFNAMES\t".trim(), "\t<channel> :End of NAMES list\t".trim() });

     RFCTags.put("367".trim(), new String[] { "RPL_BANLIST\t".trim(), "\t<channel> <banmask>\t".trim() });

     RFCTags.put("368".trim(), new String[] { "RPL_ENDOFBANLIST\t".trim(), "\t<channel> :End of channel ban list\t".trim() });

     RFCTags.put("369".trim(), new String[] { "RPL_ENDOFWHOWAS\t".trim(), "\t<nick> :End of WHOWAS\t".trim() });

     RFCTags.put("371".trim(), new String[] { "RPL_INFO\t".trim(), "\t:<string>\t".trim() });

     RFCTags.put("372".trim(), new String[] { "RPL_MOTD\t".trim(), "\t:- <text>\t".trim() });

     RFCTags.put("374".trim(), new String[] { "RPL_ENDOFINFO\t".trim(), "\t:End of INFO list\t".trim() });

     RFCTags.put("375".trim(), new String[] { "RPL_MOTDSTART\t".trim(), "\t:- <server> Message of the day - \t".trim() });

     RFCTags.put("376".trim(), new String[] { "RPL_ENDOFMOTD\t".trim(), "\t:End of MOTD command\t".trim() });

     RFCTags.put("381".trim(), new String[] { "RPL_YOUREOPER\t".trim(), "\t:You are now an IRC operator\t".trim() });

     RFCTags.put("382".trim(), new String[] { "RPL_REHASHING\t".trim(), "\t<config file> :Rehashing\t".trim() });

     RFCTags.put("383".trim(), new String[] { "RPL_YOURESERVICE\t".trim(), "\tYou are service <servicename>\t".trim() });

     RFCTags.put("391".trim(), new String[] { "RPL_TIME\t".trim(), "\t<server> :<string showing server's local time>\t".trim() });

     RFCTags.put("392".trim(), new String[] { "RPL_USERSSTART\t".trim(), "\t:UserID   Terminal  Host\t".trim() });

     RFCTags.put("393".trim(), new String[] { "RPL_USERS\t".trim(), "\t:<username> <ttyline> <hostname>\t".trim() });

     RFCTags.put("394".trim(), new String[] { "RPL_ENDOFUSERS\t".trim(), "\t:End of users\t".trim() });

     RFCTags.put("395".trim(), new String[] { "RPL_NOUSERS\t".trim(), "\t:Nobody logged in\t".trim() });

     RFCTags.put("401".trim(), new String[] { "ERR_NOSUCHNICK\t".trim(), "\t<nickname> :No such nick/channel\t".trim() });

     RFCTags.put("402".trim(), new String[] { "ERR_NOSUCHSERVER\t".trim(), "\t<server name> :No such server\t".trim() });

     RFCTags.put("403".trim(), new String[] { "ERR_NOSUCHCHANNEL\t".trim(), "\t<channel name> :No such channel\t".trim() });

     RFCTags.put("404".trim(), new String[] { "ERR_CANNOTSENDTOCHAN\t".trim(), "\t<channel name> :Cannot send to channel\t".trim() });

     RFCTags.put("405".trim(), new String[] { "ERR_TOOMANYCHANNELS\t".trim(), "\t<channel name> :You have joined too many channels\t".trim() });

     RFCTags.put("406".trim(), new String[] { "ERR_WASNOSUCHNICK\t".trim(), "\t<nickname> :There was no such nickname\t".trim() });

     RFCTags.put("407".trim(), new String[] { "ERR_TOOMANYTARGETS\t".trim(), "\t<target> :<error code> recipients. <abort message>\t".trim() });

     RFCTags.put("408".trim(), new String[] { "ERR_NOSUCHSERVICE\t".trim(), "\t<service name> :No such service\t".trim() });

     RFCTags.put("409".trim(), new String[] { "ERR_NOORIGIN\t".trim(), "\t:No origin specified\t".trim() });

     RFCTags.put("411".trim(), new String[] { "ERR_NORECIPIENT\t".trim(), "\t:No recipient given (<command>)\t".trim() });

     RFCTags.put("412".trim(), new String[] { "ERR_NOTEXTTOSEND\t".trim(), "\t:No text to send\t".trim() });

     RFCTags.put("413".trim(), new String[] { "ERR_NOTOPLEVEL\t".trim(), "\t<mask> :No toplevel domain specified\t".trim() });

     RFCTags.put("414".trim(), new String[] { "ERR_WILDTOPLEVEL\t".trim(), "\t<mask> :Wildcard in toplevel domain\t".trim() });

     RFCTags.put("415".trim(), new String[] { "ERR_BADMASK\t".trim(), "\t<mask> :Bad Server/host mask\t".trim() });

     RFCTags.put("421".trim(), new String[] { "ERR_UNKNOWNCOMMAND\t".trim(), "\t<command> :Unknown command\t".trim() });

     RFCTags.put("422".trim(), new String[] { "ERR_NOMOTD\t".trim(), "\t:MOTD File is missing\t".trim() });

     RFCTags.put("423".trim(), new String[] { "ERR_NOADMININFO\t".trim(), "\t<server> :No administrative info available\t".trim() });

     RFCTags.put("424".trim(), new String[] { "ERR_FILEERROR\t".trim(), "\t:File error doing <file op> on <file>\t".trim() });

     RFCTags.put("431".trim(), new String[] { "ERR_NONICKNAMEGIVEN\t".trim(), "\t:No nickname given\t".trim() });

     RFCTags.put("432".trim(), new String[] { "ERR_ERRONEUSNICKNAME\t".trim(), "\t<nick> :Erroneous nickname\t".trim() });

     RFCTags.put("433".trim(), new String[] { "ERR_NICKNAMEINUSE\t".trim(), "\t<nick> :Nickname is already in use\t".trim() });

     RFCTags.put("436".trim(), new String[] { "ERR_NICKCOLLISION\t".trim(), "\t<nick> :Nickname collision KILL from <user>@<host>\t".trim() });

     RFCTags.put("437".trim(), new String[] { "ERR_UNAVAILRESOURCE\t".trim(), "\t<nick/channel> :Nick/channel is temporarily unavailable\t".trim() });

     RFCTags.put("441".trim(), new String[] { "ERR_USERNOTINCHANNEL\t".trim(), "\t<nick> <channel> :They aren't on that channel\t".trim() });

     RFCTags.put("442".trim(), new String[] { "ERR_NOTONCHANNEL\t".trim(), "\t<channel> :You're not on that channel\t".trim() });

     RFCTags.put("443".trim(), new String[] { "ERR_USERONCHANNEL\t".trim(), "\t<user> <channel> :is already on channel\t".trim() });

     RFCTags.put("444".trim(), new String[] { "ERR_NOLOGIN\t".trim(), "\t<user> :User not logged in\t".trim() });

     RFCTags.put("445".trim(), new String[] { "ERR_SUMMONDISABLED\t".trim(), "\t:SUMMON has been disabled\t".trim() });

     RFCTags.put("446".trim(), new String[] { "ERR_USERSDISABLED\t".trim(), "\t:USERS has been disabled\t".trim() });

     RFCTags.put("451".trim(), new String[] { "ERR_NOTREGISTERED\t".trim(), "\t:You have not registered\t".trim() });

     RFCTags.put("461".trim(), new String[] { "ERR_NEEDMOREPARAMS\t".trim(), "\t<command> :Not enough parameters\t".trim() });

     RFCTags.put("462".trim(), new String[] { "ERR_ALREADYREGISTRED\t".trim(), "\t:Unauthorized command (already registered)\t".trim() });

     RFCTags.put("463".trim(), new String[] { "ERR_NOPERMFORHOST\t".trim(), "\t:Your host isn't among the privileged\t".trim() });

     RFCTags.put("464".trim(), new String[] { "ERR_PASSWDMISMATCH\t".trim(), "\t:Password incorrect\t".trim() });

     RFCTags.put("465".trim(), new String[] { "ERR_YOUREBANNEDCREEP\t".trim(), "\t:You are banned from this server\t".trim() });

     RFCTags.put("466".trim(), new String[] { "ERR_YOUWILLBEBANNED\t".trim(), "\t\t".trim() });

     RFCTags.put("467".trim(), new String[] { "ERR_KEYSET\t".trim(), "\t<channel> :Channel key already set\t".trim() });

     RFCTags.put("471".trim(), new String[] { "ERR_CHANNELISFULL\t".trim(), "\t<channel> :Cannot join channel (+l)\t".trim() });

     RFCTags.put("472".trim(), new String[] { "ERR_UNKNOWNMODE\t".trim(), "\t<char> :is unknown mode char to me for <channel>\t".trim() });

     RFCTags.put("473".trim(), new String[] { "ERR_INVITEONLYCHAN\t".trim(), "\t<channel> :Cannot join channel (+i)\t".trim() });

     RFCTags.put("474".trim(), new String[] { "ERR_BANNEDFROMCHAN\t".trim(), "\t<channel> :Cannot join channel (+b)\t".trim() });

     RFCTags.put("475".trim(), new String[] { "ERR_BADCHANNELKEY\t".trim(), "\t<channel> :Cannot join channel (+k)\t".trim() });

     RFCTags.put("476".trim(), new String[] { "ERR_BADCHANMASK\t".trim(), "\t<channel> :Bad Channel Mask\t".trim() });

     RFCTags.put("477".trim(), new String[] { "ERR_NOCHANMODES\t".trim(), "\t<channel> :Channel doesn't support modes\t".trim() });

     RFCTags.put("478".trim(), new String[] { "ERR_BANLISTFULL\t".trim(), "\t<channel> <char> :Channel list is full\t".trim() });

     RFCTags.put("481".trim(), new String[] { "ERR_NOPRIVILEGES\t".trim(), "\t:Permission Denied- You're not an IRC operator\t".trim() });

     RFCTags.put("482".trim(), new String[] { "ERR_CHANOPRIVSNEEDED\t".trim(), "\t<channel> :You're not channel operator\t".trim() });

     RFCTags.put("483".trim(), new String[] { "ERR_CANTKILLSERVER\t".trim(), "\t:You can't kill a server!\t".trim() });

     RFCTags.put("484".trim(), new String[] { "ERR_RESTRICTED\t".trim(), "\t:Your connection is restricted!\t".trim() });

     RFCTags.put("485".trim(), new String[] { "ERR_UNIQOPPRIVSNEEDED\t".trim(), "\t:You're not the original channel operator\t".trim() });

     RFCTags.put("491".trim(), new String[] { "ERR_NOOPERHOST\t".trim(), "\t:No O-lines for your host\t".trim() });

     RFCTags.put("501".trim(), new String[] { "ERR_UMODEUNKNOWNFLAG\t".trim(), "\t:Unknown MODE flag\t".trim() });

     RFCTags.put("502".trim(), new String[] { "ERR_USERSDONTMATCH\t".trim(), "\t:Cannot change mode for other users\t".trim() });
   }
 }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.IRC
 * JD-Core Version:    0.6.0
 */