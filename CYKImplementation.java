import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by sg1961 on 4/25/2017.
 */
public class CYKImplementation {

        public Grammar cnfGrammar;
        public String stringToParse;
        public HashMap<String, ArrayList<String>> term;
        public HashMap<String, Production> map;

    public CYKImplementation(Grammar grammar)
        {
            this.cnfGrammar = grammar;
        }

    public void prase(HashMap<String, ArrayList<String>> term, HashMap<String, Production> map) throws IOException
    {
        this.term = term;
        this.map = map;

        Scanner insert = new Scanner(System.in);

        System.out.print("\nPlease enter the word:\n");
        this.stringToParse = insert.nextLine();
        if(parse())
            System.out.print("\n\n    The word is in language\n");
        else
            System.out.print("\n\n  The word is not in language \n");

    }

    public boolean parse()
    {
        int y, z;
        ArrayList[][] matrixCYK = new ArrayList[this.stringToParse.length()][this.stringToParse.length()];
        String terminal = new String();

        printFirstRow(matrixCYK,terminal);

        printRowOfMatrix(matrixCYK[0], 0);

        for(int i = 1; i < this.stringToParse.length(); i++)
        {
            for(int j = 0; j < this.stringToParse.length() - i; j++)
            {
                y = j;
                z = i;
                matrixCYK[i][j] = new ArrayList();
                for(int k = 0; k < i; k ++)
                {
                    y+=1;z-=1;
                    if(matrixCYK[k][j] != null)
                        for (int t = 0; t < matrixCYK[k][j].size(); t++)
                        {
                            String leftCell = (String) matrixCYK[k][j].get(t);
                            Production leftProduction = map.get(leftCell);
                            if(matrixCYK[z][y] != null)
                                for(int u = 0; u < matrixCYK[z][y].size(); u++)
                                {
                                    String rightCell = (String) matrixCYK[z][y].get(u);
                                    for(int f = 0; f < leftProduction.getUsedProduction().size(); f++){
                                        Production rightProduction = map.get(leftProduction.getUsedProduction().get(f).x);
                                        if(rightProduction.getRightSide().contains(leftCell + rightCell))
                                            if(!matrixCYK[i][j].contains(leftProduction.getUsedProduction().get(f).x))
                                                matrixCYK[i][j].add(leftProduction.getUsedProduction().get(f).x);
                                    }
                                }

                        }

                }
            }
            printRowOfMatrix(matrixCYK[i], i);
        }
        return matrixCYK[this.stringToParse.length() - 1][0].contains(this.cnfGrammar.getGrammarA());
    }

    public void printFirstRow(ArrayList[][] m, String terminal)
    {
        System.out.print("          |   ");
        for(int j = 0; j < this.stringToParse.length(); j++)
        {
            terminal = this.stringToParse.substring(j, j+1);
            System.out.print("'"+ terminal + "'   |  ");
            m[0][j] = term.get(terminal);
        }
    }

    public void printRowOfMatrix(ArrayList[] toPrint, int pos)
    {
        ArrayList<String> ProductionToPrint;

        System.out.print("\npass = " +pos+"  |  ");
        for (int j = 0; j < this.stringToParse.length() - pos; j++)
        {
            ProductionToPrint = toPrint[j];
            if(ProductionToPrint != null)
                if (ProductionToPrint.size() == 0)
                    System.out.print("      ");
                else if(ProductionToPrint.size() == 1)
                    System.out.print("  " + ProductionToPrint.get(0) + "   ");
                else if(ProductionToPrint.size() == 2)
                    System.out.print(" " + ProductionToPrint.get(0) + " " + ProductionToPrint.get(1) + "  ");
                else
                    for (String p : ProductionToPrint)
                        System.out.print(p + " ");
            System.out.print(" | ");
        }
    }



}
