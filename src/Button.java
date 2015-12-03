import javax.swing.JButton;

public class Button extends JButton{
	private int location;
	
	public Button(String text, int location)
	{
		super(text);
		this.location = location;
	}
	
	public Button(String text)
	{
		this(text, -1);
	}
	
	public void setLocationValue(int location)
	{
		this.location = location;
	}
	
	public int getLocationValue()
	{
		return location;
	}
}