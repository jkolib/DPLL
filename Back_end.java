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
    public boolean no_sol = false;
    public ArrayList<String> truths = new ArrayList<String>();
    public Hashtable<String, String[]> coding = new Hashtable<String, String[]>();
    public ArrayList<Out_node> output = new ArrayList<Out_node>();

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

        if(text.equals("0")){
            bw.write("NO SOLUTION\n");
            this.no_sol = true;
            bw.close();
            br.close();
            return;
        }

        boolean back_end = false;
        while(text != null){
            if(text.equals("0")){
                back_end = true;
            }

            String[] values = text.split(" ");

            if(back_end){
                this.coding.put(values[0], values);

                text = br.readLine();
                continue;
            }

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

        for(String truth : this.truths){
            String[] values = this.coding.get(truth);

            if(values != null){
                Out_node node = new Out_node(values[1], Integer.parseInt(values[2]));
                this.output.add(node);
            }
        }

        Collections.sort(this.output, new C_out());

        for(Out_node n : this.output){
            bw.write(n.name+" ");
        }

        bw.close();
    }
}

class Out_node{
    String name;
    int time;

    public Out_node(String name, int time){
        this.name = name;
        this.time = time;
    }
}

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