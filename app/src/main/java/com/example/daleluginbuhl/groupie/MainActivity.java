package com.example.daleluginbuhl.groupie;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Fragment TAGS
    private static final String DRIVE_NO_EFFECT_TAG = "DriveNoEffectFragment";
    private static final String MOD_NO_EFFECT_TAG = "ModNoEffectFragment";
    private static final String DELAY_NO_EFFECT_TAG = "DelayNoEffectFragment";
    private static final String FLANGER_TAG = "FlangerEffectFragment";
    private static final String CHORUS_TAG = "ChorusEffectFragment";
    private static final String BITCRUSHER_TAG = "BitcrusherEffectFragment";
    private static final String DELAY_TAG = "DelayEffectFragment";
    private static final String REVERB_TAG = "ReverbEffectFragment";
    private static final String FUZZ_TAG = "FuzzEffectFragment";
    private static final String DISTORTION_TAG = "DistortionEffectFragment";

    // Effect IDs
    private static final int NO_EFFECT = -1;
    private static final int FLANGER = 0;
    private static final int CHORUS = 1;
    private static final int BITCRUSHER = 2;
    private static final int DELAY = 3;
    private static final int REVERB = 4;
    private static final int FUZZ = 5;
    private static final int DISTORTION = 6;

    // Param IDs
    private static final int FLANGER_DELAY = 0;
    private static final int FLANGER_DEPTH = 1;
    private static final int FLANGER_RATE = 2;
    private static final int CHORUS_VOICES = 3;
    private static final int BITCRUSHER_BITS = 4;
    private static final int BITCRUSHER_SAMPLE_RATE = 5;
    private static final int DELAY_DELAY_TIME = 6;
    private static final int DELAY_DELAY_NUMBER = 7;
    private static final int REVERB_ROOM_SIZE = 8;
    private static final int REVERB_DAMPING = 9;
    private static final int FUZZ_LEVEL = 10;
    private static final int DISTORTION_LEVEL = 11;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private Spinner mPresetSelectSpinner;
    private EditText mPresetNameEditText;
    private ImageView mEditedLed;
    private Button mSaveButton;
    private Spinner mDriveSelectSpinner;
    private Spinner mModSelectSpinner;
    private Spinner mDelaySelectSpinner;

    // Spinner Adapters
    private ArrayAdapter<CharSequence> mPresetSelectArrayAdapter;
    private ArrayAdapter<CharSequence> mPresetDriveArrayAdapter;
    private ArrayAdapter<CharSequence> mPresetModArrayAdapter;
    private ArrayAdapter<CharSequence> mPresetDelayArrayAdapter;

    // Holds current preset ID
    private int mCurrentPresetID;

    // Holds current preset name
    private String mCurrentPresetName;

    // Groupie data model
    private GroupieDataModel mDataModel;

    // Edited flag
    private boolean mIsEdited;

    // Effect block flags;
    private int mDriveBlock;
    private int mModBlock;
    private int mDelayBlock;

    //Name of the connected device
    private String mConnectedDeviceName = null;

    //Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    // Flags to skip spinner calls
    private int mPresetSpinnerCheck;
    private int mDriveSpinnerCheck;
    private int mModSpinnerCheck;
    private int mDelaySpinnerCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        // Create layout views
        mPresetSelectSpinner = (Spinner) findViewById(R.id.preset_select);
        mPresetNameEditText = (EditText) findViewById(R.id.preset_name);
        mEditedLed = (ImageView) findViewById(R.id.edited_led);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mDriveSelectSpinner = (Spinner) findViewById(R.id.drive_select);
        mModSelectSpinner = (Spinner) findViewById(R.id.mod_select);
        mDelaySelectSpinner = (Spinner) findViewById(R.id.delay_select);

        // TODO Handle savedInstanceState
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupUI();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groupie, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
        }
        return false;
    }

    public void paramChangeMessage(int effectID, int paramID, int paramVal){
        GroupieMessage msg = new GroupieMessage(GroupieMessage.GroupieMessageType.PARAM_CHANGE,
                mDataModel.getCurrentPresetID(), effectID, paramID, paramVal);
        sendMessage(msg.createMessageString());
    }

    private void setupUI() {

        // Configure preset selection spinner
        mPresetSelectArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.preset_select_array, R.layout.groupie_spinner_item);
        mPresetSelectArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPresetSelectSpinner.setAdapter(mPresetSelectArrayAdapter);

        mPresetSpinnerCheck = 0;
        mPresetSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++mPresetSpinnerCheck < 2){
                    return;
                }

                // Turn off Edited LED if on
                mIsEdited = false;
                mEditedLed.setImageResource(R.drawable.edited_led_off);

                int newPresetID = position;

                // Send PresetLoad message to pedal
                GroupieMessage msg = new GroupieMessage(GroupieMessage.GroupieMessageType.PARAM_CHANGE, newPresetID, -1, -1, -1);
                sendMessage(msg.createMessageString());

                // Update UI
                presetChange(newPresetID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Configure drive block effect selection spinner
        mPresetDriveArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.drive_select_array, R.layout.groupie_spinner_item);
        mPresetDriveArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDriveSelectSpinner.setAdapter(mPresetDriveArrayAdapter);

        mDriveSpinnerCheck = 0;
        mDriveSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++mDriveSpinnerCheck < 2){
                    return;
                }

                // Update the edited LED
                mIsEdited = true;
                mEditedLed.setImageResource(R.drawable.edited_led_on);

                // Send EffectOff message
                if (mDriveBlock != NO_EFFECT){
                    GroupieMessage msg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_OFF,
                            mDataModel.getCurrentPresetID(), mDriveBlock, -1, -1);
                    sendMessage(msg.createMessageString());
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (position){
                    case 0:
                        // No Effect
                        NoEffectFragment noEffectFragment = new NoEffectFragment();
                        transaction.replace(R.id.drive_block, noEffectFragment, DRIVE_NO_EFFECT_TAG);
                        transaction.commit();

                        mDriveBlock = NO_EFFECT;
                        break;
                    case 1:
                        // Distortion
                        GroupieMessage distortionMsg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_ON,
                                mDataModel.getCurrentPresetID(), DISTORTION, -1, -1);
                        sendMessage(distortionMsg.createMessageString());

                        DistortionEffectFragment distortionEffectFragment = new DistortionEffectFragment();
                        transaction.replace(R.id.drive_block, distortionEffectFragment, DISTORTION_TAG);
                        transaction.commit();

                        mDriveBlock = DISTORTION;
                        break;
                    case 2:
                        // Fuzz
                        GroupieMessage fuzzMsg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_ON,
                                mDataModel.getCurrentPresetID(), FUZZ, -1, -1);
                        sendMessage(fuzzMsg.createMessageString());

                        FuzzEffectFragment fuzzEffectFragment = new FuzzEffectFragment();
                        transaction.replace(R.id.drive_block, fuzzEffectFragment, FUZZ_TAG);
                        transaction.commit();

                        mDriveBlock = FUZZ;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Configure mod block effect selection spinner
        mPresetModArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.mod_select_array, R.layout.groupie_spinner_item);
        mPresetModArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mModSelectSpinner.setAdapter(mPresetModArrayAdapter);

        mModSpinnerCheck = 0;
        mModSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++mModSpinnerCheck < 2){
                    return;
                }

                // Update the edited LED
                mIsEdited = true;
                mEditedLed.setImageResource(R.drawable.edited_led_on);

                // Send EffectOff message
                if (mModBlock != NO_EFFECT){
                    GroupieMessage msg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_OFF,
                            mDataModel.getCurrentPresetID(), mModBlock, -1, -1);
                    sendMessage(msg.createMessageString());
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (position){
                    case 0:
                        // No Effect
                        NoEffectFragment noEffectFragment = new NoEffectFragment();
                        transaction.replace(R.id.mod_block, noEffectFragment, MOD_NO_EFFECT_TAG);
                        transaction.commit();

                        mModBlock = NO_EFFECT;
                        break;
                    case 1:
                        // Flanger
                        GroupieMessage flangerMsg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_ON,
                                mDataModel.getCurrentPresetID(), FLANGER, -1, -1);
                        sendMessage(flangerMsg.createMessageString());

                        FlangerEffectFragment flangerFragment = new FlangerEffectFragment();
                        transaction.replace(R.id.mod_block, flangerFragment, FLANGER_TAG);
                        transaction.commit();

                        mModBlock = FLANGER;
                        break;
                    case 2:
                        // Chorus
                        GroupieMessage chorusMsg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_ON,
                                mDataModel.getCurrentPresetID(), CHORUS, -1, -1);
                        sendMessage(chorusMsg.createMessageString());

                        ChorusEffectFragment chorusFragment = new ChorusEffectFragment();
                        transaction.replace(R.id.mod_block, chorusFragment, CHORUS_TAG);
                        transaction.commit();

                        mModBlock = CHORUS;
                        break;
                    case 3:
                        // Bitcrusher
                        GroupieMessage bitcrusherMsg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_ON,
                                mDataModel.getCurrentPresetID(), BITCRUSHER, -1, -1);
                        sendMessage(bitcrusherMsg.createMessageString());

                        BitcrusherEffectFragment bitcrusherFragment = new BitcrusherEffectFragment();
                        transaction.replace(R.id.mod_block, bitcrusherFragment, BITCRUSHER_TAG);
                        transaction.commit();

                        mModBlock = BITCRUSHER;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Configure delay block effect selection spinner
        mPresetDelayArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.delay_select_array, R.layout.groupie_spinner_item);
        mPresetDelayArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDelaySelectSpinner.setAdapter(mPresetDelayArrayAdapter);

        mDelaySpinnerCheck = 0;
        mDelaySelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (++mDelaySpinnerCheck < 2){
                    return;
                }

                // Update the edited LED
                mIsEdited = true;
                mEditedLed.setImageResource(R.drawable.edited_led_on);

                // Send EffectOff message
                if (mDelayBlock != NO_EFFECT){
                    GroupieMessage msg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_OFF,
                            mDataModel.getCurrentPresetID(), mDelayBlock, -1, -1);
                    sendMessage(msg.createMessageString());
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (position){
                    case 0:
                        // No Effect
                        NoEffectFragment noEffectFragment = new NoEffectFragment();
                        transaction.replace(R.id.delay_block, noEffectFragment, DELAY_NO_EFFECT_TAG);
                        transaction.commit();

                        mDelayBlock = NO_EFFECT;
                        break;
                    case 1:
                        // Reverb
                        GroupieMessage reverbMsg = new GroupieMessage(GroupieMessage.GroupieMessageType.EFFECT_ON,
                                mDataModel.getCurrentPresetID(), REVERB, -1, -1);
                        sendMessage(reverbMsg.createMessageString());

                        ReverbEffectFragment reverbEffectFragment = new ReverbEffectFragment();
                        transaction.replace(R.id.delay_block, reverbEffectFragment, REVERB_TAG);
                        transaction.commit();

                        mDelayBlock = REVERB;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mIsEdited){
                    return;
                }

                // Send PresetSave message to pedal
                GroupieMessage msg = new GroupieMessage(GroupieMessage.GroupieMessageType.PRESET_SAVE,
                        mDataModel.getCurrentPresetID(), -1, -1, -1);
                sendMessage(msg.createMessageString());

                // Save the changes to the preset
                saveChangesToPreset();

                // Turn the Edited LED off
                mIsEdited = false;
                mEditedLed.setImageResource(R.drawable.edited_led_off);
            }
        });


        mPresetNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                mIsEdited = true;
                mEditedLed.setImageResource(R.drawable.edited_led_on);

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // Configure edited LED
        mIsEdited = false;
        mEditedLed.setImageResource(R.drawable.edited_led_off);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        mDataModel = loadDataModelFromPref();

        int currentPreset = mDataModel.getCurrentPresetID();
        buildUIFromPreset(mDataModel.getPresets().get(currentPreset));
    }

    private void saveDataModelInPref(GroupieDataModel dataModel) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dataModel);
        prefsEditor.putString("GroupieDataModel", json);
        prefsEditor.commit();
    }

    private GroupieDataModel loadDataModelFromPref() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("GroupieDataModel", "");
        GroupieDataModel model;
        if (json.equals("")){
            model = buildDefaultDataModel();
        }
        else{
            model = gson.fromJson(json, GroupieDataModel.class);
        }

        return model;
    }

    private GroupieDataModel buildDefaultDataModel(){
        ArrayList<GroupieEffect> effectList = new ArrayList<GroupieEffect>();
        effectList.add(new GroupieEffect(NO_EFFECT, null));
        effectList.add(new GroupieEffect(NO_EFFECT, null));
        effectList.add(new GroupieEffect(NO_EFFECT, null));

        ArrayList<GroupiePreset> presetList = new ArrayList<GroupiePreset>();
        GroupiePreset preset = new GroupiePreset(0, "Preset 1", effectList);
        presetList.add(preset);
        preset = new GroupiePreset(1, "Preset 2", effectList);
        presetList.add(preset);
        preset = new GroupiePreset(2, "Preset 3", effectList);
        presetList.add(preset);
        preset = new GroupiePreset(3, "Preset 4", effectList);
        presetList.add(preset);
        preset = new GroupiePreset(4, "Preset 5", effectList);
        presetList.add(preset);

        GroupieDataModel model = new GroupieDataModel(0, presetList);
        return model;
    }

    private GroupiePreset buildPresetFromUI(){
        // Get UI state and build a GroupiePreset
        int presetID = mPresetSelectSpinner.getSelectedItemPosition();
        String presetName = mPresetNameEditText.getText().toString();
        ArrayList<GroupieEffect> effectList = new ArrayList<GroupieEffect>();

        switch(mDriveBlock){
            case NO_EFFECT:
                GroupieEffect noEffect = new GroupieEffect(NO_EFFECT, null);
                effectList.add(noEffect);
                break;
            case DISTORTION:
                DistortionEffectFragment distFragment = (DistortionEffectFragment) getSupportFragmentManager().findFragmentByTag(DISTORTION_TAG);
                ArrayList<GroupieEffectParameter> distParamList = distFragment.getEffectParameters();
                GroupieEffect distEffect = new GroupieEffect(DISTORTION, distParamList);
                effectList.add(distEffect);
                break;
            case FUZZ:
                FuzzEffectFragment fuzzFragment = (FuzzEffectFragment) getSupportFragmentManager().findFragmentByTag(FUZZ_TAG);
                ArrayList<GroupieEffectParameter> fuzzParamList = fuzzFragment.getEffectParameters();
                GroupieEffect fuzzEffect = new GroupieEffect(FUZZ, fuzzParamList);
                effectList.add(fuzzEffect);
                break;
        }

        switch (mModBlock){
            case NO_EFFECT:
                GroupieEffect noEffect = new GroupieEffect(NO_EFFECT, null);
                effectList.add(noEffect);
                break;
            case FLANGER:
                FlangerEffectFragment flangerFragment = (FlangerEffectFragment) getSupportFragmentManager().findFragmentByTag(FLANGER_TAG);
                ArrayList<GroupieEffectParameter> flangerParamList = flangerFragment.getEffectParameters();
                GroupieEffect flangerEffect = new GroupieEffect(FLANGER, flangerParamList);
                effectList.add(flangerEffect);
                break;
            case CHORUS:
                ChorusEffectFragment chorusFragment = (ChorusEffectFragment) getSupportFragmentManager().findFragmentByTag(CHORUS_TAG);
                ArrayList<GroupieEffectParameter> chorusParamList = chorusFragment.getEffectParameters();
                GroupieEffect chorusEffect = new GroupieEffect(CHORUS, chorusParamList);
                effectList.add(chorusEffect);
                break;
            case BITCRUSHER:
                BitcrusherEffectFragment bcFragment = (BitcrusherEffectFragment) getSupportFragmentManager().findFragmentByTag(BITCRUSHER_TAG);
                ArrayList<GroupieEffectParameter> bcParamList = bcFragment.getEffectParameters();
                GroupieEffect bcEffect = new GroupieEffect(BITCRUSHER, bcParamList);
                effectList.add(bcEffect);
                break;
        }

        switch (mDelayBlock){
            case NO_EFFECT:
                GroupieEffect noEffect = new GroupieEffect(NO_EFFECT, null);
                effectList.add(noEffect);
                break;
            case REVERB:
                ReverbEffectFragment reverbFragment = (ReverbEffectFragment) getSupportFragmentManager().findFragmentByTag(REVERB_TAG);
                ArrayList<GroupieEffectParameter> reverbParamList = reverbFragment.getEffectParameters();
                GroupieEffect reverbEffect = new GroupieEffect(REVERB, reverbParamList);
                effectList.add(reverbEffect);
                break;
        }

        return new GroupiePreset(presetID, presetName, effectList);
    }

    private void buildUIFromPreset(GroupiePreset preset){
        // Update the UI state given the preset data
        mPresetSelectSpinner.setSelection(preset.getPresetID());
        mPresetNameEditText.setText(preset.getPresetName());
        if (preset.getEffects() != null && preset.getEffects().size() != 0)
        {
            ArrayList<GroupieEffect> effectList = preset.getEffects();
            switch(effectList.get(0).getEffectID()){
                case NO_EFFECT:
                    mDriveSelectSpinner.setSelection(0);

                    FragmentTransaction neTransaction = getSupportFragmentManager().beginTransaction();
                    NoEffectFragment noEffectFragment = new NoEffectFragment();
                    neTransaction.replace(R.id.drive_block, noEffectFragment, DRIVE_NO_EFFECT_TAG);
                    neTransaction.commit();

                    mDriveBlock = NO_EFFECT;
                    break;
                case DISTORTION:
                    mDriveSelectSpinner.setSelection(1);

                    DistortionEffectFragment distFragment = new DistortionEffectFragment();

                    Bundle distArgs = new Bundle();
                    distArgs.putInt("level", effectList.get(0).getEffectParams().get(0).getParamVal());
                    distFragment.setArguments(distArgs);

                    FragmentTransaction distTransaction = getSupportFragmentManager().beginTransaction();
                    distTransaction.replace(R.id.drive_block, distFragment, DISTORTION_TAG);
                    distTransaction.commit();

                    mModBlock = DISTORTION;
                    break;
                case FUZZ:
                    mDriveSelectSpinner.setSelection(2);

                    FuzzEffectFragment fuzzFragment = new FuzzEffectFragment();

                    Bundle fuzzArgs = new Bundle();
                    fuzzArgs.putInt("level", effectList.get(0).getEffectParams().get(0).getParamVal());
                    fuzzFragment.setArguments(fuzzArgs);

                    FragmentTransaction fzTransaction = getSupportFragmentManager().beginTransaction();
                    fzTransaction.replace(R.id.drive_block, fuzzFragment, FUZZ_TAG);
                    fzTransaction.commit();

                    mModBlock = FUZZ;
                    break;
            }

            switch (effectList.get(1).getEffectID()){
                case NO_EFFECT:
                    mModSelectSpinner.setSelection(0);

                    FragmentTransaction neTransaction = getSupportFragmentManager().beginTransaction();
                    NoEffectFragment noEffectFragment = new NoEffectFragment();
                    neTransaction.replace(R.id.mod_block, noEffectFragment, MOD_NO_EFFECT_TAG);
                    neTransaction.commit();

                    mModBlock = NO_EFFECT;
                    break;
                case FLANGER:
                    mModSelectSpinner.setSelection(1);

                    FlangerEffectFragment flangerFragment = new FlangerEffectFragment();

                    Bundle flangerArgs = new Bundle();
                    flangerArgs.putInt("delay", effectList.get(1).getEffectParams().get(0).getParamVal());
                    flangerArgs.putInt("depth", effectList.get(1).getEffectParams().get(1).getParamVal());
                    flangerArgs.putInt("rate", effectList.get(1).getEffectParams().get(2).getParamVal());
                    flangerFragment.setArguments(flangerArgs);

                    FragmentTransaction flgTransaction = getSupportFragmentManager().beginTransaction();
                    flgTransaction.replace(R.id.mod_block, flangerFragment, FLANGER_TAG);
                    flgTransaction.commit();

                    mModBlock = FLANGER;
                    break;
                case CHORUS:
                    mModSelectSpinner.setSelection(2);

                    ChorusEffectFragment chorusFragment = new ChorusEffectFragment();

                    Bundle chorusArgs = new Bundle();
                    chorusArgs.putInt("voices", effectList.get(1).getEffectParams().get(0).getParamVal());
                    chorusFragment.setArguments(chorusArgs);

                    FragmentTransaction chTransaction = getSupportFragmentManager().beginTransaction();
                    chTransaction.replace(R.id.mod_block, chorusFragment, CHORUS_TAG);
                    chTransaction.commit();

                    mModBlock = CHORUS;
                    break;
                case BITCRUSHER:
                    mModSelectSpinner.setSelection(3);

                    BitcrusherEffectFragment bcFragment = new BitcrusherEffectFragment();

                    Bundle bcArgs = new Bundle();
                    bcArgs.putInt("bits", effectList.get(1).getEffectParams().get(0).getParamVal());
                    bcArgs.putInt("sample_rate", effectList.get(1).getEffectParams().get(1).getParamVal());
                    bcFragment.setArguments(bcArgs);

                    FragmentTransaction bcTransaction = getSupportFragmentManager().beginTransaction();
                    bcTransaction.replace(R.id.mod_block, bcFragment, BITCRUSHER_TAG);
                    bcTransaction.commit();

                    mModBlock = BITCRUSHER;
                    break;
            }

            switch (effectList.get(2).getEffectID()){
                case NO_EFFECT:
                    mDelaySelectSpinner.setSelection(0);

                    FragmentTransaction neTransaction = getSupportFragmentManager().beginTransaction();
                    NoEffectFragment noEffectFragment = new NoEffectFragment();
                    neTransaction.replace(R.id.delay_block, noEffectFragment, DELAY_NO_EFFECT_TAG);
                    neTransaction.commit();

                    mDelayBlock = NO_EFFECT;
                    break;
                case REVERB:
                    mDelaySelectSpinner.setSelection(2);

                    ReverbEffectFragment reverbFragment = new ReverbEffectFragment();

                    Bundle reverbArgs = new Bundle();
                    reverbArgs.putInt("room_size", effectList.get(2).getEffectParams().get(0).getParamVal());
                    reverbArgs.putInt("damping", effectList.get(2).getEffectParams().get(1).getParamVal());
                    reverbFragment.setArguments(reverbArgs);

                    FragmentTransaction rvbTransaction = getSupportFragmentManager().beginTransaction();
                    rvbTransaction.replace(R.id.delay_block, reverbFragment, REVERB_TAG);
                    rvbTransaction.commit();

                    mDelayBlock = REVERB;
                    break;
            }
        }
    }

    private void saveChangesToPreset(){
        GroupiePreset currentPreset = buildPresetFromUI();
        updateLocalDataModel(currentPreset);
        saveDataModelInPref(mDataModel);
    }

    private void updateLocalDataModel(GroupiePreset preset){
        // Update the local data model member with an updated preset
        ArrayList<GroupiePreset> presetList = mDataModel.getPresets();
        presetList.set(preset.getPresetID(), preset);
        mDataModel.setPresets(presetList);
    }

    private void presetChange(int presetID){
        mDataModel.setCurrentPresetID(presetID);
        GroupiePreset newPreset = mDataModel.getPresets().get(presetID);
        buildUIFromPreset(newPreset);
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    // Updates the status on the action bar.
    private void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    // Updates the status on the action bar.
    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    // Establish secure connection with other device
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final android.os.Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    GroupieMessage message = new GroupieMessage(readMessage);
                    switch(message.getType()){
                        case PRESET_LOAD:
                            presetChange(message.getPresetID());
                            break;
                        default:
                            break;
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Toast.makeText(MainActivity.this, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}