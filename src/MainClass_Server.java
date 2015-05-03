/*
 * This is the entry point for the project
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;


public class MainClass_Server {
	static String GET_AVG = "AVG"; 
	static String GET_MAX = "MAX"; 
	static String GET_MIN = "MIN"; 
	
	public static void main(String[] args) {
		String nodeId = "A";
		//String topologyFile = "C:\\Users\\vicky288\\workspace\\ACN012\\bin\\zz.txt";
		Scanner in ;
		int choice;
		String topologyFile = null;
		
		
		//Input Topology File
		System.out.print("Enter Topology File Name ->");
		in = new Scanner(System.in);
		topologyFile = in.nextLine();
		
		//Initialize Server node
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
			
		
			br = new BufferedReader(new FileReader(topologyFile));
			String line = "";
			line=br.readLine();
			String data[] = line.split(",");
			br.close();
			
			//if the passed nodeId is sink, constructs the spanning tree 
			//and broadcasts the message to neighbors.
			if(nodeId.equals(data[0].trim()) && data[6].trim().equals("1"))
			{
				SpanningTree eeb = new SpanningTree(nodeId,topologyFile);
				String stringToSend = eeb.stringToDisseminate;
				System.out.println("This is the Spanning Tree String->"+stringToSend);
				FileReadingWriting.CreateWriteFile(nodeId, "This is the Spanning Tree String->"+stringToSend);
				
				MessageProcessing sender = MessageProcessing.getInstance();
				Thread.currentThread().sleep(1000);
				for(Node n : myNodeInfo.neighbors)
				{
					sender.initializeClientToSend(myNodeInfo.hostname);
					sender.sendData(n.hostname, n.port, stringToSend,'s', n.nodeId);
				}
				ParseSpanningTree pst = new ParseSpanningTree(stringToSend, myNodeInfo.nodeId);
				myNodeInfo.parent = null;
				myNodeInfo.children = pst.getChildren();
				
				
				//Take user input for Temperature Operation
				while(true) {
					myNodeInfo.result = null;
					System.out.println("Press 1 to find the Average.");
					System.out.println("Press 2 to find the Max.");
					System.out.println("Press 3 to find the Min.");
					System.out.println("Press 0 to Exit.");
					System.out.print("Input your choice -> ");
					
					in = new Scanner(System.in);
					choice = in.nextInt();
					
					switch(choice) {
					case 0:
						System.exit(0);
						break;
					case 1:
						stringToSend = GET_AVG;
						break;
					case 2:
						stringToSend = GET_MAX;
						break;					
					case 3:
						stringToSend = GET_MIN;
						break;
					default:
						stringToSend = null;
						System.out.println("Wrong Input");
						break;
					}
					if(stringToSend != null) {
						//Send Tempreature Query
						for(Node n : myNodeInfo.neighbors)
						{
							sender.initializeClientToSend(myNodeInfo.hostname);
							sender.sendData(n.hostname, n.port, stringToSend,'q', n.nodeId);
						}
						//Receive Temperature Query
						System.out.print("Wait while its processing. It may take few Seconds to Minutes..");
						while(myNodeInfo.result == null){
							server.processReceivedMessage(myNodeInfo);
							System.out.print("..");
							Thread.currentThread().sleep(3000);
						}
						System.out.println();
						System.out.println("Operation finding "+stringToSend+" successfully Executed. The result is ->"+myNodeInfo.result);
					}
					
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
