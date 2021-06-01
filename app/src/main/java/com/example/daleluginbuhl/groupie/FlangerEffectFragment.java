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
public class FlangerEffectFragment extends Fragment {

    private static final int FLANGER = 0;

    // Param IDs
    private static final int FLANGER_DELAY = 0;
    private static final int FLANGER_DEPTH = 1;
    private static final int FLANGER_RATE = 2;

    // Layout Views
    private Knob mDelayKnob;
    private Knob mDepthKnob;
    private Knob mRateKnob;

    public FlangerEffectFragment() {
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
        if (bundle != null && bundle.containsKey("delay")){
            mDelayKnob.setState(getArguments().getInt("delay", 0));
        }
        mDelayKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(FLANGER, FLANGER_DELAY, state);
            }
        });

        if (bundle != null && bundle.containsKey("depth")){
            mDepthKnob.setState(getArguments().getInt("depth", 0));
        }
        mDepthKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(FLANGER, FLANGER_DEPTH, state);
            }
        });

        if (bundle != null && bundle.containsKey("rate")){
            mRateKnob.setState(getArguments().getInt("rate", 0));
        }
        mRateKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(FLANGER, FLANGER_RATE, state);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flanger_effect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDelayKnob = (Knob) view.findViewById(R.id.flanger_delay_knob);
        mDepthKnob = (Knob) view.findViewById(R.id.flanger_depth_knob);
        mRateKnob = (Knob) view.findViewById(R.id.flanger_rate_knob);
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
        GroupieEffectParameter delayParam = new GroupieEffectParameter(FLANGER_DELAY, mDelayKnob.getState());
        params.add(delayParam);
        GroupieEffectParameter depthParam = new GroupieEffectParameter(FLANGER_DEPTH, mDepthKnob.getState());
        params.add(depthParam);
        GroupieEffectParameter rateParam = new GroupieEffectParameter(FLANGER_RATE, mRateKnob.getState());
        params.add(rateParam);
        return params;
    }
}
