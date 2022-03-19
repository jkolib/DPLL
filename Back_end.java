import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

public class Back_end {
    public boolean no_sol = false; // no solution flag

    public ArrayList<String> truths = new ArrayList<String>(); // list for storing atoms assigned true
    public Hashtable<String, String[]> coding = new Hashtable<String, String[]>(); // dictionary for storing encoding
    public ArrayList<Out_node> output = new ArrayList<Out_node>(); // list for storing at atoms to be output

    public Back_end() throws IOException, FileNotFoundException{
        read_in();

        if(no_sol){
            return;
        }

        write_out();
    }

    public void read_in() throws IOException, FileNotFoundException{
        BufferedReader br = new BufferedReader(new FileReader("dpll_out.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("be_out.txt"));

        String text = br.readLine();

        // case where no solution is given
        if(text.equals("0")){
            bw.write("NO SOLUTION\n");
            this.no_sol = true;
            bw.close();
            br.close();
            return;
        }

        boolean back_end = false;
        while(text != null){ // iterating over each line of text
            // case where back end data has been reached
            if(text.equals("0")){
                back_end = true;
                text = br.readLine();
                continue;
            }

            String[] values = text.split(" ");

            // storing encoding of each number in dictionary
            if(back_end){
                this.coding.put(values[0], values);

                text = br.readLine();
                continue;
            }

            // adding true atoms to truths list
            if(values[1].equals("T")){
                this.truths.add(values[0]);
            }

            text = br.readLine();
        }

        bw.close();
        br.close();
    }

    public void write_out() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter("be_out.txt"));

        // iterating over true atoms
        for(String truth : this.truths){
            String[] values = this.coding.get(truth);

            // checking if there is code for a given true atom (meaning that it is an At atom)
            if(values != null){
                // creating Out_node for this true atom using its name and its time
                Out_node node = new Out_node(values[1], Integer.parseInt(values[2]));
                this.output.add(node); // storing Out_node in output list
            }
        }

        // sorting output list by time ascending
        Collections.sort(this.output, new C_out());

        // writing name of each node in ordered output list
        for(Out_node n : this.output){
            bw.write(n.name+" ");
        }

        bw.close();
    }
}

// Out_node class for use with comparator
class Out_node{
    String name;
    int time;

    public Out_node(String name, int time){
        this.name = name;
        this.time = time;
    }
}

// comparator for use on outputs list
class C_out implements Comparator<Out_node>{
    @Override
    public int compare(Out_node o1, Out_node o2) {
        if(o1.time == o2.time){
            return 0;
        }
        else if(o1.time > o2.time){
            return 1;
        }
        else{
            return -1;
        }
    }

}