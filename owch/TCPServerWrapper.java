package owch;

import java.net.*;
import java.io.*;
/*
 *
 * TCPServerWrapper
 *
 */
public class TCPServerWrapper implements ServerWrapper
{
	ServerSocket s;
	//TODO: figure out a cleaner way to do

	public TCPServerWrapper(int port) throws IOException
	{
		s=new ServerSocket(port);
	};

	public final void close()
	{
		try
		{
			s.close();
		}
		catch(Exception e)
		{
		};
	};

	public final Socket accept()throws IOException
	{
		return s.accept();
	};

	public final int getLocalPort()
	{
		return s.getLocalPort();
	};

	public final ServerSocket serverSocket()
	{
		return s;
	};
};

