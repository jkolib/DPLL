import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Davis_Putnam{
    public ArrayList<String> inp = new ArrayList<String>();
    public Hashtable<String, String> truth_dict = new Hashtable<String,String>();
    public ArrayList<String> back_end_lst = new ArrayList<String>();

    public Davis_Putnam() throws FileNotFoundException, IOException{
        read_inp();
        Clause_truth ct = new Clause_truth(this.inp, this.truth_dict);
        ct = dpll(ct);
        write_out(ct);
    }

    public void read_inp() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("fe_out.txt"));

        boolean back_end = false;

        String in_text = br.readLine();
        while(in_text != null){
            if(in_text.equals("0")){
                back_end = true;
            }

            if(back_end){
                back_end_lst.add(in_text);
                in_text = br.readLine();
                continue;
            }

            inp.add(in_text);

            in_text = br.readLine();
        }

        br.close();
    }

    public void write_out(Clause_truth ct) throws FileNotFoundException, IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter("dpll_out.txt"));

        for(String atom : ct.B.keySet()){
            bw.write(atom + " " + ct.B.get(atom) + "\n");
        }

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
            else{
                break;
            }
        }

        Clause_truth ct_copy = new Clause_truth(ct.CS, ct.B);

        String atom = ct.get_unbound();

        ct_copy = propogate(ct_copy, atom, "T");

        Clause_truth answer = dpll(ct_copy);

        if(answer != null){
            return answer;
        }

        ct = propogate(ct, atom, "F");

        return dpll(ct);
    }

    public boolean check_easy(Clause_truth ct){
        ArrayList<String> p_literal = new ArrayList<String>();

        for(String i : ct.CS){
            // singleton case
            if(i.strip().length() == 1){
                ct.singleton = i.strip();
                return true;
            }

            // creating a list of each atom
            String[] atoms = i.split(" ");

            for(String j : atoms){
                if(!p_literal.contains(j.strip())){
                    p_literal.add(j.strip());
                }
            }
        }

        // pure literal case
        for(String atom : p_literal){
            if(atom.length() == 1){
                if(!p_literal.contains("-"+atom)){
                    ct.p_literal = atom;
                    return true;
                }
            }
            else{
                if(!p_literal.contains(atom.substring(1))){
                    ct.p_literal = atom;
                    return true;
                }
            }
        }

        return false;
    }

    public Clause_truth easy_case(Clause_truth ct){
        String atom = "";
        String value = "";

        if(!ct.singleton.isBlank()){
            if(ct.singleton.contains("-")){
                atom = ct.singleton.substring(1);
                value = "F";
            }
            else{
                atom = ct.singleton;
                value = "T";
            }

            ct.singleton = "";
            return propogate(ct, atom, value);
        }

        if(!ct.p_literal.isBlank()){
            if(ct.p_literal.contains("-")){
                atom = ct.p_literal.substring(1);
                value = "F";
            }
            else{
                atom = ct.p_literal;
                value = "T";
            }

            ct.p_literal = "";
        }

        return propogate(ct, atom, value);
    }

    public Clause_truth propogate(Clause_truth ct, String atom, String value){
        ct.B.put(atom, value);

        for(Iterator<String> iterator_c = ct.CS.iterator(); iterator_c.hasNext();){
            String clause = iterator_c.next();
            String[] atoms = clause.split(" ");
            Iterator<String> iterator_a = Arrays.asList(atoms).iterator();

            while(iterator_a.hasNext()){
                String c_atom = iterator_a.next();

                if(c_atom.equals(atom)){
                    iterator_c.remove();
                    break;
                }

                if(value.equals("F")){
                    if(atom.equals(atom.substring(1))){
                        iterator_a.remove();
                    }
                }
                else{
                    if(atom.equals("-"+atom)){
                        iterator_a.remove();
                    }
                }
            }

            
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
        this.CS = cs;
        this.B = b;
    }

    public String get_unbound(){
        for(String clause : CS){
            for(String atom : clause.split(" ")){
                if(atom.length() == 1){
                    if(!B.containsKey(atom)){
                        return atom;
                    }
                }
                else{
                    if(!B.containsKey(atom.substring(1))){
                        return atom;
                    }
                }
            }
        }

        return "";
    }
}