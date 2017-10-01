package studio.app.stavv.question;

import android.content.Context;

import java.io.IOException;

/**
 * Created by vicru on 30.09.2017.
 */

public class People {
    private String name = "Lorem";
    private String sex = "null";
    private int age = 25;
    People(){

    }
    People(String sex){
        this.sex = sex;
    }

    public void setName(String n){
        name = n;
    }
    public void setSex(String s){
        sex = s;
    }
    public void setAge(int ag){
        age = ag;
    }
    public String getName(){
        return name;
    }

    public String getSex(){
        return sex;
    }

    public int getAge(){
        return age;
    }
}
