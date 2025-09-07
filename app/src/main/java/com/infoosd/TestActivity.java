package com.infoosd;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create a simple TextView programmatically
        TextView textView = new TextView(this);
        textView.setText("Test Activity - If you see this, basic activity works!");
        textView.setTextSize(18);
        textView.setPadding(32, 32, 32, 32);
        
        setContentView(textView);
    }
}

