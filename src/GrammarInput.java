import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GrammarInput {
    public static void main(String[] args){
        System.out.println("Please Enter a Filename: ");
        Scanner userInput = new Scanner(System.in);
        String filepath = userInput.nextLine();
        ArrayList<String> output = getValues(filepath);
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
}
