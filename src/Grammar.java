import java.util.ArrayList;

public class Grammar {
    ArrayList<Rule> rules = new ArrayList<Rule>();
    public void addRule(Rule rule){
        this.rules.add(rule);
    }
    @Override
    public String toString() {
        return rules.toString();
    }
}
