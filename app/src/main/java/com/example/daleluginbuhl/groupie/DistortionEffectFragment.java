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
public class DistortionEffectFragment extends Fragment {

    private static final int DISTORTION = 6;

    // Param IDs
    private static final int DISTORTION_LEVEL = 11;

    // Layout views
    private Knob mLevelKnob;

    public DistortionEffectFragment() {
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
        if (bundle != null && bundle.containsKey("level")){
            mLevelKnob.setState(getArguments().getInt("level", 0));
        }
        mLevelKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(DISTORTION, DISTORTION_LEVEL, state);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_distortion_effect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mLevelKnob = (Knob) view.findViewById(R.id.distortion_level_knob);
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
        GroupieEffectParameter levelParam = new GroupieEffectParameter(DISTORTION_LEVEL, mLevelKnob.getState());
        params.add(levelParam);
        return params;
    }

}
