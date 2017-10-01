package studio.app.stavv.question;

import java.util.Map;

/**
 * Created by mordy on 08.07.17.
 */

public class PeopleData {
    private int age;
    private boolean buisness;
    private int oborot;
    private String name;
    private String secondName;
    private String sex;
    private String num;
    private int turnover;
    private int sum;

    public PeopleData() {
    }

    public PeopleData(int age, boolean buisness, int oborot, String name, String secondName, String sex, String num, int turnover, int sum) {
        this.age = age;
        this.buisness = buisness;
        this.oborot = oborot;
        this.turnover = turnover;
        this.name = name;
        this.secondName = secondName;
        this.sex = sex;
        this.num = num;
        this.sum = sum;
    }

    public void setTurnover(int turnover) {
        this.turnover = turnover;
    }

    public int getTurnover() {
        return turnover;
    }
    public int getSum(){
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSex() {
        return sex;
    }

    public int getOborot() {
        return oborot;
    }

    public boolean isBuisness() {
        return buisness;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBuisness(boolean buisness) {
        this.buisness = buisness;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOborot(int oborot) {
        this.oborot = oborot;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
