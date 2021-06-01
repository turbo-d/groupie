package com.example.daleluginbuhl.groupie;

import java.util.ArrayList;

/**
 * Created by Dale Luginbuhl on 4/2/2018.
 */

public class GroupieMessage {

    public enum GroupieMessageType {
        UNDEFINED,
        SYNC,
        EFFECT_ON,
        EFFECT_OFF,
        PRESET_LOAD,
        PRESET_SAVE,
        PARAM_CHANGE
    }

    private GroupieMessageType mType;
    private int mPresetID;
    private int mEffectID;
    private int mParamID;
    private int mParamVal;

    public GroupieMessage(GroupieMessageType type,
                          int presetID,
                          int effectID,
                          int paramID,
                          int paramVal){
        mType = type;
        mPresetID = presetID;
        mEffectID = effectID;
        mParamID = paramID;
        mParamVal = paramVal;
    }

    public GroupieMessage(String msg){
        String[] subStrings = msg.split(":");

        if (subStrings[1].equals("Sync")){
            mType = GroupieMessageType.SYNC;
        }
        else if (subStrings[1].equals("EffectOn")){
            mType = GroupieMessageType.EFFECT_ON;
        }
        else if (subStrings[1].equals("EffectOff")){
            mType = GroupieMessageType.EFFECT_OFF;
        }
        else if (subStrings[1].equals("PresetLoad")){
            mType = GroupieMessageType.PRESET_LOAD;
        }
        else if (subStrings[1].equals("PresetSave")){
            mType = GroupieMessageType.PRESET_SAVE;
        }
        else if (subStrings[1].equals("ParamChange")){
            mType = GroupieMessageType.PARAM_CHANGE;
        }
        else{
            mType = GroupieMessageType.UNDEFINED;
        }

        mPresetID = Integer.parseInt(subStrings[2]);
        mEffectID = Integer.parseInt(subStrings[3]);
        mParamID = Integer.parseInt(subStrings[4]);
        mParamVal = Integer.parseInt(subStrings[5]);
    }

    public String createMessageString(){
        String type = "";
        switch(mType){
            case SYNC:
                type = "Sync";
                break;
            case EFFECT_ON:
                type = "EffectOn";
                break;
            case EFFECT_OFF:
                type = "EffectOff";
                break;
            case PRESET_LOAD:
                type = "PresetLoad";
                break;
            case PRESET_SAVE:
                type = "PresetSave";
                break;
            case PARAM_CHANGE:
                type = "ParamChange";
                break;
            default:
                break;
        }

        String msg = "Groupie:" + type + ":" + mPresetID + ":"
                + mEffectID + ":"+ mParamID + ":" + mParamVal + ":STOP";

        return msg;
    }

    public GroupieMessageType getType(){return mType;}
    public int getPresetID(){return mPresetID;}
    public int getEffectID(){return mEffectID;}
    public int getParamID(){return mParamID;}
    public int getParamVal(){return mParamVal;}

    public void setType(GroupieMessageType type){mType = type;}
    public void setPresetID(int id){mPresetID = id;}
    public void setEffectID(int id){mEffectID = id;}
    public void setParamID(int id){mParamID = id;}
    public void setmParamVal(int val){mParamVal = val;}

}
