package otw.share.client;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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
				FileWriter writer = new FileWriter(dataRetrieved.getMetaData(SharedData.MetaData.METADATA_FILE_PATH).toString());
				writer.write(dataRetrieved.getMetaData(SharedData.MetaData.METADATA_FILE_CONTENT).toString());
				writer.flush();
				writer.close();
				
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
	
	public Object listenOnServer(String ipAddress)
	{
		Object result = new Object();
		SharedData data = this.getSharedData(ipAddress);
		String sharedDataType = data.getMetaData(SharedData.MetaData.METADATA_SHARED_DATA_TYPE).toString();
		switch(sharedDataType)
		{
		case "CLIPBOARD_DATA":
			copyTextFromSharedData(data);
			break;
			
		case "FILE":
			downloadFromSharedData(data);
			break;
			
		case "OTHER":
			result = data.getSharedData();
			break;
		default:
			result = data.getSharedData();
			break;
		}
		
		return result;
	}
	
}
