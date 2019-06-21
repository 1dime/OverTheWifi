package otw.share;

import java.net.InetAddress;
import java.net.UnknownHostException;

import otw.share.SharedData.SharedDataType;
import otw.share.server.Server;

public class Host {

	public static void main(String[] args) throws UnknownHostException 
	{
		// TODO Auto-generated method stub
		Server server = new Server(SharedData.DEFAULT_PORT);
		SharedData data = SharedData.setShareData("Hello World", SharedDataType.CLIPBOARD_DATA);
		server.hostData(data, "192.168.0.32");
		
	}

}
