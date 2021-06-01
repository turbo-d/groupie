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
public class BitcrusherEffectFragment extends Fragment {

    private static final int BITCRUSHER = 2;

    // Param IDs
    private static final int BITCRUSHER_BITS = 4;
    private static final int BITCRUSHER_SAMPLE_RATE = 5;

    // Layout views
    private Knob mBitsKnob;
    private Knob mSampleRateKnob;

    public BitcrusherEffectFragment() {
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
        if (bundle != null && bundle.containsKey("bits")){
            mBitsKnob.setState(getArguments().getInt("bits", 0));
        }
        mBitsKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(BITCRUSHER, BITCRUSHER_BITS, state);
            }
        });

        if (bundle != null && bundle.containsKey("sample_rate")){
            mSampleRateKnob.setState(getArguments().getInt("sample_rate", 0));
        }
        mSampleRateKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MainActivity activity = (MainActivity) getActivity();
                activity.paramChangeMessage(BITCRUSHER, BITCRUSHER_SAMPLE_RATE, state);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bitcrusher_effect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mBitsKnob = (Knob) view.findViewById(R.id.bitcrusher_bit_knob);
        mSampleRateKnob = (Knob) view.findViewById(R.id.bitcrusher_sample_rate_knob);
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
        GroupieEffectParameter bitsParam = new GroupieEffectParameter(BITCRUSHER_BITS, mBitsKnob.getState());
        params.add(bitsParam);
        GroupieEffectParameter sampleRateParam = new GroupieEffectParameter(BITCRUSHER_SAMPLE_RATE, mSampleRateKnob.getState());
        params.add(sampleRateParam);
        return params;
    }

}
