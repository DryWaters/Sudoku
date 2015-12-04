public class LinkedStack<T> implements StackInterface<T> {
	@SuppressWarnings("rawtypes")
	private Node topNode; // references the first node in the chain
	
	public LinkedStack()
	{
		topNode = null;
	} // end default constructor
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void push(T newEntry)
	{
		Node newNode = new Node(newEntry, topNode);
		topNode = newNode;
	} // end push

	@Override
	public T pop()
	{
		T top = peek();
		
		if (topNode != null)
			topNode = topNode.getNext();
		return top;
	} // end pop

	@SuppressWarnings("unchecked")
	@Override
	public T peek() 
	{
		T top = null;
		
		if (topNode != null)
			top = (T) topNode.getData();
			return top;
	} // end peek

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return topNode == null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		topNode = null;
	}

}