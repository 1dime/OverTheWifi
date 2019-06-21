package otw.share.client;

import java.awt.font.NumericShaper.Range;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import otw.share.SharedData;
import otw.share.SharedData.SharedDataType;
import otw.share.xplatform.Clipboard;
import otw.share.xplatform.XPlatform;

public class Client extends XPlatform
{
	private Context context; //Helps determine if app using sdk is on Android or not
	private OnClientConnectedToServer onClientConnected; //For returning connection results
	private int CONNECTION_PORT = SharedData.DEFAULT_PORT; //Which port to listen to
	
	private List<Object> defaultResults = new ArrayList<>(); //Used by functions requiring an arraylist
	private Object defaultResult = new Object();
	public Client(Context context, int connectionPort)
	{
		this.context = context;
		this.CONNECTION_PORT = connectionPort;
		this.setOnClientConnectedToServer(new OnClientConnectedToServer() {
			
			@Override
			public void onDataRecieved(SharedData data) {
				// TODO Auto-generated method stub
				System.out.println(data.getSharedData().toString());
			}
			
			@Override
			public void onConnectionSuccess() {
				// TODO Auto-generated method stub
				System.out.println("Connected to host!");
			}
			
			@Override
			public void onConnectionFail(Throwable cause) {
				// TODO Auto-generated method stub
				System.out.println("Failed to connect to host: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onFailedToRetrieveData(Throwable cause) {
				// TODO Auto-generated method stub
				System.out.println("Failed to retrieve SharedData: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}
		});
	}
	
	public Client(int connectionPort)
	{
		this.CONNECTION_PORT = connectionPort;
		this.setOnClientConnectedToServer(new OnClientConnectedToServer() {
			
			@Override
			public void onDataRecieved(SharedData data) {
				// TODO Auto-generated method stub
				System.out.println(data.getSharedData().toString());
			}
			
			@Override
			public void onConnectionSuccess() {
				// TODO Auto-generated method stub
				System.out.println("Connected to host!");
			}
			
			@Override
			public void onConnectionFail(Throwable cause) {
				// TODO Auto-generated method stub
				System.out.println("Failed to connect to host: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}

			@Override
			public void onFailedToRetrieveData(Throwable cause) {
				// TODO Auto-generated method stub
				System.out.println("Failed to retrieve SharedData: " + cause.getCause());
				System.out.println("Message: " + cause.getMessage());
			}
		});
	}
	
	public void setOnClientConnectedToServer(OnClientConnectedToServer onClientConnected)
	{
		this.onClientConnected = onClientConnected;
	}
	
	protected Socket connectToServer(String hostName)
	{
		try
		{
			//Connect the client to the server
			Socket socket = new Socket(hostName, this.CONNECTION_PORT);
			//Notify the sdk that the connection was successful
			this.onClientConnected.onConnectionSuccess();
			//Return the socket for later use
			return socket;
		}catch(IOException exception)
		{
			//Notify the sdk that the connection failed
			this.onClientConnected.onConnectionFail(exception);
		}
		
		//For the inevitable, return nothing
		return null;
	}
	
	
	protected ObjectInputStream getObjectFromServer(String hostName) 
	{
		try
		{
			ObjectInputStream input = new ObjectInputStream(this.connectToServer(hostName).getInputStream());
			//Return the input
			return input;
		}catch(IOException ioe)
		{
			this.onClientConnected.onFailedToRetrieveData(ioe);
		}
		
		//Return nothing if there was an error
		return null;
	}
	
	public SharedData getSharedData(String hostName)
	{
		try
		{
			SharedData sharedData = (SharedData) this.getObjectFromServer(hostName).readObject();
			
			//Are we the intended target?
			return sharedData;
		}catch(IOException ioe)
		{
			this.onClientConnected.onFailedToRetrieveData(ioe);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			this.onClientConnected.onFailedToRetrieveData(e);
		}
		
		//Return nothing if there was an issue
		return null;
	}
	
	public void copyTextFromSharedData(SharedData data)
	{
		SharedData dataRetrieved = data;
		//Check if the dataRetrieved's type is CLIPBOARD_DATA
				if(dataRetrieved.getSharedDataType().equals(SharedDataType.CLIPBOARD_DATA))
				{
					if(isAndroid())
					{
						AlertDialog dialog = new AlertDialog.Builder(context)
								.setTitle("Copy hosted text?")
								.setMessage("Someone is sharing their clipboard data. Accept?")
								.setPositiveButton("Accept", new OnClickListener()
										{

											@Override
											public void onClick(DialogInterface arg0, int arg1) {
												// TODO Auto-generated method stub
												//Copy the text using the crossplatform clipboard
												Clipboard clipboard = new Clipboard(context);
												//Copy the text
												clipboard.copy(dataRetrieved);
											}
									
										})
								.setNegativeButton("Deny", new OnClickListener()
										{

											@Override
											public void onClick(DialogInterface arg0, int arg1) {
												// TODO Auto-generated method stub
												arg0.dismiss();
											}
									
										})
								.show();
					}
					else
					{
						//Copy the text using the crossplatform clipboard
						Clipboard clipboard = new Clipboard(this.context);
						//Copy the text
						clipboard.copy(dataRetrieved);
					}
				}
	}
	
	public void copyTextFromServer(String hostName)
	{
		//Get the hosted data
		SharedData dataRetrieved = this.getSharedData(hostName);
		if(dataRetrieved == null)
			return;
		this.copyTextFromSharedData(dataRetrieved);
	}
	
	public void downloadFromSharedData(SharedData data)
	{
		SharedData dataRetrieved = data;
		//Check if the dataRetrieved's type is FILE
		if(dataRetrieved.getSharedDataType().equals(SharedDataType.FILE))
		{
			try {
				String savePath = dataRetrieved.getMetaData(SharedData.MetaData.METADATA_FILE_PATH).toString();
				byte[] content = (byte[]) dataRetrieved.getMetaData(SharedData.MetaData.METADATA_FILE_CONTENT);
				
				FileOutputStream oos = new FileOutputStream(new File(savePath));
				oos.write(content);
				oos.flush();
				oos.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void downloadFromServer(String hostName)
	{
		//GEt the hosted data
		SharedData dataRetrieved = this.getSharedData(hostName);
		if(dataRetrieved == null)
			return;
		
		this.downloadFromSharedData(dataRetrieved);
	}
	
	public Object getDataFromOtherType(String hostname)
	{
		SharedData data = this.getSharedData(hostname);
		if(data == null)
			return "";
		
		if(data.getSharedDataType().equals(SharedDataType.OTHER))
			return data.getSharedData();
		
		return null;
	}
	
	public Object listenOnServer(String ipAddress, boolean stayListening)
	{
		Thread listener = new Thread(new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(stayListening)
						{
							while(stayListening)
							{
								try
								{
									//Get the SharedData from the ip address listed
									SharedData data = Client.this.getSharedData(ipAddress);
									//Return the SharedDataType used in SharedData as a string
									String sharedDataType = data.getMetaData(SharedData.MetaData.METADATA_SHARED_DATA_TYPE).toString();
									//Use a switch statement on sharedDataType to check if it is CLIPBOARD_DATA, FILE, or OTHER
									switch(sharedDataType)
									{
									case "CLIPBOARD_DATA":
										//Copy the text from the given SharedData instance
										copyTextFromSharedData(data);
										//Set the result to the data being shared
										defaultResult = data.getSharedData().toString();
										break;
										
									case "FILE":
										//Download the file given the SharedData instance
										downloadFromSharedData(data);
										//Set the result to the file content
										defaultResult = data.getMetaData(SharedData.MetaData.METADATA_FILE_CONTENT);
										break;
										
									case "OTHER":
										//Just set the result to the SharedData
										defaultResult = data.getSharedData();
										break;
									default:
										//Same deal, set the result to the SharedData
										defaultResult = data.getSharedData();
										break;
									}
								}catch(Throwable t)
								{
									Client.this.listenOnServer(ipAddress, stayListening);
								}
								
							}
						}
						else
						{

							//Get the SharedData from the ip address listed
							SharedData data = Client.this.getSharedData(ipAddress);
							//Return the SharedDataType used in SharedData as a string
							String sharedDataType = data.getMetaData(SharedData.MetaData.METADATA_SHARED_DATA_TYPE).toString();
							//Use a switch statement on sharedDataType to check if it is CLIPBOARD_DATA, FILE, or OTHER
							switch(sharedDataType)
							{
							case "CLIPBOARD_DATA":
								//Copy the text from the given SharedData instance
								copyTextFromSharedData(data);
								//Set the result to the data being shared
								defaultResult = data.getSharedData().toString();
								break;
								
							case "FILE":
								//Download the file given the SharedData instance
								downloadFromSharedData(data);
								//Set the result to the file content
								defaultResult = data.getMetaData(SharedData.MetaData.METADATA_FILE_CONTENT);
								break;
								
							case "OTHER":
								//Just set the result to the SharedData
								defaultResult = data.getSharedData();
								break;
							default:
								//Same deal, set the result to the SharedData
								defaultResult = data.getSharedData();
								break;
							}
						}
					}
			
				});
		
		listener.start();
		//Return the result so that the developer using this SDK can do as they please with it
		return defaultResult;
	}
	
	
	public List<Object> listenOnServers(List<Object> ipAddresses, boolean stayListening)
	{
		Thread listener = new Thread(new Runnable()
				{

					@Override
					public void run() 
					{
						// TODO Auto-generated method stub

						//Use for adding the results of each SharedDataType request
						List<Object> serverResults = new ArrayList<>();
						if(stayListening)
						{
							while(stayListening)
							{
								//Go through all the servers in the list
								for(Object serverObject : ipAddresses)
								{
									String server = serverObject.toString();
									try
									{
										//Get the shared data for server
										SharedData data = Client.this.getSharedData(server);
										//Get the SharedDataType of the data we recieved from the server as a string
										String sharedDataType = data.getMetaData(SharedData.MetaData.METADATA_SHARED_DATA_TYPE).toString();
										//Use the switch statement to detect if sharedDataType is CLIPBOARD_DATA, FILE, or OTHER
										switch(sharedDataType)
										{
										case "CLIPBOARD_DATA":
											//Copy the information we received
											Client.this.copyTextFromSharedData(data);
											//Add the information as a result
											serverResults.add("CLIPBOARD_DATA " + data.getSharedData().toString());
											break;
										case "FILE":
											//Download the file from the SharedData we acquired
											Client.this.downloadFromSharedData(data);
											//Add the shared data to the result
											serverResults.add("FILE " + data.getSharedData().toString());
											break;
										case "OTHER":
											//Add the response we got for the unknown
											serverResults.add(data.getSharedData());
											break;
											
										default:
											//Same deal as OTHER
											serverResults.add(data.getSharedData());
											break;
										}
									}catch(Throwable t)
									{
									}
								}
								
							}
						}
						else
						{
							//Go through all the servers in the list
							for(Object serverObject : ipAddresses)
							{
								String server = serverObject.toString();
								//Get the shared data for server
								SharedData data = Client.this.getSharedData(server);
								//Get the SharedDataType of the data we recieved from the server as a string
								String sharedDataType = data.getMetaData(SharedData.MetaData.METADATA_SHARED_DATA_TYPE).toString();
								//Use the switch statement to detect if sharedDataType is CLIPBOARD_DATA, FILE, or OTHER
								switch(sharedDataType)
								{
								case "CLIPBOARD_DATA":
									//Copy the information we received
									Client.this.copyTextFromSharedData(data);
									//Add the information as a result
									serverResults.add("CLIPBOARD_DATA " + data.getSharedData().toString());
									break;
								case "FILE":
									//Download the file from the SharedData we acquired
									Client.this.downloadFromSharedData(data);
									//Add the shared data to the result
									serverResults.add("FILE " + data.getSharedData().toString());
									break;
								case "OTHER":
									//Add the response we got for the unknown
									serverResults.add(data.getSharedData());
									break;
									
								default:
									//Same deal as OTHER
									serverResults.add(data.getSharedData());
									break;
								}
							}
							
						}
					}
			
				});
		
		listener.start();
		//Return the result for developers to use
		return defaultResults;
	}
	
	public List<Object> getAvailableIpAddresses(String subnet, int max)
	{
		//The speed of this is entirely dependent on 1. your device speed and 2. your device to router connection speed
		//A typical 5GHZ wifi adapter will run this just fine
		//Notify what range we are looking for
		System.out.println("Looking for ip addresses within " + subnet + ".1 - " + subnet + "." + max);
		//Use a for loop to get all ip addresses in the range 1 - max
		for(int i = 1; i <= max; i++)
		{
			String ipAddress = subnet + "." + i;
			System.out.println("Checking if " + ipAddress + " is available");
			//Check if the ip address given is a valid ip
			try 
			{
				if(InetAddress.getByName(ipAddress).isReachable(500))
				{
					System.out.println(ipAddress + " is valid!");
					this.defaultResults.add(ipAddress);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.defaultResults;
	}
	
	public List<Object> getAvailableIpAddresses(int max)
	{

		//Return the available ip addresses with subnet as 192.168.0 and max as max
		return this.getAvailableIpAddresses("192.168.0", max);
	}
	
	public List<Object> getAvailableIpAddresses(String subnet)
	{
		//Return the available ip addresses with subnet as subnet and max as 100
		return this.getAvailableIpAddresses(subnet, 100);
	}
	
	
	public List<Object> getAvailableIpAddresses()
	{
		//Return the ip addresses with 192.168.0 as subnet and max as 100
		return this.getAvailableIpAddresses("192.168.0", 100);
	}
	public void listenForAllServers(String subnet, int max, boolean stayListening)
	{
		//Notify that the information is being looked for
		System.out.println("Looking for shared information on the given list of servers");
		//Get a list of all of the devices connected to the server
		List<Object> connectedIpAddresses = this.getAvailableIpAddresses(subnet, max);
		//Listen on all connected devices via listen on all servers
		this.listenOnServers(connectedIpAddresses, stayListening);
	}
	
	public void listenForAllServers(String subnet, boolean stayListening)
	{
		//Listen on all devices with subnet as subnet, max as 100, and stayListening as stayListening
		this.listenForAllServers(subnet, 100, stayListening);
	}
	
	public void listenForAllServers(boolean stayListening)
	{
		//Listen on all devices with subnet as 192.168.0 and stayListening as stayListening
		this.listenForAllServers("192.168.0", stayListening);
	}
}
