package com.example.daleluginbuhl.groupie;

import java.util.ArrayList;

/**
 * Created by Dale Luginbuhl on 4/2/2018.
 */

public class GroupiePreset {

    private int mPresetID;
    private String mPresetName;
    private ArrayList<GroupieEffect> mEffects;

    public GroupiePreset(int presetID, String presetName, ArrayList<GroupieEffect> effectList){
        mPresetID = presetID;
        mPresetName = presetName;
        mEffects = effectList;
    }

    public int getPresetID(){return mPresetID;}
    public String getPresetName(){return mPresetName;}
    public ArrayList<GroupieEffect> getEffects(){return mEffects;}

    public void setPresetID(int id){mPresetID = id;}
    public void setPresetName(String name){mPresetName = name;}
    public void setEffects(ArrayList<GroupieEffect> effectList){mEffects = effectList;}
}
