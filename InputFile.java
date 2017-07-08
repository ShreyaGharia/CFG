import java.io.*;
import java.util.ArrayList;
/**
 Created by sg1961 on 4/22/2017.
 **/
public class InputFile {
    public ArrayList<String> inputFile = new ArrayList<String>();
    public  boolean  importFile(String fileName) throws IOException {
        String inputGrammar;
        try {

            File fileIn = new File(fileName);
            Reader IN = new FileReader(fileIn);
            LineNumberReader reader = new LineNumberReader(IN);

            while (reader.ready()) {
                this.inputFile.add(reader.readLine().concat(" "));
                inputGrammar=reader.readLine().concat(" ");
                CNFConverter cnfConverter = new CNFConverter(inputGrammar);
                Grammar cnfGrammar = cnfConverter.convert();

                if(cnfGrammar != null)
                    cnfConverter.executeParser();
                else
                    System.out.println("\nPlease insert another grammar \n");
            }

            reader.close();
            IN.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File not Found" + e.getMessage());
            return false;
        }
    }
}
