package com.allisondsr.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        EditText descriptionInput = findViewById(R.id.descriptionInput);
        EditText amountInput = findViewById(R.id.amountInput);
        EditText dateInput = findViewById(R.id.dateInput);
        EditText categoryInput = findViewById(R.id.categoryInput);

        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String description = descriptionInput.getText().toString();
            String amount = amountInput.getText().toString();
            String date = dateInput.getText().toString();
            String category = categoryInput.getText().toString();

            double amountValue = Double.parseDouble(amount);

            Expense expense = new Expense(
                    description,
                    amountValue,
                    date,
                    category
            );

            Intent intent = new Intent();
            intent.putExtra("description", description);
            intent.putExtra("amount", amountValue);
            intent.putExtra("date", date);
            intent.putExtra("category", category);

            setResult(RESULT_OK, intent);
            finish();

            Toast.makeText(
                    AddExpenseActivity.this,
                    description + " - $" + amount,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}