package pass1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class Assembler {

    HashMap<String, Integer> MOT = new HashMap<>();
    HashMap<String, Integer> RT = new HashMap<>();
    HashMap<String, Integer> DST = new HashMap<>();
    LiteralTable LT[] = new LiteralTable[10];
    SymbolTable ST[] = new SymbolTable[10];
    int STP = 0, LTP = 0;
    int location_counter = 0;

    public Assembler() {
        // Machine Opcode Table
        MOT.put("STOP", 0);
        MOT.put("ADD", 1);
        MOT.put("SUB", 2);
        MOT.put("MULT", 3);
        MOT.put("MOVER", 4);
        MOT.put("MOVEM", 5);
        MOT.put("COMP", 6);
        MOT.put("BC", 7);
        MOT.put("DIV", 8);
        MOT.put("READ", 9);
        MOT.put("PRINT", 10);

        // Register Table
        RT.put("AREG", 1);
        RT.put("BREG", 2);
        RT.put("CREG", 3);
        RT.put("DREG", 4);

        // Directive Table
        DST.put("DC", 11);
        DST.put("DS", 22);
    }

    public void pass1() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("input.txt")); // relative path
        BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
        String line;

        while ((line = br.readLine()) != null) {
            String statement = parseLine(line);
            if (!statement.isEmpty()) {
                System.out.println(statement);
                bw.write(statement + "\n");
            }
        }

        br.close();
        bw.close();

        // Print Symbol Table
        System.out.println("\nSymbol Table:");
        for (int i = 0; i < STP; i++) {
            System.out.println(i + " " + ST[i].name + " , " + ST[i].add);
        }

        // Print Literal Table
        System.out.println("\nLiteral Table:");
        for (int i = 0; i < LTP; i++) {
            System.out.println(i + " " + LT[i].name + " , " + LT[i].add);
        }
    }

    public String parseLine(String line) {
        line = line.trim();
        if (line.isEmpty()) return "";

        String[] split = line.split("\\s+");
        String row = "" + location_counter;

        // Handle START directive
        if (split[0].equalsIgnoreCase("START")) {
            location_counter = Integer.parseInt(split[1]);
            return location_counter + "";
        }

        if (split[0].equalsIgnoreCase("END") || split[0].equalsIgnoreCase("LTORG")
                || split[0].equalsIgnoreCase("ORIGIN") || split[0].equalsIgnoreCase("EQU")) {
            return "";
        }

        for (int i = 0; i < split.length; i++) {
            int opcode = getOpcode(split[i]);
            if (opcode != 9999) {
                row += " " + opcode;
            } else if (split[i].charAt(0) == '=') {
                LT[LTP] = new LiteralTable(split[i], 0);
                row += " L" + LTP;
                LTP++;
            } else {
                if (split.length > 1 && !split[1].equalsIgnoreCase("DC")) {
                    ST[STP] = new SymbolTable(split[i], location_counter);
                    row += " S" + STP;
                    STP++;
                } else {
                    int index = searchST(split[i]);
                    if (index != -1) {
                        row += " S" + index;
                    } else {
                        row += " " + split[i];
                    }
                }
            }
        }

        location_counter++;
        return row;
    }

    public int getOpcode(String key) {
        if (MOT.containsKey(key)) return MOT.get(key);
        if (RT.containsKey(key)) return RT.get(key);
        if (DST.containsKey(key)) return DST.get(key);
        return 9999;
    }

    public int searchST(String key) {
        for (int i = 0; i < STP; i++) {
            if (ST[i].name.equalsIgnoreCase(key)) return i;
        }
        return -1; // not found
    }

    public static void main(String[] args) throws Exception {
        Assembler assembler = new Assembler();
        assembler.pass1();
    }
}
