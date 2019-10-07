package otw.share;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable
{
	public static final String DATA_FILE_BUFFER = "file_buffer";
	public static final String DATA_TARGET_IP = "target_ip";
	public static final String DATA_TARGET_FILE_PATH = "target_file_path";
	
	public static enum DataType
	{
		FILE,
		CLIPBOARD,
		TEXT,
		OTHER
	}
	
	private List<String> keys = new ArrayList<>();
	private List<Object> values = new ArrayList<>();
	private DataType dataType;
	
	public void addData(String key, Object value)
	{
		this.keys.add(key);
		this.values.add(value);
	}
	
	public void setDataType(DataType type)
	{
		this.dataType = type;
	}
	
	public Object getData(String key) throws IndexOutOfBoundsException
	{
		//Get the index of the key
		int keyIndex = this.keys.indexOf(key);
		
		return this.values.get(keyIndex);
	}
	
	public List<String> getKeys()
	{
		return this.keys;
	}
	
	public List<Object> getValues()
	{
		return this.values;
	}
	
	public DataType getDataType()
	{
		return this.dataType;
	}
	
	public Object readFile(String file, String filePathOnClient) throws IOException
	{
		this.addData(Data.DATA_TARGET_FILE_PATH, filePathOnClient);
		//Open the file for reading
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		//Create a buffer for file reading
		byte[] buffer = new byte[(int) new File(file).length()];
		//Read the file's bytes into our buffer
		inputStream.read(buffer);
		//Return the buffer
		return buffer;
	}
}
