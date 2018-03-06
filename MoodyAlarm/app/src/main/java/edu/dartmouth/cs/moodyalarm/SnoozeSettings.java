package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vivianjiang on 2/25/18.
 */

public class SnoozeSettings extends Fragment {

    public View view;
    Button btn;
    private Spinner snooze_length;
    private Spinner snooze_max;
    private RadioButton defaultIm;
    private RadioButton customIm;
    private Button save;

    private Switch voiceSwtich;
    private SeekBar voiceDifficulty;
    private Switch sudokuSwtich;
    private SeekBar sudokuDifficulty;
    private Switch puzzleSwtich;
    private SeekBar puzzleDifficulty;
    private Switch mathSwtich;
    private SeekBar mathDifficulty;


    public static final String SNOOZE_LENGTH = "snooze_length";
    public static final String SNOOZE_MAX = "snooze_max";
    public static final String VOICE_ON = "voice_on";
    public static final String VOICE_DIFF = "voice_diff";
    public static final String SUDOKU_ON = "sudoku_on";
    public static final String SUDOKU_DIFF = "sudoku_diff";
    public static final String PUZZLE_ON = "puzzle_on";
    public static final String PUZZLE_DIFF = "puzzle_diff";
    public static final String MATH_ON = "math_on";
    public static final String MATH_DIFF = "math_diff";

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.snooze_settings, container, false);
        super.onCreate(savedInstanceState);

        snooze_length=(Spinner) view.findViewById(R.id.window_amount);
        snooze_max = (Spinner) view.findViewById(R.id.max_snooze);

        voiceSwtich = (Switch) view.findViewById(R.id.voiceSwtich);
        voiceDifficulty = (SeekBar) view.findViewById(R.id.voiceDiff);
        sudokuSwtich = (Switch) view.findViewById(R.id.sudokuSwtich);
        sudokuDifficulty = (SeekBar) view.findViewById(R.id.sudokuDiff);
        puzzleSwtich = (Switch) view.findViewById(R.id.puzzleSwtich);
        puzzleDifficulty = (SeekBar) view.findViewById(R.id.puzzleDiff);
        mathSwtich = (Switch) view.findViewById(R.id.mathSwtich);
        mathDifficulty = (SeekBar) view.findViewById(R.id.mathDiff);

//        defaultIm = (RadioButton) view.findViewById(R.id.defaultImages);
//        customIm = (RadioButton) view.findViewById(R.id.customImages);

        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);

        Integer snoozeLength = prefs.getInt(SNOOZE_LENGTH, 11);
        if (snoozeLength != 0) {
            snooze_length.setSelection(snoozeLength - 1);
        }
        Integer snoozeMax = prefs.getInt(SNOOZE_MAX, 3);
        if (snoozeMax != 0) {
            snooze_max.setSelection(snoozeMax);
        }
        Boolean voiceOn = prefs.getBoolean(VOICE_ON, true);
        voiceSwtich.setChecked(voiceOn);
        Integer voiceDiff = prefs.getInt(VOICE_DIFF, 100);
        voiceDifficulty.setProgress(voiceDiff);

        Boolean sudokuOn = prefs.getBoolean(SUDOKU_ON, true);
        sudokuSwtich.setChecked(sudokuOn);
        Integer sudokuDiff = prefs.getInt(SUDOKU_DIFF, 85);
        sudokuDifficulty.setProgress(sudokuDiff);

        Boolean puzzleOn = prefs.getBoolean(PUZZLE_ON, true);
        puzzleSwtich.setChecked(puzzleOn);
        Integer puzzleDiff = prefs.getInt(PUZZLE_DIFF, 60);
        puzzleDifficulty.setProgress(puzzleDiff);

        Boolean mathOn = prefs.getBoolean(MATH_ON, true);
        mathSwtich.setChecked(mathOn);
        Integer mathDiff = prefs.getInt(MATH_DIFF, 60);
        mathDifficulty.setProgress(mathDiff);

        save = (Button)view.findViewById(R.id.saveSnooze);
        save.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putInt(SNOOZE_LENGTH, snooze_length.getSelectedItemPosition());
                editor.putInt(SNOOZE_MAX, snooze_length.getSelectedItemPosition());
                editor.putBoolean("Image", defaultIm.isChecked());

                // Commit the edits
                editor.apply();
                // show 'Saved' toast
                Context context = getActivity().getApplicationContext();
                CharSequence text = "Saved";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                getFragmentManager().popBackStack();
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Snooze Settings");
    }
}