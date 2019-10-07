package otw.connector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import otw.share.Data;

public class Connector 
{
	public static int PORT = 2077;
	
	public void host(Data data, String targetIP)
	{
		//Run host with Data as data, targetIP as targetIP, and port as port
		this.host(data, targetIP, PORT);
	}
	
	public void host(Data data, String targetIP, int port)
	{
		data.addData(Data.DATA_TARGET_IP, targetIP);
		//Create a server for hosting the shared data
		new WebHelper(data, targetIP, port).run();
	}
	
	public Data get(String ip) throws ClassNotFoundException, IOException
	{
		//Return the data for the developer using
		//This to use
		return this.get(ip, PORT);
	}
	
	public Data get(String ip, int port) throws IOException, ClassNotFoundException
	{
		//Connect to the server and return the given data
		//Create a socket
		Socket socket = new Socket(ip, port);
		
		//Get the input stream
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
		//Read the data
		Data data = (Data) inputStream.readObject();
		
		//Check if our computer is the intended target
		if(data.getData(Data.DATA_TARGET_IP).toString() != this.getHostAddress())
		{
			socket.close();
			//Return the data
			return data;
		}
		socket.close();
		//Return null
		return null;
	}
	
	public static class WebHelper implements Runnable
	{
		private String targetIP;
		private int port;
		private Data data;
		
		public WebHelper(Data data, String targetIP, int port)
		{
			this.data = data;
			this.targetIP = targetIP;
			this.port = port;
		}
		
		@Override
		public void run() 
		{
			// TODO Host data
			try 
			{
				
				//Create a serversocket
				ServerSocket server = new ServerSocket(this.port);
				//Create a socket connection
				Socket socket = server.accept();
				
				//Get an output
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				
				//Write the data
				output.writeObject(this.data);
				
				//Flush the output
				output.flush();
				
				//Close the output
				output.close();
				
				//Close the socket
				socket.close();
				
				//Close the server
				server.close();
			} catch (MalformedURLException | UnknownHostException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public String getHostAddress() throws UnknownHostException
	{
		return new String(InetAddress.getLocalHost().getHostAddress());
	}
}
