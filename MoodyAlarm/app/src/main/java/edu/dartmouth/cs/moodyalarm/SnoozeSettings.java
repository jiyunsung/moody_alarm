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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vivianjiang on 2/25/18.
 */

public class SnoozeSettings extends Fragment {

    public View view;
    Button btn;
    private Spinner snooze_length;
    private Spinner dismiss_challenge;
    private RadioButton defaultIm;
    private RadioButton customIm;
    private Button save;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.snooze_settings, container, false);
        super.onCreate(savedInstanceState);

        snooze_length=(Spinner) view.findViewById(R.id.window_amount);
        dismiss_challenge = (Spinner) view.findViewById(R.id.activity_type);
        defaultIm = (RadioButton) view.findViewById(R.id.defaultImages);
        customIm = (RadioButton) view.findViewById(R.id.customImages);


        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);

        Integer restoredLength = prefs.getInt("Length", 11);
        if (restoredLength != 0) {
            snooze_length.setSelection(restoredLength - 1);
        }
        Integer restoredActivity = prefs.getInt("Activity", 3);
        if (restoredActivity != 0) {
            dismiss_challenge.setSelection(restoredActivity);
        }
        Boolean restoredImageSetting = prefs.getBoolean("Image", true);
        defaultIm.setChecked(restoredImageSetting);
        customIm.setChecked(!restoredImageSetting);

        save = (Button)view.findViewById(R.id.saveSnooze);

        save.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putInt("Length", snooze_length.getSelectedItemPosition());
                editor.putInt("Activity", dismiss_challenge.getSelectedItemPosition());
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