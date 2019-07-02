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
		SharedData data = SharedData.setShareData("kimsible.xml", SharedData.SharedDataType.FILE);
		data.addMetaData(SharedData.MetaData.METADATA_FILE_PATH, "kimsible.xml");
		data.addMetaData(SharedData.MetaData.METADATA_FILE_CONTENT, new SharedData.SharedDataFileReader("pom.xml").read());
		server.hostData(data, "localhost", "192.168.0.37");
		
		server.hostData(SharedData.setShareData("It's never", SharedDataType.CLIPBOARD_DATA), "192.168.0.31", "192.168.0.37");
	}

}
