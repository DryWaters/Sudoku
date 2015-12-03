public class Move {
	private String value;
	private int location;
	
	public Move(String value, int location)
	{
		this.value = value;
		this.location = location;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setLocation(int location)
	{
		this.location = location;
	}
	
	public int getLocation()
	{
		return location;
	}
	
	public String toString()
	{
		return String.format("Value: %s - Location: %d%n", getValue(), getLocation());
	}
}