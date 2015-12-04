public class Node<T> {
	private T data;
	@SuppressWarnings("rawtypes")
	private Node next;
	
	@SuppressWarnings("rawtypes")
	public Node(T data, Node next)
	{
		this.data = data;
		this.next = next;
	}
	
	public Node(T data)
	{
		this(data, null);
	}
	
	@SuppressWarnings("rawtypes")
	public void setNext(Node node)
	{
		next = node;
	}
	
	@SuppressWarnings("rawtypes")
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