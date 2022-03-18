import java.io.*;
import java.util.*;

public class Front_end {
    public ArrayList<String> nodes = new ArrayList<String>(); // START
    public ArrayList<String> treasures = new ArrayList<String>(); // GOLD
    public int num_steps;
    public Hashtable<String, Node> encoding = new Hashtable<String, Node>(); // START --> node START

    public Hashtable<String, Integer> atom_encoding = new Hashtable<String, Integer>(); // At(START,0) --> 1
    public int atom_num = 1;

    public ArrayList<String> at_atoms = new ArrayList<String>(); // At(START,0)
    public ArrayList<String> has_atoms = new ArrayList<String>(); // Has(GOLD,0)

    public Front_end() throws FileNotFoundException, IOException{
        read_inp();
        create_at_atoms();
        create_has_atoms();
        write_propositions();
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

    public void create_at_atoms(){
        for(String name : this.nodes){
            for(int i = 0; i <= this.num_steps; i++){
                String atom_name = "At("+name+","+i+")";
                this.at_atoms.add(atom_name);

                this.atom_encoding.put(atom_name, this.atom_num);
                this.atom_num++;
            }
        }
    }

    public void create_has_atoms(){
        for(String name : this.treasures){
            for(int i = 0; i <= this.num_steps; i++){
                String atom_name = "Has("+name+","+i+")";
                this.has_atoms.add(atom_name);
                
                this.atom_encoding.put(atom_name, this.atom_num);
                this.atom_num++;
            }
        }
    }

    public String get_time(String atom){
        return atom.substring(atom.length()-2, atom.length()-1);
    }

    public String get_name(String atom){
        return atom.substring(atom.indexOf("(")+1, atom.indexOf(","));
    }

    public String make_at_atom(String name, String time){
        return "At("+name+","+time+")";
    }

    public String make_has_atom(String name, String time){
        return "Has("+name+","+time+")";
    }

    public void write_propositions() throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter("fe_out.txt"));

        write_prop8(bw);
        write_prop6_7(bw);
        write_prop4(bw);
        write_prop3(bw);
        write_prop1(bw);
        write_prop2(bw);



        bw.write("0\n");

        bw.close();
    }

    public void write_prop8(BufferedWriter bw) throws IOException{
        for(String atom : this.has_atoms){
            String time = get_time(atom);
            if(time.equals(String.valueOf(this.num_steps))){
                bw.write(this.atom_encoding.get(atom)+"\n");
            }
        }
    }

    public void write_prop6_7(BufferedWriter bw) throws IOException{
        bw.write(this.atom_encoding.get("At(START,0)")+"\n");

        for(String treasure : this.treasures){
            for(String atom : this.has_atoms){
                String time = get_time(atom);
                String name = get_name(atom);

                if(time.equals("0") && name.equals(treasure)){
                    bw.write("-"+this.atom_encoding.get(atom)+"\n");
                }
            }
        }
    }

    public void write_prop4(BufferedWriter bw) throws IOException{
        for(int i = 1; i <= this.num_steps; i++){
            for(String treasure : this.treasures){
                for(String atom : this.has_atoms){
                    String a_name = get_name(atom);
                    int time = Integer.parseInt(get_time(atom));

                    if(a_name.equals(treasure) && time == i){
                        String prev_atom = this.has_atoms.get(this.has_atoms.indexOf(atom)-1);
                        bw.write("-"+this.atom_encoding.get(prev_atom)+" "+this.atom_encoding.get(atom)+"\n");
                    }
                }
            }
        }
    }

    public void write_prop3(BufferedWriter bw) throws IOException{
        for(int i = 0; i <= this.num_steps; i++){
            for(String node_name : this.encoding.keySet()){
                Node node = this.encoding.get(node_name);

                if(node.treasures != null){
                    for(String treasure : node.treasures){
                        String at_atom = make_at_atom(node_name, String.valueOf(i));
                        String has_atom = make_has_atom(treasure, String.valueOf(i));

                        bw.write("-"+this.atom_encoding.get(at_atom)+" "+this.atom_encoding.get(has_atom)+"\n");
                    }
                }
            }
        }
    }

    public void write_prop2(BufferedWriter bw) throws IOException{
        for(int i = 0; i < this.num_steps; i++){
            for(String node_name : this.encoding.keySet()){
                Node node = this.encoding.get(node_name);

                if(node.next != null){
                    String current_node = make_at_atom(node_name, String.valueOf(i));
                    String out = "-"+this.atom_encoding.get(current_node);

                    for(String next : node.next){
                        String at_next = make_at_atom(next, String.valueOf(i+1));
                        String code = String.valueOf(this.atom_encoding.get(at_next));
                        out += " "+code;

                        System.out.println("-"+current_node+" "+at_next);
                    }
                    
                    System.out.println();
                    bw.write(out+"\n");
                }
            }
        }
    }

    public void write_prop1(BufferedWriter bw) throws IOException{
        for(int i = 0; i <= this.num_steps; i++){
            for(String node1 : this.nodes){
                String at_atom1 = make_at_atom(node1, String.valueOf(i));

                for(String node2 : this.nodes){
                    if(!node2.equals(node1)){
                        String at_atom2 = make_at_atom(node2, String.valueOf(i));

                        bw.write("-"+this.atom_encoding.get(at_atom1)+" -"+this.atom_encoding.get(at_atom2)+"\n");
                    }
                }
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
