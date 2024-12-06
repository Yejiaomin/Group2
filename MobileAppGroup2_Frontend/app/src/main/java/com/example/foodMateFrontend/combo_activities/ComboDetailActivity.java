package com.example.foodMateFrontend.combo_activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodMateFrontend.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ComboDetailActivity extends AppCompatActivity {

    private LinearLayout comboLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_detail);

        comboLayout = findViewById(R.id.comboListLayout);
        String comboFilePath = getIntent().getStringExtra("combo_file_path");

        loadComboData(comboFilePath);
    }

    private void loadComboData(String comboFilePath) {
        if (comboFilePath == null || comboFilePath.isEmpty()) {
            Log.e("ComboDetailActivity", "Combo file path is missing or empty.");
            showEmptyMessage();
            return;
        }

        File comboFile = new File(comboFilePath);
        if (!comboFile.exists()) {
            Log.e("ComboDetailActivity", "Combo file not found at: " + comboFilePath);
            showEmptyMessage();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(comboFile))) {
            String line;
            comboLayout.removeAllViews();

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    createComboItem(line);
                }
            }
        } catch (IOException e) {
            Log.e("ComboDetailActivity", "Error reading combo file: " + e.getMessage(), e);
            showEmptyMessage();
        }
    }

    private void createComboItem(String comboLine) {
        // Extract combo name and details
        String[] comboParts = comboLine.split("-");
        String comboName = comboParts[0].trim(); // Combo name (e.g., "Combo1: Pickle, Coke")
        String comboDetails = comboParts[1].trim(); // Total price (e.g., "Total Price: $35.00")

        // Container for each combo
        LinearLayout comboContainer = new LinearLayout(this);
        comboContainer.setOrientation(LinearLayout.VERTICAL);
        comboContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
        comboContainer.setPadding(16, 16, 16, 16);
        comboContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Horizontal layout for title and dropdown arrow
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Combo title
        TextView comboTitle = new TextView(this);
        comboTitle.setText(comboName); // Set combo name
        comboTitle.setTextSize(16);
        comboTitle.setTextColor(Color.BLACK);
        comboTitle.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1 // Use weight for flexible spacing
        ));

        // Dropdown arrow
        ImageView dropdownArrow = new ImageView(this);
        dropdownArrow.setImageResource(R.drawable.ic_dropdown_arrow); // Replace with the correct arrow resource
        dropdownArrow.setLayoutParams(new LinearLayout.LayoutParams(
                40, 40 // Arrow size
        ));

        // Add title and arrow to the title layout
        titleLayout.addView(comboTitle);
        titleLayout.addView(dropdownArrow);

        // Add title layout to the combo container
        comboContainer.addView(titleLayout);

        // Combo details (e.g., items and total price)
        TextView comboDetailsView = new TextView(this);
        comboDetailsView.setText(comboDetails); // Display the total price
        comboDetailsView.setTextSize(14);
        comboDetailsView.setTextColor(Color.GRAY);
        comboDetailsView.setVisibility(View.GONE); // Initially hidden

        // Add details view to the container
        comboContainer.addView(comboDetailsView);

        // Toggle visibility on click
        titleLayout.setOnClickListener(v -> {
            if (comboDetailsView.getVisibility() == View.GONE) {
                comboDetailsView.setVisibility(View.VISIBLE);
                dropdownArrow.setRotation(180); // Arrow points down
            } else {
                comboDetailsView.setVisibility(View.GONE);
                dropdownArrow.setRotation(0); // Arrow points up
            }
        });

        // Add combo container to the main layout
        comboLayout.addView(comboContainer);
    }


    private void showEmptyMessage() {
        comboLayout.removeAllViews();
        TextView emptyMessage = new TextView(this);
        emptyMessage.setText("Waiting.");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(Color.GRAY);
        emptyMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
        comboLayout.addView(emptyMessage);
    }
}
