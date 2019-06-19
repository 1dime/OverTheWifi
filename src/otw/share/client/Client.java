package otw.share.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

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
			if(isCurrentClientIntendedTarget(sharedData))
			{
				return sharedData;
			}
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
	
	public void copyTextFromServer(String hostName)
	{
		//Get the hosted data
		SharedData dataRetrieved = this.getSharedData(hostName);
		if(dataRetrieved == null)
			return;
		//Check if the dataRetrieved's type is CLIPBOARD_DATA
		if(dataRetrieved.getSharedDataType().equals(SharedDataType.CLIPBOARD_DATA))
		{
			if(isCurrentClientIntendedTarget(dataRetrieved))
			{
				if(isAndroid())
				{
					AlertDialog dialog = new AlertDialog.Builder(context)
							.setTitle("Accept shared text?")
							.setMessage("Someone is trying to share their clipboard data with you. Accept?")
							.setPositiveButton("Accept", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									//Copy the text using the crossplatform clipboard
									Clipboard clipboard = new Clipboard(context);
									//Copy the text
									clipboard.copy(dataRetrieved);
								}
							})
							.setNegativeButton("Deny", new OnClickListener() {

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
					System.out.println("Copying data targeted to you");
					//Copy the text using the crossplatform clipboard
					Clipboard clipboard = new Clipboard(this.context);
					//Copy the text
					clipboard.copy(dataRetrieved);
				}
			}else
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
	}
	
	public void downloadFromServer(String hostName)
	{
		//GEt the hosted data
		SharedData dataRetrieved = this.getSharedData(hostName);
		if(dataRetrieved == null)
			return;
		//Check if the dataRetrieved's type is FILE
		if(dataRetrieved.getSharedDataType().equals(SharedDataType.FILE))
		{
			if(this.isCurrentClientIntendedTarget(dataRetrieved))
			{
				if(isAndroid())
				{
					//Show a dialog or use notifications
				}
				else
				{
					
				}
			}
		}
	}

	boolean isIntendedTarget = false;
	public boolean isCurrentClientIntendedTarget(SharedData data)
	{
		String address;
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements())
			{
				NetworkInterface deviceInterface = interfaces.nextElement();
				if(deviceInterface.isLoopback() || !deviceInterface.isUp())
					continue;
				Enumeration<InetAddress> innerAddresses = deviceInterface.getInetAddresses();
				while(innerAddresses.hasMoreElements())
				{
					InetAddress inetAddress = innerAddresses.nextElement();
					if(inetAddress instanceof Inet6Address) continue;
					address = inetAddress.getHostAddress();
					if(data.getMetaData(SharedData.MetaData.METADATA_TARGET_CLIENT) != null)
					{
						if(data.getMetaData(SharedData.MetaData.METADATA_TARGET_CLIENT).equals(address))
						{
							return true;
						}else
						{
							isIntendedTarget = false;
						}
					}else
					{
						isIntendedTarget = false;
					}
				}
			}
		}catch(SocketException se)
		{
			se.printStackTrace();
		}
		return isIntendedTarget;
	}
}
