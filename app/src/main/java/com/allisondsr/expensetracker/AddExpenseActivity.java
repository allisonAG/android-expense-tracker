package com.allisondsr.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.DatePickerDialog;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Connect Java variables to the input fields in the layout.
        EditText descriptionInput = findViewById(R.id.descriptionInput);
        EditText amountInput = findViewById(R.id.amountInput);
        EditText dateInput = findViewById(R.id.dateInput);
        Spinner categoryInput = findViewById(R.id.categoryInput);

        // Define the available expense categories.
        String[] categories = {
                "Select a category",
                "Food",
                "Transportation",
                "Entertainment",
                "Shopping",
                "Bills",
                "Other"
        };

        // Create an adapter to display the categories in the Spinner.
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        // Define the layout used to display the dropdown options.
        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        // Connect the category list to the Spinner.
        categoryInput.setAdapter(categoryAdapter);

        // Open the date picker when the user taps the date field.
        dateInput.setOnClickListener(v -> {

            // Get the current date to use as the initial selection.
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a dialog that allows the user to select a date.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddExpenseActivity.this,

                    // Format the selected date and display it in the input field.
                    (view, selectedYear, selectedMonth, selectedDay) -> {

                        String selectedDate = String.format(
                                java.util.Locale.US,
                                "%02d/%02d/%04d",
                                selectedMonth + 1,
                                selectedDay,
                                selectedYear
                        );

                        dateInput.setText(selectedDate);
                    },
                    year,
                    month,
                    day
            );

            // Display the date picker dialog.
            datePickerDialog.show();
        });

        // Connect the Save and Cancel buttons to their Java variables.
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        // Close the screen without saving if the user taps Cancel.
        cancelButton.setOnClickListener(v -> {
            finish();
        });

        // Validate the form and return the expense information when Save is tapped.
        saveButton.setOnClickListener(v -> {

            // Retrieve the input values and remove extra whitespace.
            String description = descriptionInput.getText().toString().trim();
            String amount = amountInput.getText().toString().trim();
            String date = dateInput.getText().toString().trim();
            String category = categoryInput.getSelectedItem().toString();

            // Check that the description is not empty.
            if (description.isEmpty()) {
                descriptionInput.setError("Description is required");
                descriptionInput.requestFocus();
                return;
            }

            // Check that the amount is not empty.
            if (amount.isEmpty()) {
                amountInput.setError("Amount is required");
                amountInput.requestFocus();
                return;
            }

            // Check that a date has been selected.
            if (date.isEmpty()) {
                dateInput.setError("Date is required");
                dateInput.requestFocus();
                return;
            }

            // Make sure the user selected a category instead of the placeholder.
            if (categoryInput.getSelectedItemPosition() == 0) {
                Toast.makeText(
                        AddExpenseActivity.this,
                        "Please select a category",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            // Convert the amount from String to double.
            double amountValue;

            try {
                amountValue = Double.parseDouble(amount);
            } catch (NumberFormatException e) {

                // Show an error if the amount cannot be converted to a number.
                amountInput.setError("Enter a valid amount");
                amountInput.requestFocus();
                return;
            }

            // Prevent the user from saving zero or negative amounts.
            if (amountValue <= 0) {
                amountInput.setError("Amount must be greater than zero");
                amountInput.requestFocus();
                return;
            }

            // Create an Expense object with the validated information.
            Expense expense = new Expense(
                    description,
                    amountValue,
                    date,
                    category
            );

            // Package the expense information so MainActivity can receive it.
            Intent intent = new Intent();
            intent.putExtra("description", description);
            intent.putExtra("amount", amountValue);
            intent.putExtra("date", date);
            intent.putExtra("category", category);

            // Return the result to MainActivity and close this screen.
            setResult(RESULT_OK, intent);
            finish();

            // Confirm that the expense was submitted.
            Toast.makeText(
                    AddExpenseActivity.this,
                    description + " - $" + amount,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}
