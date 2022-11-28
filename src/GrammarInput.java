import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class GrammarInput {
    public static void main(String[] args){

        //Get input from file
        System.out.println("Please Enter a Filename: ");
        Scanner userInput = new Scanner(System.in);
        String filepath = userInput.nextLine();
        ArrayList<String> input = getValues(filepath);

        //Convert Input File to Grammar
        Grammar inputGrammar = storeValue(input);
        //TODO: Transform Chomsky to Grammar
        Grammar chomskyFormGrammar = toChomsky(inputGrammar);
        //TODO: Transform Chomsky to PDA
        PDA pda = toPDA(chomskyFormGrammar);
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

    /** In this method, we will store the parsed values into a grammar object that contains the variable
        as well as a 2d Arraylist that contains the all of the rules from a given variable and if epsilons are
        found then ignore and continue, all of these grammar objects will be stored in a global arraylist
     */
    public static Grammar storeValue(ArrayList<String> input){
        Grammar grammar = new Grammar();
        for (int i = 0; i < input.size(); i++) {
            //Add rule names
            String temp = input.get(i);
            String[] tempArray = temp.split(" ");
            Rule newRule = new Rule();
            newRule.name = tempArray[0];
            //Add clauses
            String name = "";

            for (int j = 2; j < tempArray.length; j++) {
                if (tempArray[j].equals("|")) {
                    Clause newClause = new Clause();
                    newClause.name = name;
                    newRule.addClause(newClause);
                    name = "";
                } else if (j == tempArray.length - 1) {
                    name = name + tempArray[j];
                    Clause newClause = new Clause();
                    newClause.name = name;
                    newRule.addClause(newClause);
                    name = "";
                } else {
                    if (j < tempArray.length - 1) {
                        name = name + tempArray[j];
                    }
                }
            }
            grammar.addRule(newRule);
        }
        return grammar;
    }
    /**In this method we will be applying Chomsky Normal form to a given Grammar by:
        1. A new starting grammar object that contains the original start variable
        2. Then we traverse through the global arraylist and check to see if there are any grammars that have an arraylist of one
        and are the same as the variable if so delete that arraylist
        3. Then we traverse the global arraylist and if there are any arraylist with size one that also contain a variable
        then we copy that variable's arraylist and add it to the grammar that originally contained the arraylist shown above
        4. We then traverse and check to see if there are any arraylists that have a size larger than three if so then create size/2 new grammars
        storing the new grammar rules accordingly
        5. Then traverse and create a new arraylist that contains all of the terminals, then traverse and check
        for any instance of the terminal and replace with a new grammar containing the single terminal*/
    public static Grammar toChomsky(Grammar inputGrammar){

        Grammar chomskyGrammar = new Grammar();
        //Step 1
        addStartRule(chomskyGrammar, inputGrammar);
        //Step 2
        removeEmptyString(chomskyGrammar, inputGrammar);

        System.out.println(inputGrammar.toString());
        return chomskyGrammar;
    }
    public static PDA toPDA(Grammar gram){
        PDA ret = new PDA();
        /*System.out.println("To convert to PDA is simple the set of states only contains {start,Q1,Qloop and Qaccept}\n" +
                "The input alphabet contains all of the terminals, and the stack alphabet contains input alphabet, $, and variables\n" +
                "The table would just be start going to Q1 on an epsilon and push $ then from Q1 to Qloop on an epsilon and push the start variable \n" +
                "Then on Qloop we create all of the loops with terminals we pop a terminal, else we pop a variable then push remaining values within array\n" +
                "Then from Qloop we pop a $ and go to Q accept after that the start state is always start and accept state is always Qaccept");*/
        return ret;
    }
    public static void addStartRule(Grammar chomskyGrammar, Grammar inputGrammar){
        //Step 1
        Rule startRule = new Rule();
        startRule.name = "S0";
        Clause startClause = new Clause();
        startClause.name = inputGrammar.rules.get(0).name;
        startRule.addClause(startClause);
        chomskyGrammar.addRule(startRule);
    }
    private static void removeEmptyString(Grammar chomskyGrammar, Grammar inputGrammar) {
        //Step 2
        //loop through and look for empty strings
        //if found, create rules
    }
}
