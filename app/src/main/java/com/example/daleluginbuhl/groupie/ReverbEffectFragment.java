package com.example.daleluginbuhl.groupie;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.beppi.knoblibrary.Knob;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReverbEffectFragment extends Fragment {

    private static final int REVERB = 4;

    // Param IDs
    private static final int REVERB_ROOM_SIZE = 8;
    private static final int REVERB_DAMPING = 9;

    // Layout views
    private Knob mRoomSizeKnob;
    private Knob mDampingKnob;

    public ReverbEffectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("room_size")){
            mRoomSizeKnob.setState(getArguments().getInt("room_size", 0));
        }
        mRoomSizeKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(REVERB, REVERB_ROOM_SIZE, state);
            }
        });

        if (bundle != null && bundle.containsKey("damping")){
            mDampingKnob.setState(getArguments().getInt("damping", 0));
        }
        mDampingKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(REVERB, REVERB_DAMPING, state);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reverb_effect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRoomSizeKnob = (Knob) view.findViewById(R.id.reverb_room_size_knob);
        mDampingKnob = (Knob) view.findViewById(R.id.reverb_damping_knob);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public ArrayList<GroupieEffectParameter> getEffectParameters(){
        ArrayList<GroupieEffectParameter> params = new ArrayList<GroupieEffectParameter>();
        GroupieEffectParameter roomSizeParam = new GroupieEffectParameter(REVERB_ROOM_SIZE, mRoomSizeKnob.getState());
        params.add(roomSizeParam);
        GroupieEffectParameter dampingParam = new GroupieEffectParameter(REVERB_DAMPING, mDampingKnob.getState());
        params.add(dampingParam);
        return params;
    }

}
