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
public class ChorusEffectFragment extends Fragment {

    private static final int CHORUS = 1;

    // Param IDs
    private static final int CHORUS_VOICES = 3;

    Knob mVoicesKnob;

    public ChorusEffectFragment() {
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
        if (bundle != null && bundle.containsKey("voices")){
            mVoicesKnob.setState(getArguments().getInt("voices", 0));
        }
        mVoicesKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(CHORUS, CHORUS_VOICES, state);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chorus_effect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mVoicesKnob = (Knob) view.findViewById(R.id.chorus_voices_knob);
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
        GroupieEffectParameter voicesParam = new GroupieEffectParameter(CHORUS_VOICES, mVoicesKnob.getState());
        params.add(voicesParam);
        return params;
    }

}
