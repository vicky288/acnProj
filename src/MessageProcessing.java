/*
 * Has the code for socket communication and
 * processing of the received message
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class MessageProcessing implements Runnable{
	static int MESSAGE_SIZE = 1024;

	private static MessageProcessing instance = null;
	//public static TreeMap<String, MessageStructure> spanningTreeMsg = new TreeMap<String, MessageStructure>();
	public static List<MessageStructure> receivedSTMsgsList = new ArrayList<MessageStructure>();
	public static List<MessageStructure> receivedTempQueryList = new ArrayList<MessageStructure>();
	public static List<MessageStructure> receivedTempDataList = new ArrayList<MessageStructure>();
	public static int result;

	public DatagramSocket serverSocket;
	public static String clientId;
	public static DatagramSocket clientSocket;
	Node myNodeInfo;

	private MessageProcessing() {
		
	}
	public static MessageProcessing getInstance(Node myNodeinfo) {
		if(instance == null) {
			System.out.println("Node Instance is null");
			instance = new MessageProcessing();
			instance.myNodeInfo = myNodeinfo;
		}
		return instance;
	}

	public static MessageProcessing getInstance() {
		if(instance == null) {
			System.out.println("Node Instance is null");

			System.out.println("Please create an instance passing Node as argument");
		}
		return instance;
	}



	////Methods used to Receive data- start
	public String initializeServerToReceive(int serverPort)
	{
		String statusMessage="OK";
		try
		{
			serverSocket = new DatagramSocket(serverPort);
		} 
		catch (SocketException e) 
		{
			statusMessage = "UDP Port"+ serverPort +"is occupied.";
			e.printStackTrace();
		}
		return statusMessage;
	}

	//run server on a separate thread
	public void run() {
		try 
		{
			receiveData();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}

	}
	////Methods used to Receive data - end


	public void receiveData() throws IOException, ClassNotFoundException
	{	
		while (true) 
		{ 
			byte[] receiveData = new byte[MESSAGE_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Waiting for datagram packet......."+myNodeInfo.nodeId);
			//Receive Info
			serverSocket.receive(receivePacket);
			MessageStructure receivedMsg = (MessageStructure)MessageStructure.deserialize(receivePacket.getData()); 
			InetAddress IPAddress = receivePacket.getAddress(); 
			int port = receivePacket.getPort();
			char messageType = receivedMsg.messageType;
			String senderId = receivedMsg.sourceId;
			//Print Info

			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "***********Message Received*************");
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "From: "+receivedMsg.sourceId);
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, ", Message: " + receivedMsg.message);

			synchronized(receivedSTMsgsList)
			{
				if (messageType == 's' ) {
					FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "its a spanning tree message");
					receivedSTMsgsList.add(receivedMsg);
				}
				if (messageType == 'q' ) {
					FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Its a temp query message");
					if (myNodeInfo.parent != null && myNodeInfo.parent.equalsIgnoreCase(senderId)){
						FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Its a temp query message from Parent");
						receivedTempQueryList.add(receivedMsg);	        			        			
					} else {
						FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "The temp query message is not from Parent....Discarding the message....");	        			
					}
				}

				if (messageType == 'r' ) {
					FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Its a temp Data message");
					if(isSpanningTreeDescendant(senderId)){
						FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Its a temp Data message from Spanning Tree Descendant ->" + senderId);
						receivedTempDataList.add(receivedMsg);
					} else {
						FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "The temp data is not a Descendant....Discarding the message....");	        			        			
					}
				}	
			}	        
		}
	}


	public boolean isSpanningTreeDescendant(String senderId) {
		boolean result = false;
		for(String spanninNeighbor:myNodeInfo.children) {
			if(spanninNeighbor.equalsIgnoreCase(senderId)) {
				return true;
			}
		}
		return result;
	}


	////Process Received message

	public void processReceivedMessage(Node myNodeInfo) {

		if (myNodeInfo.nodeId.equalsIgnoreCase("A")) {
			//Code to process data at server
			//System.out.println("Processing Received Message at the sink");
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Processing Received Message at the sink....");
			if(receivedTempDataList.size() == myNodeInfo.neighbors.size()) {
				//System.out.println("Procesing Temperature Data Message");
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Procesing Temperature Data Message");
				processTempDataMsg();
			}

		} else {
			//Code to process data at nodes
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Processing Received Message if any at the node "+myNodeInfo.nodeId);
			//int noOfNeighbors = myNodeInfo.neighbors.size();

			if(receivedSTMsgsList.size()>0) {
				//System.out.println("Procesing Spanning Tree Message");
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Procesing Spanning Tree Message");
				processSpanningTreeMsg();
			}
			else if(receivedTempQueryList.size()>0) {
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Procesing Temperature Query Message");
				processTempQueryMsg();
			}
			else if(myNodeInfo.parent != null && receivedTempDataList.size() == myNodeInfo.children.size()) {
				System.out.println("Procesing Temperature Data Message");
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Procesing Temperature Data Message");
				processTempDataMsg();
			}
		}
	}


	////Methods used to send data - start
	public String initializeClientToSend(String clientIP){
		clientId =clientIP;
		String statusMessage="OK";
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			statusMessage = "Issue in Intilizing Client.";
			e.printStackTrace();
		}
		return statusMessage;
	}

	public String sendData(String receiverIP, int receiverPort, String message, char messageType, String receiverID) 
	{
		String statusMessage = "OK";
		String serverHostname = receiverIP;
		byte[] sendDataHolder = new byte[MESSAGE_SIZE]; 
		MessageStructure msgToSend = createMessage(message, messageType);
		try 
		{
			InetAddress IPAddress = InetAddress.getByName(serverHostname);
			sendDataHolder = MessageStructure.serialize(msgToSend);
			DatagramPacket sendPacket = new DatagramPacket(sendDataHolder, sendDataHolder.length, IPAddress, receiverPort); 
			clientSocket.send(sendPacket); 
			System.out.println("Send data to:" + receiverID);
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Send data to:" + receiverID);

		} 
		catch (UnknownHostException e) 
		{
			statusMessage = "UnknownHostException...check IP of Receiver";
			//e.printStackTrace();

		}
		catch (IOException e) {
			statusMessage = "Exception while Sending Data";
			//e.printStackTrace();
		}
		return statusMessage;
	}

	public MessageStructure createMessage(String message, char messageType) {
		MessageStructure msgToSend = new MessageStructure(myNodeInfo.nodeId, messageType, message);
		return msgToSend;
	}
	////Methods used to send data - end



	//Method to process temp data
	public void processTempDataMsg(){
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Inside processTempDataMsg() *******");
		String operation = null;
		String temperature;
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		synchronized (receivedTempDataList) {
			for(MessageStructure getMsg :receivedTempDataList ){
				String message = getMsg.message;
				//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$-"+myNodeInfo.nodeId+"->"+message);
				String[] splited = message.split("#");
				operation = splited[0];
				temperature = splited[1];
				int tempVal = Integer.valueOf(temperature);
				tempList.add(tempVal);
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "############------------Temp val->"+tempVal);
			}
			receivedTempDataList.clear();
		}
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "############------------Operation->"+operation);
		String tempResultToEmit = performTempCalculation(tempList, operation);
		//If Parent node Store the result
		if(myNodeInfo.parent == null){
			String[] splited = tempResultToEmit.split("#");
			operation = splited[0];
			temperature = splited[1];
			myNodeInfo.result = temperature;
			System.out.println("The operation was "+ operation +". The result is "+ temperature);

		} else {
			emitTempData(tempResultToEmit);
		}
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "End of processTempDataMsg() .....");
	}

	public String performTempCalculation(ArrayList<Integer> temperatureList, String operation){
		String resultString = null;
		int result = 0;
		if(operation.equalsIgnoreCase("AVG")) {
			for(Integer temp: temperatureList){
				result = result + temp;
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "############------------calculating Avg->"+result);

			}
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "############------------calculating Avg->"+myNodeInfo.temperature);
			result = result + myNodeInfo.temperature;
			//result = result/(temperatureList.size()+1);
			if(myNodeInfo.parent==null){
				result = result/20;
			}
		}
		if(operation.equalsIgnoreCase("MAX")) {
			for(Integer temp: temperatureList){
				if(temp >= result) {
					result = temp;
					FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "############------------calculating Max->"+result);
				}
			}
			if(result < myNodeInfo.temperature) {
				result = myNodeInfo.temperature;
			}
		}
		if(operation.equalsIgnoreCase("MIN")) {
			result = 999;
			for(Integer temp: temperatureList){
				if(temp <= result) {
					result = temp;
					FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "############------------calculating Min->"+result);
				}
			}
			if(result > myNodeInfo.temperature) {
				result = myNodeInfo.temperature;
			}

		}
		
		resultString = operation + "#" +result;
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "############------------resultString->"+resultString);
		return resultString;
	}

	public void emitTempData(String tempResultToEmit){

		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Start of emitTempData() *****");
		for(Node n : myNodeInfo.neighbors)
		{
			initializeClientToSend(myNodeInfo.hostname);
			sendData(n.hostname, n.port, tempResultToEmit,'r',n.nodeId);
		}
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "End of emitTempData() .....");
	}


	///process Temperature Query Msg
	public void processTempQueryMsg(){
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Inside processTempQueryMsg() *******");
		MessageStructure getMsg;
		synchronized (receivedTempQueryList)
		{
			getMsg = receivedTempQueryList.remove(0);
		}
		//If Leaf node Broadcast Temp else emit temp Query
		if(myNodeInfo.children.size() == 1 && myNodeInfo.children.get(0).equals("")){
			broadcastTemp(getMsg);

		} else {
			emitTempQuery(getMsg);
		}
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "End of processTempQueryMsg() ....."+myNodeInfo.children+" size ->"+myNodeInfo.children.size()+" value ->"+ myNodeInfo.children.get(0));		
	}


	/// Emit Temp Query
	public void emitTempQuery(MessageStructure getMsg){
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Start of emitTempQuery() *****");
		for(Node n : myNodeInfo.neighbors)
		{
			initializeClientToSend(myNodeInfo.hostname);
			sendData(n.hostname, n.port, getMsg.message,'q',n.nodeId);
		}
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "End of emitTempQuery() .....");				
	}

	///Broadcast Temp
	public void broadcastTemp(MessageStructure getMsg) {
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Start of broadcastTemp() ~~~~~~~~~~~~~~~~~~");
		String operationToPerform = getMsg.message;
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "operationToPerform->"+operationToPerform);
		String tmpMsgToBroadcast =  operationToPerform + "#" +myNodeInfo.temperature ;
		for(Node n : myNodeInfo.neighbors)
		{
			initializeClientToSend(myNodeInfo.hostname);
			sendData(n.hostname, n.port, tmpMsgToBroadcast,'r',n.nodeId);
		}
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "End of broadcastTemp() ~~~~~~~~~~~~~~~~~~~~");
	}



	//// Process Data Received
	public void processSpanningTreeMsg() 
	{
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Inside processSpanningTreeMsg() ******");
		MessageStructure getMsg;
		synchronized (receivedSTMsgsList)
		{
			getMsg = receivedSTMsgsList.remove(0);
		}
		if(getMsg.messageType=='s' && myNodeInfo.parent==null) {
			emitSpanningTreeMsg(getMsg);
		} else {
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Already In Spanning Tree Discarding Message...");			
		}
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "End of processSpanningTreeMsg().......");			
	}
	////end process Data

	public void emitSpanningTreeMsg(MessageStructure getMsg)
	{
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Emitting Spanning Tree.......");
		ParseSpanningTree pst = new ParseSpanningTree(getMsg.message, myNodeInfo.nodeId);
		if(pst.checkToBeConsidered())
		{
			if(pst.parent.equals(getMsg.sourceId))
			{
				myNodeInfo.parent = pst.parent;
				myNodeInfo.children = pst.getChildren();
				System.out.println("NodeId:"+ myNodeInfo.nodeId);
				System.out.println("Parent:"+ myNodeInfo.parent);
				System.out.println("NodeId:"+ myNodeInfo.children);
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "###########Spanning Tree Info##########");
				FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "NodeId: "+ myNodeInfo.nodeId+" Parent: "+ myNodeInfo.parent+" Children: "+ myNodeInfo.children);

				for(Node n : myNodeInfo.neighbors)
				{
					initializeClientToSend(myNodeInfo.hostname);
					sendData(n.hostname, n.port, pst.getParentChildString(getMsg.message),'s',n.nodeId);
				}

			}
			else
				//System.out.println("Discarding message "+getMsg.message+" from "+getMsg.sourceId);
			FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Discarding message "+getMsg.message+" from "+getMsg.sourceId);


		}
		else
			//System.out.println("Discarding message "+getMsg.message+" from "+getMsg.sourceId);
		FileReadingWriting.CreateWriteFile(myNodeInfo.nodeId, "Discarding message "+getMsg.message+" from "+getMsg.sourceId);
	}
}
