public class Dolphin {

    private static int num = 0;
    private String name;
    private int number;

    public Dolphin(){
        setNumber(num++);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}