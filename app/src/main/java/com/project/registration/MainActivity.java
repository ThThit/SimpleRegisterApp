package com.project.registration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText txtName, txtEmail, txtPassword;
    private RadioGroup genderGroup;
    private Spinner country_spinner;
    private CheckBox termCheck;
    private ProgressBar status;
    FloatingActionButton btnImg;
    ImageView img;
    Uri selectedImage = null;

    ActivityResultLauncher<Intent> resultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI elements
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPw);
        genderGroup = findViewById(R.id.genderRadio);
        country_spinner = findViewById(R.id.spinCountry);
        termCheck = findViewById(R.id.termsStatus);
        btnImg = findViewById(R.id.btnImg);
        img = findViewById(R.id.imageView);
        status = findViewById(R.id.registerStatus);
        status.setVisibility(View.INVISIBLE);

        registerResult();
        btnImg.setOnClickListener(view -> pickImg());

    }

    private void pickImg(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    public void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try {
                            assert o.getData() != null;
                            selectedImage = o.getData().getData();
                            img.setImageURI(selectedImage);
                        } catch (Exception e){
                            Toast.makeText(MainActivity.this, "no selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    public void submitInfo(View view) {
        String name = txtName.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String country = country_spinner.getSelectedItem().toString().trim();
        boolean agreed = termCheck.isChecked();

        // check null case text
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImage == null) {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
            return;
        }

        // country spinner
        if (country.equals("Select Country")) {
            Toast.makeText(this, "Please select a valid country!", Toast.LENGTH_SHORT).show();
            return;
        }

        // radio
        int selectedId = genderGroup.getCheckedRadioButtonId();
        // null selection
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a gender!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate terms agreement
        if (!agreed) {
            Toast.makeText(this, "You must agree to the terms!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected gender text
        RadioButton selectedGender = findViewById(selectedId);
        String gender = selectedGender.getText().toString();

        status.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.incrementProgressBy(10);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setVisibility(View.GONE);

                        // Reset the form
                        txtName.setText("");
                        txtEmail.setText("");
                        txtPassword.setText("");
                        genderGroup.clearCheck();
                        country_spinner.setSelection(0);  // Reset to first item
                        termCheck.setChecked(false);
                        img.setImageDrawable(null);
                        selectedImage = null;

                        // Show a success message
                        Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        thread.start();

        // Print registration details to terminal (Logcat)
        Log.d("RegistrationInfo", "Name: " + name);
        Log.d("RegistrationInfo", "Email: " + email);
        Log.d("RegistrationInfo", "Gender: " + gender);
        Log.d("RegistrationInfo", "Country: " + country);
        Log.d("RegistrationInfo", "Image URI: " + selectedImage.toString());

    }

}