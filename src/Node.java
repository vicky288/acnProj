/* 
 * Gives the Node structure
 */

import java.util.*;

public class Node {
	String nodeId;
	int x,y;
	int temperature;
	int transmissionRange;
	boolean isInSpanningTree;
	boolean isVisited;
	int port;
	String hostname;
	//TreeMap<String, Node> neighbours = new TreeMap<String, Node>();
	List<Node> neighbors;
	//List<String> spanningTreeNeighbors;
	String parent = null;
	List<String> children;
	
	String result;
	
	public Node()
	{
		neighbors = null;
	}
	
	public void addNeighbor(Node n)
	{
		if(neighbors == null)
		{
			neighbors = new ArrayList<Node>();
		}
		neighbors.add(n);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if(neighbors != null)
		{
			for(Node n : neighbors)
		
		{
			sb.append(n.nodeId+", ");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.deleteCharAt(sb.length()-1);
		}
		sb.append("]");
		return sb.toString();
	}
	
}
