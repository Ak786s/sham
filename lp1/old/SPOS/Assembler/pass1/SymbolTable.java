package pass1;
public class SymbolTable {
    public String name;
    public int add;      // <--- make public
    public SymbolTable(String name, int add) {
        this.name = name;
        this.add = add;
    }
}
