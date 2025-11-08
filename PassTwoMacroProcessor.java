import java.util.*;

public class PassTwoMacroProcessor {

    static class MNTEntry {
        String name;
        int mdtIndex;
        MNTEntry(String n, int i) { name = n; mdtIndex = i; }
    }

    static class MDTEntry {
        String line;
        MDTEntry(String l) { line = l; }
    }

    static class ALA {
        List<String> args = new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // -------- INPUT: MNT --------
        System.out.print("Enter number of entries in MNT: ");
        int mntCount = Integer.parseInt(sc.nextLine().trim());
        List<MNTEntry> MNT = new ArrayList<>();
        System.out.println("Enter MNT entries: <MACRO_NAME> <MDT_INDEX>");
        for (int i = 0; i < mntCount; i++) {
            String[] parts = sc.nextLine().trim().split("\\s+");
            MNT.add(new MNTEntry(parts[0], Integer.parseInt(parts[1])));
        }

        // -------- INPUT: MDT --------
        System.out.print("Enter number of entries in MDT: ");
        int mdtCount = Integer.parseInt(sc.nextLine().trim());
        List<MDTEntry> MDT = new ArrayList<>();
        System.out.println("Enter MDT entries (one line per entry):");
        for (int i = 0; i < mdtCount; i++) {
            MDT.add(new MDTEntry(sc.nextLine().trim()));
        }

        // -------- INPUT: ALA --------
        Map<String, ALA> ALAmap = new LinkedHashMap<>();
        System.out.print("Enter number of macros in ALA: ");
        int alaMacros = Integer.parseInt(sc.nextLine().trim());
        for (int i = 0; i < alaMacros; i++) {
            System.out.print("Enter macro name for ALA: ");
            String macroName = sc.nextLine().trim();
            System.out.print("Enter number of arguments for " + macroName + ": ");
            int argCount = Integer.parseInt(sc.nextLine().trim());
            ALA ala = new ALA();
            System.out.println("Enter argument names (e.g., &ARG1):");
            for (int j = 0; j < argCount; j++) {
                ala.args.add(sc.nextLine().trim());
            }
            ALAmap.put(macroName, ala);
        }

        // -------- INPUT: INTERMEDIATE CODE --------
        System.out.print("Enter number of intermediate lines: ");
        int interCount = Integer.parseInt(sc.nextLine().trim());
        String[] intermediate = new String[interCount];
        System.out.println("Enter intermediate code (each line):");
        for (int i = 0; i < interCount; i++) {
            intermediate[i] = sc.nextLine().trim();
        }

        // -------- PASS-II PROCESSING --------
        System.out.println("\n----- OUTPUT AFTER PASS-II -----");
        for (String line : intermediate) {
            String[] parts = line.trim().split("\\s+");
            if (parts[0].equalsIgnoreCase("START") || parts[0].equalsIgnoreCase("END")) {
                System.out.println(line);
                continue;
            }

            boolean isMacro = false;
            for (MNTEntry m : MNT) {
                if (m.name.equalsIgnoreCase(parts[0])) {
                    isMacro = true;
                    expandMacro(m, line, MNT, MDT, ALAmap);
                    break;
                }
            }

            if (!isMacro) System.out.println(line);
        }
    }

    // -------- MACRO EXPANSION FUNCTION --------
    static void expandMacro(MNTEntry m, String line, List<MNTEntry> MNT,
                            List<MDTEntry> MDT, Map<String, ALA> ALAmap) {

        String[] tokens = line.trim().split("\\s+", 2);
        String macroName = tokens[0];
        String[] actualArgs = (tokens.length > 1) ? tokens[1].split(",") : new String[0];
        for (int i = 0; i < actualArgs.length; i++) actualArgs[i] = actualArgs[i].trim();

        ALA ala = ALAmap.get(macroName);
        int mdtIndex = m.mdtIndex;

        // Traverse MDT until MEND
        for (int i = mdtIndex; i < MDT.size(); i++) {
            String mdtLine = MDT.get(i).line.trim();
            if (mdtLine.equalsIgnoreCase("MEND")) break;

            // Replace arguments
            for (int j = 0; j < ala.args.size(); j++) {
                if (j < actualArgs.length)
                    mdtLine = mdtLine.replace(ala.args.get(j), actualArgs[j]);
            }

            System.out.println(mdtLine);
        }
    }
}
//START
INCR N1, N2
DECR N3, N4
END