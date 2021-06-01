package com.example.daleluginbuhl.groupie;

import java.util.ArrayList;

/**
 * Created by Dale Luginbuhl on 4/2/2018.
 */

public class GroupieDataModel {

    private int mCurrentPresetID;
    private ArrayList<GroupiePreset> mPresets;

    public GroupieDataModel(int currentPresetID, ArrayList<GroupiePreset> presetList){
        mCurrentPresetID = currentPresetID;
        mPresets = presetList;
    }

    public int getCurrentPresetID(){return mCurrentPresetID;}
    public ArrayList<GroupiePreset> getPresets(){return mPresets;}

    public void setCurrentPresetID(int id){mCurrentPresetID = id;}
    public void setPresets(ArrayList<GroupiePreset> presetList){mPresets = presetList;}
}

