public class Node<T> {
	private T data;
	private Node next;
	
	//Constructor
	public Node(T data, Node next)
	{
		this.data = data;
		this.next = next;
	}
	
	//Constructor with data only
	public Node(T data)
	{
		this(data, null);
	}
	
	//Setter
	public void setNext(Node node)
	{
		next = node;
	}
	
	//Getter
	public Node getNext()
	{
		return next;
	}
	
	//Setter
	public void setData(T data)
	{
		this.data = data;
	}
	
	//Getter
	public T getData()
	{
		return data;
	}
}