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

        //Transform Chomsky to Grammar
        toChomsky(inputGrammar);
        System.out.println(inputGrammar.toString());

        //Transform Grammar to PDA
        PDA pda = toPDA(inputGrammar);
        System.out.println(pda.toString());
    }

    /**
     * This method retrieves every line of the input grammar file,
     * and stores it in an arrayList which it returns.
     * @param filepath
     * @return ArrayList<String>
     */

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

    /**
     * This method takes an arrayList of rules, and creates a Grammar object
     * @param input
     * @return Grammar with rules
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

    /**
     * 1. A new starting grammar object that contains the original start variable
     * 2. Then we traverse through the global arraylist and check to see if there are any grammars that have an arraylist of one
     * and are the same as the variable if so delete that arraylist
     * 3. Then we traverse the global arraylist and if there are any arraylist with size one that also contain a variable
     * then we copy that variable's arraylist and add it to the grammar that originally contained the arraylist shown above
     * 4. We then traverse and check to see if there are any arraylists that have a size larger than three if so then create size/2 new grammars
     * storing the new grammar rules accordingly
     * 5. Then traverse and create a new arraylist that contains all of the terminals, then traverse and check
     * for any instance of the terminal and replace with a new grammar containing the single terminal
     * @param inputGrammar
     * @return
     */
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

    /**
     * Modifies input Grammar to have a new start rule S0, which points to the original start rule
     * @param inputGrammar
     */
    public static void addStartRule(Grammar inputGrammar){
        //Step 1
        Rule startRule = new Rule();
        startRule.name = "S0";
        Clause startClause = new Clause();
        startClause.name = inputGrammar.rules.get(0).name;
        startRule.addClause(startClause);
        inputGrammar.addRule(startRule);
    }

    /**
     * Modifies the input Grammar by replacing instances of epsilon
     * and adding clauses for the Rule instead
     *
     * Is called recursively until there is no more instances of epsilon
     * @param inputGrammar
     */
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
    }

    /**
     * Helper method for removeEmptyString. Generates different combinations of rules in which one character is removed
     * For example if A was being removed from AsA, it would generate: sA, As, s
     * @param inputGrammar
     * @param rule
     */
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

    /**
     * Helper method for countMatches. Suprised Java didn't have a built-in method for this.
     * @param s
     * @return
     */
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

    /**
     * First removes rules that call itself, such as A->A.
     * Then, removes other rules and replaces them with all the rules they point to
     * @param inputGrammar
     */
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

    /**
     * This method removes rules with three or more characters, as long as none of them are numbers.
     * New rules follow the convention of Uppercase A then a number. This excludes all those new rules.
     * @param inputGrammar
     * @param counter
     */
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

    /**
     * Since we ignored rules with numbers in them, such as A1A2A3, we have to deal with them.
     * This creates new rules that are broken down, smaller pieces of the new rules.
     * For example, A1A2A3, now becomes A4->A1A2, and there is a new rule of A4A3
     * However, A1A2A4 still exists as a rule.
     * @param inputGrammar
     * @param counter
     */
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

    /**
     * Since the rules were not deleted in the previous step, all rules including those in the A + number format
     * must be removed. This removes them.
     * @param inputGrammar
     */
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

    /**
     * Any rule with an uppercase letter and a lowercase letter will be replaced
     * For example, A-> aB becomes A->A1B ; A1 -> a
     * @param inputGrammar
     * @param counter
     */
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

    /**
     * Now that new rules have been created, the rules with and uppercase and
     * lowercase letter will be deleted.
     * @param inputGrammar
     */
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

    /**
     * Taking an input Grammar, we will first add the initial states for a flower power transformation
     * We then add the inital transitions between states
     * We then add letters to the alphabet of the PDA based on the grammar
     * We then add letters to the stack alphabet based on the grammar
     * We then add rules based on the input grammar
     * We finally add the last transition to pop the $ sign.
     * @param inputGrammar
     * @return PDA
     */
    public static PDA toPDA(Grammar inputGrammar){
        int counter = 0;
        PDA pda = new PDA();
        pda.states.add("q_start");
        pda.states.add("q1");
        pda.states.add("q_loop");
        pda.states.add("q_accept");
        pda.name = "Chomsky PDA";
        pda.transitions.add("q_start -> q1 : ε, ε -> $");
        pda.transitions.add("q1 -> q_loop : ε, ε -> S");
        //add the lower case terminals to the alphabet
        for(int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                char[] nameCharArray = name.toCharArray();
                if (Character.isLowerCase(nameCharArray[0]) && nameCharArray.length == 1) {
                    pda.addToAlphabet(name);
                }
            }
        }

        //Add all the rules used in the Grammar to the stack alphabet
        for(int i = 0; i < inputGrammar.rules.size(); i++) {
            pda.addToStackAlphabet(inputGrammar.rules.get(i).name);
        }

        //add the lower case terminals to the stack alphabet
        for(int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                char[] nameCharArray = name.toCharArray();
                if (Character.isLowerCase(nameCharArray[0]) && nameCharArray.length == 1) {
                    pda.addToStackAlphabet(name);
                }
            }
        }

        //add self loop transition for the remaining rules
        for(int i = 0; i < inputGrammar.rules.size(); i++) {
            for (int j = 0; j < inputGrammar.rules.get(i).clauses.size(); j++) {
                //if rule has one character
                String name = inputGrammar.rules.get(i).clauses.get(j).name;
                char[] nameCharArray = name.toCharArray();
                if (nameCharArray.length == 1) {
                    pda.transitions.add("q_loop -> q_loop : ε, " + inputGrammar.rules.get(i).name + " -> " + name);
                } else {
                    if (name.contains("A")) {
                        //There can be one A or two A's, so I have to deal with them individually
                        if (countMatches(name, "A") == 1) {
                            pda.transitions.add("q_loop -> q_" + counter + " : ε, " + inputGrammar.rules.get(i).name + " -> " + nameCharArray[nameCharArray.length - 1]);
                            String transition = "q_"+ counter + " -> q_loop : ε, ε -> A";
                            for (int k = 1; k < nameCharArray.length - 1; k++ ) {
                                transition = transition + nameCharArray[k];
                            }
                            pda.transitions.add(transition);
                            counter++;
                        } else {
                            String[] nameArray = name.split("A");
                            pda.transitions.add("q_loop -> q_" + counter + " : ε, " + inputGrammar.rules.get(i).name + " -> " + "A" + nameArray[2]);
                            pda.transitions.add("q_"+ counter + " -> q_loop : ε, ε -> " +"A"+ nameArray[1]);
                            counter++;
                        }
                    } else {
                        //The name is just two single letters
                        pda.transitions.add("q_loop -> q_" + counter + " : ε, " + inputGrammar.rules.get(i).name + " -> " + nameCharArray[1]);
                        pda.transitions.add("q_"+ counter + " -> q_loop : ε, ε -> " + nameCharArray[0]);
                        counter++;
                    }

                }
            }
        }

        pda.transitions.add("q_loop -> q_accept : ε, $ -> ε");
        return pda;
    }
}
