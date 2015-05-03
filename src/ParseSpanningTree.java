import java.util.ArrayList;
import java.util.List;


public class ParseSpanningTree {
	
	//String spanningInfo="A(B(D,E(H(I)),F(G,M(P))),C(J,K(N),L(O(T(R)),Q,S)))";
	//String spanningInfo = "B(D,E(H(I)),F(G,M(P)))";
//	String spanningInfo ="(A(B(D(F(),G()),E(H(),I())),C(J(K(T()),L(Q(),R(S()))),M(N(O(),P()))))";
//	String nodeid="D";
	String spanningInfo, nodeid, parent;
	public ParseSpanningTree(String spanningInfo, String nodeid)
	{
		this.spanningInfo = spanningInfo;
		this.nodeid = nodeid;
	}
	
	
//	public static void main(String[] args) {
//		ParseSpanningTree p=new ParseSpanningTree();
//		boolean flag=p.checkToBeConsidered(p.spanningInfo);
//		String result_str=p.getParentChildString(p.spanningInfo);
//		System.out.println(result_str);
//		String children=p.getChildren(result_str);
//		System.out.println("Childs :"+children);
//	}
	
	//Check if the Spanning Tree message received should be processed or not 
	public boolean checkToBeConsidered()
	{
		System.out.println(spanningInfo);
		String []info=spanningInfo.split("\\(");
		if(!info[0].trim().equals(""))
			parent = info[0];
		else
			parent = info[1];
		//System.out.println(parent);
		if(spanningInfo.indexOf(nodeid)!=-1)
		{
			return true;
		}
		return false;
	}
	
	
	//Get parent-child string to pass on to Children
	public String getParentChildString(String info)
	{
		String []info_arr=info.split("");
		String parent=info_arr[0];
		String leftBracket="(";
		String rightBracket=")";
		int leftBracket_cnt=0;
		int rightBracket_cnt=0;
		int node_index=0;
		String res="";
		for(int i=0;i<info_arr.length;i++)
		{
			if(info_arr[i].equals(nodeid))
			{
				node_index=i;				
			}
		
			if(node_index!=0)
			{
				if(info_arr[i].equals(leftBracket))
				{
					leftBracket_cnt++;
				}
				else if(info_arr[i].equals(rightBracket))
				{
					rightBracket_cnt++;
				}
				if((leftBracket_cnt==rightBracket_cnt) && (leftBracket_cnt!=0 || rightBracket_cnt!=0))
				{
					if(i==info_arr.length)
					{
						res= info.substring(node_index-1, i-1);
					}
					else
					{
						res= info.substring(node_index-1, i);
					}
					break;
					
				}
			}
		}
		return res;
	}
	
	//Get current nodes children in Spanning Tree
	public List<String> getChildren()
	{
		String result_str = getParentChildString(spanningInfo);
		String mychild=result_str.substring(2,result_str.length()-1);
		String [] mychild_arr=mychild.split("");
		String leftBracket="(";
		String rightBracket=")";
		int leftBracket_cnt=0;
		int rightBracket_cnt=0;
		String finalString="";
		List<String> children= new ArrayList<String>();
		for(int i=0;i<mychild_arr.length;i++)
		{
			if(mychild_arr[i].equals(leftBracket))
			{
				leftBracket_cnt++;
			}
			else if(mychild_arr[i].equals(rightBracket))
			{
				rightBracket_cnt++;
			}
			else
			{
				if(leftBracket_cnt==0 && rightBracket_cnt==0)
				{
					finalString+=mychild_arr[i];
				}
			}
			
			if((leftBracket_cnt==rightBracket_cnt) && (leftBracket_cnt!=0 || rightBracket_cnt!=0))
			{
				leftBracket_cnt=0;
				rightBracket_cnt=0;
			}
			
		}
		
		String splits[] = finalString.split(",");
		for(String s : splits)
			children.add(s);
		return children;
	}
}
