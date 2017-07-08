import java.util.ArrayList;
/**
 Created by sg1961 on 4/22/2017.
 **/

public class Grammar
{
    public String grammarString;
    public String grammarA;
    public ArrayList<String> alphabet;
    public ArrayList<Production> productions;

    public Grammar(String inputGrammar)
    {
        this.grammarString = inputGrammar;
        this.alphabet = new ArrayList<>();
        this.productions = new ArrayList<>();
    }

    public String getGrammarString() {
        return grammarString;
    }

    public String getGrammarA() {
        return grammarA;
    }

    public void setGrammarA(String grammarA) {
        this.grammarA = grammarA;
    }

    public ArrayList<Production> getProductions() {
        return productions;
    }

    public void addProduction(Production p)
    {
        this.productions.add(p);
    }

}
