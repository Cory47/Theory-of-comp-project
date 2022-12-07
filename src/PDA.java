import java.util.ArrayList;

public class PDA {
    String name;
    ArrayList<String> states = new ArrayList<String>();
    ArrayList<String> alphabet = new ArrayList<String>();
    ArrayList<String> stackAlphabet = new ArrayList<String>();
    ArrayList<String> transitions = new ArrayList<String>();
    public void addToAlphabet(String newString) {
        boolean addToAlphabet = true;
        for (int i = 0; i < alphabet.size(); i++){
            if (newString.equals(alphabet.get(i))) {
                addToAlphabet = false;
            }
        }
        if (addToAlphabet == true) {
            alphabet.add(newString);
            transitions.add("q_loop -> q_loop : " + newString + ", " + newString + " -> ε");
        }
    }
    public void addToStackAlphabet(String newString) {
        boolean addToStackAlphabet = true;
        for (int i = 0; i < stackAlphabet.size(); i++){
            if (newString.equals(stackAlphabet.get(i))) {
                addToStackAlphabet = false;
            }
        }
        if (addToStackAlphabet == true) {
            stackAlphabet.add(newString);
        }
    }
    @Override
    public String toString() {
        String pda = name;
        pda = pda + ": (\n{";

        //print the states
        for (int i = 0; i < states.size(); i++) {
            if (i < states.size() - 1) {
                pda = pda + states.get(i) + ", ";
            } else {
                pda = pda + states.get(i);
            }
        }
        pda = pda + "},\n{";

        //print the alphabet
        for (int i = 0; i < alphabet.size(); i++) {
            if (i < alphabet.size() - 1) {
                pda = pda + alphabet.get(i) + ", ";
            } else {
                pda = pda + alphabet.get(i);
            }
        }
        pda = pda + "},\n{";

        //print the stack alphabet
        for (int i = 0; i < stackAlphabet.size(); i++) {
            if (i < stackAlphabet.size() - 1) {
                pda = pda + stackAlphabet.get(i) + ", ";
            } else {
                pda = pda + stackAlphabet.get(i);
            }
        }
        pda = pda + ", $},\nδ,\n{q_start},\n{q_accept})\n\n";
        pda = pda + "δ: \n";
        for (int i = 0; i < transitions.size(); i++) {
            pda = pda + transitions.get(i) + "\n";
        }
        return pda;
    }
}
