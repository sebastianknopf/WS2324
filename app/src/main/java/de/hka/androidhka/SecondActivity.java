package de.hka.androidhka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = this.getIntent();
        if (intent != null) {
            TextView txtMessage = this.findViewById(R.id.txtMessage);
            String message = intent.getStringExtra("MESSAGE");

            txtMessage.setText(message);
        }
    }
}