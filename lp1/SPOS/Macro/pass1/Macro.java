package pass1; 
 
import java.io.BufferedReader; 
import java.io.BufferedWriter; 
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
 
public class Macro { 
    List<String> MDT = new ArrayList<>(); 
    List<MacroNameTable> MNT = new ArrayList<>(); 
    Map<String, List<String>> macroParams = new HashMap<>(); // Store parameters for each macro 
 
    private void pass1() throws IOException { 
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) { 
            String line; 
            boolean inMacro = false; 
            String currentMacro = null; 
            List<String> currentParams = new ArrayList<>(); 
 
            while ((line = br.readLine()) != null) { 
                line = line.trim(); 
                if (line.equalsIgnoreCase("MACRO")) { 
                    inMacro = true; 
                    line = br.readLine().trim(); 
                    String[] keys = line.split("\\s+"); 
                    currentMacro = keys[0]; 
                    currentParams.clear(); 
                    for (int i = 1; i < keys.length; i++) { 
                        currentParams.add(keys[i]); 
                    } 
                    MNT.add(new MacroNameTable(currentMacro, MDT.size(), keys.length - 1)); 
                    macroParams.put(currentMacro, new ArrayList<>(currentParams)); 
                } else if (line.equalsIgnoreCase("MEND")) { 
                    inMacro = false; 
                    MDT.add(line); 
                } else if (inMacro) { 
                    MDT.add(line); 
                } 
            } 
        } 
    } 
 
    private void pass2() throws IOException { 
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt")); 
             BufferedWriter wr = new BufferedWriter(new FileWriter("output.txt"))) { 
            String line; 
            while ((line = br.readLine()) != null) { 
                line = line.trim(); 
                String[] split = line.split("\\s+"); 
                if (split[0].equalsIgnoreCase("START")) { 
                    while ((line = br.readLine()) != null) { 
                        line = line.trim(); 
                        String[] split1 = line.split("\\s+"); 
                        boolean isMacro = false; 
                        for (MacroNameTable mnt : MNT) { 
                            if (mnt.name.equalsIgnoreCase(split1[0])) { 
                                isMacro = true; 
                                int mdtIndex = mnt.index; 
                                List<String> params = macroParams.get(mnt.name); 
                                String[] args = new String[split1.length - 1]; 
                                System.arraycopy(split1, 1, args, 0, args.length); 
 
                                while (!MDT.get(mdtIndex).equalsIgnoreCase("MEND")) { 
                                    String mdtLine = MDT.get(mdtIndex); 
                                    for (int i = 0; i < params.size() && i < args.length; i++) { 
                                        mdtLine = mdtLine.replace(params.get(i), args[i]); 
                                    } 
                                    wr.write(mdtLine + "\n"); 
                                    mdtIndex++; 
                                } 
                                break; 
                            } 
                        } 
                        if (!isMacro) { 
                            wr.write(line + "\n"); 
                        } 
                    } 
                } else if (split[0].equalsIgnoreCase("END")) { 
                    break; 
                } 
            } 
        } 
    } 
 
    public static void main(String[] args) { 
        try { 
            Macro m = new Macro(); 
            m.pass1(); 
            m.pass2(); 
            System.out.println("Macro processing completed. Check output.txt."); 
        } catch (IOException e) { 
            System.err.println("Error: " + e.getMessage()); 
        } 
    } 
} 