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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Expense> expenses = new ArrayList<>();
    private TextView expensesText;
    private TextView totalText;
    private TextView categoryTotalsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addExpenseButton = findViewById(R.id.addExpenseButton);
        expensesText = findViewById(R.id.expensesText);
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
        StringBuilder text = new StringBuilder();

        for (Expense expense : expenses) {
            text.append(expense.getDescription())
                    .append(" - $")
                    .append(expense.getAmount())
                    .append("\n")
                    .append("Date: ")
                    .append(expense.getDate())
                    .append("\n")
                    .append("Category: ")
                    .append(expense.getCategory())
                    .append("\n\n");
        }

        expensesText.setText(text.toString());
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