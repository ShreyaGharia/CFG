import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
/**
 Created by sg1961 on 4/22/2017.
 **/


public class Production
{
    private CharConverter leftSide;
    private ArrayList<String> rightSide;
    private boolean isNullable;
    private boolean eliminateEpsilon;
    public ArrayList<TempStorage<String, Integer>> usedProduction;

    public Production()
    {
        this.rightSide = new ArrayList<>();
        this.usedProduction = new ArrayList<>();
        this.isNullable = false;
        this.eliminateEpsilon = true;
    }

    public Production(String leftSide, Grammar grammar, HashMap<String, Production> maps)
    {
        this.rightSide = new ArrayList<>();
        this.usedProduction = new ArrayList<>();
        this.isNullable = false;
        this.leftSide = new CharConverter(leftSide);
        grammar.addProduction(this);
        maps.put(leftSide, this);
        this.eliminateEpsilon = true;
    }

    public CharConverter getLeftSide() {
        return leftSide;
    }

    public void addProduction(String string, Grammar g, ArrayList<String> nullTerm, ArrayList<TempStorage<String,String>> unitProduction)
    {
        if(string.equals(leftSide.character))
            return;

        if(Pattern.matches("[A-Z]", string))
            unitProduction.add(new TempStorage<>(leftSide.character, string));

        if(string.equals("e")){
            isNullable = true;
            nullTerm.add(leftSide.getChar());
            if (eliminateEpsilon){
                if(leftSide.character.equals(g.getProductions().get(0).getLeftSide().character))
                    rightSide.add(string);
                return;
            }
            rightSide.add(string);
        }
        else
            rightSide.add(string);
    }

    public ArrayList<String> getRightSide() {
        return rightSide;
    }

    public void setLeftSide(CharConverter leftSide) {
        this.leftSide = leftSide;
    }

    public Boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        this.isNullable = nullable;
    }

    public ArrayList<TempStorage<String, Integer>> getUsedProduction() {
        return usedProduction;
    }

    public void addUsedProduction(TempStorage<String, Integer> usage) {
        this.usedProduction.add(usage);
    }
}
