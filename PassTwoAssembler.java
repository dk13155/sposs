import java.util.*;
import java.util.regex.*;

public class PassTwoAssembler {

    static class Symbol {
        String name;
        int addr;
        Symbol(String n, int a) { name = n; addr = a; }
    }

    static class Literal {
        String lit;
        int addr;
        Literal(String l, int a) { lit = l; addr = a; }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ---------------- INPUT ----------------
        System.out.println("Enter number of symbols:");
        int symCount = Integer.parseInt(sc.nextLine().trim());
        List<Symbol> symtab = new ArrayList<>();
        System.out.println("Enter symbol table lines: <SYMBOL> <ADDRESS>");
        for (int i = 0; i < symCount; i++) {
            String[] tok = sc.nextLine().trim().split("\\s+");
            symtab.add(new Symbol(tok[0], Integer.parseInt(tok[1])));
        }

        System.out.println("Enter number of literals:");
        int litCount = Integer.parseInt(sc.nextLine().trim());
        List<Literal> littab = new ArrayList<>();
        System.out.println("Enter literal table lines: <LITERAL> <ADDRESS>");
        for (int i = 0; i < litCount; i++) {
            String[] tok = sc.nextLine().trim().split("\\s+");
            littab.add(new Literal(tok[0], Integer.parseInt(tok[1])));
        }

        System.out.println("Enter number of intermediate lines:");
        int icCount = Integer.parseInt(sc.nextLine().trim());
        List<String> icLines = new ArrayList<>();
        System.out.println("Enter intermediate lines (exactly as in IC):");
        for (int i = 0; i < icCount; i++) {
            icLines.add(sc.nextLine().trim());
        }

        // ---------------- PROCESSING ----------------
        int LC = 0;
        List<String> machine = new ArrayList<>();

        Pattern isPat = Pattern.compile("\\(IS,\\s*(\\d{1,2})\\)");
        Pattern adPat = Pattern.compile("\\(AD,\\s*(\\d{1,2})\\)");
        Pattern dlPat = Pattern.compile("\\(DL,\\s*(\\d{1,2})\\)");
        Pattern symPat = Pattern.compile("\\(S,\\s*(\\d+)\\)");
        Pattern litPat = Pattern.compile("\\(L,\\s*(\\d+)\\)");
        Pattern constPat = Pattern.compile("\\(C,\\s*(-?\\d+)\\)");

        for (String line : icLines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // START
            if (line.startsWith("(AD,01)")) {
                Matcher m = constPat.matcher(line);
                if (m.find()) LC = Integer.parseInt(m.group(1));
                continue;
            }

            // END
            if (line.startsWith("(AD,02)")) break;

            // ORIGIN
            if (line.startsWith("(AD,03)")) {
                Matcher m = Pattern.compile("\\(S,(\\d+)\\)\\+?(\\d+)?").matcher(line);
                if (m.find()) {
                    int symIndex = Integer.parseInt(m.group(1));
                    int offset = (m.group(2) != null) ? Integer.parseInt(m.group(2)) : 0;
                    LC = symtab.get(symIndex - 1).addr + offset;
                }
                continue;
            }

            // LTORG
            if (line.startsWith("(AD,05)")) continue;

            // EQU
            if (line.startsWith("(AD,04)")) continue;

            // DL statements (DS/DC)
            if (line.startsWith("(DL")) {
                Matcher c = constPat.matcher(line);
                int val = 0;
                if (c.find()) val = Integer.parseInt(c.group(1));
                if (line.contains("(DL,01)"))
                    machine.add(String.format("%d\tDC\t%d", LC++, val));  // DC
                else if (line.contains("(DL,02)"))
                    machine.add(String.format("%d\tDC\t0", LC++));       // DS initialized as 0
                continue;
            }

            // Literal placement lines
            if (line.matches("\\(L,\\s*\\d+\\)")) {
                Matcher m = litPat.matcher(line);
                if (m.find()) {
                    int lidx = Integer.parseInt(m.group(1));
                    Literal lit = littab.get(lidx - 1);
                    Matcher num = Pattern.compile("\\d+").matcher(lit.lit);
                    int val = num.find() ? Integer.parseInt(num.group()) : 0;
                    machine.add(String.format("%d\tDC\t%d", LC++, val));
                }
                continue;
            }

            // Imperative statements
            Matcher mis = isPat.matcher(line);
            if (mis.find()) {
                String opcode = mis.group(1);
                String reg = "0";
                int mem = 0;

                // Find register
                Matcher regM = Pattern.compile("\\s(\\d)\\s|\\s(\\d)\\(").matcher(line);
                if (regM.find()) reg = (regM.group(1) != null) ? regM.group(1) : regM.group(2);

                // Find memory operand
                Matcher sm = symPat.matcher(line);
                Matcher lm = litPat.matcher(line);
                if (sm.find()) {
                    int sidx = Integer.parseInt(sm.group(1));
                    mem = symtab.get(sidx - 1).addr;
                } else if (lm.find()) {
                    int lidx = Integer.parseInt(lm.group(1));
                    mem = littab.get(lidx - 1).addr;
                }

                machine.add(String.format("%d\t%s\t%s\t%d", LC++, opcode, reg, mem));
                continue;
            }
        }

        // ---------------- OUTPUT ----------------
        System.out.println("\n--- MACHINE CODE / MEMORY IMAGE ---");
        for (String s : machine) System.out.println(s);
    }
}
