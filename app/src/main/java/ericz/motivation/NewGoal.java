package ericz.motivation;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class NewGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);

        final SeekBar mileBar = findViewById(R.id.mileGoalBar);
        final SeekBar moneyBar = findViewById(R.id.moneyBar);

        final TextInputEditText mileText = findViewById(R.id.mileText);
        final TextInputEditText moneyText = findViewById(R.id.moneyText);

        //Getting the data from the bar with a listener
        mileBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int mileProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mileProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mileText.setText(String.valueOf(mileProgress));
                mileText.setSelection(mileText.getText().length());

            }
        });


        //Getting the data from the bar with a listener
        moneyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int moneyProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moneyProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(moneyProgress < 4)
                {
                    moneyText.setText(String.valueOf(4));
                    moneyText.setSelection(moneyText.getText().length());

                }
                else {
                    moneyText.setText(String.valueOf(moneyProgress));
                    moneyText.setSelection(moneyText.getText().length());

                }
            }
        });




        //TEXT LISTENERS
        mileText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    //Update Seekbar value after entering a number
                    mileBar.setProgress(Integer.parseInt(editable.toString()));

                } catch(Exception ex) {

                }

            }
        });

        moneyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    //Update Seekbar value after entering a number
                    if(Integer.parseInt(editable.toString()) < 4)
                    {
                        moneyText.setError("Value is below $4. ");
                    }

                    moneyBar.setProgress(Integer.parseInt(editable.toString()));


                } catch(Exception ex) {

                }

            }
        });

        Button cancel = findViewById(R.id.button2);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finishActivity(1);
            }
        });

    }

}
