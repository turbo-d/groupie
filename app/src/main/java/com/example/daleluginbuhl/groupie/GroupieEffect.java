package com.example.daleluginbuhl.groupie;

import java.util.ArrayList;

/**
 * Created by Dale Luginbuhl on 4/2/2018.
 */

public class GroupieEffect {

    private int mEffectID;
    private ArrayList<GroupieEffectParameter> mEffectParams;

    public GroupieEffect(int id, ArrayList<GroupieEffectParameter> paramList){
        mEffectID = id;
        mEffectParams = paramList;
    }

    public int getEffectID(){return mEffectID;}
    public ArrayList<GroupieEffectParameter> getEffectParams(){return mEffectParams;}

    public void setEffectID(int id){mEffectID = id;}
    public void setEffectParams(ArrayList<GroupieEffectParameter> paramList){mEffectParams = paramList;}
}
