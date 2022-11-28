import java.util.ArrayList;

public class Rule {
    String name;
    ArrayList<Clause> clauses = new ArrayList<Clause>();

    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }

    public String toString() {
        String clause = "";
        for (int i = 0; i < clauses.size(); i++) {
            if (i == 0) {
                clause = clause + " " + this.clauses.get(i).name + " ";
            } else {
                clause = clause + "| " + this.clauses.get(i).name + " ";
            }
        }

        return name + " ->" + clause;
    }
}
