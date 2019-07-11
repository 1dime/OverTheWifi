package otw.share.server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;

import android.content.Context;
import otw.share.SharedData;
import otw.share.xplatform.XPlatform;

public class Server extends XPlatform
{
	private static boolean killSecondsAfter = false;
	private static boolean keepAlive = true;
	private boolean hasConnectionBeenMade = false; //For the while loop for multiple connections
	private boolean reconnect = false;
	private Socket socket;
	private ServerSocket serverSocket;
	private Context context;
	private int HOSTING_PORT = SharedData.DEFAULT_PORT;
	private OnServerHostingSharedData onServerHosting;
	
	public Server()
	{
		setOnServerHostingSharedData(new OnServerHostingSharedData()
				{

					@Override
					public void onServerSuccessfullyCreated() 
					{
						// TODO Auto-generated method stub
						System.out.println("Successfully created the server!");
					}

					@Override
					public void onFailedToCreateServer(Throwable cause) 
					{
						// TODO Auto-generated method stub
						System.out.println("Failed to create the server: " + cause.getCause());
						System.out.println("Message: " + cause.getMessage());
					}

					@Override
					public void onHosted() 
					{
						// TODO Auto-generated method stub
						System.out.println("Hosted the information successfully!");
					}

					@Override
					public void onFailedToHostSharedData(Throwable cause) 
					{
						// TODO Auto-generated method stub
						System.out.println("Failed to host: " + cause.getCause());
						System.out.println("Message: " + cause.getMessage());
					}

					@Override
					public void onShared() 
					{
						// TODO Auto-generated method stub
						System.out.println("Successfully shared the information!");
					}

					@Override
					public void onFailedToShare(Throwable throwable) 
					{
						// TODO Auto-generated method stub
						System.out.println("Failed to share: " + throwable.getCause());
						System.out.println("Message: " + throwable.getMessage());
					}
			
				});
		this.HOSTING_PORT = SharedData.DEFAULT_PORT;
	}
	
	public Server(Context context)
	{
		this.context = context;
		this.HOSTING_PORT = SharedData.DEFAULT_PORT;
		setOnServerHostingSharedData(new OnServerHostingSharedData()
		{

			@Override
			public void onServerSuccessfullyCreated() 
			{
				// TODO Auto-generated method stub
				System.out.println("Successfully created the server!");
			}

			@Override
			public void onFailedToCreateServer(Throwable cause) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to create the server: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onHosted() 
			{
				// TODO Auto-generated method stub
				System.out.println("Hosted the information successfully!");
			}

			@Override
			public void onFailedToHostSharedData(Throwable cause) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to host: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onShared() 
			{
				// TODO Auto-generated method stub
				System.out.println("Successfully shared the information!");
			}

			@Override
			public void onFailedToShare(Throwable throwable) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to share: " + throwable.getCause());
				System.out.println("Message: " + throwable.getMessage());
			}
	
		});
	}
	
	public Server(int hostingPort)
	{
		this.HOSTING_PORT = hostingPort;
		setOnServerHostingSharedData(new OnServerHostingSharedData()
		{

			@Override
			public void onServerSuccessfullyCreated() 
			{
				// TODO Auto-generated method stub
				System.out.println("Successfully created the server!");
			}

			@Override
			public void onFailedToCreateServer(Throwable cause) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to create the server: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onHosted() 
			{
				// TODO Auto-generated method stub
				System.out.println("Hosted the information successfully!");
			}

			@Override
			public void onFailedToHostSharedData(Throwable cause) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to host: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onShared() 
			{
				// TODO Auto-generated method stub
				System.out.println("Successfully shared the information!");
			}

			@Override
			public void onFailedToShare(Throwable throwable) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to share: " + throwable.getCause());
				System.out.println("Message: " + throwable.getMessage());
			}
	
		});
	}
	
	public Server(Context context, int hostingPort)
	{
		this.context = context;
		this.HOSTING_PORT = hostingPort;
		setOnServerHostingSharedData(new OnServerHostingSharedData()
		{

			@Override
			public void onServerSuccessfullyCreated() 
			{
				// TODO Auto-generated method stub
				System.out.println("Successfully created the server!");
			}

			@Override
			public void onFailedToCreateServer(Throwable cause) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to create the server: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onHosted() 
			{
				// TODO Auto-generated method stub
				System.out.println("Hosted the information successfully!");
			}

			@Override
			public void onFailedToHostSharedData(Throwable cause) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to host: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onShared() 
			{
				// TODO Auto-generated method stub
				System.out.println("Successfully shared the information!");
			}

			@Override
			public void onFailedToShare(Throwable throwable) 
			{
				// TODO Auto-generated method stub
				System.out.println("Failed to share: " + throwable.getCause());
				System.out.println("Message: " + throwable.getMessage());
			}
	
		});
	}
	
	public void mustKeepAlive(boolean keepAlive)
	{
		this.keepAlive = keepAlive;
	}
	
	public void setKillSecondsAfter(boolean killSecondsAfter)
	{
		this.killSecondsAfter = killSecondsAfter;
	}
	
	public void setOnServerHostingSharedData(OnServerHostingSharedData onServerHosting)
	{
		this.onServerHosting = onServerHosting;
	}
	
	protected ServerSocket createSocketServer(String hostName) throws NullPointerException
	{
		try {
			//Create the server via ServerSocket class, using HOSTING_PORT
			serverSocket = new ServerSocket(this.HOSTING_PORT);
			serverSocket.setReuseAddress(true);
			
			//Notify the sdk that the server was created successfully
			this.onServerHosting.onServerSuccessfullyCreated();
			//Return the serverSocket
			return serverSocket;
		} catch (IOException e) {
			//Notify the sdk that there was an error during the creation of the server
			this.onServerHosting.onFailedToCreateServer(e);
		}
		
		//Return nothing as there was an error
		return null;
	}
	
	protected Socket createSocketConnection(String hostName) throws NullPointerException
	{
		try {
			//Create a socket connection by accepting the connection
			socket = this.createSocketServer(hostName).accept();
			//Notify that the socket connection was successful
			this.onServerHosting.onHosted();
			//Return the socket
			return socket;
		} catch (IOException e) 
		{
			//Notify the sdk of the error we arrived at during Socket creation
			this.onServerHosting.onFailedToHostSharedData(e);
		}
		
		//Return null due to any error
		return null;
	}
	
	protected ObjectOutputStream getStreamWriter(String hostName) throws NullPointerException
	{
		try 
		{
			//Return the ObjectOutputStream instance
			return new ObjectOutputStream(this.createSocketConnection(hostName).getOutputStream());
		} catch (IOException e)
		{
			//Print out the error
			e.printStackTrace();
		}
		
		//Return nothing due to an error
		return null;
	}
	
	protected Socket getSocket()
	{
		return socket;
	}
	
	protected ServerSocket getServerSocket()
	{
		return serverSocket;
	}
	
	public OnServerHostingSharedData getOnServerHostingSharedData()
	{
		return this.onServerHosting;
	}
	
	public void hostData(SharedData sharedData, String hostName, String targetHost)
	{
		//Check if metadata contains shareddatatype for automation
		if(sharedData.getMetaData(SharedData.MetaData.METADATA_SHARED_DATA_TYPE) == null)
		{
			this.onServerHosting.onFailedToCreateServer(new Throwable(SharedData.MetaData.METADATA_SHARED_DATA_TYPE + "metadata is not set in SharedData"));
			return;
		}
		//Add the TARGET_CLIENT metadata
		sharedData.addMetaData("TARGET_CLIENT", targetHost.toString());

		try 
		{
			if(!this.hasConnectionBeenMade)
			{
				ServerSocket serverSocket = new ServerSocket(this.HOSTING_PORT);
				new Thread(new ServerThread(sharedData, serverSocket)).start();
				
			}
		} catch (IOException | NullPointerException e) 
		{
			//Notify of the error
			this.onServerHosting.onFailedToHostSharedData(e);
		}
	}
	
	public void hostData(SharedData sharedData, String hostName)
	{
		//Check if metadata contains shareddatatype for automation
		if(sharedData.getMetaData(SharedData.MetaData.METADATA_SHARED_DATA_TYPE) == null)
		{
			this.onServerHosting.onFailedToCreateServer(new Throwable(SharedData.MetaData.METADATA_SHARED_DATA_TYPE + " metadata is not set in SharedData"));
			return;
		}
		
		try 
		{
			if(!this.hasConnectionBeenMade)
			{
				ServerSocket serverSocket = new ServerSocket(this.HOSTING_PORT);
				new Thread(new ServerThread(sharedData, serverSocket)).start();
				this.hasConnectionBeenMade = false;
			}
		} catch (IOException | NullPointerException e) 
		{
			//Notify of the error
			this.onServerHosting.onFailedToHostSharedData(e);
		}
	}
	
	public static class ServerThread implements Runnable
	{
		private SharedData sharedData;
		private Socket socket;
		private ServerSocket serverSocket;
		public ServerThread(SharedData sharedData, ServerSocket serverSocket) throws IOException
		{
			this.sharedData = sharedData;
			this.serverSocket = serverSocket;
			this.socket = this.serverSocket.accept();
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//Create the writer
			ObjectOutputStream streamWriter;
			try {
				streamWriter = new ObjectOutputStream(this.socket.getOutputStream());
				//Write it all out
				streamWriter.writeObject(sharedData);
				//Flush the writer
				streamWriter.flush();
				//Close the writer
				streamWriter.close();
				
				//Close the socket
				socket.close();
				if(killSecondsAfter)
				{
					try {
						Thread.sleep(5000);
						serverSocket.close();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else if(keepAlive)
					new Thread(new ServerThread(this.sharedData, this.serverSocket)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
