import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class SudokuSolver 
{
	public static final int N = 9;	//Size of puzzle array.

	// Read puzzle from a file.
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
	
	// Read solved puzzle from a file.
	public static int[][] readAnswer() throws IOException 
	{
		FileReader file = new FileReader("answer.txt");
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
				table[row][col] = Integer.parseInt(parts[col].trim());
			}		
			
		file.close();
			
		return (table);

	}
	
	
	
	// Write solved puzzle to a file.
	public static void writeAnswer(int[][] table) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter("answer.txt", "UTF-8");
				
		for(int row=0; row<N; ++row)
		{
			for(int col=0; col<N; ++col)
				writer.print((col == 8 ? table[row][col] : table[row][col] + ","));
				writer.println();
			
		}
		
		writer.close();
	}
	
	public void test(int k, int[][] table, int lastK) throws FileNotFoundException, UnsupportedEncodingException
	{
		int i, j, x;
		while (table[k / N][k%N] != 0)
			k++;	//Skip blocks which already have numbers.
		i = k / N; j = k%N;
		for (x = 1; x <= N; x++) //Choose number from 1 to 9 to fill in
		{
			if (isOK(i, j, x, table))
			{
				table[i][j] = x;
				if (k == lastK)
					writeAnswer(table);
								
				else
					test(k + 1, table, lastK);
				
				table[i][j] = 0;
			}
			
		}
	
	}

	public static boolean isOK(int i, int j, int x, int[][] table)
	{
		int k, t;
		int tmpX, tmpY; //Current position of row and column

		//Check row i.
		for (k = 0; k<N; k++)
			if (table[i][k] == x)
				return false;
		//Check column j.
		for (k = 0; k<N; k++)
			if (table[k][j] == x)
				return false;

		//Check small block 3x3
		tmpX = i % 3; tmpY = j % 3;
		for (k = i - tmpX; k <= i - tmpX + 2; k++)
			for (t = j - tmpY; t <= j - tmpY + 2; t++)
				if (table[k][t] == x)
					return false;
		return true;
	}

	public int findLastK(int[][] table) //Last recursion step to do.
	{
		int i, j;
		for (i = N - 1; i >= 0; i--)
			for (j = N - 1; j >= 0; j--)
				if (table[i][j] == 0)
					return (i*N + j);
		return 0;
	}
}
