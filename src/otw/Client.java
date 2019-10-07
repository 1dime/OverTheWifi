package otw;

import java.io.IOException;

import otw.connector.Connector;
import otw.share.Data;

public class Client 
{


	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		Connector connector = new Connector();
		System.out.println(connector.getHostAddress());
		Data data = connector.get(connector.getHostAddress());
		
	}

}
