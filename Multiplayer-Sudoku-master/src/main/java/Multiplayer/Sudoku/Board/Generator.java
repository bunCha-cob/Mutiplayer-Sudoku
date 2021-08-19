package Multiplayer.Sudoku.Board;

import java.io.IOException;
import java.util.Random;

/** The Generator class generates the Sedoku games based on difficulty.
 */
public class Generator {
    
    static int [][] a = new int [9][9];  
    static int [][] fullGrid = new int [9][9];
    
    public static void main(String[] args) throws IOException{
        //createBoard();
    }
    
    /** The createBoard() function creates a new Sedoku game based on a difficulty value. 
     * @param diffLevel is the game difficulty level set by the user.
     */
    public static void createBoard(int diffLevel) {
        
        //BufferedReader obj=new BufferedReader(new InputStreamReader(System.in));
        int num_del;
        
        do {
            num_del = 0;
            // generate a valid sudoku grid
            generate();
            
            // interchange rows and columns within a same group
            swapInGroup(1); // interchange rows 
            swapInGroup(0); // interchange columns
            
            // interchange the whole group 
            swapWholeGroup(1); // interchange row group
            swapWholeGroup(0); // interchange column group
            
            // save the full grid sudoku board
            for (int i=0; i<9; i++)
            for (int j=0; j<9; j++)
            fullGrid[i][j] = a[i][j];
            
            // striking out
            for(int i = 0; i < 9; i++) {
                for (int j=4; j>=0; j--) {
                    if (satisfiesDifficultyLevel(diffLevel, num_del))
                    return;
                    
                    if (a[i][j]!=0) {
                        strike_out(i,j);
                        
                        if (a[i][j]==0)
                        num_del++;
                    }
                }
                for (int j = 5; j < 9; j++) {
                    if (satisfiesDifficultyLevel(diffLevel, num_del))
                    return;
                    
                    if (a[i][j]!=0) {
                        strike_out(i,j);
                        
                        if (a[i][j]==0)
                        num_del++;
                    }
                }
            }
        } while (true);
    }
    
    /** The satisfiesDifficultyLevel() function checks to see if the game board that has been
     * generated meets the requirements for the difficulty level that was selected.
     * @param diffLevel is the difficulty level that the game was created based on.
     * @param num_del is the number of deleted values from the complete sudoku board to make 
     * the game.
     * @return true if the diffLevel matches the associated case statement.
     */
    private static boolean satisfiesDifficultyLevel(int diffLevel, int num_del) {
        switch (diffLevel) {
            case 1:
                return (num_del>=49) && (num_del<51);
            case 2:
                return (num_del>=51) && (num_del<53);
            case 3:
                return (num_del>=53) && (num_del<=56);
            default:
                return false;
        }
    }
    
    
    public static int[][] getFullGrid() {
        return fullGrid;
    }
    
    public static int[][] getBoard(int diffLevel) {
        createBoard(diffLevel);
        return a;
    }
    
    /**
    *  generate a valid sudoku grid satisfies
    *  - for each row: every number 1 -> 9 occur exactly one
    *  - for each column: every number 1 -> 9 occur exactly one
    *  - for each 3x3 square with a thicker border: every number 1 -> 9 occur exactly one
    */
    public static void generate(){
        
        int k=1, n=1;
        
        for(int i=0; i<9; i++) {
            k=n;
            for(int j=0; j<9; j++) {
                if(k <= 9) {
                    a[i][j]=k;
                    k++;
                } else {
                    k=1;
                    a[i][j]=k;
                    k++;
                }
            }

            n=k+3;

            if (k==10) 
                n=4;
                
            if (n>9)
                n=(n%9)+1;
        }
    }
    
    // Interchange rows or columns in a same group
    public static void swapInGroup(int type){
        
        int k1, k2, max=2, min=0;
        Random r = new Random();
        for(int i=0; i<3; i++){
            
            // generate a random number in range [min, max]
            k1 = r.nextInt(max-min+1)+min;
            
            // generate another random number in range [min, max]
            // ensure that k2 != k1
            do {
                k2 = r.nextInt(max-min+1)+min;
            } while(k1==k2);
            
            // increase min and max to point at next group
            max+=3;min+=3;
            
            if(type==1) 
            permutation_row(k1,k2); // interchange rows
            else if(type==0)
            permutation_col(k1,k2); // interchange columns
        }
    }
    
    // Interchange the whole group
    public static void swapWholeGroup(int type){
        int k1, k2;
        Random rand=new Random();
        int n[]={0,3,6};
        for(int i=0; i<2; i++)
        {
            // generate a random number
            k1 = n[rand.nextInt(n.length)];
            
            // generate another random number
            // ensure that k2 != k1
            do{
                k2 = n[rand.nextInt(n.length)];
            } while (k1==k2);

            if(type==1)
                row_change(k1,k2);
            else if (type==0)
                col_change(k1,k2);  
        }
    }
    
    // Interchange rows 
    public static void permutation_row(int k1, int k2){
        int temp;
        for(int j=0;j<9;j++) {
            temp     = a[k1][j];
            a[k1][j] = a[k2][j];
            a[k2][j] = temp;
        }
    }
    
    // Interchange columns
    public static void permutation_col(int k1,int k2){
        int temp;

        for(int j=0;j<9;j++) {
            temp     = a[j][k1];
            a[j][k1] = a[j][k2];
            a[j][k2] = temp;
        }
    }
    
    public static void row_change(int k1,int k2){
        int temp;

        for(int n=1;n<=3;n++) {
            for(int i=0;i<9;i++) {
                temp     = a[k1][i];
                a[k1][i] = a[k2][i];
                a[k2][i] = temp;
            }
            k1++;
            k2++;
        }
    }
    public static void col_change(int k1,int k2){
        int temp;
        for(int n=1;n<=3;n++) {
            for(int i=0;i<9;i++) {
                temp     = a[i][k1];
                a[i][k1] = a[i][k2];
                a[i][k2] = temp;
            }
            k1++;
            k2++;
        }
    }
    
    public static void strike_out(int k1,int k2) {
        
        int row_from, row_to, col_from, col_to;
        int rem1, rem2, flag;

        int count = 9;
        
        for(int i = 1; i<=9 ; i++) { // try all possible values
            
            flag = 1; // flag if the value is valid
            
            // check with column
            for(int j = 0; j<9 ; j++) {
                if(j!=k2) {
                    if( i!=a[k1][j] )
                        continue;
                    else {
                        flag=0; // flag if the value is invalid
                        break;
                    }
                } 
            }
            
            if(flag==1) {
                // check with row
                for(int c=0; c<9; c++) {
                    if(c!=k1) {
                        if(i!=a[c][k2])
                            continue;
                        else {
                            flag=0; // flag if the value is invalid
                            break; 
                        }
                    }
                }
            }
            
            if(flag==1){
                // check with 3x3 cell
                rem1=k1%3; rem2=k2%3;
                row_from=k1-rem1;
                row_to=k1+(2-rem1);
                col_from=k2-rem2;
                col_to=k2+(2-rem2);

                for(int c=row_from; c<=row_to; c++) {
                    for(int b=col_from; b<=col_to; b++) {
                        if(c!=k1 && b!=k2) {
                            if(i!=a[c][b])
                                continue;
                            else {
                                flag=0; // flag if the value is invalid
                                break;
                            }
                        }
                    }
                }
            }
            
            // if the value is invalid then eliminate it from set of possible values 
            if(flag==0)
                count--; 
        }
        
        // if there is only one possible value, we can delete it
        if(count==1){ 
            a[k1][k2]=0;
            
        }
    }
}



