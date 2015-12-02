import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;


import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;

public class Sudoku {

	private static final int N = 9;
	
	private JFrame frmMainFrame;
	
	private JPanel pnlNumberSelector;
	private JPanel pnlPuzzle;
	private JPanel pnlMain;
	private JPanel pnlInfo;
	private JPanel pnlPlayButtonNavigation;
	private JPanel pnlCreateButtonNavigation;
	private JPanel pnlClear;
	
	private JButton[] btnNumberSelectors = new JButton[9];
	private JButton[] btnNumbers = new JButton[81];
	private JButton btnSelection;	
	
	private ImageIcon[] buttonImages = new ImageIcon[10];
	private ImageIcon[] buttonRolloverImages = new ImageIcon[10];
	private ImageIcon[] buttonPressedImages = new ImageIcon[10];
	private ImageIcon[] buttonDisabledImages = new ImageIcon[10];
	private ImageIcon[] buttonErrorImages = new ImageIcon[10];
	
	private int[][] puzzle = new int[9][9];
	private int[][] blankPuzzle = new int[9][9];
	private int[] disabledLocations = new int[81];
	private int selectedRow;
	private int selectedColumn;
	
	private boolean isNewPuzzle = false;
	private boolean puzzleDone = false;
	private boolean hasErrors = false;
	
	private Generator puzzleGenerator;
	private SudokuSolver sudokuSolver;
	
	
	public static void main(String[] args) throws IOException {
		Sudoku window = new Sudoku();
		window.frmMainFrame.setVisible(true);		
	}

	public Sudoku() throws IOException {
		
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				blankPuzzle[i][j]=0;
		
		writeBlankPuzzle();
		
		frmMainFrame = new JFrame("Sudoku");
		frmMainFrame.setResizable(false);
		frmMainFrame.setBounds(100, 100, 900, 600);
		frmMainFrame.setLayout(null);
		frmMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BufferedImage backgroundImage = ImageIO.read(new File("./resources/BackgroundSmallLogo.png"));
		frmMainFrame.setContentPane(new JLabel(new ImageIcon(backgroundImage)));
		
		pnlNumberSelector = createNumberSelector();
		pnlNumberSelector.setVisible(false);

		try {
			pnlMain = createMainMenu();
			pnlMain.setVisible(true);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		pnlPlayButtonNavigation = createPlayButtonNavigation();
		pnlPlayButtonNavigation.setVisible(false);
		pnlCreateButtonNavigation = createButtonNavigation();
		pnlCreateButtonNavigation.setVisible(false);
		pnlClear = createClearPanel();
		pnlClear.setVisible(false);
				
		pnlInfo = createInfoPanel();
		pnlInfo.setVisible(false);
		
		
		
		frmMainFrame.add(pnlNumberSelector);	
		frmMainFrame.add(pnlMain);
		frmMainFrame.add(pnlInfo);
		frmMainFrame.add(pnlPlayButtonNavigation);
		frmMainFrame.add(pnlCreateButtonNavigation);
		frmMainFrame.add(pnlClear);
	}
	
	private JPanel createClearPanel() {
	
		JPanel pnlClearButton = new JPanel();
		JButton btnClearButton = new JButton("Clear");
						
		pnlClearButton.setLayout(null);
		pnlClearButton.setBounds(725, 375, 75, 30);
		btnClearButton.setBounds(0,0,75,30);
		btnClearButton.addActionListener(new ClearSelection());
		
		
		pnlClearButton.add(btnClearButton);
		
		return pnlClearButton;
	}

	private JPanel createButtonNavigation() {
				
		JPanel pnlCreateButtonNavigation = new JPanel();
		
		JButton btnGoBack = new JButton("To Main");
		JButton btnGenerate = new JButton ("Generate");
		btnGenerate.setBounds(75, 250, 100, 50);
		btnGoBack.setBounds(75, 150, 100, 50);
		
		btnGenerate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					puzzleGenerator = new Generator();
					puzzleGenerator.generate();
					puzzle = readFile(1);
					frmMainFrame.remove(pnlPuzzle);
					pnlPuzzle = createPuzzle();
					frmMainFrame.add(pnlPuzzle);
					pnlPuzzle.revalidate();
					hasErrors = false;
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				
				
			}
			
		});
		
		
		btnGoBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (hasErrors)
					{
						int choice = 0;
						choice = JOptionPane.showConfirmDialog(null,  "There are errors.  Are you sure you want to create this puzzle?", "Errors", JOptionPane.YES_NO_OPTION);
						if (choice == 0)
						{
							writePuzzle();
							puzzle=readFile(1);
							checkDisabledLocations();
							pnlPuzzle.setVisible(false);
							pnlCreateButtonNavigation.setVisible(false);
							pnlNumberSelector.setVisible(false);
							pnlClear.setVisible(false);
							btnSelection=null;
							pnlMain.setVisible(true);
						}

					}
					else {
						writePuzzle();
						puzzle=readFile(1);
						checkDisabledLocations();
						pnlPuzzle.setVisible(false);
						pnlCreateButtonNavigation.setVisible(false);
						pnlNumberSelector.setVisible(false);
						pnlClear.setVisible(false);
						btnSelection=null;
						pnlMain.setVisible(true);
					}
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}


				
			}
			
		});
		
		
		
		
		
		pnlCreateButtonNavigation.setBackground(Color.WHITE);
				
		pnlCreateButtonNavigation.setLayout(null);
		pnlCreateButtonNavigation.setBounds(0, 0, 200, 600);
		
		pnlCreateButtonNavigation.add(btnGoBack);
		pnlCreateButtonNavigation.add(btnGenerate);
		
		return pnlCreateButtonNavigation;
	}
	
	
	private JPanel createPlayButtonNavigation() {
		
		JPanel pnlButtonNavigation = new JPanel();
		
		JButton btnGoBack = new JButton("Main");
		JButton btnSolve = new JButton("Solve");
		btnSolve.setBounds(75, 250, 100, 50);
		btnSolve.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sudokuSolver = new SudokuSolver();
				int lastK = sudokuSolver.findLastK(puzzle);
				try {
					if (countEmpty() <= 60)
					{
						sudokuSolver.test(0, puzzle, lastK);
						puzzle = readFile(3);
						frmMainFrame.remove(pnlPuzzle);
						pnlPuzzle = createPuzzle();
						frmMainFrame.add(pnlPuzzle);
						frmMainFrame.revalidate();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Too many empty squares to solve!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					

				} catch (IOException e) {
					e.printStackTrace();
				}

				
				
				
			}
			
			
			
		});
		
		
		btnGoBack.setBounds(75, 150, 100, 50);
		btnGoBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				

				
				try {
					if(puzzleDone)
					{
						writeBlankPuzzle();
						puzzle=readFile(1);
						checkDisabledLocations();						
						isNewPuzzle=false;
					}
					else
						writeUserPuzzle();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				pnlPuzzle.setVisible(false);
				pnlButtonNavigation.setVisible(false);
				pnlNumberSelector.setVisible(false);
				pnlClear.setVisible(false);
				btnSelection=null;

				pnlMain.setVisible(true);
				
			}

		});
		
		
		pnlButtonNavigation.setBackground(Color.WHITE);
				
		pnlButtonNavigation.setLayout(null);
		pnlButtonNavigation.setBounds(0, 0, 200, 600);
		
		pnlButtonNavigation.add(btnGoBack);
		pnlButtonNavigation.add(btnSolve);
		
		return pnlButtonNavigation;
	}

	private JPanel createInfoPanel()
	{
		String infoText = "<html><center><h1>Coding</h1><h3>Sang Tan Le</h3><h1>GUI</h1><h3>Daniel Waters</h3>"
				+ "<h1>Music</h1><h3>longzijun<br>https://longzijun.wordpress.com/</center></html>";
		
		JPanel pnlInfo = new JPanel();
		JButton btnReturn = new JButton("Return");
		JTextPane txtCredits = new JTextPane();
		txtCredits.setBounds(90, 150, 700, 350);
		txtCredits.setContentType("text/html");
		txtCredits.setText(infoText);
		
				
		pnlInfo.setLayout(null);
		pnlInfo.setBounds(0, 0, 900, 600);
		
		ImageIcon smallBackgroundIcon = new ImageIcon("./resources/BackgroundSmallLogo.png");
		
		JLabel background = new JLabel();
		background.setIcon(smallBackgroundIcon);
		background.setBounds(0, 0, 900, 600);
		
				
		ImageIcon returnImage = new ImageIcon("./resources/Return.png");
		
		
		btnReturn.setBounds(410, 500, 60, 60);
		btnReturn.setIcon(returnImage);
		
		btnReturn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pnlInfo.setVisible(false);
				pnlMain.setVisible(true);
			}
		});
		
		pnlInfo.add(btnReturn);		
		pnlInfo.add(txtCredits);
		pnlInfo.add(background);
		
		return pnlInfo;
		
	}
	
	private JPanel createNumberSelector()
	{
		JPanel pnlNumberSelector = new JPanel();

			
		pnlNumberSelector.setLayout(new GridLayout(3,3));
		pnlNumberSelector.setBounds(700, 240, 125, 125);
		
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
	
	private JPanel createMainMenu() throws UnsupportedAudioFileException 
	{

		
        try {
        	AudioInputStream audioBackgroundStream = AudioSystem.getAudioInputStream(new File("./resources/backgroundMusic.wav"));			
    		Clip clipBackgroundMusic = AudioSystem.getClip( );
			clipBackgroundMusic.open(audioBackgroundStream);
	        clipBackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
	        clipBackgroundMusic.start( );
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ImageIcon backgroundIcon = new ImageIcon("./resources/MainBackground.png");

		
		JLabel background = new JLabel();
		background.setIcon(backgroundIcon);
		background.setBounds(0, 0, 900, 600);
		       
        
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(null);
		pnlMain.setBounds(0, 0, 900, 600);
		
		
				
		JButton btnCreatePuzzle = new JButton("Create Puzzle");
		JButton btnPlayPuzzle = new JButton("Play Puzzle");
		
		
		JButton btnInfoSelect = new JButton("Info");
		ImageIcon infoImage = new ImageIcon("./resources/Info.png");
		btnInfoSelect.setIcon(infoImage);
		
		

	
		btnCreatePuzzle.setBounds(375, 325, 150, 40);
		btnPlayPuzzle.setBounds(375, 400, 150, 40);
		btnInfoSelect.setBounds(600, 500, 33, 35);
		btnInfoSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlMain.setVisible(false);
				pnlInfo.setVisible(true);
			}
		});
		
		btnPlayPuzzle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlMain.setVisible(false);
				pnlPlayButtonNavigation.setVisible(true);
				
					if (pnlPuzzle!= null)
					{
						frmMainFrame.remove(pnlPuzzle);

					}	
					
					try {
						checkFile();
						

						pnlPuzzle = createPuzzle();
						frmMainFrame.add(pnlPuzzle);
						pnlPuzzle.setVisible(true);
						frmMainFrame.revalidate();
						isNewPuzzle = true;
						
					} catch (IOException e1) {
					
						e1.printStackTrace();
					
					}

					

			}
		});
		
		btnCreatePuzzle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlMain.setVisible(false);
				pnlCreateButtonNavigation.setVisible(true);
				try {
					writeBlankPuzzle();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				if (pnlPuzzle!= null)
				{
					frmMainFrame.remove(pnlPuzzle);
					
				}	
				
				try {
					
					puzzle = readFile(1);
					checkDisabledLocations();
					pnlPuzzle = createPuzzle();
					frmMainFrame.add(pnlPuzzle);
					pnlPuzzle.setVisible(true);
					frmMainFrame.revalidate();
					isNewPuzzle = false;
					puzzleDone = false;
					hasErrors = false;
						
				} catch (IOException e1) {
					
						e1.printStackTrace();
					
					}

					

			}
		});	
		
		
		

			
		
		pnlMain.add(btnCreatePuzzle);
		pnlMain.add(btnPlayPuzzle);
		pnlMain.add(btnInfoSelect);
		pnlMain.add(background);

		
				
		
	
		return pnlMain;
		
	}
	

	private void checkFile() throws IOException
	{
		int choice;
		File tempFile = new File("editedPuzzle.txt");
		
		if(tempFile.exists()&&(isNewPuzzle))
		{
			choice = JOptionPane.showConfirmDialog(null,  "Would you like to load previous changes?", "Load", JOptionPane.YES_NO_OPTION);
			puzzle = readFile(choice);
		}
		else
			puzzle = readFile(1);
		
		checkDisabledLocations();
		
	}
	
	
	private JPanel createPuzzle() throws IOException 
	{
			
		
		JPanel pnlPuzzle = new JPanel();
		pnlPuzzle.setLayout(new GridLayout(9,9));
		pnlPuzzle.setBounds(230, 100, 400, 400);
		
		String temp = "";
		
		int counter = 0;
		
		for(int i = 0; i < 9; i++)
		{
			
			for(int j = 0; j < 9; j++)
			{
				btnNumbers[counter] = new JButton(temp+puzzle[i][j]);
				btnNumbers[counter].addActionListener(new CheckButton());
				if (disabledLocations[counter]!=1)
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
		
		final String[] buttonErrorImageList = {"./resources/Button0Error.png", "./resources/Button1Error.png", "./resources/Button2Error.png", "./resources/Button3Error.png",
				"./resources/Button4Error.png", "./resources/Button5Error.png", "./resources/Button6Error.png", "./resources/Button7Error.png",
				"./resources/Button8Error.png", "./resources/Button9Error.png"};
			for (int i = 0; i < buttonErrorImageList.length; i++)
				buttonErrorImages[i] = new ImageIcon(buttonErrorImageList[i]);
		
		
		
		
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
	
	public class ClearSelection implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			pnlNumberSelector.setVisible(false);
			pnlClear.setVisible(false);
					
			btnSelection.setRolloverIcon(buttonRolloverImages[0]);
			btnSelection.setIcon(buttonImages[0]);
			btnSelection.setPressedIcon(buttonPressedImages[0]);
			btnSelection.setText("0");
			puzzle[selectedRow][selectedColumn]=0;

								
			btnSelection.setRolloverEnabled(true);
			btnSelection.setEnabled(false);
			btnSelection.setEnabled(true);
			btnSelection = null;
		
		}		
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
					pnlClear.setVisible(false);
					
					if (isValid(Integer.parseInt(e.getActionCommand()), selectedRow, selectedColumn))
					{
						btnSelection.setRolloverIcon(buttonRolloverImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setIcon(buttonImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setPressedIcon(buttonPressedImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setText(e.getActionCommand());
						puzzle[selectedRow][selectedColumn]=Integer.parseInt(e.getActionCommand());
						if(isDone())
						{
							victoryDance();
							btnSelection = null;
							
						}
							
						else
						{
							btnSelection.setRolloverEnabled(true);
							btnSelection.setEnabled(false);
							btnSelection.setEnabled(true);
							btnSelection = null;
						}
						
					} 
					else
					{
						btnSelection.setRolloverIcon(buttonErrorImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setIcon(buttonErrorImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setPressedIcon(buttonErrorImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setText(e.getActionCommand());
						puzzle[selectedRow][selectedColumn]=Integer.parseInt(e.getActionCommand());
						btnSelection.setRolloverEnabled(true);
						btnSelection.setEnabled(false);
						btnSelection.setEnabled(true);
						btnSelection = null;
					}
						
					
					
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
				pnlClear.setVisible(true);
				
				for (int i = 0; i < btnNumbers.length; i++)
					if (btnSelection==btnNumbers[i])
					{
						selectedRow = i/9;
						selectedColumn = i%9;
					}
				
			}			
			
		}
	}
	
	private int[][] readFile(int choice) throws IOException 
	{
		FileReader file;
				
		if (choice == 0)
			file = new FileReader("editedPuzzle.txt");
		else if (choice == 1)
			file = new FileReader("puzzle.txt");
		else
			file = new FileReader("answer.txt");
		
		BufferedReader reader = new BufferedReader(file);
		
		int[][] table = new int[11][11];
		String[] parts = new String[11];
		String[] line = new String[11];
			
		for (int row = 0; row < 9; row++)
		{
			line[row] = reader.readLine();
		}
		
		for (int row = 0; row < 9;row++)
			for (int col = 0; col < 9; col++)
			{
				parts = line[row].split(",");
				table[row][col] = Integer.parseInt(parts[col]);
			}		
			
		file.close();
		
		return (table);

	}
	
    private void writeUserPuzzle() throws IOException, UnsupportedEncodingException
    {
    	PrintWriter writer = new PrintWriter("editedPuzzle.txt", "UTF-8");
    				
    	for(int row = 0; row<9; ++row)
    	{
    		for(int col = 0; col<9; ++col)
    			writer.print( (col == 8 ? puzzle[row][col] : puzzle[row][col] + ",") );
    			writer.println();	
    	}
    		
    	writer.close();
    }
    
    private void writePuzzle() throws IOException, UnsupportedEncodingException
    {
    	PrintWriter writer = new PrintWriter("puzzle.txt", "UTF-8");
    				
    	for(int row = 0; row<9; ++row)
    	{
    		for(int col = 0; col<9; ++col)
    			writer.print( (col == 8 ? puzzle[row][col] : puzzle[row][col] + ",") );
    			writer.println();	
    	}
    		
    	writer.close();
    }
    
    
    private void writeBlankPuzzle() throws IOException, UnsupportedEncodingException
    {
    	PrintWriter writer = new PrintWriter("puzzle.txt", "UTF-8");
    				
    	for(int row = 0; row<9; ++row)
    	{
    		for(int col = 0; col<9; ++col)
    			writer.print( (col == 8 ? blankPuzzle[row][col] : blankPuzzle[row][col] + ",") );
    			writer.println();	
    	}
    		
    	writer.close();
    }
    
    private void checkDisabledLocations() throws IOException
    {
    	FileReader file = new FileReader("puzzle.txt");
		
		BufferedReader reader = new BufferedReader(file);
		
		int[][] table = new int[11][11];
		String[] parts = new String[11];
		String[] line = new String[11];
			
		for (int row = 0; row < 9; row++)
		{
			line[row] = reader.readLine();
		}
		
		for (int row = 0; row < 9;row++)
			for (int col = 0; col < 9; col++)
			{
				parts = line[row].split(",");
				table[row][col] = Integer.parseInt(parts[col]);
			}		
			

    	
		int counter = 0;
		for (int row = 0; row < 9;row++)
			for (int col = 0; col < 9; col++)
			{
				if (table[row][col] == 0)
					disabledLocations[counter] = 1;
				else
					disabledLocations[counter] = 0;
					
				counter++;
			}		
		
		reader.close();
    }
    
	private boolean isValid(int number, int row, int column) {

		//Check row 
		for (int i = 0; i<9; i++)
			if (puzzle[i][column] == number)
			{
				hasErrors = true;
				return false;
			}

		//Check column
		for (int i = 0; i<9; i++)
			if (puzzle[row][i] == number)
			{
				hasErrors = true;
				return false;
			}

		//Check small block 3x3
		int tmpX = row % 3; 
		int tmpY = column % 3;
		for (int k = row - tmpX; k <= row - tmpX + 2; k++)
			for (int t = column - tmpY; t <= column - tmpY + 2; t++)
				if (puzzle[k][t] == number)
				{
					hasErrors = true;
					return false;
				}
					
		hasErrors = false;
		return true;
	}
	
	private boolean isDone() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if(puzzle[i][j]==0)
					return false;
		
		return true;
		
	}
	

	private void victoryDance()  {
		
		Integer[] victoryNumbers = {10, 12, 15, 19, 21, 23, 25, 28, 30, 32, 34, 38, 41, 42, 43, 47, 50, 52, 56, 59, 61};
		ArrayList<Integer> victoryList = new ArrayList<Integer>(Arrays.asList(victoryNumbers));
		
		for (int i = 0; i < 81; i++)
		{

			if (victoryList.contains(i))
				btnNumbers[i].setDisabledIcon(buttonErrorImages[1]);
			else
				btnNumbers[i].setDisabledIcon(buttonImages[0]);
			
			btnNumbers[i].setEnabled(false);
			
		}

		puzzleDone = true;
	}
	
	private int countEmpty() {
		int counter = 0;
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (puzzle[i][j] == 0)					
					counter++;
		return counter;
	}
		
}





