package code;

public class Cell {
	/*A Cell represents a single square on the Sudoku Game Board. 
	 * It knows it's number - 0 means it is not solved.
	 * It knows the potential numbers that it could have from 1-9.
	 * The Sudoku game board is sub-divided into 9 smaller 3x3 sections that I will call a box. 
	 * These boxes will be numbered from left to right, top to bottom, from 1 to 9.  Each cell
	 * will know which box it belongs in.
	 */
	
	private int number; // This is the solved value of the cell.
	private boolean[] potential = {false, true, true, true, true, true, true, true, true, true};
	/*This array represents the potential of the cell to be each of the given index numbers.  Index [0] is not used since
	 * the cell cannot be solved for 0. 
	 */
	private int boxID;//The boxID is the box to which the cell belongs.
	
	//This method returns TRUE or False depending on whether the cell has the potential to be number
	public boolean canBe(int number)
	{
		return potential[number];
	}
	
	//This sets the potential array to be false for a given number
	public void cantBe(int number)
	{
		this.potential[number] = false;
	}
	
	//This method returns a count of the number of potential numbers that the cell could be.
	public int numberOfPotentials()
	{
		int counter = 0;
		for (int x = 1; x <= 9; x++) {
			if(this.potential[x] == true)
				counter++;
		}
		return counter;
	}
	
	//This method will return the first number that a cell can possibly be.
	public int getFirstPotential()
	{
		for(int x = 1; x <=9; x++) {
			if(canBe(x) == true)
				return x;
			else
				continue;
		}
		return 0;
	}
	
	public int getSecondPotential()
	{
		int counter = 0;
		int place = 0;
		for(int x = 1; x <=9; x++) {
			
			if(canBe(x) == true) {
				counter++;
			}
			else
				continue;
			if(counter == 2)
				return x;
		}
		return 0;
	}
	
	
	
	
	//GETTERS AND SETTERS
	public int getNumber() {
		return number;
	}
	
	//Done       /TODO: setNumber
	// This method sets the number for the cell but also sets all of the potentials for the cell (except for the solved number)
	//		to be false
	public void setNumber(int number) {
		this.number = number;
		for(int x = 1; x <= 9; x++)
			if(x == number)
				this.potential[x] = true;
			else
				this.potential[x] = false;
	}
	
	
	
	public boolean[] getPotential() {
		return potential;
	}
	
	public void setPotential(int num, boolean state) {
		potential[num] = state;
	}
	public int getBoxID() {
		return boxID;
	}
	public void setBoxID(int boxID) {
		this.boxID = boxID;
	}
}
