package Cheetah;

import owch.*;
import java.util.*;
import java.io.*;
import java.net.*;

/** SocketProxy */
public class SocketProxy extends Node implements Runnable{
    ServerSocket ss;


    public static void main(String[] args) {
        Map m = Env.parseCmdLine(args);
        if (!(m.containsKey("JMSReplyTo") && m.containsKey("SourcePort") && m.containsKey("SourceHost") && m.containsKey("ProxyPort"))) {
            System.out.println("\n\n******************** cmdline syntax error\n" +
			       "SocketProxy Agent usage:\n\n" +
			       "-name       (String)name\n"  + 
			       "-SourceHost (String)hostname/IP\n"+
			       "-SourcePort (int)port\n" + 
			       "-ProxyPort  (int)port\n" +
			       "[-Clone 'host1[ ..hostn]']\n"+			
			       "[-Deploy 'host1[ ..hostn]']\n"+
			       "$Id: SocketProxy.java,v 1.2 2001/05/04 10:59:08 grrrrr Exp $\n");
            System.exit(2);
        };
        SocketProxy d = new SocketProxy(m);
    };

    /**
     * @param to recipient owch node name
     * @param arg the text of the message
     */
    synchronized public void receive(MetaProperties notificationIn) {
        Thread.currentThread().yield();
        if (notificationIn == null) return;
        String type;
        String subJMSType;
        type = (String)notificationIn.get("JMSType");
        Env.debug(8, "SocketProxy - receive type = " + type);
        if (type != null) {
        }; // if type != NULL
        super.receive(notificationIn); // superclass might know the JMSType
    };

    public int getSourcePort() {
        return Integer.decode((String)get("SourcePort")).intValue();
    }

    public int getProxyPort() {
        return Integer.decode((String)get("ProxyPort")).intValue();
    }

    /**
     *       this has the effect of taking over the command of the http
     * service on the agent host and handling messages to marshal http registrations
     */
    public SocketProxy(Map m) {
        super(m);
	try{

	
	if(containsKey("Clone"))
	    {
		String clist=(String)get("Clone");
		remove("Clone");
		Env.debug(500,getClass().getName()+" **Cloning for "+clist);
		StringTokenizer st=new StringTokenizer(clist);
		while(st.hasMoreTokens())
		    clone_state1(st.nextToken());
	    };
	if(containsKey("Deploy"))
	    {
		String clist=(String)get("Deploy");
		remove("Deploy");
		Env.debug(500,getClass().getName()+" **Cloning for "+clist);
		StringTokenizer st=new StringTokenizer(clist);
		while(st.hasMoreTokens())
		    clone_state1(st.nextToken());
		Thread.currentThread().sleep(15*1000);//kludge, allow udp messages to arrive...
		System.exit(0);//TODO: allow our host to stay alive...
	    };
	ss=new ServerSocket(getProxyPort());
 
	}catch( Exception e){
	    e.printStackTrace();
	};
    };
     
    
    PipeFactory pf=new PipeFactory();

    public void run(){
	interval=1000*2;
	while(ss==null)
	    waitInterval();
	while(!killFlag){
	    try{
		//todo: time out somehow
		Socket inbound=ss.accept();//wait for connection on ProxyPort
		PipeSocket ps=new PipeSocket(inbound);
		ps.connectTarget(new Socket((String)get("SourceHost"),getSourcePort()));
		ps.spin();
	    } catch ( java.io.InterruptedIOException   e){
		Env.debug(500,getClass().getName()+"::interrupt "+e.getMessage());
	    } catch (Exception e){
		Env.debug(10,getClass().getName()+"::run "+e.getMessage());
		e.printStackTrace();
	    };
	};
    };
    public void clone_state1(String host)
    { 
	MetaProperties n2=new Location(this);
	n2.put("JMSType","DeployNode"); 
	n2.put("Class",getClass().getName()); 
	n2.put("JMSReplyTo",getJMSReplyTo()+"."+host); 
	//resource remains constant in this incarnation
	//n2.put( "Resource",get("Resource"));//produces 3 Strings
	n2.put("JMSDestination", host);
	send(n2);
 
    }; 
}
    //$Log: SocketProxy.java,v $
    //Revision 1.2  2001/05/04 10:59:08  grrrrr
    //WIP
    //
    //Revision 1.1.2.1  2001/04/30 04:27:56  grrrrr
    //SocketProxy + Deploy methods
    //
