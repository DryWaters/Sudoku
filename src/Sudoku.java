import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Sudoku {

	private static final int N = 9;
	private JPanel pnlNumberSelector;
	private JPanel pnlPuzzle;
	private JFrame frmMainFrame;
	private JButton[] btnNumberSelectors = new JButton[9];
	private JButton[] btnNumbers = new JButton[81];
	private JButton btnSelection;	
	private ImageIcon[] buttonImages = new ImageIcon[10];
	private ImageIcon[] buttonRolloverImages = new ImageIcon[10];
	private ImageIcon[] buttonPressedImages = new ImageIcon[10];
	private ImageIcon[] buttonDisabledImages = new ImageIcon[10];
	
	private int[][] puzzle = new int[9][9];
	
	
	public static void main(String[] args) throws IOException {
		Sudoku window = new Sudoku();
		window.frmMainFrame.setVisible(true);
	}

	public Sudoku() throws IOException {
		
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
			setButton(btnNumberSelectors[i], i + 1);
			pnlNumberSelector.add(btnNumberSelectors[i]);
		}
		
		return pnlNumberSelector;
	}
	
	private JPanel createPuzzle() throws IOException 
	{
		// String btnLabels[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
		JPanel pnlPuzzle = new JPanel();
				pnlPuzzle.setLayout(new GridLayout(9,9));
		pnlPuzzle.setBounds(200, 100, 400, 400);
		
		puzzle = readFile();
		
		String temp = "";
		
		int counter = 0;
		
		for(int i = 0; i < 9; i++)
		{
			
			for(int j = 0; j < 9; j++)
			{
				btnNumbers[counter] = new JButton(temp+puzzle[i][j]);
				btnNumbers[counter].addActionListener(new CheckButton());
				if (!btnNumbers[counter].getText().equalsIgnoreCase("0"))
				{
					btnNumbers[counter].setEnabled(false);
				}
				setButton(btnNumbers[counter], puzzle[i][j], counter);
				pnlPuzzle.add(btnNumbers[counter++]);
				temp = "";
				
			}
		}
		
	
		return pnlPuzzle;
		
	}
	
	private JButton setButton(JButton button, int value, int location)
	{
		final String[] buttonImageList = {"./resources/Button0.png", "./resources/Button1.png", "./resources/Button2.png", 
				"./resources/Button3.png", "./resources/Button4.png", "./resources/Button5.png", "./resources/Button6.png",
				 "./resources/Button7.png", "./resources/Button8.png", "./resources/Button9.png"};
			for (int i = 0; i < buttonImageList.length; i++)
				buttonImages[i] = new ImageIcon(buttonImageList[i]);
		
			final String[] buttonRolloverImageList = {"./resources/Button0Rollover.png", "./resources/Button1Rollover.png", "./resources/Button2Rollover.png", "./resources/Button3Rollover.png",
				"./resources/Button4Rollover.png", "./resources/Button5Rollover.png", "./resources/Button6Rollover.png", "./resources/Button7Rollover.png",
				"./resources/Button8Rollover.png", "./resources/Button9Rollover.png"};
			for (int i = 0; i < buttonRolloverImageList.length; i++)
				buttonRolloverImages[i] = new ImageIcon(buttonRolloverImageList[i]);
		
			final String[] buttonPressedImageList = {"./resources/Button0Pressed.png", "./resources/Button1Pressed.png", "./resources/Button2Pressed.png", "./resources/Button3Pressed.png",
				"./resources/Button4Pressed.png", "./resources/Button5Pressed.png", "./resources/Button6Pressed.png", "./resources/Button7Pressed.png",
				"./resources/Button8Pressed.png", "./resources/Button9Pressed.png"};
			for (int i = 0; i < buttonPressedImageList.length; i++)
				buttonPressedImages[i] = new ImageIcon(buttonPressedImageList[i]);
			
			final String[] buttonDisabledImageList = {"./resources/Button1Disabled.png", "./resources/Button1Disabled.png", "./resources/Button2Disabled.png", "./resources/Button3Disabled.png",
					"./resources/Button4Disabled.png", "./resources/Button5Disabled.png", "./resources/Button6Disabled.png", "./resources/Button7Disabled.png",
					"./resources/Button8Disabled.png", "./resources/Button9Disabled.png"};
				for (int i = 0; i < buttonPressedImageList.length; i++)
					buttonDisabledImages[i] = new ImageIcon(buttonDisabledImageList[i]);
					
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
		
		
		button.setIcon(buttonImages[value%10]);
		button.setRolloverIcon(buttonRolloverImages[value%10]);
		button.setPressedIcon(buttonPressedImages[value%10]);
		button.setDisabledIcon(buttonDisabledImages[value%10]);
		
		button.setRolloverEnabled(true);
		
				
		
		return button;
	}
	
	private JButton setButton(JButton button, int value)
	{
		final String[] buttonImageList = {"./resources/Button0.png", "./resources/Button1.png", "./resources/Button2.png", 
				"./resources/Button3.png", "./resources/Button4.png", "./resources/Button5.png", "./resources/Button6.png",
				 "./resources/Button7.png", "./resources/Button8.png", "./resources/Button9.png"};
			for (int i = 0; i < buttonImageList.length; i++)
				buttonImages[i] = new ImageIcon(buttonImageList[i]);
		
			final String[] buttonRolloverImageList = {"./resources/Button0Rollover.png", "./resources/Button1Rollover.png", "./resources/Button2Rollover.png", "./resources/Button3Rollover.png",
				"./resources/Button4Rollover.png", "./resources/Button5Rollover.png", "./resources/Button6Rollover.png", "./resources/Button7Rollover.png",
				"./resources/Button8Rollover.png", "./resources/Button9Rollover.png"};
			for (int i = 0; i < buttonRolloverImageList.length; i++)
				buttonRolloverImages[i] = new ImageIcon(buttonRolloverImageList[i]);
		
			final String[] buttonPressedImageList = {"./resources/Button0Pressed.png", "./resources/Button1Pressed.png", "./resources/Button2Pressed.png", "./resources/Button3Pressed.png",
				"./resources/Button4Pressed.png", "./resources/Button5Pressed.png", "./resources/Button6Pressed.png", "./resources/Button7Pressed.png",
				"./resources/Button8Pressed.png", "./resources/Button9Pressed.png"};
			for (int i = 0; i < buttonPressedImageList.length; i++)
				buttonPressedImages[i] = new ImageIcon(buttonPressedImageList[i]);
		
			final String[] puzzleBorders = 
				{"0,0,0,0", "5,5,1,1", "5,1,1,1", "5,1,1,5", "1,5,1,1", "1,1,1,1", 
					"1,1,1,5", "1,5,5,1", "1,1,5,1", "1,1,5,5" 	
					};
			
			
			String[] border = puzzleBorders[value].split(",");
			button.setBorder(new MatteBorder(Integer.parseInt(border[0]), 
					Integer.parseInt(border[1]), Integer.parseInt(border[2]), 
					Integer.parseInt(border[3]), Color.BLACK));
			
			
			
		button.setIcon(buttonImages[value%10]);
		button.setRolloverIcon(buttonRolloverImages[value%10]);
		button.setPressedIcon(buttonPressedImages[value%10]);
		
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
					btnSelection.setRolloverIcon(buttonRolloverImages[Integer.parseInt(e.getActionCommand())]);
					btnSelection.setIcon(buttonImages[Integer.parseInt(e.getActionCommand())]);
					btnSelection.setPressedIcon(buttonPressedImages[Integer.parseInt(e.getActionCommand())]);
					btnSelection.setText(e.getActionCommand());
					
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
				btnSelection.setIcon(buttonRolloverImages[Integer.parseInt(e.getActionCommand())]);
				pnlNumberSelector.setVisible(true);
			}			
			
		}
	}
	
	public static int[][] readFile() throws IOException 
	{
		FileReader file = new FileReader("puzzle.txt");
		BufferedReader reader = new BufferedReader(file);
		
		int[][] table = new int[11][11];
		String[] parts = new String[11];
		String[] line = new String[11];
			
		for (int row = 0; row < N; row++)
		{
			line[row] = reader.readLine();
		}
		
		for (int row = 0; row < N;row++)
			for (int col = 0; col < N; col++)
			{
				parts = line[row].split(",");
				table[row][col] = Integer.parseInt(parts[col]);
			}		
			
		file.close();
			
		return (table);

	}
	
	
}





