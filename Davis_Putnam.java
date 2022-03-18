import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Davis_Putnam{
    // for storing inputs
    public ArrayList<String> inp = new ArrayList<String>();
    public ArrayList<String> inp_atoms = new ArrayList<String>();
    public Hashtable<String, String> truth_dict = new Hashtable<String,String>();
    public ArrayList<String> back_end_lst = new ArrayList<String>();

    public Davis_Putnam() throws FileNotFoundException, IOException{
        read_inp(); // storing data from fe_out.txt into above arraylists
        Clause_truth ct = new Clause_truth(this.inp, this.truth_dict); // creating local clause_truth so recursive calls can be made 
        ct = dpll(ct); // calling dpll algorithm and returning clause_truth with finalized dictionary of truth values
        write_out(ct); // writing result of dpll algorithm to dpll_out.txt
    }

    public void read_inp() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("fe_out.txt"));

        boolean back_end = false; // boolean flag for if dealing with back end data

        // reading in data from text file
        String in_text = br.readLine();
        while(in_text != null){
            // changing flag when back end data is reached
            if(in_text.equals("0")){
                back_end = true;
            }

            // adding back end data to back_end_lst
            if(back_end){
                back_end_lst.add(in_text);
                in_text = br.readLine();
                continue;
            }

            // adding clauses to input list
            inp.add(in_text);

            // adding new unique atoms to atoms list 
            for(String atom : in_text.split(" ")){
                if(!atom.contains("-")){
                    if(!this.inp_atoms.contains(atom)){
                        inp_atoms.add(atom);
                    }
                }
                else{
                    if(!this.inp_atoms.contains(atom.substring(1, atom.length()))){
                        inp_atoms.add(atom.substring(1, atom.length()));
                    }
                }
            }

            in_text = br.readLine();
        }

        br.close();
    }

    public void write_out(Clause_truth ct) throws FileNotFoundException, IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter("dpll_out.txt"));

        // checking that a solution exists and writing to file
        if(ct != null){
            // iterating over list of atoms
            for(String atom : this.inp_atoms){
                // checking if solution has assigned a value to atom
                if(!ct.B.keySet().contains(atom)){
                    // if not, atom can be assigned T or F, arbitrarily assigning T
                    bw.write(atom + " T\n");
                    continue;
                }

                // otherwise, writing atom and its assigned value
                bw.write(atom + " " + ct.B.get(atom) + "\n");
            }
        }

        // writing back end data to file
        for(String line : this.back_end_lst){
            bw.write(line + "\n");
        }

        bw.close();
    }

    public Clause_truth dpll(Clause_truth ct){
        while(true){
            // all clauses are satisfied
            if(ct.CS.isEmpty()){
                return ct;
            }

            // all literals are deleted
            if(ct.CS.contains("")){
                return null;
            }

            // checking easy cases
            if(check_easy(ct)){
                ct = easy_case(ct);
            }
            else{ // otherwise, have to try assigning truth value arbitrarily
                break;
            }
        }

        Clause_truth ct_copy = new Clause_truth(ct.getCS(), ct.getB()); // creating copy of current ct

        String atom = ct.get_unbound(); // getting the first atom not assigned a truth value

        ct_copy = propogate(ct_copy, atom, "T"); // arbitrarily assigning unbound atom true and dealing with easy cases

        Clause_truth answer = dpll(ct_copy); // getting result of running dpll on ct_copy

        // if an answer is returned, return that answer
        if(answer != null){
            return answer;
        }

        // otherwise, assign unbound atom to be false and return result of dpll
        ct = propogate(ct, atom, "F");

        return dpll(ct);
    }

    public boolean check_easy(Clause_truth ct){
        ArrayList<String> p_literal = new ArrayList<String>();

        // iterating over each clause
        for(String i : ct.CS){
            // singleton case
            if(i.strip().length() == 1){
                ct.singleton = i.strip(); // storing singleton for processing in easy_case
                return true;
            }

            // creating a list of each atom
            String[] atoms = i.split(" ");

            // adding unique literals to list
            for(String j : atoms){
                if(!p_literal.contains(j.strip())){
                    p_literal.add(j.strip());
                }
            }
        }

        // pure literal case, iterating over literals in p_literal
        for(String atom : p_literal){
            // if literal is not negated, checking if its negation is present
            if(!atom.contains("-")){
                // if negation not present, storing pure literal and returning true
                if(!p_literal.contains("-"+atom)){
                    ct.p_literal = atom;
                    return true;
                }
            }

            // else, checking if non-negated literal is present
            else{
                // if non-negated present, storing pure literal and returning true
                if(!p_literal.contains(atom.substring(1, atom.length()))){
                    ct.p_literal = atom;
                    return true;
                }
            }
        }

        return false; // no pure literals found
    }

    public Clause_truth easy_case(Clause_truth ct){
        String atom = "";
        String value = "";

        // singleton case
        if(!ct.singleton.isBlank()){
            // singleton is a negation
            if(ct.singleton.contains("-")){
                // calculating values of atom and value
                atom = ct.singleton.substring(1, ct.singleton.length());
                value = "F";
            }

            // singleton is not a negation
            else{
                atom = ct.singleton;
                value = "T";
            }

            ct.singleton = ""; // resetting stored value
            return propogate(ct, atom, value);
        }

        // pure literal case
        if(!ct.p_literal.isBlank()){
            // literal is a negation
            if(ct.p_literal.contains("-")){
                atom = ct.p_literal.substring(1, ct.p_literal.length());
                value = "F";
            }

            // literal is not a negation
            else{
                atom = ct.p_literal;
                value = "T";
            }

            ct.p_literal = ""; // resetting stored pure literal value

            return propogate(ct, atom, value);
        }

        return ct;
    }

    public Clause_truth propogate(Clause_truth ct, String atom, String value){
        // adding atom and its assigned value to dictionary
        if(atom.contains("-")){
            atom = atom.substring(1, atom.length());
            ct.B.put(atom, value);
        }
        else{
            ct.B.put(atom, value);
        }

        // making atom into a literal
        if(value.equals("F")){
            atom = "-"+atom;
        }

        ArrayList<String> new_clause = new ArrayList<String>(); // for storing a list of edited clauses

        // iterating over clauses of ct
        for(Iterator<String> iterator_c = ct.CS.iterator(); iterator_c.hasNext();){
            String clause = iterator_c.next(); // getting a clause

            // getting literals from clause and storing as an arraylist
            List<String> atoms = Arrays.asList(clause.split(" "));
            ArrayList<String> alist_atoms = new ArrayList<String>(atoms);
            Iterator<String> iterator_a = alist_atoms.iterator(); // iterator for literal arraylist

            String n_clause = ""; // container for constructing new clause
            boolean edited = false; // flag for if a clause was edited

            // iteraing over literals in alist_atoms
            while(iterator_a.hasNext()){
                String c_atom = iterator_a.next(); // getting a literal

                // if clause literal equals input literal, clause can be deleted
                if(c_atom.equals(atom)){
                    iterator_c.remove();
                    break;
                }

                // removing individual literals
                // input literal is negated
                if(value.equals("F")){
                    // removing clause literal if it is same atom, different sign to input literal
                    if(c_atom.equals(atom.substring(1, atom.length()))){
                        edited = true;
                        iterator_a.remove();
                    }
                }

                // input literal is not negated
                else{
                    // removing clause literal if it is same atom, different sign to input literal
                    if(c_atom.equals("-"+atom)){
                        edited = true;
                        iterator_a.remove();
                    }
                }
            }

            // dealing with edited clauses
            if(edited){
                iterator_c.remove(); // removing old clause

                // iterating over updated list of literals
                for(int i = 0; i < alist_atoms.size(); i++){
                    // case where last literal reached
                    if(i == alist_atoms.size() - 1){
                        // adding literal to new clause string without end space
                        n_clause += alist_atoms.get(i);
                        break;
                    }

                    // adding literal to new clause string
                    n_clause += alist_atoms.get(i) + " ";
                }

                // adding new clause to new clause arraylist
                new_clause.add(n_clause);

                edited = false; // resetting boolean flag
            }
            
        }

        // adding new clauses to ct's clause list
        for(String clause : new_clause){
            ct.CS.add(clause);
        }

        return ct;
    }
}

class Clause_truth{
    public ArrayList<String> CS = new ArrayList<String>();
    public Hashtable<String, String> B = new Hashtable<String,String>();

    public String singleton = "";
    public String p_literal = "";

    public Clause_truth(){
        
    }

    public Clause_truth(ArrayList<String> cs, Hashtable<String, String> b){
        this.CS = new ArrayList<String>(cs);
        this.B = new Hashtable<String, String>(b);
    }

    public ArrayList<String> getCS(){
        return this.CS;
    }

    public Hashtable<String, String> getB(){
        return this.B;
    }

    public String get_unbound(){
        // iterating over clauses
        for(String clause : CS){
            // iteraing over literals in clause
            for(String atom : clause.split(" ")){
                // if literal is not negated
                if(!atom.contains("-")){
                    // checking if atom was not assigned a value
                    if(!B.containsKey(atom)){
                        return atom;
                    }
                }

                // if literal is negated
                else{
                    // checking if atom was not assigned a value
                    if(!B.containsKey(atom.substring(1, atom.length()))){
                        return atom;
                    }
                }
            }
        }

        return "";
    }
}