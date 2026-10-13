package com.allisondsr.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Stores all expenses currently loaded in the application.
    private ArrayList<Expense> expenses = new ArrayList<>();

    // References to the UI elements that display expenses and totals.
    private LinearLayout expensesContainer;
    private TextView totalText;
    private TextView categoryTotalsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect Java variables to the corresponding layout elements.
        Button addExpenseButton = findViewById(R.id.addExpenseButton);
        expensesContainer = findViewById(R.id.expensesContainer);
        totalText = findViewById(R.id.totalText);
        categoryTotalsText = findViewById(R.id.categoryTotalsText);

        // Load saved expenses and update the information displayed on the screen.
        loadExpenses();
        displayExpenses();
        updateTotal();
        updateCategoryTotals();

        // Open the form when the user clicks the Add Expense button.
        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check whether the Add Expense screen returned a successfully saved expense.
        if (requestCode == 1 && resultCode == RESULT_OK) {

            // Retrieve the expense information sent by AddExpenseActivity.
            String description = data.getStringExtra("description");
            double amount = data.getDoubleExtra("amount", 0);
            String date = data.getStringExtra("date");
            String category = data.getStringExtra("category");

            // Create a new Expense object using the received information.
            Expense expense = new Expense(
                    description,
                    amount,
                    date,
                    category
            );

            // Add the expense, save the updated list, and refresh the screen.
            expenses.add(expense);
            saveExpenses();
            displayExpenses();
            updateTotal();
            updateCategoryTotals();
        }
    }

    // Creates and displays a card for each expense in the list.
    private void displayExpenses() {

        // Remove the existing cards before displaying the updated list.
        expensesContainer.removeAllViews();

        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);

            // Create the main vertical layout for the expense card.
            LinearLayout expenseCard = new LinearLayout(this);
            expenseCard.setOrientation(LinearLayout.VERTICAL);
            expenseCard.setPadding(20, 18, 20, 18);
            expenseCard.setBackgroundResource(R.drawable.expense_background);

            // Display the expense description.
            TextView descriptionText = new TextView(this);
            descriptionText.setText(expense.getDescription());
            descriptionText.setTextSize(18);
            descriptionText.setTextColor(Color.parseColor("#302C2D"));
            descriptionText.setTypeface(null, android.graphics.Typeface.BOLD);

            // Display the expense amount using two decimal places.
            TextView amountText = new TextView(this);
            amountText.setText(
                    String.format("$%.2f", expense.getAmount())
            );
            amountText.setTextSize(22);
            amountText.setTextColor(Color.parseColor("#A56B78"));
            amountText.setTypeface(null, android.graphics.Typeface.BOLD);

            // Add spacing around the amount.
            LinearLayout.LayoutParams amountParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            amountParams.setMargins(0, 6, 0, 10);
            amountText.setLayoutParams(amountParams);

            // Display the expense date.
            TextView dateText = new TextView(this);
            dateText.setText("Date: " + expense.getDate());
            dateText.setTextSize(14);
            dateText.setTextColor(Color.parseColor("#82777A"));

            // Display the expense category.
            TextView categoryText = new TextView(this);
            categoryText.setText("Category: " + expense.getCategory());
            categoryText.setTextSize(14);
            categoryText.setTextColor(Color.parseColor("#82777A"));

            // Add all expense information to the card.
            expenseCard.addView(descriptionText);
            expenseCard.addView(amountText);
            expenseCard.addView(dateText);
            expenseCard.addView(categoryText);

            // Set the spacing between expense cards.
            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, 0, 0, 12);
            expenseCard.setLayoutParams(cardParams);

            // Show a confirmation dialog when the user taps an expense card.
            expenseCard.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Expense")
                        .setMessage("Are you sure you want to delete this expense?")

                        // Delete the expense only if the user confirms.
                        .setPositiveButton("Delete", (dialog, which) -> {
                            expenses.remove(expense);

                            // Save the changes and refresh all displayed information.
                            saveExpenses();
                            displayExpenses();
                            updateTotal();
                            updateCategoryTotals();
                        })

                        // Close the dialog without deleting the expense.
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            // Add the completed card to the screen.
            expensesContainer.addView(expenseCard);
        }
    }

    // Calculates and displays the total amount of all expenses.
    private void updateTotal() {
        double total = 0;

        // Add the amount of every expense to the total.
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }

        // Display the total with two decimal places.
        totalText.setText(String.format("Total: $%.2f", total));
    }

    // Calculates and displays the total amount spent in each category.
    private void updateCategoryTotals() {

        // Store each category and its accumulated expense amount.
        HashMap<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double amount = expense.getAmount();

            // Add the amount to the existing category total, or create a new entry.
            if (categoryTotals.containsKey(category)) {
                double currentTotal = categoryTotals.get(category);
                categoryTotals.put(category, currentTotal + amount);
            } else {
                categoryTotals.put(category, amount);
            }
        }

        // Build the text that will be displayed in the category totals section.
        StringBuilder text = new StringBuilder();

        for (String category : categoryTotals.keySet()) {
            double total = categoryTotals.get(category);

            text.append(category)
                    .append(": $")
                    .append(String.format("%.2f", total))
                    .append("\n");
        }

        // Update the category totals displayed on the screen.
        categoryTotalsText.setText(text.toString());
    }

    // Saves all expenses to an internal file on the device.
    private void saveExpenses() {
        try {
            // Open the file in private mode, replacing its previous contents.
            FileOutputStream fileOutput = openFileOutput("expenses.txt", MODE_PRIVATE);

            // Write each expense as a separate line.
            for (Expense expense : expenses) {
                String line = expense.getDescription() + "|" +
                        expense.getAmount() + "|" +
                        expense.getDate() + "|" +
                        expense.getCategory() + "\n";

                fileOutput.write(line.getBytes());
            }

            fileOutput.close();

        } catch (IOException e) {
            // Print the error details if saving fails.
            e.printStackTrace();
        }
    }

    // Loads previously saved expenses from the internal file.
    private void loadExpenses() {
        try {
            FileInputStream fileInput = openFileInput("expenses.txt");

            // Read the file one line at a time.
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fileInput)
            );

            String line;

            while ((line = reader.readLine()) != null) {

                // Split each line into its four expense fields.
                String[] parts = line.split("\\|");

                String description = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String date = parts[2];
                String category = parts[3];

                // Recreate the Expense object from the saved information.
                Expense expense = new Expense(
                        description,
                        amount,
                        date,
                        category
                );

                // Add the restored expense to the in-memory list.
                expenses.add(expense);
            }

            reader.close();

        } catch (IOException e) {
            // Print the error details if the file cannot be opened or read.
            e.printStackTrace();
        }
    }
}

