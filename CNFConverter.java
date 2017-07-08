import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Created by sg1961 on 4/22/2017.


public class CNFConverter {
    public Grammar grammar;

    public ArrayList<String> newNonTerminalSymbols;
    public ArrayList<String> newTerminalSymbols;
    public HashMap<String, Production> productionNonTerminal;
    public ArrayList<String> listNullable;
    public ArrayList<TempStorage<String, String>> unitProduction;
    public HashMap<String, ArrayList<String>> termProduction;

    public CNFConverter(String givenGrammar)
    {
        this.grammar = new Grammar(givenGrammar);
        this.newNonTerminalSymbols = new ArrayList<>();
        this.newTerminalSymbols = new ArrayList<>();
        this.productionNonTerminal = new HashMap<>();
        this.listNullable = new ArrayList<>();
        this.unitProduction = new ArrayList<>();
        this.termProduction = new HashMap<>();

    }
    public boolean isInUpperCase(String str)
    {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher m = pattern.matcher(str.substring(0,1));

        return m.matches();
    }

    public String getChar(int i, String s)
    {
        return s.substring(i, i + 1);
    }

    public String newNonTerminalProduction(HashMap<String , Production> m)
    {
        for(java.lang.Character alphabet = 'A'; alphabet <= 'Z'; alphabet++)
            if(m.get(alphabet.toString()) == null)
                return alphabet.toString();
        return null;
    }

    public Grammar convert()
    {
        if(!checkGrammar())
        {
            System.out.println("\nBad grammar\n");
            return null;
        }
        removeEpsilonProduction();
        removeUnitProduction();
        cnf();
        return grammar;
    }

    public void executeParser() throws IOException
    {
        CYKImplementation parser = new CYKImplementation(grammar);
        parser.prase(termProduction,productionNonTerminal);
    }


    private boolean checkGrammar()
    {
        int i = 0;
        String leftSide;
        String character;
        Production tempProduction;
        Production usedProduction;

        for(;;)
        {
            leftSide = getChar(i,grammar.getGrammarString());

            if((tempProduction = productionNonTerminal.get(leftSide)) == null)
            {
                tempProduction = new Production(leftSide,grammar,productionNonTerminal);
                if (!isInUpperCase(leftSide))
                    return false;
                if (i == 0)
                    grammar.setGrammarA(leftSide);
            }

            i+=1;

            if(!grammar.getGrammarString().substring(i,i+2).equals("  "))
                return false;

            i+=2;

            for(int j = i; j < grammar.getGrammarString().length(); j++)
            {
                character = getChar(j,grammar.getGrammarString());
                if(character.equals(" ")){
                    tempProduction.addProduction(grammar.getGrammarString().substring(i, j),grammar,this.listNullable,unitProduction);
                    i = j + 1;
                }

                if(character.equals(";"))
                {
                    tempProduction.addProduction(grammar.getGrammarString().substring(i, j),grammar,listNullable,unitProduction);
                    i = j + 1;
                    break;
                }

                else if(character.equals("."))
                {
                    tempProduction.addProduction(grammar.getGrammarString().substring(i, j),grammar,listNullable,unitProduction);
                    return true;
                }

                else if (isInUpperCase(character))
                {
                    if((usedProduction = productionNonTerminal.get(character)) == null)
                        usedProduction = new Production(character,grammar,productionNonTerminal);
                    usedProduction.addUsedProduction(new TempStorage<>(leftSide, tempProduction.getRightSide().size()));
                }
            }
        }
    }

    private void removeEpsilonProduction()
    {
        Production newProduction;
        Production nullableProduction, temp;
        String right, lhs, newRight;
        boolean eliminated;

        for(int j = 0; j < listNullable.size() ; j++)
        {
            String nullable = listNullable.get(j);
            nullableProduction = productionNonTerminal.get(nullable);
            lhs = nullableProduction.getLeftSide().getChar();


            for(int k = 0; k < nullableProduction.getUsedProduction().size(); k++)
            {
                TempStorage<String, Integer> tupleOfUsedProduction =  nullableProduction.getUsedProduction().get(k);
                temp = productionNonTerminal.get(tupleOfUsedProduction.x);
                right = temp.getRightSide().get(tupleOfUsedProduction.y);
                eliminated = false;

                for(int i = 0; i < right.length(); i++)
                {
                    int lenght = temp.getRightSide().size() - 1;
                    if(right.substring(i, i+1).equals(lhs) && !eliminated)
                    {
                        eliminated = true;
                        newRight = right.substring(0, i) + right.substring(i+1, right.length());
                        if (newRight.length() == 0)
                        {
                            temp.setNullable(true);
                            if(!listNullable.contains(tupleOfUsedProduction.x))
                                listNullable.add(tupleOfUsedProduction.x);
                        }
                        else
                        if (!temp.getRightSide().contains(newRight))
                            temp.addProduction(newRight,grammar,listNullable,unitProduction);
                        continue;
                    }
                    if(isInUpperCase(right.substring(i, i + 1)))
                    {
                        newProduction = productionNonTerminal.get(right.substring(i, i+1));
                        newProduction.addUsedProduction(new TempStorage<>(tupleOfUsedProduction.x, lenght));
                    }
                }
            }
        }


        nullableProduction = grammar.getProductions().get(0);
        if(nullableProduction.isNullable())
        {
            if(!nullableProduction.getRightSide().contains("e"))
                nullableProduction.addProduction("e",grammar,listNullable,unitProduction);
        }
        for(Production ignored : grammar.getProductions())
            nullableProduction.usedProduction = new ArrayList<TempStorage<String, Integer>>();
    }

    private void removeUnitProduction()
    {
        TempStorage<String, String> nonTerminalUnit;
        Production production1, production2;


        String tempRhs;

        if(unitProduction.size() == 0) {
            return;
        }
        while(unitProduction.size() != 0)
        {

            nonTerminalUnit = unitProduction.get(0);
            unitProduction.remove(0);
            production1 = productionNonTerminal.get(nonTerminalUnit.x);
            production2 = productionNonTerminal.get(nonTerminalUnit.y);
            production1.getRightSide().remove(nonTerminalUnit.y);
            for(int i = 0; i < production2.getRightSide().size(); i++)
            {
                tempRhs = production2.getRightSide().get(i);
                if(!production1.getRightSide().contains(tempRhs))
                    production1.addProduction(tempRhs,grammar,listNullable,unitProduction);
                if(Pattern.matches("[A-Z]", tempRhs) && !nonTerminalUnit.x.equals(tempRhs))
                    this.unitProduction.add(new TempStorage<>(nonTerminalUnit.x, tempRhs));
            }
        }

    }

    private void cnf()
    {

        String rightSide, oldLeftSide, newLeftSide = null;
        String newTerminalLeft;
        String newTerminalRight;
        int j;
        Production newProduction, oldProduction;
        ArrayList<String> temp;

        for(int h = 0; h < grammar.getProductions().size(); h++)
        {
            oldProduction  = grammar.getProductions().get(h);
            for(int i = 0; i < oldProduction.getRightSide().size(); i++)
            {
                rightSide = oldProduction.getRightSide().get(i);
                oldLeftSide = oldProduction.getLeftSide().getChar();
                j = i;
                if (rightSide.length() == 1 && !Pattern.matches("[A-Z]", rightSide))
                {
                    temp = this.termProduction.get(rightSide);
                    if (temp == null)
                    {
                        this.termProduction.put(rightSide, new ArrayList<>());
                        temp = this.termProduction.get(rightSide);
                    }
                    if(!temp.contains(oldLeftSide))
                        temp.add(oldLeftSide);
                    continue;
                }

                boolean entered = false;

                while(rightSide.length() > 2)
                {
                    entered = true;
                    if((newTerminalLeft = FindCreateTerminal(rightSide, 0)) == null)
                        newTerminalLeft = rightSide.substring(0,1);

                    newLeftSide = newNonTerminalProduction(this.productionNonTerminal);

                    productionNonTerminal.get(oldLeftSide).getRightSide().set(j, newTerminalLeft + newLeftSide);
                    productionNonTerminal.get(newTerminalLeft).addUsedProduction(new TempStorage<>(oldLeftSide, i));

                    newProduction = new Production(newLeftSide, this.grammar,this.productionNonTerminal);
                    newProduction.addUsedProduction(new TempStorage<>(oldLeftSide, i));

                    rightSide = rightSide.substring(1, rightSide.length());
                    newProduction.addProduction(rightSide,this.grammar,this.listNullable,this.unitProduction);

                    j = newProduction.getRightSide().size() - 1;
                }
                if(!entered)
                    newLeftSide = oldLeftSide;

                if((newTerminalLeft = FindCreateTerminal(rightSide, 0)) != null)
                {
                    productionNonTerminal.get(newLeftSide).getRightSide().set(j, newTerminalLeft + rightSide.substring(1,2));
                    productionNonTerminal.get(newTerminalLeft).addUsedProduction(new TempStorage<>(newLeftSide, j));
                    rightSide = newTerminalLeft + rightSide.substring(1,2);
                }
                else
                    productionNonTerminal.get(rightSide.substring(0,1)).addUsedProduction(new TempStorage<>(newLeftSide, j));

                if((newTerminalRight = FindCreateTerminal(rightSide, 1)) != null)
                {
                    productionNonTerminal.get(newLeftSide).getRightSide().set(j, rightSide.substring(0,1) + newTerminalRight);
                    productionNonTerminal.get(newTerminalRight).addUsedProduction(new TempStorage<>(newLeftSide, j));
                }
                else
                    productionNonTerminal.get(rightSide.substring(1,2)).addUsedProduction(new TempStorage<>(newLeftSide, j));
            }
        }

    }


    public String FindCreateTerminal(String rightSide, int positionOfRight)
    {
        Production newProduction;
        String newNonTerminal, characterOfRightSide;
        ArrayList<String> tempList;

        characterOfRightSide = rightSide.substring(positionOfRight, positionOfRight + 1);

        if(!Pattern.matches("[A-Z]", characterOfRightSide))
        {
            tempList = termProduction.get(rightSide.substring(positionOfRight, positionOfRight+1));
            if(tempList != null && tempList.size() == 1)
                newNonTerminal = tempList.get(0);
            else
            {
                newNonTerminal = newNonTerminalProduction(this.productionNonTerminal);

                newProduction = new Production();
                newProduction.setLeftSide(new CharConverter(newNonTerminal));

                this.grammar.getProductions().add(newProduction);
                productionNonTerminal.put(newNonTerminal, newProduction);
                newProduction.addProduction(characterOfRightSide,this.grammar,this.listNullable,this.unitProduction);

                if(termProduction.get(characterOfRightSide) == null)
                    termProduction.put(characterOfRightSide, new ArrayList<String>());

                termProduction.get(characterOfRightSide).add(newNonTerminal);

            }
            return newNonTerminal;
        }
        else
            return null;


    }


}
