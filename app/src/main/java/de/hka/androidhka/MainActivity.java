package de.hka.androidhka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String logValue = "onCreate";
        Log.d("MainActivity", logValue);

        List<String> stringList = new ArrayList<>();
        stringList.add("Super Test-String");
        stringList.add("Noch ein String!");
        stringList.add("Da schau her!");

        Button btnClick = this.findViewById(R.id.btnClick);
        Button btnNext = this.findViewById(R.id.btnNext);
        TextView txtClick = this.findViewById(R.id.txtOutput);

        btnClick.setOnClickListener(view -> {
            Random random = new Random();
            int nextIndex = random.nextInt(stringList.size());

            txtClick.setText(stringList.get(nextIndex));
        });

        btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra("MESSAGE", txtClick.getText().toString());

            this.startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("MainActivity", "onResume");
    }
}