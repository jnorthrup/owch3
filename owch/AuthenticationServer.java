package owch;

import owch.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class AuthenticationServer extends TCPServerWrapper implements Runnable, ListenerReference
{
 
    public String getProtocol(){
	return "auth";
    };
    public long getExpiration() {
	return (long) 0;
    }
    public int getThreads(){
	return 2;
    }
    public ServerWrapper getServer(){
	return this;
    };
    public void expire(){	
	close();
    };

    public static void create() {
	try{
	    ListenerCache lc=new ListenerCache(); 
	    lc.put(new AuthenticationServer(2112));
	    Env.getProtocolCache().put("auth",lc);
	}
	catch(Exception e){Env.debug(1,"auth server failure");
	};
    };

    private  AuthenticationServer(int port)throws java.io.IOException {
	super((int)port);
	Thread t=null;

        for (int i=0;i<getThreads();i++) {
	    Env.debug(10,"starting AuthThread #"+i+" / port "+this.getLocalPort());
	    t=new Thread(this,"AuthThread #"+i+" / port "+this.getLocalPort());
	    t.setDaemon(true);
	    //t.setPriority(Thread.MAX_PRIORITY);
	    t.start();
	};
    }


    /*
      Client Request format

      "NodeName"  -- user's name
      "Pathisword"	-- user's pathisword (soon to be keyed with a random seed and sent via hash)
      "AuthURL"	-- unimportant, but present

      Domain Update Format

      "NodeName"	-- as above
      "URL"		-- routing entry point
      "Pathisword"	-- as above
      "Domain"	-- to be determined... perhaps the group authority name

      Secure Room Update Format

      "NodeName"	-- as above
      "URL"		-- routing entry point
      "Pathisword"	-- as above
      "Room"		-- to be determined... perhaps the group authority name

    */

    public void run()
    {
	Location l=null;
	Location t=null;

	while(true)
	    {
		Socket s=null;
		try
		    {
			s=this.accept();
			Env.debug(10,"debug: Auth server got a bite. in thread "+Thread.currentThread().getName());
		    }
		catch(IOException ex)
		    {
			Env.debug(5,"debug: accept failed, Listener Thread going down");
			return;
		    };

		l=new Location();

		try
		    {
			l.load(s.getInputStream());
		    }
		catch(IOException ex)
		    {
			Env.debug(10,"debug: Listener Thread having problems"+ex.toString());
		    };

		//test for Domain Location Update
		//TODO: build proxy expire code

		t=new Location(l);
		if(t!=null)
		    {
			String DomainUrl=Env.getProtocolCache().getLocation("owch").getURL();

			t.put("ParentURL",DomainUrl);
			t.put("OK",":-)");
			try
			    {
				t.save(s.getOutputStream());
		 
				Env.debug(10,"debug: Auth server sent "+l.getNodeName()+" an echo in  "+Thread.currentThread().getName());
			    }
			catch (IOException ex)
			    {
				Env.debug(10,"debug: Listener Thread exception");
			 
			    };
		    };
	    };
    }
    ;

    public static void main(String[] arg)
    {
	AuthenticationServer.create();
    };
    
};
