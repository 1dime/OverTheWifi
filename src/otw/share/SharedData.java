package otw.share;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SharedData extends Object implements Serializable
{
	
	public SharedData(SharedDataType dataType)
	{
		this.setSharedDataType(dataType);
	}
	
	public SharedData()
	{
		//Notify that not using new SharedData(SharedDataType) is not recommended
		System.out.println("Not using new SharedData(SharedDataType) will require you to run setSharedDataType(SharedDataType)");
	}
	
	public void setSharedDataType(SharedDataType type)
	{
		this.dataType = type;
		this.addMetaData(MetaData.METADATA_SHARED_DATA_TYPE, type);
	}
	
	public static int DEFAULT_PORT = 2077; //There's a cyberpunk reference in there somewhere
	public static enum SharedDataType implements Serializable
	{
		CLIPBOARD_DATA, //Supports hosting clipboard data
		FILE, //Supports hosting files
		OTHER //Support for images and others not stated
	}
	
	public static class SharedDataFileReader implements Serializable
	{
		private String path;
		public SharedDataFileReader(String path)
		{
			this.path = path;
		}
		
		public byte[] read()
		{
			try
			{
				byte[] content = new byte[(int)this.length()];
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(this.path)));
				bis.read(content);
				return content;
			}catch(IOException ioe)
			{
				
			}
			return null;
		}
		
		public long length()
		{
			return new File(this.path).length();
		}
	}
	
	public static class MetaData implements Serializable
	{
		public static String METADATA_FILE_PATH = "FILE_PATH";
		public static String METADATA_FILE_CONTENT = "FILE_CONTENT";
		public static String METADATA_FILE_SIZE = "FILE_SIZE";
		public static String METADATA_TARGET_CLIENT = "TARGET_CLIENT";
		public static String METADATA_SHARED_DATA_TYPE = "SHARED_DATA_TYPE";
	}
	
	private List<Object> sharedDataList = new ArrayList<>();
	private List<SharedDataType> sharedDataTypeList = new ArrayList<>();
	private Object sharedData;
	private SharedDataType dataType;
	private String storePath;
	private List<String> metaDataKeys = new ArrayList<>();
	private List<Object> metaDataValues = new ArrayList<>();
	public static SharedData setShareData(Object data, SharedDataType dataType)
	{
		SharedData shareData = new SharedData(dataType);
		shareData.sharedData = data;
		shareData.sharedDataList = new ArrayList<>();
		shareData.sharedDataList.add(shareData.sharedData);
		shareData.dataType = dataType;
		shareData.sharedDataTypeList = new ArrayList<>();
		shareData.sharedDataTypeList.add(shareData.dataType);
		return shareData;
	}
	
	public Object getSharedData()
	{
		return sharedData;
	}
	
	public SharedDataType getSharedDataType()
	{
		return dataType;
	}
	
	public List<Object> getSharedDataList()
	{
		return sharedDataList;
	}
	
	public List<SharedDataType> getSharedDataTypeList()
	{
		return this.sharedDataTypeList;
	}
	
	public void add(Object object, SharedDataType dataType)
	{
		this.sharedDataList.add(object);
		this.sharedDataTypeList.add(dataType);
	}
	
	public void addMetaData(String key, Object value)
	{
		//Add the metadata key to the metaDataKeys list
		metaDataKeys.add(key);
		//Add the metadata value to the metaDataValues list
		metaDataValues.add(value);
	}
	
	public Object getMetaData(String key)
	{
		//Create an empty metaDataValue Object instance
		Object metaDataValue = new Object();
		//Loop through the metaDataKeys
		for(int i = 0; i < metaDataKeys.size(); i++)
		{
			//Get the current metadata key
			String metaDataKey = metaDataKeys.get(i);
			//Check if the metadata key equals the required key
			if(metaDataKey.equals(key))
			{
				//Set the metaDataValue
				metaDataValue = metaDataValues.get(i);
			}
		}
		
		return metaDataValue;
	}
	
	public List<String> getMetaDataKeys()
	{
		//Return the metaDataKeys list
		return this.metaDataKeys;
	}
	
	public List<Object> getMetaDataValues()
	{
		//Return the metaDataValues list
		return this.metaDataValues;
	}
	
	@Override
	public String toString()
	{
		return this.getSharedData().toString();
	}
}
