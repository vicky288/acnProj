/*
 * This is the entry point for the project
 */
import java.io.BufferedReader;
import java.io.FileReader;


public class MainClass_Client {
	
	public static void main(String[] args) {
		String nodeId = args[0];
		String topologyFile = args[1];
		
		NodeInitialize ni = new NodeInitialize();
		ni.addNodes(nodeId,topologyFile);
		Node myNodeInfo = ni.nodeInfo;
		
		BufferedReader br;
		try
		{
			MessageProcessing server = MessageProcessing.getInstance(myNodeInfo);
			server.initializeServerToReceive(myNodeInfo.port);
			Thread serverThread = new Thread(server);
			Thread.currentThread().sleep(3000);
			serverThread.start();
			
			///->12/06
			while ( true ){
				FileReadingWriting.CreateWriteFile(nodeId, "The MainClassClient of "+myNodeInfo.nodeId+" running");
				
				server.processReceivedMessage(myNodeInfo);
				Thread.currentThread().sleep(5000);
			}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
