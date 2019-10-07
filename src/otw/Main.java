package otw;

import java.io.IOException;

import otw.connector.Connector;
import otw.share.Data;


public class Main 
{

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		Connector connector = new Connector();
		Data data = new Data();
		data.addData(Data.DATA_FILE_BUFFER, data.readFile("kimsible.xml", "landbridge.xml"));
		
		connector.host(data,connector.getHostAddress());
		
	}

}
