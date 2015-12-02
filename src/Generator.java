import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public class Generator {
	public static final int N = 9;		// Fake size of puzzle.
	public static final int DELTA = 2;	// Extra size for methods.
	int[][] a = new int[N+2][N+2];		// Actual size of puzzle.
	private int k = 0;			// A counter to determine when to switch between fillCount and subGrid.
    	private int fillCount = 1;		// A counter from 1 to 9 to fill in puzzle.
    	private int subGrid = 1;		// A sub variable for fillCount.
    	SecureRandom randomNumbers = new SecureRandom();
    	
    
    public Generator ()
    {
    
    	for (int i = 0 ; i  < 9 ; i++)
    		for (int j = 0; j < 9; j++)
    			a[i][j]=0;
    }
    	
    public void basePuzzle() 		// Create a standard solved puzzle.
    {
    	for (int row = 0; row<N ; row++)
    	{
            if (k == 3)
            {
                k = 1;			// Reset counter k.
                subGrid++;		
                fillCount = subGrid;
            }
            else
            {
                k++;
                if (row != 0)
                	fillCount = fillCount + 3;
            }
            for(int col=0 ; col<N ; col++)
            {
                if (fillCount == N)
                {
                    a[row][col] = fillCount;
                    fillCount = 1;
                }
                else
                {
                    a[row][col] = fillCount++;
                }
            }
        }
    }
    
    public void swap(int x, int y)	// Swapping method.
    {
    	int flag = x;
    	
    	x = y;
    	y = flag;
    }
    
    public void randomPuzzle()		// Create random 0 or blank from the base puzzle.
    {
    	int count = 1, row, col;
    	
    	do
    	{
    		row = randomNumbers.nextInt(9);
    		col = randomNumbers.nextInt(9);
    		if (a[row][col] != 0)
    		{
    			a[row][col] = 0;
    			++count;
    		}
    	}
    	while(count != 54);		// 54 is the number of 0 or blank element in array.
    }
    
    // method to write the puzzle to a file named "puzzle.txt" using a PrintWriter object.
    public void writePuzzle() throws IOException, UnsupportedEncodingException
    {
    	PrintWriter writer = new PrintWriter("puzzle.txt", "UTF-8");
    				
    	for(int row = 0; row<N; ++row)
    	{
    		for(int col = 0; col<N; ++col)
    			writer.print( (col == 8 ? a[row][col] : a[row][col] + ",") );
    			writer.println();	
    	}
    		
    	writer.close();
    }
    
    public void rowSwap()		// Choosing a random column and swapping elements from one side to another.                                                                                                                                                                                                                                                                                      
    {
    	int col;
    	col = randomNumbers.nextInt(9);
    	
    	
    	for(int i=0; i<4; ++i)
    		swap(a[i][col], a[N-i][col]);
    }
    
    public void colSwap()		// Choosing a random row and swapping elements from one side to another.
    {
    	int row;
    	row = randomNumbers.nextInt(9);
    	
    	
    	for(int i=0; i<4; ++i)
    		swap(a[row][i], a[row][N-i]);
    }
    
    public void diagonalSwap()		// Swapping elements from one side to another over left main diagonal.
    {
    	int row, col;
    	row = randomNumbers.nextInt(9);
		col = randomNumbers.nextInt(9);
		
		swap(a[row][col], a[col][row]);
    }
    
    public void generate() throws UnsupportedEncodingException, IOException	// Main method of the class which creates puzzles.
    {
    	int number;
    	
    	number = randomNumbers.nextInt(3);	// Random swapping method.
    	basePuzzle();				
    	randomPuzzle();				
    	
    	switch(number)
    	{
    		case 0:
    			rowSwap();
    			break;
    		case 1:
    			colSwap();
    			break;
    		case 2:
    			diagonalSwap();
    			break;
    		default:
    			break;
    	}
    	

    	writePuzzle();

    }
}
