public class Node<T> {
	private T data;
	private Node next;
	
	public Node(T data, Node next)
	{
		this.data = data;
		this.next = next;
	}
	
	public Node(T data)
	{
		this(data, null);
	}
	
	public void setNext(Node node)
	{
		next = node;
	}
	
	public Node getNext()
	{
		return next;
	}
	
	public void setData(T data)
	{
		this.data = data;
	}
	
	public T getData()
	{
		return data;
	}
}