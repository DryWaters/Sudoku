import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Sudoku {

	private JPanel pnlNumberSelector;
	private JPanel pnlPuzzle;
	private JFrame frmMainFrame;
	private JButton[] btnNumberSelectors = new JButton[9];
	private JButton[] btnNumbers = new JButton[81];
	private JButton btnSelection;	
	private ImageIcon[] buttonImages = new ImageIcon[9];
	private ImageIcon[] buttonRolloverImages = new ImageIcon[9];
	private ImageIcon[] buttonPressedImages = new ImageIcon[9];
	
	private SecureRandom randomizer = new SecureRandom();	
	
	public static void main(String[] args) {
		Sudoku window = new Sudoku();
		window.frmMainFrame.setVisible(true);
	}

	public Sudoku() {
		
		frmMainFrame = new JFrame();
		frmMainFrame.setBounds(100, 100, 900, 600);
		frmMainFrame.setLayout(null);
		frmMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pnlNumberSelector = createNumberSelector();
		pnlNumberSelector.setVisible(false);
		pnlPuzzle = createPuzzle();
		
		frmMainFrame.add(pnlNumberSelector);
		frmMainFrame.add(pnlPuzzle);
	
		}
	
	private JPanel createNumberSelector()
	{
		JPanel pnlNumberSelector = new JPanel();
		pnlNumberSelector.setLayout(new GridLayout(3,3));
		pnlNumberSelector.setBounds(700, 250, 125, 125);
		
		String btnLabels[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
				
		for (int i = 0; i < btnLabels.length; i++)
		{
			btnNumberSelectors[i] = new JButton(btnLabels[i]);
			btnNumberSelectors[i].addActionListener(new CheckSelection());
			setButton(btnNumberSelectors[i], i);
			pnlNumberSelector.add(btnNumberSelectors[i]);
		}
		
		return pnlNumberSelector;
	}
	
	private JPanel createPuzzle() 
	{
		String btnLabels[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
		JPanel pnlPuzzle = new JPanel();
				pnlPuzzle.setLayout(new GridLayout(9,9));
		pnlPuzzle.setBounds(200, 100, 400, 400);
		
		for(int i = 0; i < btnLabels.length*9; i++)
		{
			btnNumbers[i] = new JButton(btnLabels[i%9]);
			btnNumbers[i].addActionListener(new CheckButton());
			setButton(btnNumbers[i], i);
			if (randomizer.nextInt(3)==1)
			{
				btnNumbers[i].setEnabled(false);
			}
			pnlPuzzle.add(btnNumbers[i]);
			
		}
		
	
		return pnlPuzzle;
		
	}
	
	private JButton setButton(JButton button, int location)
	{
		final String[] buttonImageList = {"./resources/Button1.png", "./resources/Button2.png", "./resources/Button3.png",
				"./resources/Button4.png", "./resources/Button5.png", "./resources/Button6.png", "./resources/Button7.png",
				"./resources/Button8.png", "./resources/Button9.png"};
			for (int i = 0; i < buttonImageList.length; i++)
				buttonImages[i] = new ImageIcon(buttonImageList[i]);
		
			final String[] buttonRolloverImageList = {"./resources/Button1Rollover.png", "./resources/Button2Rollover.png", "./resources/Button3Rollover.png",
				"./resources/Button4Rollover.png", "./resources/Button5Rollover.png", "./resources/Button6Rollover.png", "./resources/Button7Rollover.png",
				"./resources/Button8Rollover.png", "./resources/Button9Rollover.png"};
			for (int i = 0; i < buttonRolloverImageList.length; i++)
				buttonRolloverImages[i] = new ImageIcon(buttonRolloverImageList[i]);
		
			final String[] buttonPressedImageList = {"./resources/Button1Pressed.png", "./resources/Button2Pressed.png", "./resources/Button3Pressed.png",
				"./resources/Button4Pressed.png", "./resources/Button5Pressed.png", "./resources/Button6Pressed.png", "./resources/Button7Pressed.png",
				"./resources/Button8Pressed.png", "./resources/Button9Pressed.png"};
			for (int i = 0; i < buttonPressedImageList.length; i++)
				buttonPressedImages[i] = new ImageIcon(buttonPressedImageList[i]);
					
			final String[] puzzleBorders = 
			{"3,3,1,1", "3,1,1,1", "3,1,1,3", "3,3,1,1", "3,1,1,1", 
				"3,1,1,3", "3,3,1,1", "3,1,1,1", "3,1,1,3",
			 "1,3,1,1", "1,1,1,1", "1,1,1,3", "1,3,1,1", "1,1,1,1",
			 	"1,1,1,3", "1,3,1,1", "1,1,1,1", "1,1,1,3",
			 "1,3,3,1", "1,1,3,1", "1,1,3,3", "1,3,3,1", "1,1,3,1",
			 	"1,1,3,3", "1,3,3,1", "1,1,3,1", "1,1,3,3",
			 "3,3,1,1", "3,1,1,1", "3,1,1,3", "3,3,1,1", "3,1,1,1",
			 	"3,1,1,3", "3,3,1,1", "3,1,1,1", "3,1,1,3",
			 "1,3,1,1", "1,1,1,1", "1,1,1,3", "1,3,1,1", "1,1,1,1",
			 	"1,1,1,3", "1,3,1,1", "1,1,1,1", "1,1,1,3",
			 "1,3,3,1", "1,1,3,1", "1,1,3,3", "1,3,3,1", "1,1,3,1",
			 	"1,1,3,3", "1,3,3,1", "1,1,3,1", "1,1,3,3",
			 "3,3,1,1", "3,1,1,1", "3,1,1,3", "3,3,1,1" ,"3,1,1,1",
			 	"3,1,1,3", "3,3,1,1", "3,1,1,1", "3,1,1,3",
		 	 "1,3,1,1", "1,1,1,1", "1,1,1,3", "1,3,1,1", "1,1,1,1",
		 	 	"1,1,1,3", "1,3,1,1", "1,1,1,1", "1,1,1,3",
		 	 "1,3,3,1", "1,1,3,1", "1,1,3,3", "1,3,3,1", "1,1,3,1",
		 	 	"1,1,3,3", "1,3,3,1", "1,1,3,1", "1,1,3,3"			 			
				};
		
		
		String[] border = puzzleBorders[location].split(",");
		button.setBorder(new MatteBorder(Integer.parseInt(border[0]), 
				Integer.parseInt(border[1]), Integer.parseInt(border[2]), 
				Integer.parseInt(border[3]), Color.BLACK));
		
		
		button.setIcon(buttonImages[location%9]);
		button.setRolloverIcon(buttonRolloverImages[location%9]);
		button.setPressedIcon(buttonPressedImages[location%9]);
		button.setRolloverEnabled(true);
		
				
		
		return button;
	}
	
	public class CheckSelection implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			for (int i = 0; i < btnNumberSelectors.length; i++)
			{
				if(e.getSource()== btnNumberSelectors[i]) 
				{
					pnlNumberSelector.setVisible(false);
					btnSelection.setRolloverIcon(buttonRolloverImages[Integer.parseInt(e.getActionCommand())-1]);
					btnSelection.setIcon(buttonImages[Integer.parseInt(e.getActionCommand())-1]);
					btnSelection.setPressedIcon(buttonPressedImages[Integer.parseInt(e.getActionCommand())-1]);
					
					btnSelection.setRolloverEnabled(true);
					btnSelection.setEnabled(false);
					btnSelection.setEnabled(true);
					btnSelection = null;
				}
			}		
		}
	}
	
	public class CheckButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			if ((btnSelection == null))
			{
				btnSelection = (JButton) e.getSource();
				btnSelection.setIcon(buttonRolloverImages[Integer.parseInt(e.getActionCommand())-1]);
				pnlNumberSelector.setVisible(true);
			}			
			
		}
	}
}





