package otw.share;

import java.net.InetAddress;
import java.net.UnknownHostException;

import otw.share.SharedData.SharedDataType;
import otw.share.server.Server;

public class Host {

	public static void main(String[] args) throws UnknownHostException 
	{
		// TODO Auto-generated method stub
		Server server = new Server();
		SharedData data = SharedData.setShareData("Hello".getBytes(), SharedDataType.FILE);
		data.addMetaData(SharedData.MetaData.METADATA_FILE_PATH, "REAME.txt");
		data.addMetaData(SharedData.MetaData.METADATA_FILE_CONTENT, new SharedData.SharedDataFileReader("README.md").read());
		data.addMetaData(SharedData.MetaData.METADATA_FILE_SIZE, new SharedData.SharedDataFileReader("README.md").length());
		data.addMetaData(SharedData.MetaData.METADATA_TARGET_CLIENT, "192.168.0.31");
		server.hostData(data, "localhost");
	}

}
