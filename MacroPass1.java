import java.util.*;
import java.util.regex.*;

public class MacroPass1 {

    // MNT entry
    static class MNTEntry {
        String name;
        int mdtIndex; // starting index in MDT (1-based for display)
        int argCount;
        List<String> formalParams; // &ARG1, &ARG2 ...

        MNTEntry(String name, int mdtIndex, List<String> formalParams) {
            this.name = name;
            this.mdtIndex = mdtIndex;
            this.argCount = formalParams.size();
            this.formalParams = new ArrayList<>(formalParams);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter assembly lines (type END on a line to finish):");

        List<String> inputLines = new ArrayList<>();
        while (true) {
            String line = sc.nextLine();
            if (line == null) break;
            line = line.trim();
            inputLines.add(line);
            if ("END".equalsIgnoreCase(line)) break;
        }

        // Data structures
        Map<String, MNTEntry> mnt = new LinkedHashMap<>(); // preserve insertion order
        List<String> mdt = new ArrayList<>();
        Map<String, List<String>> alaFormal = new HashMap<>(); // formal param lists for each macro
        List<Map<String,String>> invocationALAs = new ArrayList<>(); // ALA instances for invocations
        List<String> invocationNames = new ArrayList<>(); // corresponding macro names for invocationALAs

        boolean inMacroDef = false;
        String currentMacroName = null;
        List<String> currentFormals = null;

        // We will number MDT lines from 1 for display; but in list index we'll use 0-based
        for (int i = 0; i < inputLines.size(); i++) {
            String line = inputLines.get(i).trim();
            if (line.equalsIgnoreCase("MACRO")) {
                // start macro definition; next line should contain header: NAME <params>
                inMacroDef = true;
                // read header from next non-empty line
                i++;
                while (i < inputLines.size() && inputLines.get(i).trim().isEmpty()) i++;
                if (i >= inputLines.size()) break;
                String header = inputLines.get(i).trim();
                // header expected like: INCR &ARG1, &ARG2
                String[] parts = header.split("\\s+", 2);
                currentMacroName = parts[0].trim();
                currentFormals = new ArrayList<>();
                if (parts.length > 1) {
                    String params = parts[1].trim();
                    // split by comma
                    String[] tokens = params.split(",");
                    for (String t : tokens) {
                        String p = t.trim();
                        if (!p.startsWith("&")) {
                            // if user didn't put &, still treat as formal by prefixing &
                            p = "&" + p;
                        }
                        currentFormals.add(p);
                    }
                }
                // add MNT entry with current MDT size + 1
                int mdtIndex = mdt.size() + 1; // 1-based
                MNTEntry entry = new MNTEntry(currentMacroName, mdtIndex, currentFormals);
                mnt.put(currentMacroName, entry);
                alaFormal.put(currentMacroName, new ArrayList<>(currentFormals));

                // Add a header entry to MDT to show macro and formal mapping (optional)
                StringBuilder hdr = new StringBuilder();
                hdr.append("MACRO_HEADER ").append(currentMacroName);
                if (!currentFormals.isEmpty()) {
                    hdr.append(" -> ");
                    for (int k=0;k<currentFormals.size();k++) {
                        if (k>0) hdr.append(", ");
                        hdr.append(currentFormals.get(k)).append("=#").append(k+1);
                    }
                }
                mdt.add(hdr.toString());

                // Now read macro body until MEND
                i++;
                while (i < inputLines.size()) {
                    String bodyLine = inputLines.get(i).trim();
                    if (bodyLine.equalsIgnoreCase("MEND")) {
                        mdt.add("MEND");
                        inMacroDef = false;
                        currentMacroName = null;
                        currentFormals = null;
                        break;
                    } else {
                        // Replace occurrences of formal parameter names (exact) with positional markers #1, #2...
                        String replaced = bodyLine;
                        for (int f = 0; f < (entry.formalParams.size()); f++) {
                            String formal = entry.formalParams.get(f); // e.g. &ARG1
                            // Replace exact tokens that match formal using regex word boundary for &name
                            // Formal may appear attached to punctuation, so we'll replace all exact substrings
                            // Use regex escape
                            String formalRegex = Pattern.quote(formal);
                            replaced = replaced.replaceAll(formalRegex, "#" + (f+1));
                        }
                        mdt.add(replaced);
                    }
                    i++;
                }
                // continue outer loop from current i (which is at MEND)
            } else if (line.equalsIgnoreCase("END")) {
                break;
            } else {
                // Not in macro def: check if this line is a macro invocation (macro name at start)
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\s+", 2);
                String first = tokens[0];
                if (mnt.containsKey(first)) {
                    // macro invocation
                    String macroName = first;
                    String actualsPart = "";
                    if (tokens.length > 1) actualsPart = tokens[1].trim();
                    List<String> actuals = new ArrayList<>();
                    if (!actualsPart.isEmpty()) {
                        String[] atoks = actualsPart.split(",");
                        for (String a : atoks) actuals.add(a.trim());
                    }

                    // Build ALA mapping actuals to formal positions: #1->actual1 etc.
                    Map<String,String> alaInstance = new LinkedHashMap<>();
                    List<String> formals = alaFormal.get(macroName);
                    int formalCount = formals == null ? 0 : formals.size();
                    for (int j = 0; j < formalCount; j++) {
                        String pos = "#" + (j+1);
                        String actual = (j < actuals.size()) ? actuals.get(j) : "";
                        alaInstance.put(pos, actual);
                    }
                    invocationALAs.add(alaInstance);
                    invocationNames.add(macroName);
                } else {
                    // regular statement - ignore for pass I (but could be stored)
                }
            }
        } // end for lines

        // Print MNT
        System.out.println("\n========= MNT (Macro Name Table) =========");
        System.out.printf("%-6s %-12s %-8s %-20s\n", "Index", "Macro Name", "MDT_PTR", "Arg Count / Formals");
        int mntIndex = 1;
        for (MNTEntry e : mnt.values()) {
            System.out.printf("%-6d %-12s %-8d %-20s\n", mntIndex, e.name, e.mdtIndex, e.argCount + " / " + e.formalParams);
            mntIndex++;
        }

        // Print MDT
        System.out.println("\n========= MDT (Macro Definition Table) =========");
        System.out.printf("%-6s %s\n", "Index", "MDT Entry");
        for (int idx = 0; idx < mdt.size(); idx++) {
            System.out.printf("%-6d %s\n", idx+1, mdt.get(idx));
        }

        // Print ALA formal (for each macro)
        System.out.println("\n========= ALA (Formal parameter lists for macros) =========");
        for (String mname : alaFormal.keySet()) {
            List<String> fl = alaFormal.get(mname);
            System.out.println(mname + " : " + fl);
        }

        // Print invocation ALAs
        System.out.println("\n========= ALA instances for macro invocations found in main program =========");
        if (invocationALAs.isEmpty()) {
            System.out.println("No macro invocations found in the non-macro part of the input.");
        } else {
            for (int k = 0; k < invocationALAs.size(); k++) {
                System.out.println("Invocation " + (k+1) + " -> Macro: " + invocationNames.get(k));
                Map<String,String> map = invocationALAs.get(k);
                for (Map.Entry<String,String> me : map.entrySet()) {
                    System.out.println("    " + me.getKey() + " => " + me.getValue());
                }
            }
        }

        sc.close();
    }
}
