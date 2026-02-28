package code;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;

public class Board{
	
	/*The Sudoku Board is made of 9x9 cells for a total of 81 cells.
	 * In this program we will be representing the Board using a 2D Array of cells.
	 * 
	 */

	private Cell[][] board = new Cell[9][9];
	
	//The variable "level" records the level of the puzzle being solved.
	private String level = "";

	
	//This must initialize every cell on the board with a generic cell.  It must also assign all of the boxIDs to the cells
	public Board()
	{
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				board[x][y] = new Cell();
				board[x][y].setBoxID( 3*(x/3) + (y)/3+1);
			}
	}
	

	public void loadPuzzle() throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/puzzle.json")));
        JSONObject jsonObject = new JSONObject(content);
        JSONArray puzzleArray = jsonObject.getJSONArray("Puzzle");

        for (int x = 0; x < 9; x++) {
            JSONArray row = puzzleArray.getJSONArray(x);
            for (int y = 0; y < 9; y++) {
                int number = row.getInt(y);
                if (number != 0) {
                    solve(x, y, number);
                }
            }
        }
    }
	
	/*This method scans the board and returns TRUE if every cell has been solved.  Otherwise it returns FALSE
	 * 
	 */
	public boolean isSolved()
	{
		for(int x = 0; x < 9; x++) {
			for(int y = 0; y < 9; y++) {
				if(board[x][y].getNumber() == 0)
					return false;
			}
		}
		return true;
	}

	public void display()
	{
		System.out.println(" ------- ------- -------");
		for(int x = 0; x < 9; x++) {
			System.out.print("| ");
			for(int y = 0; y < 9; y++) {
				System.out.print(board[x][y].getNumber() + " ");
				if((y+1)%3 == 0)
					System.out.print("| ");
				}
			//System.out.print();
			if((x+1)%3 == 0) {
				System.out.println();
				System.out.println(" ------- ------- -------");
			}
			else
				System.out.println();
		}
		//System.out.println(board[0][0].getFirstPotential());
	}
	
	/*This method solves a single cell at x,y for number.  It also must adjust the potentials of the remaining cells in the same row,
	 * column, and box.
	 */
	public void solve(int x, int y, int number)
	{
		board[x][y].setNumber(number);
		
		//Row
		for(int z = 0; z < 9; z++) {
			if(board[z][y] == board[x][y]) 
				continue;
			else
				board[z][y].cantBe(number);
		}
		
		//Column
		for(int w = 0; w < 9; w++) {
			if(board[x][w] == board[x][y])
				continue;
			else
				board[x][w].cantBe(number);
		}
		
		//Box
		for(int z = 0; z < 9; z++) {
			for(int w = 0; w < 9; w++) {
				if(board[w][z].getBoxID() == board[x][y].getBoxID() && board[w][z] != board[x][y]) {
					board[w][z].cantBe(number);
				}
			}
		}
	}
	
	//logicCycles() continuously cycles through the different logic algorithms until no more changes are being made.
	public void logicCycles()throws Exception
	{
		
		
			int changesMade = 0;
			do
			{
				changesMade = 0;
				changesMade += logic1();
				changesMade += logic2();
				changesMade += logic3();
				changesMade += logic4();
				if(errorFound())
					break;
			}while(changesMade != 0 && isSolved() == false);	
	}
	
	/*This method searches each row of the puzzle and looks for cells that only have one potential.  If it finds a cell like this, it solves the cell 
	 * for that number. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic1()
	{
		int changesMade = 0;
		for(int y = 0; y < 9; y++) {
			for(int x = 0; x < 9; x++) {
				if(board[x][y].numberOfPotentials() == 1 && board[x][y].getNumber() != board[x][y].getFirstPotential()) {
					solve(x,y, board[x][y].getFirstPotential());
					changesMade++;
				}
			}
		}
		return changesMade;
					
	}
	
	/*This method searches each row for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell.  It then does the same thing for the columns.This also tracks the number of cells that 
	 * it solved as it traversed the board and returns that number.
	 */
	
	public int logic2()
	{
		int changesMade = 0;
		int placeX = 0;
		int placeY = 0;
		int occurences = 0;
		for(int num = 1; num <=9; num++) {
			
			//Rows
			for(int y = 0; y < 9; y++) {
				placeX = 0;
				placeY = 0;
				occurences = 0;
				for(int x = 0; x < 9; x++) {
					if(board[x][y].canBe(num) && board[x][y].getNumber() != num) {
						occurences++;
						placeX = x;
						placeY = y;
					}
					if(occurences > 1)
						break;
				}
				if(occurences == 1) {
					changesMade++;
					solve(placeX, placeY, num);
				}
			}
			
			//Columns
			for(int x = 0; x < 9; x++) {
				placeX = 0;
				placeY = 0;
				occurences = 0;
				for(int y = 0; y < 9; y++) {
					if(board[x][y].canBe(num) && board[x][y].getNumber() != num) {
						occurences++;
						placeX = x;
						placeY = y;
					}
					if(occurences > 1)
						break;
				}
				if(occurences == 1) {
					changesMade++;
					solve(placeX, placeY, num);
				}
			}
			
		}
		
		return changesMade;
	}
	
	/*This method searches each box for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic3()
	{
		int changesMade = 0;
		int placeX = 0;
		int placeY = 0;
		int occurences = 0;
		
		for(int box = 1; box <= 9; box++) {
		for(int num = 1; num <=9; num++) {
			occurences = 0;
			for(int y = 0; y < 9; y++) {
				for(int x = 0; x < 9; x++) {
					if(board[x][y].getBoxID() == box && board[x][y].canBe(num) && board[x][y].getNumber() != num) {
						placeX = x;
						placeY = y;
						occurences++;
					}
				}
			}
			if(occurences == 1) {
				changesMade++;
				solve(placeX, placeY, num);
			}
		}
		}
		return changesMade;
	}
	
	
	///TODO: logic4
		/*This method searches each row for the following conditions:
		 * 1. There are two unsolved cells that only have two potential numbers that they can be
		 * 2. These two cells have the same two potentials (They can't be anything else)
		 * 
		 * Once this occurs, all of the other cells in the row cannot have these two potentials.  Write an algorithm to set these two potentials to be false
		 * for all other cells in the row.
		 * 
		 * Repeat this process for columns and rows.
		 * 
		 * This also tracks the number of cells that it solved as it traversed the board and returns that number.
		 */
	public int logic4()
	{
		int changesMade = 0;
		
		for(int x = 0; x < 9; x++) {
		for(int y = 0; y < 9; y++) {
			if(board[x][y].numberOfPotentials() == 2) {
				//at this point board[x][y] is a cell with 2 potentials
				
				for(int z = y+1; z < 9; z++) {
					if(board[x][z].numberOfPotentials() == 2) { //Potential match
						if(board[x][y].getFirstPotential() == board[x][z].getFirstPotential() && board[x][y].getSecondPotential() == board[x][z].getSecondPotential()) {
							//We found a situation!
							for(int q = 0; q < 9; q++) {
								if(q == y || q == z) {
									continue;
								}
								if(board[x][q].canBe(board[x][y].getFirstPotential())) {
									board[x][q].cantBe(board[x][y].getFirstPotential());
									changesMade++;
								}
								if(board[x][q].canBe(board[x][y].getSecondPotential())) {
									board[x][q].cantBe(board[x][y].getSecondPotential());
									changesMade++;
								}
							}
						}
					}
				}
			}
		}
		}
		
		
		for(int y = 0; y < 9; y++) {
			for(int x = 0; x < 9; x++) {
				if(board[x][y].numberOfPotentials() == 2) {
					//at this point board[x][y] is a cell with 2 potentials
					for(int z = x+1; z < 9; z++) {
						if(board[z][y].numberOfPotentials() == 2) //Potential match
							if(board[x][y].getFirstPotential() == board[z][y].getFirstPotential() && board[x][y].getSecondPotential() == board[z][y].getSecondPotential()) {
								//We found a situation!
								for(int q = 0; q < 9; q++) {
									if(q == x || q == z)
										continue;
									if(board[q][y].canBe(board[x][y].getFirstPotential())) {
										board[q][y].cantBe(board[x][y].getFirstPotential());
										changesMade++;
									}
									if(board[q][y].canBe(board[x][y].getSecondPotential())) {
										board[q][y].cantBe(board[x][y].getSecondPotential());
										changesMade++;
									}
								}
							}
					}
				}
			}
			}
		
		return changesMade;
	}
	
	Board[] twoByFour = new Board[81];
	int numbaOfGesses = 0;
	
	public void setNumber(int x, int y, int num) {
		board[x][y].setNumber(num);
	}
	
	public void setPotential(int x, int y, int num, boolean tf) {
		board[x][y].setPotential(num, tf);
	}
	public void guess() throws Exception{
		Board copy = new Board();
		for(int x = 0; x < 9; x++) {
			for(int y = 0; y < 9; y++) {
				copy.setNumber(x, y, board[x][y].getNumber());
				for(int pot = 1; pot < 10; pot++) {
					copy.setPotential(x,y,pot, board[x][y].canBe(pot));
				}
			}
		}
		twoByFour[numbaOfGesses++] = copy;
		
		for(int x = 0; x < 9; x++) {
			for(int y = 0; y< 9; y++) {
				if(board[x][y].getNumber() == 0) {
					solve(x,y,board[x][y].getFirstPotential());
					logicCycles();
					if(errorFound()) {
						for(int z = 0; z < 9; z++) {
							for(int w = 0; w < 9; w++) {
								board[z][w].setNumber(twoByFour[numbaOfGesses-1].board[z][w].getNumber());
								for(int ξ = 1; ξ < 10; ξ++)
									board[z][w].setPotential(ξ, twoByFour[numbaOfGesses-1].board[z][w].canBe(ξ));
							}
						}
						board[x][y].cantBe(board[x][y].getFirstPotential());
						guess();
					}
					else if(!errorFound() && !isSolved()) {
						guess();
					}
					else if(!errorFound() && isSolved()) {
						return;
					}
				}
			}
		}
	}
	
	//          Done    /TODO: errorFound
	/*This method scans the board to see if any logical errors have been made.  It can detect this by looking for a cell that no longer has the potential to be 
	 * any number.
	 */
	public boolean errorFound()
	{
		for(int y = 0; y < 9; y++)
			for(int x = 0; x < 9; x++) 
				if(board[x][y].numberOfPotentials() == 0) 
					return true;
		return false;
	}
	
	
	
	
}