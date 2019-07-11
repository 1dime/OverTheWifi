package otw.share;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import otw.share.SharedData.SharedDataType;
import otw.share.server.Server;

public class Host {

	public static void main(String[] args) throws UnknownHostException 
	{
		// TODO Auto-generated method stub
		Server server = new Server(SharedData.DEFAULT_PORT);
		SharedData data = SharedData.setShareData("kimsible.xml", SharedData.SharedDataType.FILE);
		data.addMetaData(SharedData.MetaData.METADATA_FILE_PATH, "/root/kimsible.xml");
		data.addMetaData(SharedData.MetaData.METADATA_FILE_CONTENT, new SharedData.SharedDataFileReader("pom.xml").read());
		server.hostData(data, "localhost", "127.0.0.1");
		
		//server.hostData(SharedData.setShareData("It's never", SharedDataType.CLIPBOARD_DATA), "192.168.0.24", "192.168.0.24");
	}

}
