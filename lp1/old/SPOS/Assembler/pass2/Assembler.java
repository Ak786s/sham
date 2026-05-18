package pass2;

import java.io.*;
import pass1.SymbolTable;
import pass1.LiteralTable;

public class Assembler {

    // In a real scenario, these should be initialized from Pass-I output
    SymbolTable ST[] = new SymbolTable[10]; 
    LiteralTable LT[] = new LiteralTable[10]; 
    int STP = 0, LTP = 0;

    public Assembler() {
        // Example initialization for testing (replace with real ST/LT if needed)
        ST[0] = new SymbolTable("A", 104);
        ST[1] = new SymbolTable("B", 105);
        STP = 2;

        LT[0] = new LiteralTable("=10", 101);
        LTP = 1;
    }

    public void pass2() throws Exception {
        // Read out.txt from root folder (where Pass-I generated it)
        BufferedReader br = new BufferedReader(new FileReader("out.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("machine_code.txt"));

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            StringBuilder machineLine = new StringBuilder();

            for (String token : tokens) {
                if (token.startsWith("S")) { // Symbol
                    int index = Integer.parseInt(token.substring(1));
                    machineLine.append(ST[index].add).append(" ");
                } else if (token.startsWith("L")) { // Literal
                    int index = Integer.parseInt(token.substring(1));
                    machineLine.append(LT[index].add).append(" ");
                } else { // Opcode or number
                    machineLine.append(token).append(" ");
                }
            }

            bw.write(machineLine.toString().trim() + "\n");
            System.out.println(machineLine.toString().trim());
        }

        br.close();
        bw.close();
        System.out.println("\nMachine code generated in machine_code.txt");
    }

    public static void main(String[] args) throws Exception {
        Assembler assembler = new Assembler();
        assembler.pass2();
    }
}
