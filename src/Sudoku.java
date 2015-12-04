import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
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
	
	//Reference variables
	private JFrame frmMainFrame;
	
	private JPanel pnlNumberSelector;
	private JPanel pnlPuzzle;
	private JPanel pnlMain;
	private JPanel pnlInfo;
	private JPanel pnlPlayButtonNavigation;
	private JPanel pnlCreateButtonNavigation;
	private JPanel pnlClear;
	
	private Button[] btnNumberSelectors = new Button[9];
	private Button[] btnNumbers = new Button[81];
	private Button btnSelection;	
	
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
	
	private String[] puzzleBorders = new String[81];
	private String[] selectionBorders = new String[81];	
	private String btnLabels[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	private boolean isNewPuzzle = false;
	private boolean puzzleDone = false;
	private boolean hasErrors = false;
	
	private Generator puzzleGenerator;
	private SudokuSolver sudokuSolver;
	private StackInterface<Move> myStack;
		
//	Main just creates frame and sets it to visible
	public static void main(String[] args) {
		Sudoku window = new Sudoku();
		window.frmMainFrame.setVisible(true);		
	}//end main

//	Constructor to setup the frame and load resources.
	public Sudoku() {
		
//		Initialize the blank puzzle to all 0's and write the file as a default.
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				blankPuzzle[i][j]=0;
		
//		Create the initial frame, panels with backgrounds, and set the background and play music.
		try {
			loadResources();
			writeBlankPuzzle();
			frmMainFrame = new JFrame("Sudoku");
			frmMainFrame.setResizable(false);
			frmMainFrame.setBounds(100, 100, 900, 600);
			frmMainFrame.setLayout(null);
			frmMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			BufferedImage backgroundImage = ImageIO.read(new File("./resources/BackgroundSmallLogo.png"));
			frmMainFrame.setContentPane(new JLabel(new ImageIcon(backgroundImage)));
			pnlMain = createMainMenu();
			pnlMain.setVisible(true);
		} catch (IOException | UnsupportedAudioFileException e) {

			e.printStackTrace();
		}
		
//		Create the remaining panels that do not need file reads.
		pnlInfo = createInfoPanel();
		pnlNumberSelector = createNumberSelector();
		pnlClear = createClearPanel();
		pnlPlayButtonNavigation = createPlayButtonNavigation();
		pnlCreateButtonNavigation = createButtonNavigation();
		
//		Hide all of the panels except the main menu.
		pnlInfo.setVisible(false);
		pnlNumberSelector.setVisible(false);		
		pnlClear.setVisible(false);
		pnlPlayButtonNavigation.setVisible(false);
		pnlCreateButtonNavigation.setVisible(false);
		
//		Add all the panels to the frame.
		frmMainFrame.add(pnlNumberSelector);	
		frmMainFrame.add(pnlMain);
		frmMainFrame.add(pnlInfo);
		frmMainFrame.add(pnlPlayButtonNavigation);
		frmMainFrame.add(pnlCreateButtonNavigation);
		frmMainFrame.add(pnlClear);
	}
	
//	Panel just created to hold the clear button when creating or playing a puzzle.
	private JPanel createClearPanel() {
	
//		Creates one button on the panel and adds the ActionListener class called ClearSelection()
		JPanel pnlClearButton = new JPanel();
		pnlClearButton.setLayout(null);
		pnlClearButton.setBounds(725, 375, 75, 30);
		
		JButton btnClearButton = new JButton("Clear");
		btnClearButton.setBounds(0,0,75,30);
		btnClearButton.addActionListener(new ClearSelection());						
			
		pnlClearButton.add(btnClearButton);
		
		return pnlClearButton;
	}

//	Panel to hold the options for a user that selects to create a new puzzle.
	private JPanel createButtonNavigation() {
				
//		Creates three buttons on the panel.  One to generate a random puzzle , one to undo previous choice, and one to return to main menu.
		JPanel pnlCreateButtonNavigation = new JPanel();
		pnlCreateButtonNavigation.setBackground(Color.WHITE);
		pnlCreateButtonNavigation.setLayout(null);
		pnlCreateButtonNavigation.setBounds(0, 0, 200, 600);
		
		JButton btnGoBack = new JButton("To Main");
		btnGoBack.setBounds(75, 150, 100, 50);
		
		JButton btnGenerate = new JButton ("Generate");
		btnGenerate.setBounds(75, 250, 100, 50);
		
		JButton btnUndo = new JButton("Undo");
		btnUndo.setBounds(75, 350, 100, 50);
		btnUndo.addActionListener(new Undo());
		
		pnlCreateButtonNavigation.add(btnGoBack);
		pnlCreateButtonNavigation.add(btnGenerate);		
		pnlCreateButtonNavigation.add(btnUndo);
		
//		Actions for Generate Puzzle:  Creates a new Generator object and runs the method to write a random puzzle to puzzle.txt,
//		removes the previous puzzle panel from the frame, and then creates a new panel with the new file, also automatically 
//		sets the errors to false because we are loading pre-generated puzzles.
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
					playSound("loadPuzzle");
					pnlPuzzle.revalidate();
					hasErrors = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
//		Actions for return to Main Menu:  Checks if there are errors with the puzzle the user created. If so, they are prompted if they are sure they want to play.
//		It also sets the visibility for the panels to:  pnlPuzzle - False, pnlNumberSelector - False, pnlClear - False, pnlMain - True.
//		It also clears the pointer to their selection, if they click to go back to the main menu before selecting a number.
		btnGoBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
//					If the puzzle has reported problems then prompt the user.
					if (hasErrors)
					{
//						Choice 0 = "YES" play puzzle
//						Choice 1 = "NO" messed up puzzle and want to return
						int choice = 0;
						choice = JOptionPane.showConfirmDialog(frmMainFrame,  "There are errors.  Are you sure you want to create this puzzle?", "Errors", JOptionPane.YES_NO_OPTION);
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
						else
						{
						// Do nothing
						}
					}
//					If no errors, then just writes their puzzle and shows main menu panel.
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
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
				
		return pnlCreateButtonNavigation;
	}
	
//	Panel to hold the options for a user that selects to play a puzzle.
	private JPanel createPlayButtonNavigation() {
		
//		Creates three buttons on the panel.  One to solve a puzzle, one to undo the previous choice, and one to return to main menu.
		JPanel pnlButtonNavigation = new JPanel();
		pnlButtonNavigation.setBackground(Color.WHITE);
		pnlButtonNavigation.setLayout(null);
		pnlButtonNavigation.setBounds(0, 0, 200, 600);
		
		JButton btnUndo = new JButton("Undo");
		btnUndo.setBounds(75, 350, 100, 50);
		btnUndo.addActionListener(new Undo());
		
		JButton btnGoBack = new JButton("Main");
		btnGoBack.setBounds(75, 150, 100, 50);
		
		JButton btnSolve = new JButton("Solve");
		btnSolve.setBounds(75, 250, 100, 50);
			
		pnlButtonNavigation.add(btnGoBack);
		pnlButtonNavigation.add(btnSolve);
		pnlButtonNavigation.add(btnUndo);

		
//		Actions for Solve Puzzle:  Creates a new Solver object and runs the method (findLastK) to know when to stop the recursion
//		It then solves the puzzle with the method (test) and writes that to "answer.txt".  It then removes the previous puzzle and recreates a new panel
//		after reading in the answer from readFile(3)
		btnSolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sudokuSolver = new SudokuSolver();
				int lastK = sudokuSolver.findLastK(puzzle);
			
//				Will only try and solve a puzzle if there are less than 61 empty squares and there are no reported errors.
				try {
					if ((countEmpty() <= 60) && (!hasErrors))
					{
						sudokuSolver.test(0, puzzle, lastK);
						puzzle = readFile(3);
						frmMainFrame.remove(pnlPuzzle);
						pnlPuzzle = createPuzzle();
						frmMainFrame.add(pnlPuzzle);
						playSound("solvedPuzzle");
						frmMainFrame.revalidate();
					}
					else
					{
//						If the puzzle has users, it lets the user know to fix the problems.
						if (hasErrors)
						{
							JOptionPane.showMessageDialog(frmMainFrame, "Current puzzle has errors.  Please fix before using \"Solve\" button!", "Error", JOptionPane.ERROR_MESSAGE);
						} 
						else {
							JOptionPane.showMessageDialog(frmMainFrame, "Too many empty squares to solve!", "Error", JOptionPane.ERROR_MESSAGE);	
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
//		Actions for GoBack:  If the user returns and the puzzle is already completed, then clear and start over.
//		If it is not solved, then save the user's current progress to "editedPuzzle.txt"
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
		
		return pnlButtonNavigation;
	}

//	Create panel to show who did the coding and then music that is used.  Creates one button for returning back.
	private JPanel createInfoPanel()
	{
		JPanel pnlInfo = new JPanel();
		pnlInfo.setLayout(null);
		pnlInfo.setBounds(0, 0, 900, 600);

//		Use return image icon for the button to return to main menu
		JButton btnReturn = new JButton("Return");
		ImageIcon returnImage = new ImageIcon("./resources/Return.png");
		btnReturn.setBounds(410, 470, 60, 60);
		btnReturn.setIcon(returnImage);
		
		String infoText = "<html><center><h1>Coding</h1><h3>Sang Tan Le</h3><h1>GUI</h1><h3>Daniel Waters</h3>"
				+ "<h1>Music</h1><h3>longzijun<br>https://longzijun.wordpress.com/</center></html>";
		JTextPane txtCredits = new JTextPane();
		txtCredits.setBounds(90, 120, 700, 340);
		txtCredits.setContentType("text/html");
		txtCredits.setText(infoText);
					
//		Use a JLabel to cover the entire panel with a background image.
		ImageIcon smallBackgroundIcon = new ImageIcon("./resources/BackgroundSmallLogo.png");
		JLabel background = new JLabel();
		background.setIcon(smallBackgroundIcon);
		background.setBounds(0, 0, 900, 600);
		
		pnlInfo.add(btnReturn);		
		pnlInfo.add(txtCredits);
		pnlInfo.add(background);
				
//		Return to main menu action.  Just sets the info panel visibility to false and the main menu to true.
		btnReturn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pnlInfo.setVisible(false);
				pnlMain.setVisible(true);
			}
		});
		
		return pnlInfo;
	}
	
//	Create the panel to hold the numbers for selection purposes
	private JPanel createNumberSelector()
	{
		JPanel pnlNumberSelector = new JPanel();
		pnlNumberSelector.setLayout(new GridLayout(3,3));
		pnlNumberSelector.setBounds(700, 240, 125, 125);
		
//		Assigns the images based on the location in the array btnLabels
//		Also assigns an ActionListener class "CheckSelection()" to each button.
		for (int i = 0; i < btnLabels.length; i++)
		{
			btnNumberSelectors[i] = new Button(btnLabels[i]);
			btnNumberSelectors[i].addActionListener(new CheckSelection());
			setButton(btnNumberSelectors[i], i + 1);
			pnlNumberSelector.add(btnNumberSelectors[i]);
		}
			
		return pnlNumberSelector;
	}
	
//	Create the panel for the Main Menu	
	private JPanel createMainMenu() throws UnsupportedAudioFileException 
	{
//		Play the background music
        try {
        	AudioInputStream audioBackgroundStream = AudioSystem.getAudioInputStream(new File("./resources/backgroundMusic.wav"));			
    		Clip clipBackgroundMusic = AudioSystem.getClip( );
			clipBackgroundMusic.open(audioBackgroundStream);
	        clipBackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
	        clipBackgroundMusic.start( );
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
		
		ImageIcon backgroundIcon = new ImageIcon("./resources/MainBackground.png");
		JLabel background = new JLabel();
		ImageIcon infoImage = new ImageIcon("./resources/Info.png");
		
		background.setIcon(backgroundIcon);
		background.setBounds(0, 0, 900, 600);
		         
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(null);
		pnlMain.setBounds(0, 0, 900, 600);
			
		JButton btnCreatePuzzle = new JButton("Create Puzzle");
		btnCreatePuzzle.setBounds(375, 325, 150, 40);
		JButton btnPlayPuzzle = new JButton("Play Puzzle");
		btnPlayPuzzle.setBounds(375, 400, 150, 40);
		JButton btnInfoSelect = new JButton("Info");
		btnInfoSelect.setIcon(infoImage);
		btnInfoSelect.setBounds(600, 500, 33, 35);
		
		pnlMain.add(btnCreatePuzzle);
		pnlMain.add(btnPlayPuzzle);
		pnlMain.add(btnInfoSelect);
		pnlMain.add(background);
				
//		Action for Info is to just change the visibility to Info panel to true and Main Menu to false
		btnInfoSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pnlMain.setVisible(false);
				pnlInfo.setVisible(true);
			}
		});
		
//		Actions for playing a puzzle is:  Remove the current panel if it exists and then if a previous worked puzzle exits, 
//		then prompt user if they want to play that one. checkFile().
//		Then create a new puzzle panel by reading in the file and calling createPuzzle().
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
		
//		Actions for creating a puzzle is:  always start with a new blank puzzle.  writeBlankPuzzle();  Then remove the previous panel and create a new one if it exists.
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
		
		return pnlMain;		
	}
	
//	Check if user has already started on a puzzle.  If it exits then prompt the user if they want to load it.
	private void checkFile() throws IOException
	{
		int choice;
		File tempFile = new File("editedPuzzle.txt");
		
		if(tempFile.exists()&&(isNewPuzzle))
		{
			choice = JOptionPane.showConfirmDialog(frmMainFrame,  "Would you like to load previous changes?", "Load", JOptionPane.YES_NO_OPTION);
			puzzle = readFile(choice);
		}
		else
			puzzle = readFile(1);
		
		checkDisabledLocations();	
	}
	
//	Create the puzzle panel.  Create a new stack to hold the undo
	private JPanel createPuzzle() throws IOException 
	{
			
		JPanel pnlPuzzle = new JPanel();
		pnlPuzzle.setLayout(new GridLayout(9,9));
		pnlPuzzle.setBounds(230, 100, 400, 400);
		
		String temp = "";
		myStack = new LinkedStack<Move>();
		
		int counter = 0;
		
//		Checks if the buttons already have information from the create puzzle, and disables those.
		for(int i = 0; i < 9; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				btnNumbers[counter] = new Button(temp+puzzle[i][j], counter);
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
	
//	Creates the borders and images for the puzzle buttons
	private JButton setButton(JButton button, int value, int location)
	{
		String[] border = puzzleBorders[location].split(",");
		button.setBorder(new MatteBorder(Integer.parseInt(border[0]), 
				Integer.parseInt(border[1]), Integer.parseInt(border[2]), 
				Integer.parseInt(border[3]), Color.BLACK));
		
//		Uses the 4 image arrays to assign the images.
		button.setIcon(buttonImages[value%10]);
		button.setRolloverIcon(buttonRolloverImages[value%10]);
		button.setPressedIcon(buttonPressedImages[value%10]);
		button.setDisabledIcon(buttonDisabledImages[value%10]);
		button.setRolloverEnabled(true);
		
		return button;
	}
	
//	Creates the borders and images for the selection panel buttons 
	private JButton setButton(JButton button, int value)
	{
		String[] border = selectionBorders[value].split(",");
		button.setBorder(new MatteBorder(Integer.parseInt(border[0]), 
				Integer.parseInt(border[1]), Integer.parseInt(border[2]), 
				Integer.parseInt(border[3]), Color.BLACK));
			

//		Uses only 3 image arrays to assign the images because selection buttons are never disabled.
		button.setIcon(buttonImages[value%10]);
		button.setRolloverIcon(buttonRolloverImages[value%10]);
		button.setPressedIcon(buttonPressedImages[value%10]);
		button.setRolloverEnabled(true);
		
		return button;
	}
	
//	ActionListener class that clears a selection.  It hides the number selector and clear button panels.
//	It clears the value and assigns the blank images.
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
			hasErrors = isValid(puzzle[selectedRow][selectedColumn]);

			btnSelection.setRolloverEnabled(true);
			btnSelection.setEnabled(false);
			btnSelection.setEnabled(true);
			btnSelection = null;
		}		
	}

//	ActionListener class to check what number the user selects from the number selection panel.
//	It hides the number selection panel and clear panel.
//	It also checks if the number is a valid selection and that the puzzle is done with isDone(); 
	public class CheckSelection implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {		

//			Locate which button the user clicks by comparing the ActionEvent to the list of buttons
			for (int i = 0; i < btnNumberSelectors.length; i++)
			{
				if(e.getSource()== btnNumberSelectors[i]) 
				{
					pnlNumberSelector.setVisible(false);
					pnlClear.setVisible(false);
					puzzle[selectedRow][selectedColumn] = 0;
					
//					If the user chooses a valid selection, then change the images to valid images.
					if (isValid(Integer.parseInt(e.getActionCommand())))
					{
						btnSelection.setRolloverIcon(buttonRolloverImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setIcon(buttonImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setPressedIcon(buttonPressedImages[Integer.parseInt(e.getActionCommand())]);
						btnSelection.setText(e.getActionCommand());
						puzzle[selectedRow][selectedColumn]=Integer.parseInt(e.getActionCommand());

//						If it is valid then also check if puzzle is done.
						if(isDone())
						{
							victoryDance();
							btnSelection = null;
						}

//						If not done, then just assign the value.
						else
						{
							btnSelection.setRolloverEnabled(true);
							btnSelection.setEnabled(false);
							btnSelection.setEnabled(true);
							btnSelection = null;
						}
					} 

//					If the user chooses a non valid number then use the error images. 
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
	
//	Find out which button the user clicks on.  Also pushes the move onto the Undo stack.
	public class CheckButton implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

//			Only select a new button if the previous button already has a value.
			if ((btnSelection == null))
			{
				btnSelection = (Button) e.getSource();
				btnSelection.setIcon(buttonRolloverImages[Integer.parseInt(e.getActionCommand())]);
				pnlNumberSelector.setVisible(true);
				pnlClear.setVisible(true);
				
				for (int i = 0; i < btnNumbers.length; i++)
					if (btnSelection==btnNumbers[i])
					{
						selectedRow = i/9;
						selectedColumn = i%9;
					}
				
//				Pushes the move onto the Undo stack with the location and value.
				myStack.push(new Move(""+puzzle[selectedRow][selectedColumn], btnSelection.getLocationValue()));
			}			
		}
	}
	
//	Read method to load from the files the puzzle being worked on.
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
	
//	Write the user edited puzzle to a file called "editedPuzzle.txt"
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
    
//	Write the user created puzzle to a file called "puzzle.txt"
    private void writePuzzle() throws IOException, UnsupportedEncodingException
    {
    	PrintWriter writer = new PrintWriter("puzzle.txt", "UTF-8");
    				
    	for(int row = 0; row<9; ++row)
    	{
    		for(int col = 0; col<9; ++col)
    		{
    			writer.print( (col == 8 ? puzzle[row][col] : puzzle[row][col] + ",") );
    		}
    		writer.println();
    	}
    	writer.close();
    }
    
//	Write a blank puzzle to the file "puzzle.txt"
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
    
    //Prevent player to click on original numbers
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
    
    //Check if you option is valid to put in or not.
	private boolean isValid(int number) {

		//Check row 
		for (int i = 0; i<9; i++)
			if (puzzle[selectedRow][i] == number)
			{
				hasErrors = true;
				return false;
			}

		//Check column
		for (int i = 0; i<9; i++)
			if (puzzle[i][selectedColumn] == number)
			{
				hasErrors = true;
				return false;
			}

		//Check small block 3x3
		int tmpX = selectedRow % 3; 
		int tmpY = selectedColumn % 3;
		for (int k = selectedRow - tmpX; k <= selectedRow - tmpX + 2; k++)
			for (int t = selectedColumn - tmpY; t <= selectedColumn - tmpY + 2; t++)
				if (puzzle[k][t] == number)
				{
					hasErrors = true;
					return false;
				}
					
		hasErrors = false;
		return true;
	}
	
	//Check if the player finish playing the puzzle
	private boolean isDone() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if(puzzle[i][j]==0)
					return false;
		
		return true;
		
	}
	
	//Congratulation panel appears if the player win
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
	
	//Check how many empty squares in the puzzle.
	private int countEmpty() {
		int counter = 0;
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (puzzle[i][j] == 0)					
					counter++;
		return counter;
	}
	
	//Upload resources for images and border layout
	private void loadResources() {
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
				
		final String[] buttonErrorImageList = {"./resources/Button0Error.png", "./resources/Button1Error.png", "./resources/Button2Error.png", "./resources/Button3Error.png",
				"./resources/Button4Error.png", "./resources/Button5Error.png", "./resources/Button6Error.png", "./resources/Button7Error.png",
				"./resources/Button8Error.png", "./resources/Button9Error.png"};
			for (int i = 0; i < buttonErrorImageList.length; i++)
				buttonErrorImages[i] = new ImageIcon(buttonErrorImageList[i]);
			
		final String[] puzzleBorderMeasurements = 
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
		
		for (int i = 0; i < puzzleBorderMeasurements.length; i++)
			puzzleBorders[i] = new String(puzzleBorderMeasurements[i]);
		
		
		final String[] selectionBorderMeasurements = 
			{"0,0,0,0", "5,5,1,1", "5,1,1,1", "5,1,1,5", "1,5,1,1", "1,1,1,1", 
				"1,1,1,5", "1,5,5,1", "1,1,5,1", "1,1,5,5" 	
			};
		
		for (int i = 0; i < selectionBorderMeasurements.length; i++)
			selectionBorders[i] = new String(selectionBorderMeasurements[i]);
		
			
	}
	
	//Handler for "Undo" button. It returns to the previous choice(image and puzzle).
	private class Undo implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (!myStack.isEmpty())
			{
				Move temp = myStack.pop();// local variable to save top stack value.
				
				for(int i = 0; i < 81; i++)
				{
					if (i == temp.getLocation())
					{
						int row = i / 9;
						int col = i % 9 ;
						puzzle[row][col] = Integer.parseInt(temp.getValue());//Change value in the puzzle.
						hasErrors = isValid(puzzle[row][col]);// Check puzzle again after undo.
						btnNumbers[i].setRolloverIcon(buttonRolloverImages[Integer.parseInt(temp.getValue())]);
						btnNumbers[i].setIcon(buttonImages[Integer.parseInt(temp.getValue())]);
						btnNumbers[i].setPressedIcon(buttonPressedImages[Integer.parseInt(temp.getValue())]);
						btnNumbers[i].setText(temp.getValue());
						setButton(btnNumbers[i], Integer.parseInt(temp.getValue()), i);//Change image display of the button.
					}
				}
			}
		}
	}
	
	//Adding sound for "Generate" and "Solve" buttons.
	private void playSound(String clipName)
	{
		try 
		{
			AudioInputStream soundToPlay = AudioSystem.getAudioInputStream(new File("./resources/"+clipName+".wav"));
			Clip clipToPlay = AudioSystem.getClip( );
			clipToPlay.open(soundToPlay);
			clipToPlay.start();
		} 
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) 
		{
			e.printStackTrace();
		}
	}
		
}// end class Sudoku