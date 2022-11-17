import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class GrammarInput {
    public static void main(String[] args){
        System.out.println("Please Enter a Filename: ");
        Scanner userInput = new Scanner(System.in);
        String filepath = userInput.nextLine();
        ArrayList<String> output = getValues(filepath);
        Grammar test = storeValue();
        System.out.println(test.toString());
        Grammar chomsky = Chomsky(test);
        System.out.println(chomsky.toString());
        PDA pda = toPDA(chomsky);
        System.out.println(pda.toString());
        System.out.println("We have therefore converted from a general grammar to a chomsky normal form grammar to a PDA");
        //TODO: Transformation to Chomsky
        //TODO: Transformation to PDA
        System.out.print(output.toString());
    }

    public static ArrayList<String> getValues(String filepath) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(filepath);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String strLine;
        ArrayList<String> lines = new ArrayList<String>();
        try {
            while ((strLine = reader.readLine()) != null) {
                lines.add(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    public static Grammar storeValue(){
        Grammar gram = new Grammar();
        System.out.println("In this method we will store the parsed values into a grammar object that contains the variable \n" +
                            "as well as a 2d Arraylist that contains the all of the rules from a given variable and if epsilons are \n" +
                            "found then ignore and continue, all of these grammar objects will be stored in a global arraylist\n");
        return gram;

    }

    public static Grammar Chomsky(Grammar gram){
        System.out.println("In this method we will be applying Chomsky Normal form to a given Grammar by:\n" +
                "1. A new starting grammar object that contains the original start variable \n" +
                "2. Then we traverse through the global arraylist and check to see if there are any grammars that have an arraylist of one \n" +
                "   and are the same as the variable if so delete that arraylist\n" +
                "3. Then we traverse the global arraylist and if there are any arraylist with size one that also contain a variable \n" +
                "   then we copy that variable's arraylist and add it to the grammar that originally contained the arraylist shown above\n" +
                "4. We then traverse and check to see if there are any arraylists that have a size larger than three if so then create size/2 new grammars \n" +
                "   storing the new grammar rules accordingly \n" +
                "5. Then traverse and create a new arraylist that contains all of the terminals, then traverse and check\n" +
                "   for any instance of the terminal and replace with a new grammar containing the single terminal\n");
        return gram;
    }
    public static PDA toPDA(Grammar gram){
        PDA ret = new PDA();
        System.out.println("To convert to PDA is simple the set of states only contains {start,Q1,Qloop and Qaccept}\n" +
                "The input alphabet contains all of the terminals, and the stack alphabet contains input alphabet, $, and variables\n" +
                "The table would just be start going to Q1 on an epsilon and push $ then from Q1 to Qloop on an epsilon and push the start variable \n" +
                "Then on Qloop we create all of the loops with terminals we pop a terminal, else we pop a variable then push remaining values within array\n" +
                "Then from Qloop we pop a $ and go to Q accept after that the start state is always start and accept state is always Qaccept");
        return ret;

    }
}
