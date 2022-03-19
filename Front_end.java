import java.io.*;
import java.util.*;

public class Front_end {
    public ArrayList<String> nodes = new ArrayList<String>(); // list for storing the name of each node
    public ArrayList<String> treasures = new ArrayList<String>(); // list for storing the name of each treasure
    public int num_steps;
    public Hashtable<String, Node> encoding = new Hashtable<String, Node>(); // dictionary for storing Node objects

    // dictionary for storing the encoding for each atom
    public Hashtable<String, Integer> atom_encoding = new Hashtable<String, Integer>();
    public int atom_num = 1;

    // lists for storing At(N,I) and Has(T,I) atoms separately
    public ArrayList<String> at_atoms = new ArrayList<String>();
    public ArrayList<String> has_atoms = new ArrayList<String>();

    public Front_end() throws FileNotFoundException, IOException{
        read_inp();
        create_at_atoms();
        create_has_atoms();
        write_propositions();
    }

    public void read_inp() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("fe_in.txt"));

        String in_text = br.readLine();
        int line = 1; // counter to keep track of which line we're on
        while(in_text != null){ // iterating over each line of the input file
            switch(line){
                // adding nodes to list of nodes
                case 1:
                    for(String node : in_text.split(" ")){
                        this.nodes.add(node);
                    }
                    line++;
                    in_text = br.readLine();
                    break;

                // adding treasures to list of treasures
                case 2:
                    for(String treasure : in_text.split(" ")){
                        this.treasures.add(treasure);
                    }
                    line++;
                    in_text = br.readLine();
                    break;

                // setting num_steps equal to given number of steps
                case 3:
                    this.num_steps = Integer.parseInt(in_text);
                    line++;
                    in_text = br.readLine();
                    break;
                
                // creating Nodes from info given about nodes
                default:
                    // containers for holding Node info
                    String name = "";
                    ArrayList<String> c_treasures = new ArrayList<String>();
                    ArrayList<String> next_nodes = new ArrayList<String>();

                    String[] code_array = in_text.split(" ");
                    boolean at_treasure = false;
                    for(int i = 0; i < code_array.length; i++){ // iterating over item in line
                        // storing Node name
                        if(i == 0){
                            name = code_array[i];
                            continue;
                        }

                        // checking if section about NEXT nodes is reached
                        if(code_array[i].equals("NEXT")){
                            at_treasure = false;
                            continue;
                        }

                        // triggering flag when TREASURE reached
                        if(i == 1){
                            at_treasure = true;
                            continue;
                        }

                        // adding treasures to c_treasures list 
                        if(at_treasure){
                            c_treasures.add(code_array[i]);
                            continue;
                        }

                        // by default adding next nodes to next_nodes list
                        next_nodes.add(code_array[i]);
                    }

                    // creating new Node from parsed info
                    Node new_node = new Node(name, c_treasures, next_nodes);

                    // storing Node in dictionary where Node name is the key
                    this.encoding.put(name, new_node);

                    in_text = br.readLine();
            } // switch
        } // while

        br.close();
    }

    public void create_at_atoms(){
        // iterating over all node names
        for(String name : this.nodes){
            // iterating over time starting from 0
            for(int i = 0; i <= this.num_steps; i++){
                // creating At atom and storing in at_atoms list
                String atom_name = "At("+name+","+i+")";
                this.at_atoms.add(atom_name);

                // storing encoding of atom in atom_encoding dictionary
                // each atom is assigned a number starting at 1
                this.atom_encoding.put(atom_name, this.atom_num);
                this.atom_num++;
            }
        }
    }

    public void create_has_atoms(){
        // iterating over all treasures
        for(String name : this.treasures){
            // iterating over time starting at 0
            for(int i = 0; i <= this.num_steps; i++){
                // creating Has atom and storing in has_atoms list
                String atom_name = "Has("+name+","+i+")";
                this.has_atoms.add(atom_name);
                
                // storing encoding of atom in atom_encoding
                this.atom_encoding.put(atom_name, this.atom_num);
                this.atom_num++;
            }
        }
    }

    public String get_time(String atom){
        return atom.substring(atom.length()-2, atom.length()-1); // returns time from an atom
    }

    public String get_name(String atom){
        return atom.substring(atom.indexOf("(")+1, atom.indexOf(",")); // returns name of atom
    }

    public String make_at_atom(String name, String time){
        return "At("+name+","+time+")"; // returns string of at atom using given name and time
    }

    public String make_has_atom(String name, String time){
        return "Has("+name+","+time+")"; // returns string of has atom using given name and time
    }

    public void write_propositions() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter("fe_out.txt"));

        // writing propositions
        write_prop1(bw);
        write_prop2(bw);
        write_prop3(bw);
        write_prop4(bw);
        write_prop5(bw);
        write_prop6_7(bw);
        write_prop8(bw);

        // writing separating character for back end
        bw.write("0\n");

        // writing generated encoding
        write_encoding(bw);

        bw.close();
    }

    // at time K, player has all treasures
    public void write_prop8(BufferedWriter bw) throws IOException{
        // iterating over all has atoms
        for(String atom : this.has_atoms){
            String time = get_time(atom);

            // if time of has atom equals max number of steps (K), writing encoding
            if(time.equals(String.valueOf(this.num_steps))){
                bw.write(this.atom_encoding.get(atom)+"\n");
            }
        }
    }

    // player is at START at time 0 and at time 0, player has no treasures
    public void write_prop6_7(BufferedWriter bw) throws IOException{
        bw.write(this.atom_encoding.get("At(START,0)")+"\n"); // writing encoding of at START at time 0

        // iterating over all treasures
        for(String treasure : this.treasures){
            // iterating over all has atoms
            for(String atom : this.has_atoms){
                String time = get_time(atom);
                String name = get_name(atom);

                // writing atoms who have time = 0 for each treasure
                if(time.equals("0") && name.equals(treasure)){
                    bw.write("-"+this.atom_encoding.get(atom)+"\n");
                }
            }
        }
    }

    // if the player does not have treasure T at time I-1 and has T at time I, then at time I they must be at one of the nodes M1 ... Mq
    public void write_prop5(BufferedWriter bw) throws IOException{
        // iterating over all times starting at time = 1
        for(int i = 1; i <= this.num_steps; i++){
            // iterating over all treasures
            for(String treasure : this.treasures){
                boolean write = false; // flag for whether to write or not

                // creating has atoms for treasure T at time i and i-1
                String t_atom1 = make_has_atom(treasure, String.valueOf(i-1));
                String t_atom2 = make_has_atom(treasure, String.valueOf(i));

                // output string to store clause to write
                String out = this.atom_encoding.get(t_atom1)+" -"+this.atom_encoding.get(t_atom2);

                // iterating over Nodes
                for(String node_name : this.encoding.keySet()){
                    Node node = this.encoding.get(node_name);

                    // if a Node contains treasure T, add its At atom to output string
                    if(node.treasures.contains(treasure)){
                        write = true;
                        String n_atom = make_at_atom(node_name, String.valueOf(i));
                        out += " "+this.atom_encoding.get(n_atom);
                    }
                }

                if(write){
                    bw.write(out+"\n");
                }
            }
        }
    }

    // if player has treasure at time i-1, then player has treasure at time i
    public void write_prop4(BufferedWriter bw) throws IOException{
        // iterating over all times starting at time = 1
        for(int i = 1; i <= this.num_steps; i++){
            // iterating over treasures
            for(String treasure : this.treasures){
                // iterating over has atoms
                for(String atom : this.has_atoms){
                    String a_name = get_name(atom);
                    int time = Integer.parseInt(get_time(atom));

                    // writing code current has atom is for current treasure and its time == current time
                    if(a_name.equals(treasure) && time == i){
                        String prev_atom = this.has_atoms.get(this.has_atoms.indexOf(atom)-1); // getting atom for Has(T,I-1) since has atoms were stored grouped by treasure
                        bw.write("-"+this.atom_encoding.get(prev_atom)+" "+this.atom_encoding.get(atom)+"\n");
                    }
                }
            }
        }
    }

    // suppose that treasure T is located at node N. Then if the player is at N at time I, then at time I the player has T
    public void write_prop3(BufferedWriter bw) throws IOException{
        // iterating over all times
        for(int i = 0; i <= this.num_steps; i++){
            // iterating over Nodes
            for(String node_name : this.encoding.keySet()){
                Node node = this.encoding.get(node_name);

                if(node.treasures != null){
                    // iterating over current Node's treasures
                    for(String treasure : node.treasures){
                        String at_atom = make_at_atom(node_name, String.valueOf(i));
                        String has_atom = make_has_atom(treasure, String.valueOf(i));

                        bw.write("-"+this.atom_encoding.get(at_atom)+" "+this.atom_encoding.get(has_atom)+"\n");
                    }
                }
            }
        }
    }

    // player must move on edges
    public void write_prop2(BufferedWriter bw) throws IOException{
        // iterating over all times
        for(int i = 0; i < this.num_steps; i++){
            // iterating over Nodes
            for(String node_name : this.encoding.keySet()){
                Node node = this.encoding.get(node_name);

                if(node.next != null){
                    String current_node = make_at_atom(node_name, String.valueOf(i)); // at atom representation of current Node
                    String out = "-"+this.atom_encoding.get(current_node); // string container for holding clause

                    // iterating over current Node's next nodes list
                    for(String next : node.next){
                        String at_next = make_at_atom(next, String.valueOf(i+1)); // at atom representation of next node
                        String code = String.valueOf(this.atom_encoding.get(at_next)); // code for above representation
                        out += " "+code;
                    }

                    bw.write(out+"\n");
                }
            }
        }
    }

    // player is only at one place at a time
    public void write_prop1(BufferedWriter bw) throws IOException{
        ArrayList<String> a_list = new ArrayList<String>(); // list for storing clauses and their equivalent representations already written

        // iterating over all times
        for(int i = 0; i <= this.num_steps; i++){
            // iterating over all nodes
            for(String node1 : this.nodes){
                String at_atom1 = make_at_atom(node1, String.valueOf(i)); // making at atom of current node1

                // iterating over all nodes again
                for(String node2 : this.nodes){
                    String at_atom2 = make_at_atom(node2, String.valueOf(i)); // making at atom of current node2

                    // getting all possible representations of the atom1 atom2 combination
                    String combo1 = at_atom1+" "+at_atom2;
                    String combo2 = at_atom2+" "+at_atom1;

                    // writing coding if combo or its variation wasn't already written
                    if(!node2.equals(node1) && !a_list.contains(combo1) && !a_list.contains(combo2)){
                        a_list.add(combo1);
                        a_list.add(combo2);
                        bw.write("-"+this.atom_encoding.get(at_atom1)+" -"+this.atom_encoding.get(at_atom2)+"\n");
                    }
                }
            }
        }
    }

    public void write_encoding(BufferedWriter bw) throws IOException{
        // iterating over all coded values for atoms
        for(String atom : this.atom_encoding.keySet()){
            // only writing at atoms
            if(atom.substring(0, 2).equals("At")){
                String name = get_name(atom);
                String time = get_time(atom);
                String num = String.valueOf(this.atom_encoding.get(atom));

                bw.write(num+" "+name+" "+time+"\n");
            }
        }
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
