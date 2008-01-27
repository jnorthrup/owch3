package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import static java.lang.Thread.*;
import java.net.*;
import java.util.*;

public class SocksProxy extends AbstractAgent implements Runnable {
    private ServerSocket ss;

    final static String[] errs = new String[]{
            "succeeded",
            "general SOCKS server failure",
            "connection not allowed by ruleset",
            "Network unreachable",
            "Host unreachable",
            "Connection refused",
            "TTL expired",
            "Command not supported",
            "Address type not supported",
            "to X'FF' unassigned",
    };

    public static void main(String[] args) {
        Map<?, ?> m = Env.getInstance().parseCommandLineArgs(args);
        if (!(m.containsKey(Message.REPLYTO_KEY) && m.containsKey("SocksHost") && m.containsKey("SourcePort") &&
                m.containsKey("SourceHost") && m.containsKey("AgentPort"))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" + "SocketProxy Agent usage:\n\n" +
                    "-name       (String)name\n" + "-SourceHost (String)hostname/IP\n" + "-SocksHost (String)hostname/IP\n" +
                    "-SourcePort (int)port\n" + "-AgentPort  (int)port\n" +
                    "[-SocksPort (int)port]\n" + "[-Clone 'host1[ ..hostn]']\n" + "[-Deploy 'host1[ ..hostn]']\n" +
                    "$Id$\n");
        }
        SocketProxy d = new SocketProxy(m);
        Thread t = new Thread();
        try {
            t.start();
            while (!Env.getInstance().shutdown) {
                sleep(6000);
            } //todo: something
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getSourcePort() {
        return Integer.decode((String) get("SourcePort")).intValue();
    }

    public int getSocksPort() {
        if (this.containsKey("SocksPort")) {
            return Integer.decode((String) get("SocksPort")).intValue();
        } else {
            return 1080;
        }
    }

    public int getProxyPort() {
        return Integer.decode((String) get("AgentPort")).intValue();
    }

    /**
     * handy remote deployment code
     */
    public SocksProxy(Map m) {
        super(m);
        try {
            relocate();
            setSs(new ServerSocket(getProxyPort()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int socksPort = 1080;

    /**
     * The client connects to the server, and sends a version * identifier/method selection message: <TABLE BORDER WIDTH=1>
     * <TR> <TH>VER <TH>NMETHODS <TH> METHODS </TR> <TR><TD>1<TD>1 <TD> 1 to 255 </TABLE>
     * <P> The VER field is set to X'05' for this version of the protocol. The NMETHODS field contains the number of
     * method identifier octets that appear in the METHODS field. The server selects from one of the
     * app given in METHODS, and sends a METHOD selection message: <TABLE BORDER WIDTH=1>
     * <TR><TH>VER <TH>METHOD </TR> <TR><TD>1 <TD>1 </TR></TABLE> <P>If the selected METHOD is X'FF', none of the app
     * listed by the client are acceptable, and the client MUST close the connection.  The values currently defined for METHOD
     * are: <TABLE BORDER WIDTH=1> <TR><TD>X'00'TD<TD> NO AUTHENTICATION REQUIRED</TR> <TR><TD>X'01'<TD> GSSAPI</TR>
     * <TR><TD>X'02' <TD>USERNAME/PASSWORD </TR> <TR><TD>X'03' to X'7F' <TD>IANA ASSIGNED </TR>
     * <TR><TD>X'80' to X'FE'<TD> RESERVED FOR PRIVATE METHODS </TR> <TR><TD> X'FF' <TD>NO ACCEPTABLE METHODS </TABLE>
     */
    public void run() {
        while (!killFlag) {
            try {
                //todo: time out somehow
                Socket inbound = getSs().accept(); //wait for
                // connection on AgentPort
                PipeSocket ps = new PipeSocket(inbound);
                Socket socks = new Socket((String) get("SocksHost"), getSocksPort());
                byte[] app = new byte[]{
                        0 //,2
                };
                byte ver = 5, napp = (byte) app.length;
                socks.getOutputStream().write(new byte[]{ver, napp});
                socks.getOutputStream().write(app);
                byte[] resp = new byte[2];
                int i = socks.getInputStream().read(resp);
                if (!(resp[0] == 5 && resp[1] == 0)) {
                    inbound.close();
                    return;
                }
                this.send_request(socks);
                if (this.handle_response(socks)) {
                    ps.connectTarget(socks);
                    ps.spin();
                }
            }
            catch (InterruptedIOException e) {
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p> Requests Once the method-dependent subnegotiation has completed, the client sends the request details.  If the
     * negotiated method includes encapsulation for purposes of integrity checking and/or confidentiality, these requests
     * MUST be encapsulated in the method- dependent encapsulation. <p>The SOCKS request is formed as follows: <P> <pre>
     * <TABLE BORDER WIDTH=1>
     * <TR> <TH>VER <TH>CMD <TH>RSV  <TH>ATYP <TH>DST.ADDR<TH>DST.PORT </TR>
     * <TR> <TD>1   <TD>1   <TD>X'00'<TD>1    <TD>Variable<TD>2 </TABLE>
     * <p/>
     * <p/>
     * <p/>
     *         Where:<UL>
     *                <LI>VER    protocol version: X'05'
     *                <LI>CMD<UL>
     *                   <LI>CONNECT X'01'
     *                   <LI>BIND X'02'
     *                   <LI>UDP ASSOCIATE X'03'</UL>
     *                <LI>RSV    RESERVED
     *                <LI>ATYP   address type of following address
     *                   <UL><LI>IP V4 address: X'01'
     *                   <LI>DOMAINNAME: X'03'
     *                   <LI>IP V6 address: X'04'</UL>
     *                <LI>DST.ADDR       desired destination address
     *                <LI>DST.PORT desired destination port in network octet order </UL>
     * <p/>
     * <p/>
     *                   <P>The SOCKS server will typically evaluate the
     *                   request based on source and destination addresses,
     *                   and return one or more reply messages, as
     *                   appropriate for the request type.
     */
    void send_request(Socket socks) {
        try {
            DataOutputStream os = new DataOutputStream(socks.getOutputStream());
            short sport = (short) this.getSourcePort();
            byte VER = 5, CMD = 1, //CONNECT
                    RSV = 0, ATYP = 3, DST_ADDR[] = get("SourceHost").toString().getBytes(),
                    DST_ADDR_LEN = (byte) DST_ADDR.length;
            os.write(VER);
            os.write(CMD);
            os.write(RSV);
            os.write(ATYP);
            os.write(DST_ADDR_LEN);
            os.write(DST_ADDR);
            os.write(sport);
        }
        catch (Exception e) {
        }
    }

    /**
     * <PRE>
     * <p/>
     * The SOCKS request information is sent by the client as soon as it has
     * established a connection to the SOCKS server, and completed the
     * authentication negotiations.  The server evaluates the request, and
     * returns a reply formed as follows:
     * <p/>
     * <TABLE BORDER WIDTH=1>
     * <TR><TH>
     * VER <TH>REP<TH> RSV <TH> ATYP <TH> BND.ADDR <TH> BND.PORT </TR>
     * <TR><TD>1  <TD> 1  <TD> X'00' <TD>  1   <TD> Variable<TD>    2  </TABLE>
     * <p/>
     * <p/>
     * Where:<UL>
     * <LI>VER    protocol version: X'05'
     * <LI>REP    Reply field:
     * <UL>
     * <LI>  X'00' succeeded
     * <LI>  X'01' general SOCKS server failure
     * <LI>  X'02' connection not allowed by ruleset
     * <LI> X'03' Network unreachable
     * <LI>  X'04' Host unreachable
     * <LI>  X'05' Connection refused
     * <LI>  X'06' TTL expired
     * <LI>  X'07' Command not supported
     * <LI>  X'08' Address type not supported
     * <LI>  X'09' to X'FF' unassigned</UL>
     * <LI>  RSV    RESERVED
     * <LI>ATYP   address type of following address
     * <UL><LI>  IP V4 address: X'01'
     * <LI>DOMAINNAME: X'03'
     * <LI>IP V6 address: X'04'<UL>
     * <LI>BND.ADDR       server bound address
     * <LI>BND.PORT       server bound port in network octet order
     * </UL></UL>
     * <P>Fields marked RESERVED (RSV) must be set to X'00'.
     * If the chosen method includes encapsulation for purposes of
     * authentication, integrity and/or confidentiality, the replies are
     * encapsulated in the method-dependent encapsulation.
     */
    public static boolean handle_response(Socket socks) {
        try {
            DataInputStream is = new DataInputStream(socks.getInputStream());
            //  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
            byte VER = (byte) is.read(), REP = (byte) is.read(), RSV = (byte) is.read(), ATYP = (byte) is.read(),
                    BND_ADDR_LEN = (byte) is.read(),
                    BND_ADDR[] = new byte[BND_ADDR_LEN];
            int i = is.read(BND_ADDR);
            short BND_PORT = is.readShort();
            return REP == 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    ServerSocket getSs() {
        return ss;
    }

    void setSs(ServerSocket ss) {
        this.ss = ss;
    }
}

//$Log: SocksProxy.java,v $
//Revision 1.3  2005/06/03 18:27:47  grrrrr
//no message
//
//Revision 1.2  2005/06/01 06:43:11  grrrrr
//no message
//
//Revision 1.1.1.1  2002/12/08 16:05:50  grrrrr
//
//
//Revision 1.1.1.1  2002/12/08 16:41:52  jim
//
//
//Revision 1.5  2002/06/05 15:27:46  grrrrr
//*** empty log message ***
//
//Revision 1.4.10.1  2002/06/03 16:21:52  grrrrr
//*** empty log message ***
//
//Revision 1.4.6.1  2002/06/02 20:10:26  grrrrr
//*** empty log message ***
//
//Revision 1.4  2002/05/20 07:47:01  grrrrr
//irc doesnt compress nicely
//
//Revision 1.3  2002/05/19 21:34:08  grrrrr
//intellij damage
//
//Revision 1.2  2002/05/17 07:54:08  grrrrr
//gratuitous together/J formatting.
//
//Revision 1.1.1.1  2002/05/11 18:55:35  grrrrr
//new Features:
//
//IRC agent
//idyuts has been incorporated and folded in
//some SWing gui work on the IRC agent, proof its possible
//new package names
//
//
//
//
//
//
//
//Revision 1.2  2001/09/30 23:01:03  grrrrr
//IRC agent added
//
//Revision 1.1  2001/09/30 05:47:42  grrrrr
//socks proxy written but untested
//
//Revision 1.3  2001/09/23 10:20:10  grrrrr
//lessee
//
//2 major enhancements
//
//1) we now use reflection to decode message types.
//
//a message looks for handle_<JMSType> method that takes a
// MetaProperties as its input
//
//2) we now serve HTTP / 1.1 at every opportunity, sending
// content-length, and last-modified, and content type by
// default.  (MobilePayload still needs a few updates to catch up)
//
//Revision 1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy app
//


