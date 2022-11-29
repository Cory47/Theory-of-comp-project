import java.util.ArrayList;

public class Grammar {
    ArrayList<Rule> rules = new ArrayList<Rule>();
    public void addRule(Rule rule){
        this.rules.add(rule);
    }
    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < rules.size(); i++) {
            output = output + (rules.get(i).toString()) + "\n";
        }
        return output;
    }
}
