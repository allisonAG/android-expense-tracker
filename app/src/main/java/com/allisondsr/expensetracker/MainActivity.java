package com.allisondsr.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Expense> expenses = new ArrayList<>();
    private LinearLayout expensesContainer;
    private TextView totalText;
    private TextView categoryTotalsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addExpenseButton = findViewById(R.id.addExpenseButton);
        expensesContainer = findViewById(R.id.expensesContainer);
        totalText = findViewById(R.id.totalText);
        categoryTotalsText = findViewById(R.id.categoryTotalsText);


        loadExpenses();
        displayExpenses();
        updateTotal();
        updateCategoryTotals();

        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            String description = data.getStringExtra("description");
            double amount = data.getDoubleExtra("amount", 0);
            String date = data.getStringExtra("date");
            String category = data.getStringExtra("category");

            Expense expense = new Expense(
                    description,
                    amount,
                    date,
                    category
            );

            expenses.add(expense);
            saveExpenses();
            displayExpenses();
            updateTotal();
            updateCategoryTotals();
        }
    }

    private void displayExpenses() {
        expensesContainer.removeAllViews();

        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);

            TextView expenseText = new TextView(this);

            expenseText.setText(
                    expense.getDescription()
                            + " - $"
                            + String.format("%.2f", expense.getAmount())
                            + "\nDate: "
                            + expense.getDate()
                            + "\nCategory: "
                            + expense.getCategory()
            );

            expenseText.setTextSize(16);
            expenseText.setPadding(0, 16, 0, 16);

            expenseText.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Expense")
                        .setMessage("Are you sure you want to delete this expense?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            expenses.remove(expense);

                            saveExpenses();
                            displayExpenses();
                            updateTotal();
                            updateCategoryTotals();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });


            expensesContainer.addView(expenseText);
        }
    }

    private void updateTotal() {
        double total = 0;

        for (Expense expense : expenses) {
            total += expense.getAmount();
        }

        totalText.setText(String.format("Total: $%.2f", total));
    }

    private void updateCategoryTotals() {
        HashMap<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double amount = expense.getAmount();

            if (categoryTotals.containsKey(category)) {
                double currentTotal = categoryTotals.get(category);
                categoryTotals.put(category, currentTotal + amount);
            } else {
                categoryTotals.put(category, amount);
            }
        }

        StringBuilder text = new StringBuilder("By Category\n\n");

        for (String category : categoryTotals.keySet()) {
            double total = categoryTotals.get(category);

            text.append(category)
                    .append(": $")
                    .append(String.format("%.2f", total))
                    .append("\n");
        }

        categoryTotalsText.setText(text.toString());
    }

    private void saveExpenses() {
        try {
            FileOutputStream fileOutput = openFileOutput("expenses.txt", MODE_PRIVATE);

            for (Expense expense : expenses) {
                String line = expense.getDescription() + "|" +
                        expense.getAmount() + "|" +
                        expense.getDate() + "|" +
                        expense.getCategory() + "\n";

                fileOutput.write(line.getBytes());
            }

            fileOutput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        try {
            FileInputStream fileInput = openFileInput("expenses.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fileInput)
            );

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                String description = parts[0];
                double amount = Double.parseDouble(parts[1]);
                String date = parts[2];
                String category = parts[3];

                Expense expense = new Expense(
                        description,
                        amount,
                        date,
                        category
                );

                expenses.add(expense);
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}