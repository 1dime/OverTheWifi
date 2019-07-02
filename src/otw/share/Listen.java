package otw.share;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.util.Log;
import otw.share.client.Client;

public class Listen {

	public static void main(String[] args) throws Exception 
	{
		Client client = new Client(SharedData.DEFAULT_PORT);
		//Listen on all connected devices constantly
		client.copyTextFromServer("localhost");
	}

   
}
