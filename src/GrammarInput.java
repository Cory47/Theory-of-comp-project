import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
public class GrammarInput {
    public static void main(String[] args){

        //Get input from file
        System.out.println("Please Enter a Filename: ");
        Scanner userInput = new Scanner(System.in);
        String filepath = "Input.txt";
        ArrayList<String> input = getValues(filepath);

        //Convert Input File to Grammar
        Grammar inputGrammar = storeValue(input);
        //Transform Chomsky to Grammar
        toChomsky(inputGrammar);
        System.out.print(inputGrammar.toString());
        //TODO: Transform Chomsky to PDA
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
        Counter counter = new Counter();
        counter.num = 0;

        Grammar chomskyGrammar = new Grammar();
        //Step 1
        addStartRule(inputGrammar);
        //Step 2
        removeEmptyString(inputGrammar);
        //Step 3
        removeUppercaseSingleLetters(inputGrammar);
        //Step 4
        removeRulesGreaterThanThreeItems(inputGrammar, counter);
        breakDownNewRules(inputGrammar, counter);
        removeTooLongRules(inputGrammar);

        //Step 5
        replaceLowercaseMixedClauses(inputGrammar, counter);
        removeMixedClauses(inputGrammar);
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
    public static void addStartRule(Grammar inputGrammar){
        //Step 1
        Rule startRule = new Rule();
        startRule.name = "S0";
        Clause startClause = new Clause();
        startClause.name = inputGrammar.rules.get(0).name;
        startRule.addClause(startClause);
        inputGrammar.addRule(startRule);
    }
    private static void removeEmptyString(Grammar inputGrammar) {
        //Step 2
        //loop through and look for empty strings as a clause
        String name;
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                name = inputGrammar.rules.get(i).clauses.get(j).name;
                if (name.contains("ε")) {
                    //remove the ε rule.
                    inputGrammar.rules.get(i).clauses.remove(j);
                    String rule = inputGrammar.rules.get(i).name;
                    replaceEmpty(inputGrammar, rule);
                    //Find single Uppercase letters and replace with Epsilon
                    for (int k = 0; k < inputGrammar.rules.size(); k++) {
                        for (int l = 0; l < inputGrammar.rules.get(k).clauses.size(); l++) {
                            if (inputGrammar.rules.get(k).clauses.get(l).name.equals(inputGrammar.rules.get(i).name)) {
                                Clause newClause = new Clause();
                                newClause.name = "ε";
                                inputGrammar.rules.get(k).addClause(newClause);
                                //Find all instances of the rule and create duplicates
                                removeEmptyString(inputGrammar);
                            }
                        }
                    }


                }
            }
        }
        //if found, return the rule
        //Find all instances of the rule, and create duplicate rules without that character
    }

    private static void replaceEmpty(Grammar inputGrammar, String rule) {
        String name;
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                name = inputGrammar.rules.get(i).clauses.get(j).name;
                if (name.contains(rule)){
                    //check how many times the rule is contained in the String
                    int matches = countMatches(name, rule);
                    //find index of every match
                    int index = 0;
                    for (int k = 0; k < matches; k++) {
                        index = name.indexOf(rule, index);
                        //create a new rule where that particular index is missing from the string
                        char[] temp = name.toCharArray();
                        String newRule = "";
                        for (int l = 0; l < temp.length; l++) {
                            if (l != index) {
                                newRule = newRule.concat("" + temp[l]);
                            }
                        }
                        Clause newClause = new Clause();
                        newClause.name = newRule;
                        inputGrammar.rules.get(i).addClause(newClause);
                        index++;
                    }

                }
            }
        }
    }
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
    private static int countMatches(String text, String str){
        if (isEmpty(text) || isEmpty(str)) {
            return 0;
        }

        int index = 0, count = 0;
        while (true)
        {
            index = text.indexOf(str, index);
            if (index != -1)
            {
                count ++;
                index += str.length();
            }
            else {
                break;
            }
        }

        return count;
    }
    private static void removeUppercaseSingleLetters(Grammar inputGrammar) {
        //Case for self-referential clause
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                if (inputGrammar.rules.get(i).clauses.get(j).name.equals(inputGrammar.rules.get(i).name)) {
                    inputGrammar.rules.get(i).clauses.remove(j);
                }
            }
        }
        //Case for single Uppercase Letter
        String letter;
        for (int k = 0; k < inputGrammar.rules.size(); k++){
            letter = inputGrammar.rules.get(k).name;
            for (int i = 0; i < inputGrammar.rules.size(); i++) {
                for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                    if (letter.equals(inputGrammar.rules.get(i).clauses.get(j).name)) {
                        //remove the letter
                        inputGrammar.rules.get(i).clauses.remove(j);
                        //add new rules for everything in the new letter
                        for (int l = 0; l < inputGrammar.rules.get(k).clauses.size(); l++) {
                            Clause newClause = new Clause();
                            newClause.name = inputGrammar.rules.get(k).clauses.get(l).name;
                            inputGrammar.rules.get(i).addClause(newClause);
                        }
                    }
                }
            }
        }
    }

    private static void removeRulesGreaterThanThreeItems(Grammar inputGrammar, Counter counter) {
        counter.num = 0;
        String newRuleName = "A" + counter.num;
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                if (    name.length() > 2 &&
                        !name.contains("1") &&
                        !name.contains("2") &&
                        !name.contains("3") &&
                        !name.contains("5") &&
                        !name.contains("6") &&
                        !name.contains("7") &&
                        !name.contains("8") &&
                        !name.contains("9") &&
                        !name.contains("0")) {
                    //determine what has too many letters
                    String temp = name;
                    String[] splits = new String[0];
                    splits = temp.split("(?<=\\G..)");

                    //remove the problematic clauses
                    inputGrammar.rules.get(i).clauses.remove(j);

                    ArrayList<String> concatRuleNames = new ArrayList<String>();
                    for (int k = 0; k < splits.length; k++) {
                        //create rules to compensate for the losses
                        Rule newRule = new Rule();
                        newRule.name = newRuleName;

                        concatRuleNames.add(newRuleName);
                        Clause newClause = new Clause();
                        newClause.name = splits[k];
                        newRule.addClause(newClause);
                        inputGrammar.addRule(newRule);
                        counter.num++;
                        newRuleName = "A" + counter.num;
                    }

                    //add rules as clauses to the original rules
                    String newClauseName = "";
                    for(int l = 0; l < concatRuleNames.size(); l++) {
                        newClauseName = newClauseName + concatRuleNames.get(l);
                    }

                    Clause newClause = new Clause();
                    newClause.name = newClauseName;
                    inputGrammar.rules.get(i).addClause(newClause);
                }
            }
        }
        //Do a final loop through to determine if everything is in format. If not, call the function again.
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                if (    name.length() > 2 &&
                        !name.contains("1") &&
                        !name.contains("2") &&
                        !name.contains("3") &&
                        !name.contains("5") &&
                        !name.contains("6") &&
                        !name.contains("7") &&
                        !name.contains("8") &&
                        !name.contains("9") &&
                        !name.contains("0")) {
                    removeRulesGreaterThanThreeItems(inputGrammar, counter);
                }
            }
        }
    }
    private static void breakDownNewRules(Grammar inputGrammar, Counter counter) {
        String newRuleName = "A" + counter.num;
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                int matches = countMatches(name, "A");
                if (matches > 3) {
                    String[] temp = name.split("A");
                    ArrayList<String> temp2 = new ArrayList<>();
                    //take two elements in the array, and put them together in a rule
                    //Smash an A between them
                    for (int k = 0; k < temp.length; k++){
                        if (temp[k] != "") {
                            temp2.add(temp[k]);
                        }
                    }
                    for (int k = 0; k < temp2.size(); k = k + 2){
                        Clause clause = new Clause();
                        if (k+1 < temp2.size()) {
                            //create the new rule
                            clause.name = "A" + temp2.get(k) + "A" + temp2.get(k + 1);
                            Rule newRule = new Rule();
                            newRuleName = "A" + counter.num;
                            newRule.name = newRuleName;
                            counter.num++;
                            newRule.addClause(clause);
                            inputGrammar.addRule(newRule);
                            Clause newClause = new Clause();
                            newClause.name = newRuleName;
                            inputGrammar.rules.get(i).addClause(newClause);
                        } else {
                            //create the new rule
                            clause.name = "A" + temp2.get(k);
                            Rule newRule = new Rule();
                            newRuleName = "A" + counter.num;
                            newRule.name = newRuleName;
                            counter.num++;
                            newRule.addClause(clause);
                            inputGrammar.addRule(newRule);
                            Clause newClause = new Clause();
                            newClause.name = newRuleName;
                            inputGrammar.rules.get(i).addClause(newClause);
                        }
                    }
                }
            }
        }
    }
    private static void removeTooLongRules(Grammar inputGrammar) {
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                if (countMatches(name, "A") > 3) {
                    inputGrammar.rules.get(i).clauses.remove(j);
                    removeTooLongRules(inputGrammar);
                }
            }
        }
    }
    private static void replaceLowercaseMixedClauses(Grammar inputGrammar, Counter counter) {
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                char[] chars = name.toCharArray();
                boolean hasUpper = false;
                boolean hasLower = false;
                for (int k = 0; k < chars.length; k++) {
                    if (Character.isUpperCase(chars[k])) {
                        hasUpper = true;
                    }
                    if (Character.isLowerCase(chars[k])) {
                        hasLower = true;
                    }
                }
                if (hasUpper && hasLower){
                    //if first letter is lower, replace it
                    if (Character.isLowerCase(chars[0])) {
                        //create new clause
                        String newName = "A" + counter.num + chars[1];
                        Clause clause = new Clause();
                        clause.name = newName;
                        inputGrammar.rules.get(i).addClause(clause);
                        //create new rule
                        Rule newRule = new Rule();
                        newRule.name = "A" + counter.num;
                        Clause anotherClause = new Clause();
                        anotherClause.name = "" + chars[0];
                        newRule.addClause(anotherClause);
                        inputGrammar.addRule(newRule);
                        counter.num++;
                    } else {
                        //create new clause
                        String newName = "A" + counter.num + chars[0];
                        Clause clause = new Clause();
                        clause.name = newName;
                        inputGrammar.rules.get(i).addClause(clause);
                        //create new rule
                        Rule newRule = new Rule();
                        newRule.name = "A" + counter.num;
                        Clause anotherClause = new Clause();
                        anotherClause.name = "" + chars[1];
                        newRule.addClause(anotherClause);
                        inputGrammar.addRule(newRule);
                        counter.num++;
                    }
                }
            }
        }
    }
    private static void removeMixedClauses(Grammar inputGrammar) {
        for (int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                char[] chars = name.toCharArray();
                boolean hasUpper = false;
                boolean hasLower = false;
                for (int k = 0; k < chars.length; k++) {
                    if (Character.isUpperCase(chars[k])) {
                        hasUpper = true;
                    }
                    if (Character.isLowerCase(chars[k])) {
                        hasLower = true;
                    }
                }
                if (hasUpper && hasLower){
                    inputGrammar.rules.get(i).clauses.remove(j);
                    removeMixedClauses(inputGrammar);
                }
            }
        }
    }
}
