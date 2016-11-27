package net.itgoo.android_uidatepicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.itgoo.uidatepicker.UIDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.text);
        final UIDatePicker datePicker = new UIDatePicker(MainActivity.this);
        datePicker.setDatePickerMode(UIDatePicker.DATE_PICKER_MODE_DATE);
        datePicker.setIsRememberLastSelectDate(true);
        datePicker.setDatePickerListener(new UIDatePicker.UIDatePickerListener() {

            @Override
            public void onDatePickerComplete(UIDatePicker dataPicker, Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                textView.setText(sdf.format(date));
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getWindow().getDecorView());
            }
        });
    }
}
