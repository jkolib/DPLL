# DPLL
Using the DPLL algorithm to solve a maze game.

## Format of Front_end Input
First line: A list of the nodes, separated by white space. Each node is a string of up to five characters.  
Second line: A list of the treasures, separated by white space. Each treasure is a string of up to ten characters.  
Third line: The number of allowed steps.  
Remaining lines: The encoding of the maze. Each line consists of:
- A node N.
- The keyword "TREASURES" followed by the list of treasures, separated by white space.
- The keyword "NEXT" followed by the list of nodes that N is connected to, separated by white space.

## Format of Back_end Output
The path generated from the input file is written to the file be_out.txt. The nodes are ordered by time. For a sample output START A B, at time 0, the player is at the node START and at time 1, the player is at node A, and so on.

## Categories of Propositions
There are 7 categories of propositions.

1. The player is only at one place at a time.  
For any time I, for any two distinct nodes M and N, ¬(At(M,I) ∧ At(N,I)).  
In CNF this becomes ¬At(M,I) ∨ ¬At(N,I).  
For example, ¬At(C,2) ∨ ¬At(F,2).  

2. The player must move on edges. Suppose that node N is connected to M1 ... Mq. For any time I, if the player is at node N at time I, then the player moves to M1 or to M2 ... or to Mq at time I+1.  
Thus At(N,I) → At(M1,I+1) ∨ ... ∨ At(Mq,I+1).  
In CNF, ¬At(N,I) ∨ At(M1,I+1) ∨... ∨ At(Mk,I+1).  
For example, ¬At(C,2) ∨ At(START,3) ∨ At(D,3) ∨ At(F,3)  

3. Suppose that treasure T is located at node N. Then if the player is at N at time I, then at time I the player has T.  
At(N,I) → Has(T,I).  
In CNF, ¬At(N,I) ∨ Has(T,I). For example ¬At(C,2) ∨ Has(Ruby,2)  

4. If the player has treasure T at time I-1, then the player has T at time I. i (I=1..K)  
Has(T,I-1) → Has(T,I)  
In CNF, ¬Has(T,I-1) ∨ Has(T,I)  
For example ¬Has(GOLD,2) ∨ Has(GOLD,3).  

5. Let M1 ... Mq be the nodes that supply treasure T. If the player does not have treasure T at time I-1 and has T at time I, then at time I they must be at one of the nodes M1 ... Mq.  
(¬Has(T,I-1) ∧Has(T,I)) → At(M1,I) ∨ At(M2,I) ∨ ... ∨At(Mq,I).  
In CNF Has(T,I-1) ∨ ¬Has(T,I) ∨ At(M1,I) ∨ At(M2,I) ∨ ... ∨At(Mq,I).  
For example Has(GOLD,1) ∨ ¬Has(GOLD,2) ∨ At(A,2) ∨ At(H,2).  

6. The player is at START at time 0. At(START,0).  

7. At time 0, the player has none of the treasures.  
For each treasure T, ¬Has(T,0).  
For instance: ¬Has(GOLD,0).  

8. At time K, the player has all the treasures.  
For each treasure T, Has(T,K).  
For instance: Has(GOLD,4).  
