import java.io.*;
import java.util.*;

public class Front_end {
    public ArrayList<String> nodes = new ArrayList<String>();
    public ArrayList<String> treasures = new ArrayList<String>();
    public int num_steps;
    public Hashtable<String, Node> encoding = new Hashtable<String, Node>();

    public Front_end() throws FileNotFoundException, IOException{
        read_inp();

        
    }

    public void read_inp() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("fe_in.txt"));

        String in_text = br.readLine();
        int line = 1;
        while(in_text != null){
            switch(line){
                case 1:
                    for(String node : in_text.split(" ")){
                        this.nodes.add(node);
                    }
                    line++;
                    in_text = br.readLine();
                    break;

                case 2:
                    for(String treasure : in_text.split(" ")){
                        this.treasures.add(treasure);
                    }
                    line++;
                    in_text = br.readLine();
                    break;

                case 3:
                    this.num_steps = Integer.parseInt(in_text);
                    line++;
                    in_text = br.readLine();
                    break;
                
                default:
                    String name = "";
                    ArrayList<String> c_treasures = new ArrayList<String>();
                    ArrayList<String> next_nodes = new ArrayList<String>();

                    String[] code_array = in_text.split(" ");
                    boolean at_treasure = false;
                    for(int i = 0; i < code_array.length; i++){
                        if(i == 0){
                            name = code_array[i];
                            continue;
                        }

                        if(code_array[i].equals("NEXT")){
                            at_treasure = false;
                            continue;
                        }

                        if(i == 1){
                            at_treasure = true;
                            continue;
                        }

                        if(at_treasure){
                            c_treasures.add(code_array[i]);
                            continue;
                        }

                        next_nodes.add(code_array[i]);
                    }

                    Node new_node = new Node(name, c_treasures, next_nodes);

                    this.encoding.put(name, new_node);

                    in_text = br.readLine();
            } // switch
        } // while

        br.close();
    }
}

class Node{
    public String name;
    public ArrayList<String> treasures;
    public ArrayList<String> next;

    public Node(){
        this.name = "";
        this.treasures = new ArrayList<String>();
        this.next = new ArrayList<String>();
    }

    public Node(String name, ArrayList<String> treasures, ArrayList<String> next_nodes){
        this.name = name;
        this.treasures = new ArrayList<String>(treasures);
        this.next = new ArrayList<String>(next_nodes);
    }

}
