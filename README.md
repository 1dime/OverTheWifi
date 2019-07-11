# OverTheWiFi

What is OverTheWifi?
OverTheWifi is an open source project that uses java to transmit 
information from one device to another. The transmitting device is called 
the server, and the recieving device is called the client.

What can OverTheWifi be used for?
OverTheWifi can be used to allow users to share files, copied / cut text, 
and much more over their wireless network. This also works if you know your 
device's public ip address. I highly recommend using ShareData.DEFAULT_PORT 
for the port on server and client.

How can it be run?

Server:

	```
	Server server = new Server();
	
	ShareData data = ShareData.setShareData("text or other object to be shared", ShareDataType.OTHER);
	
	server.sendData(data, ShareData.DEFAULT_PORT);
	```


Client:

	```
	Client client = new Client();
	
	ShareData data = client.listen("localhost", ShareData.DEFAULT_PORT);
	```

Make sure that localhost is replaced with whichever IP address you want to 
use, and replace ShareData.DEFAULT_PORT with your own value. As you may 
have noticed, the Client class has aleady several functions 
for listening on all ip addresses connected to the wireless network. Edit 
that to your heart's desire :)


v 0.0.2:

How can I share a file?

Server:

	```
	Server server = new Server();
	SharedData data = new SharedData(SharedDataType.FILE);
	data.addMetaData(SharedData.METADATA_FILE_PATH, "PATH_WHERE_FILE_WILL_BE_STORED");
	data.addMetaData(SharedData.METADATA_FILE_CONTENT, new SharedData.SharedDataFileReader("FILE_TO_SHARE").read());
	data.addMetaData(SharedData.METADATA_FILE_SIZE, new SharedData.SharedDataFileReader("FILE_TO_SHARE").length());
	server.hostData(data, "localhost");
	```

Client:

	```
	Client client = new Client(SharedData.DEFAULT_PORT);
	client.downloadFromServer("localhost");
	```
What else does 0.0.2 provide?

  Version 0.0.2 provides commented code and a more refined edition of 
OverTheWifi. Automating the SDK is coming soon in the next few updates. 
Version 0.0.2 provides metadata support, allowing you to add information to 
the content being shared that can be intercepted by the Client class.


v 0.0.3:
Listening on all devices connected to the wireless network is now possible! Version 0.0.2 also includes a new file sharing update, and client automation. Client automation is listening on a provided ip address, list of ip addresses, or ip addresses connected to your wifi router and automatically storing files on request, copying clipboard data, and much more! It's advisable to extend the Client class if you wish to implement a consent-based system.

How do I listen automatically?

Here's how:
Please note that true is used to continuously listen on the provided list.
To listen on all connected devices:
	```

	Client client = new Client(SharedData.DEFAULT_PORT);
	client.listenForAllServers(true);
	```
To listen on a given set of IP addresses:
	```
	
	Client client = new Client(SharedData.DEFAULT_PORT);
	List<String> ipAddresses = new ArrayList<>();
	client.listenOnServers(ipAddresses,true);
	```
To listen on a given IP address:
	```
	
	Client client = new Client(SharedData.DEFAULT_PORT);
	client.listenOnServer("ipaddress", true);
	```
	
There's many more new features, so I suggest reading the code for yourself. The documentation of the code is excellent and explains nearly in depth what a function does, and what the variables inside a function are responsible for.

v 0.0.4:
General under-the-hood repairs, bug fixes and more. Also, since Android needs AsyncTask, copying and downloading files is held exclusively towards PC / Mac / 
Linux. I mean the download from server and copy text from server functions. You can still download from SharedData and copy from SharedData. If you're so 
inclined, check out my open source Android version of OverTheWifi at https://github.com/RedDogCode/OverTheWifiAndroid.git

v 0.0.5:
As promised, a server keep-alive feature was added. Now you do not have to worry about the unintended device connecting to your shareddata server,
as the device itself will simply ignore the information not intended for it.
