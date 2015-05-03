/*
 * Gives the message structure
 */

import java.io.*;
public class MessageStructure implements Serializable
{
	String sourceId;
	
	// 's' : SpanningTreeDessiminationMessage
	// 'q' : query
	// 'r' : reply to the query
	char messageType;
	String message;
	
	public MessageStructure(String sourceId, char messageType, String message)
	{
		this.sourceId = sourceId;
		this.messageType = messageType;
		this.message = message;
	}
	
	public static byte[] serialize(Object obj) throws IOException 
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(obj);
	    return baos.toByteArray();
	}
	
	public static Object deserialize(byte[] bufArr) throws IOException, ClassNotFoundException 
	{	
		ByteArrayInputStream bais = new ByteArrayInputStream(bufArr);
	    ObjectInputStream ois = new ObjectInputStream(bais);
	    return ois.readObject();	
	}
}
