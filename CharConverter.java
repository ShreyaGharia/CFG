/**
 * Created by sg1961 on 4/22/2017.
 */
public class CharConverter {

    public String character;

    public CharConverter(String str)
    {
        this.character = str.substring(0, 1);
    }


    public String getChar()
    {
        return character;
    }
}
