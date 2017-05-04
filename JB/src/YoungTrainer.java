/**
 * Created by User on 03.05.2017.
 */
public class YoungTrainer /*extends Trainer*/ {

    public YoungTrainer() {  }

    private String nickname = "";

    private int age = 25;

    private Dolphin dol = null;

    private PhD phd = null;

    public int getYouth() { return age; }

    public void setYouth(int a) { this.age = a; }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String name) {
        this.nickname = name;
    }

    public Dolphin getDol() { return dol; }

    public void setDol(Dolphin d) { dol = d; }

    public PhD getPhd() { return phd; }

    public void setPhd(PhD p) { phd = p; }
}
