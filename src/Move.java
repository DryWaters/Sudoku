public class Move {
	private String value;//Variable holds Button text
	private int location;//Variable holds Button location
	
	//Constructor
	public Move(String value, int location)
	{
		this.value = value;
		this.location = location;
	}
	
	//Setter
	public void setValue(String value)
	{
		this.value = value;
	}
	
	//Getter
	public String getValue()
	{
		return value;
	}
	
	//Setter
	public void setLocation(int location)
	{
		this.location = location;
	}
	
	//Getter
	public int getLocation()
	{
		return location;
	}
	
	//toString method for debugging
	public String toString()
	{
		return String.format("Value: %s - Location: %d%n", getValue(), getLocation());
	}
}