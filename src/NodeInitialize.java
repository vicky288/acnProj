/*
 * Initializes each node 
 */
import java.io.*;
import java.util.*;
public class NodeInitialize {
	Node nodeInfo = null;
	
	public void addNodes(String nodeId, String topologyFile)
	{
		BufferedReader br;
		ArrayList <String[]> nodes_arr=new ArrayList<String[]>();
		try
		{
			br = new BufferedReader(new FileReader(topologyFile));
			String line = "";
			while((line=br.readLine())!=null)
			{
				String data[] = line.split(",");
				if(data[0].equals(nodeId))
				{
					nodeInfo = createNode(data); 
				}
				else
				{
					nodes_arr.add(data);
				}
			}	
			Iterator<String[]> iterator = nodes_arr.iterator();
	        while (iterator.hasNext())
	        {
	            String[] nodes_data=iterator.next();
	            int x = Integer.parseInt(nodes_data[1].trim()) - nodeInfo.x;
				int y = Integer.parseInt(nodes_data[2].trim()) - nodeInfo.y;
				int sq = x*x + y*y;
				double distance = Math.sqrt(sq);
				//System.out.println(nodes_data[0]+":"+distance);
				if(distance <= nodeInfo.transmissionRange)
				{
					Node neighbour=createNode(nodes_data);
					nodeInfo.addNeighbor(neighbour);
				}
	        }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		} finally {
			if(nodeInfo!=null) {
				//System.out.println(nodeInfo.toString());
				FileReadingWriting.CreateWriteFile(nodeInfo.nodeId,"Neighbors of Node "+nodeInfo.nodeId+"->" +nodeInfo.toString());
			}
		}

	}
	public  Node createNode(String data[])
	{
		Node n = new Node();
		n.nodeId=data[0];
		n.x = Integer.parseInt(data[1].trim());
		n.y = Integer.parseInt(data[2].trim());
		n.hostname = data[3].split("/")[0].trim();
		n.port = Integer.parseInt(data[3].split("/")[1].trim());
		n.temperature= Integer.parseInt(data[4].trim());
		n.transmissionRange = Integer.parseInt(data[5].trim());
		return n;

	}
}
