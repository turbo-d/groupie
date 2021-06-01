package com.example.daleluginbuhl.groupie;

/**
 * Created by Dale Luginbuhl on 4/2/2018.
 */

public class GroupieEffectParameter {

    private int mParamID;
    private int mParamVal;

    public GroupieEffectParameter(int id, int val){
        mParamID = id;
        mParamVal = val;
    }

    public int getParamID(){return mParamID;}
    public int getParamVal(){return mParamVal;}

    public void setParamID(int id){mParamID = id;}
    public void setParamVal(int val){mParamVal = val;}
}
