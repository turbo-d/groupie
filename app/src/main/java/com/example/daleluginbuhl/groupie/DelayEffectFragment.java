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
public class DelayEffectFragment extends Fragment {

    private static final int DELAY = 3;

    // Param IDs
    private static final int DELAY_DELAY_TIME = 6;
    private static final int DELAY_DELAY_NUMBER = 7;

    // Layout views
    private Knob mDelayTimeKnob;
    private Knob mDelayNumberKnob;

    public DelayEffectFragment() {
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
        if (bundle != null && bundle.containsKey("delay_time")){
            mDelayTimeKnob.setState(getArguments().getInt("delay_time", 0));
        }
        mDelayTimeKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(DELAY, DELAY_DELAY_TIME, state);
            }
        });

        if (bundle != null && bundle.containsKey("delay_number")){
            mDelayNumberKnob.setState(getArguments().getInt("delay_number", 0));
        }
        mDelayNumberKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(DELAY, DELAY_DELAY_NUMBER, state);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delay_effect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDelayTimeKnob = (Knob) view.findViewById(R.id.delay_time_knob);
        mDelayNumberKnob = (Knob) view.findViewById(R.id.delay_number_knob);
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
        GroupieEffectParameter delayTimeParam = new GroupieEffectParameter(DELAY_DELAY_TIME, mDelayTimeKnob.getState());
        params.add(delayTimeParam);
        GroupieEffectParameter delayNumParam = new GroupieEffectParameter(DELAY_DELAY_NUMBER, mDelayNumberKnob.getState());
        params.add(delayNumParam);
        return params;
    }

}
