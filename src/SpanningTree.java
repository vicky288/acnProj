/*
 * Builds the spanning tree and the string for dissemination
 */
import java.io.*;
import java.util.*;

public class SpanningTree {
	Map<String,Node> nodesInGraph = new TreeMap<String,Node>();
	Map<String,ArrayList<String>> spanningTree = new TreeMap<String,ArrayList<String>>();
	String sinkNode="", stringToDisseminate;
	String nodeId;
	Queue<Node> q = new LinkedList<Node>(); 
	String topologyFile;
	public SpanningTree(String nodeId, String topologyFile)
	{
		this.topologyFile = topologyFile;
		this.nodeId = nodeId;
		
		addNodes(nodeId);
		findNeighbors();

		//System.out.println("Neighors for each node are:");
		//System.out.println(nodesInGraph);

		buildSpanningTree();
		
		//System.out.println("\nSpanning Tree neighbors are:");
		//System.out.println(spanningTree);

		StringBuilder sb = new StringBuilder();
		Stack<String> stack = new Stack<String>();
		stack.push(sinkNode);
		sb.append("(");
		buildStringForDissemination(stack,sb);
		sb.append(")");
		//System.out.println("\nSpanning Tree Dissemination String:");
		//System.out.println(sb);
		
		stringToDisseminate = sb.toString();
	}
	public void buildStringForDissemination(Stack<String> stack, StringBuilder sb)
	{
		while(!stack.isEmpty())
		{
			String str = stack.pop();
			sb.append(str);
			sb.append("(");
			List<String> children = spanningTree.get(str);
			spanningTree.remove(str);
			for(String s: children)
			{
				stack.push(s);
				buildStringForDissemination(stack,sb);
				//if(sb.charAt(sb.length()-1)=='(')
				//	sb.deleteCharAt(sb.length()-1);
				//else
					sb.append(")");
				if(sb.charAt(sb.length()-1)!='(')
					sb.append(",");
			}
			if(sb.charAt(sb.length()-1)==',')
				sb.deleteCharAt(sb.length()-1);
		}
	}

	//build a spanning tree using breadth first search
	public void buildSpanningTree()
	{
		q.add(nodesInGraph.get(sinkNode));
		nodesInGraph.get(sinkNode).isInSpanningTree = true;
		while(!q.isEmpty())
		{
			Node curr = q.poll();
			String key = curr.nodeId;
			ArrayList<String> values = new ArrayList<String>();
			List<Node> neighbors = curr.neighbors;
			for(Node n:neighbors)
			{
				if(!n.isInSpanningTree)
				{
					n.isInSpanningTree = true;
					q.add(n);
					values.add(n.nodeId);
				}
			}
			spanningTree.put(key, values);
		}
	}

	// Use the distance formula to calculate if the distance is within transmission range
	// and use it to determine the neighbors
	public void findNeighbors()
	{
		if(nodesInGraph != null)
		{
			Iterator it = nodesInGraph.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<String,Node> pairs = (Map.Entry<String,Node>)it.next();
				Node curr = pairs.getValue();
				Set<String> keys = nodesInGraph.keySet();
				for(String s: keys)
				{
					Node n = nodesInGraph.get(s);
					if(curr!=n)
					{
						int x = curr.x - n.x;
						int y = curr.y - n.y;
						int sq = x*x + y*y;
						double distance = Math.sqrt(sq);
						if(distance <= curr.transmissionRange)
						{
							curr.addNeighbor(n);
						}

					}
				}

				pairs.setValue(curr);
			}
		}
	}


	// Read from the topology file and add all the nodes in a map
	public void addNodes(String nodeId)
	{
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(topologyFile));
			String line = "";
			line=br.readLine();

			String data[] = line.split(",");

			Node n = createNode(data); 

			//check if this node is a sink node
			//Assumption: If a node is a sink node, it would be the first entry in the 
			//topology file.
			if(nodeId.equals(data[0].trim()) && data[6].trim().equals("1"))
			{
				sinkNode = nodeId;
				nodesInGraph.put(data[0], n);
				while((line=br.readLine())!=null)
				{
					data = line.split(",");
					n = createNode(data);
					nodesInGraph.put(data[0], n);
				}

			}
			else
			{
				nodesInGraph = null;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public  Node createNode(String data[])
	{
		Node n = new Node();
		n.nodeId=data[0];
		n.x = Integer.parseInt(data[1].trim());
		n.y = Integer.parseInt(data[2].trim());
		n.temperature= Integer.parseInt(data[4].trim());
		n.transmissionRange = Integer.parseInt(data[5].trim());
		return n;

	}

}
