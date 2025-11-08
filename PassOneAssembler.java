import java.util.*;

class PassOneAssembler {
    // Opcode tables
    static Map<String, String> IS = Map.of(
        "STOP","00", "ADD","01", "SUB","02", "MULT","03",
        "MOVER","04", "MOVEM","05", "COMP","06", "BC","07", "DIV","08"
    );
    static Map<String, String> DL = Map.of("DC","01","DS","02");
    static Map<String, String> AD = Map.of(
        "START","01","END","02","ORIGIN","03","EQU","04","LTORG","05"
    );
    static Map<String, String> REG = Map.of("AREG","1","BREG","2","CREG","3","DREG","4");

    static class Symbol {
        String name; int address; boolean defined;
        Symbol(String n, int a, boolean d){ name=n; address=a; defined=d; }
    }
    static class Literal {
        String name; int address; Literal(String n,int a){name=n;address=a;}
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> source = new ArrayList<>();
        System.out.println("Enter Assembly program (end with END):");
        while(true){
            String line = sc.nextLine().trim();
            source.add(line);
            if(line.equalsIgnoreCase("END")) break;
        }

        int lc = 0;
        Map<String,Symbol> symtab = new LinkedHashMap<>();
        List<Literal> littab = new ArrayList<>();
        List<Integer> pooltab = new ArrayList<>();
        List<String> intermediate = new ArrayList<>();
        pooltab.add(0);

        for(int i=0; i<source.size(); i++){
            String line = source.get(i).trim();
            if(line.isEmpty()) continue;
            String[] parts = line.split("[ ,\\t]+");
            String label = null, opcode = null;
            int pos = 0;

            // Identify label or opcode
            if(IS.containsKey(parts[0].toUpperCase()) ||
               AD.containsKey(parts[0].toUpperCase()) ||
               DL.containsKey(parts[0].toUpperCase())) {
                opcode = parts[0].toUpperCase();
                pos = 1;
            } else {
                label = parts[0];
                opcode = parts.length>1 ? parts[1].toUpperCase() : "";
                pos = 2;
                // Define symbol
                symtab.put(label, new Symbol(label, lc, true));
            }

            // START
            if(opcode.equals("START")){
                lc = Integer.parseInt(parts[pos]);
                intermediate.add("(AD,01) (C,"+lc+")");
                continue;
            }

            // ORIGIN
            if(opcode.equals("ORIGIN")){
                String operand = parts[pos];
                if(operand.contains("+")){
                    String[] e = operand.split("\\+");
                    lc = symtab.get(e[0]).address + Integer.parseInt(e[1]);
                } else lc = symtab.get(operand).address;
                intermediate.add("(AD,03) ("+operand+")");
                continue;
            }

            // LTORG
            if(opcode.equals("LTORG")){
                intermediate.add("(AD,05)");
                for(int j=pooltab.get(pooltab.size()-1); j<littab.size(); j++){
                    littab.get(j).address = lc++;
                }
                pooltab.add(littab.size());
                continue;
            }

            // EQU
            if(opcode.equals("EQU")){
                String sym2 = parts[pos];
                if(symtab.containsKey(sym2))
                    symtab.get(label).address = symtab.get(sym2).address;
                intermediate.add("(AD,04) ("+sym2+")");
                continue;
            }

            // END
            if(opcode.equals("END")){
                intermediate.add("(AD,02)");
                for(int j=pooltab.get(pooltab.size()-1); j<littab.size(); j++){
                    littab.get(j).address = lc++;
                }
                pooltab.add(littab.size());
                break;
            }

            // Declarative Statements
            if(opcode.equals("DS")){
                int size = Integer.parseInt(parts[pos]);
                intermediate.add(lc + " (DL,02) (C,"+size+")");
                lc += size;
                continue;
            }
            if(opcode.equals("DC")){
                intermediate.add(lc + " (DL,01) (C,1)");
                lc++;
                continue;
            }

            // Imperative Statements
            if(IS.containsKey(opcode)){
                StringBuilder ic = new StringBuilder();
                ic.append(lc).append(" (IS,").append(IS.get(opcode)).append(") ");
                for(int k=pos; k<parts.length; k++){
                    String op = parts[k];
                    if(REG.containsKey(op.toUpperCase()))
                        ic.append("(RG,").append(REG.get(op.toUpperCase())).append(") ");
                    else if(op.startsWith("=")) {
                        littab.add(new Literal(op, -1));
                        ic.append("(L,").append(littab.size()).append(") ");
                    } else if(symtab.containsKey(op)){
                        int idx = new ArrayList<>(symtab.keySet()).indexOf(op)+1;
                        ic.append("(S,").append(idx).append(") ");
                    } else if(!op.isEmpty()){
                        // forward symbol
                        symtab.put(op, new Symbol(op, -1, false));
                        int idx = new ArrayList<>(symtab.keySet()).indexOf(op)+1;
                        ic.append("(S,").append(idx).append(") ");
                    }
                }
                intermediate.add(ic.toString());
                lc++;
            }
        }

        // ---------- OUTPUT ----------
        System.out.println("\n--- INTERMEDIATE CODE ---");
        for(String s: intermediate) System.out.println(s);

        System.out.println("\n--- SYMBOL TABLE ---");
        int i=1;
        for(Symbol s: symtab.values())
            System.out.printf("%d\t%s\t%d\n", i++, s.name, s.address);

        System.out.println("\n--- LITERAL TABLE ---");
        i=1;
        for(Literal l: littab)
            System.out.printf("%d\t%s\t%d\n", i++, l.name, l.address);

        System.out.println("\n--- POOL TABLE ---");
        i=1;
        for(Integer p: pooltab)
            System.out.printf("%d\t%d\n", i++, p+1);
    }
}
