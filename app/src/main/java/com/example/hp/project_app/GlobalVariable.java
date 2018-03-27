package com.example.hp.project_app;

/**
 * @author Grasp
 *  @version 1.0 on 09-03-2018.
 */

public class GlobalVariable {

    //Declare the class singleton class
    private static GlobalVariable myObj;
    public static GlobalVariable getInstance(){
        if(myObj == null){
            myObj = new GlobalVariable();
        }
        return myObj;
    }

    public  String[] globalClient = new String[8];

    public  String[] globalProduct = new String[8];

}
