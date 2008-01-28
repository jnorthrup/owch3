package net.sourceforge.owch2.agent;

/**
 * @version $Id$
 * @Author James Northrup
 * @copyright All Rights Reserved Glamdring Inc.
 */


enum RFC_IRC {
    RPL_AWAY(301, "<nick> :<away message>"),
    ERR_FILEERROR(424, ":File error doing <file op> on <file>"),
    RPL_LUSERCHANNELS(254, "<integer> :channels formed"),
    RPL_LISTEND(323, ":End of LIST"),
    RPL_LUSERCLIENT(251, ":There are <integer> users and <integer> services on <integer> servers"),
    RPL_WHOREPLY(352, "<channel> <user> <host> <server> <nick>"),
    RPL_TRACELOG(261, "File <logfile> <debug level>"),
    RPL_ENDOFINFO(374, ":End of INFO list"),
    RPL_ENDOFMOTD(376, ":End of MOTD command"),
    ERR_NOCHANMODES(477, "<channel> :Channel doesn't support modes"),
    RPL_EXCEPTLIST(348, "<channel> <exceptionmask>"),
    RPL_LUSEROP(252, "<integer> :operator(s) online"),
    ERR_BADCHANMASK(476, "<channel> :Bad Channel Mask"),
    ERR_NOMOTD(422, ":MOTD File is missing"),
    RPL_MOTDSTART(375, ":- <server> EventDescriptor of the day -"),
    RPL_TRYAGAIN(263, "<command> :Please wait a while and try again."),
    RPL_STATSUPTIME(242, ":Server Up %d days %d:%02d:%02d"),
    RPL_USERSSTART(392, ":UserID   Terminal  Host"),
    ERR_UNKNOWNCOMMAND(421, "<command> :Unknown command"),
    ERR_UMODEUNKNOWNFLAG(501, ":Unknown MODE flag"),
    ERR_NOTREGISTERED(451, ":You have not registered"),
    ERR_USERSDONTMATCH(502, ":Cannot change mode for other users"),
    ERR_BANNEDFROMCHAN(474, "<channel> :Cannot join channel (+b)"),
    ERR_NOADMININFO(423, "<server> :No administrative info available"),
    ERR_KEYSET(467, "<channel> :Channel key already set"),
    RPL_ENDOFINVITELIST(347, "<channel> :End of channel invite list"),
    RPL_INVITING(341, "<channel> <nick>"),
    RPL_LUSERUNKNOWN(253, "<integer> :unknown connection(s)"),
    ERR_WILDTOPLEVEL(414, "<mask> :Wildcard in toplevel domain"),
    RPL_BOUNCE(005, "Try server <server name>, port <port number>"),
    RPL_STATSCOMMANDS(212, "<command> <count> <byte count> <remote count>"),
    RPL_INVITELIST(346, "<channel> <invitemask>"),
    RPL_USERHOST(302, ":*1<reply> *("),
    RPL_WHOISSERVER(312, "<nick> <server> :<server info>"),
    RPL_TRACELINK(200, "Link <version & debug level> <destination>               <next server> V<protocol version>               <link uptime in seconds> <backstream sendq>               <upstream sendq>"),
    ERR_UNIQOPPRIVSNEEDED(485, ":You're not the original channel operator"),
    ERR_NOTEXTTOSEND(412, ":No text to route"),
    ERR_NOSUCHSERVER(402, "<server name> :No such server"),
    RPL_WHOISUSER(311, "<nick> <user> <host> * :<real name>"),
    RPL_ENDOFLINKS(365, "<mask> :End of LINKS list"),
    ERR_CANTKILLSERVER(483, ":You can't kill a server!"),
    RPL_ENDOFBANLIST(368, "<channel> :End of channel ban list"),
    ERR_TOOMANYCHANNELS(405, "<channel name> :You have joined too many channels"),
    RPL_TIME(391, "<server> :<string showing server's local time>"),
    ERR_NOTOPLEVEL(413, "<mask> :No toplevel domain specified"),
    RPL_LINKS(364, "<mask> <server> :<hopcount> <server info>"),
    ERR_INVITEONLYCHAN(473, "<channel> :Cannot join channel (+i)"),
    ERR_NOSUCHSERVICE(408, "<service name> :No such service"),
    RPL_NAMREPLY(353, ""),
    RPL_NOWAWAY(306, ":You have been marked as being away"),
    RPL_MYINFO(004, "<servername> <version> <available user modes>    <available ;        channel modes        private short code;        private String format;        >"),
    ERR_TOOMANYTARGETS(407, "<target> :<error code> recipients. <abort message>"),
    RPL_LISTSTART(321, "Obsolete."),
    RPL_UNAWAY(305, ":You are no longer marked as being away"),
    ERR_WASNOSUCHNICK(406, "<nickname> :There was no such nickname"),
    RPL_STATSOLINE(243, "O <hostmask> * <name>"),
    ERR_CHANNELISFULL(471, "<channel> :Cannot join channel (+l)"),
    ERR_ERRONEUSNICKNAME(432, "<nick> :Erroneous nickname"),
    RPL_TRACESERVICE(207, "Service <class> <name> <type> <active type>"),
    RPL_YOURHOST(002, "Your host is <servername>, running version <ver>"),
    RPL_ADMINLOC2(258, ":<admin info>"),
    RPL_SERVLISTEND(235, "<mask> <type> :End of service listing"),
    RPL_LIST(322, "<channel> <# visible> :<topic>"),
    ERR_NOORIGIN(409, ":No origin specified"),
    RPL_YOURESERVICE(383, "You are service <servicename>"),
    ERR_NONICKNAMEGIVEN(431, ":No nickname given"),
    RPL_CREATED(003, "This server was created <date>"),
    RPL_SERVLIST(234, "<name> <server> <mask> <type> <hopcount> <info>"),
    RPL_TRACEHANDSHAKE(202, "H.S. <class> <server>"),
    ERR_NOSUCHCHANNEL(403, "<channel name> :No such channel"),
    RPL_BANLIST(367, "<channel> <banmask>"),
    ERR_NOOPERHOST(491, ":No O-lines for your host"),
    RPL_REHASHING(382, "<config file> :Rehashing"),
    RPL_VERSION(351, "<version>.<debuglevel> <server> :<comments>"),
    RPL_TRACECONNECTING(201, "Try. <class> <server>"),
    ERR_BADCHANNELKEY(475, "<channel> :Cannot join channel (+k)"),
    ERR_CANNOTSENDTOCHAN(404, "<channel name> :Cannot route to channel"),
    ERR_NICKNAMEINUSE(433, "<nick> :Nickname is already in use"),
    RPL_YOUREOPER(381, ":You are now an IRC operator"),
    ERR_YOUREBANNEDCREEP(465, ":You are banned from this server"),
    RPL_SUMMONING(342, "<user> :Summoning user to IRC"),
    RPL_WHOISCHANNELS(319, "<nick> :*( ( @"),
    ERR_USERONCHANNEL(443, "<user> <channel> :is already on channel"),
    RPL_TRACENEWTYPE(208, "<newtype> 0 <client name>"),
    RPL_ENDOFSTATS(219, "<stats letter> :End of STATS report"),
    RPL_WHOISIDLE(317, "<nick> <integer> :seconds idle"),
    RPL_WHOWASUSER(314, "<nick> <user> <host> * :<real name>"),
    ERR_YOUWILLBEBANNED(466, ""),
    ERR_NOPRIVILEGES(481, ":Permission Denied- You're not an IRC operator"),
    RPL_WELCOME(001, "Welcome to the Internet Relay Network   <nick>!<user>@<host>"),
    ERR_NOLOGIN(444, "<user> :User not logged in"),
    ERR_NOSUCHNICK(401, "<nickname> :No such nick/channel"),
    RPL_ENDOFNAMES(366, "<channel> :End of NAMES list"),
    RPL_ENDOFWHOWAS(369, "<nick> :End of WHOWAS"),
    ERR_PASSWDMISMATCH(464, ":Password incorrect"),
    RPL_NOTOPIC(331, "<channel> :No topic is set"),
    RPL_ADMINLOC1(257, ":<admin info>"),
    ERR_NOTONCHANNEL(442, "<channel> :You're not on that channel"),
    RPL_UMODEIS(221, "<user mode string>"),
    ERR_UNKNOWNMODE(472, "<char> :is unknown mode char to me for <channel>"),
    ERR_SUMMONDISABLED(445, ":SUMMON has been disabled"),
    RPL_ADMINEMAIL(259, ":<admin info>"),
    ERR_RESTRICTED(484, ":Your connection is restricted!"),
    RPL_ADMINME(256, "<server> :Administrative info"),
    ERR_USERNOTINCHANNEL(441, "<nick> <channel> :They aren't on that channel"),
    RPL_TRACECLASS(209, "Class <class> <count>"),
    RPL_UNIQOPIS(325, "<channel> <nickname>"),
    RPL_WHOISOPERATOR(313, "<nick> :is an IRC operator"),
    RPL_CHANNELMODEIS(324, "<channel> <mode> <mode params>"),
    RPL_TOPIC(332, "<channel> :<topic>"),
    ERR_NORECIPIENT(411, ":No recipient given (<command>)"),
    RPL_STATSLINKINFO(211, "<linkname> <sendq> <sent messages>               <sent Kbytes> <received messages>               <received Kbytes> <time open>"),
    ERR_BANLISTFULL(478, "<channel> <char> :Channel list is full"),
    ERR_USERSDISABLED(446, ":USERS has been disabled"),
    RPL_TRACERECONNECT(210, "Unused."),
    RPL_TRACEUNKNOWN(203, "???? <class> [<client IP address in dot form>]"),
    ERR_NOPERMFORHOST(463, ":Your host isn't among the privileged"),
    RPL_MOTD(372, ":- <text>"),
    ERR_UNAVAILRESOURCE(437, "<nick/channel> :Nick/channel is temporarily unavailable"),
    RPL_LUSERME(255, ":I have <integer> clients and <integer> servers"),
    RPL_TRACESERVER(206, "Serv <class> <int>S <int>C <server>               <nick!user|*!*>@<host|server> V<protocol version>"),
    RPL_TRACEEND(262, "<server name> <version & debug level> :End of TRACE"),
    ERR_BADMASK(415, "<mask> :Bad Server/host mask"),
    ERR_NEEDMOREPARAMS(461, "<command> :Not enough parameters"),
    RPL_TRACEUSER(205, "User <class> <nick>"),
    RPL_ENDOFEXCEPTLIST(349, "<channel> :End of channel exception list"),
    RPL_TRACEOPERATOR(204, "Oper <class> <nick>"),
    ERR_ALREADYREGISTRED(462, ":Unauthorized command (already registered)"),
    RPL_NOUSERS(395, ":Nobody logged in"),
    ERR_CHANOPRIVSNEEDED(482, "<channel> :You're not channel operator"),
    RPL_INFO(371, ":<string>"),
    RPL_ENDOFWHO(315, "<name> :End of WHO list"),
    RPL_ENDOFUSERS(394, ":End of users"),
    RPL_USERS(393, ":<username> <ttyline> <hostname>"),
    RPL_ENDOFWHOIS(318, "<nick> :End of WHOIS list"),
    ERR_NICKCOLLISION(436, "<nick> :Nickname collision KILL from <user>@<host>");

    short code;
    private String format;


    RFC_IRC(int code, String format) {
        this.code = (short) code;
        this.format = format;

    }
}
