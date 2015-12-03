import javax.swing.JButton;

public class Button extends JButton{
	private int location;// Variable to hold the position of the Button
	
	//Constructor
	public Button(String text, int location)
	{
		super(text);
		this.location = location;
	}
	
	//Constructor with text only, default position is -1
	public Button(String text)
	{
		this(text, -1);
	}
	
	//Setter
	public void setLocationValue(int location)
	{
		this.location = location;
	}
	
	//Getter
	public int getLocationValue()
	{
		return location;
	}
}