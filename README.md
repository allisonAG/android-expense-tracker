# Overview

Expense Tracker is an application designed to help users keep track of their expenses. It allows users to add expenses with a description, amount, date, and category.

Expenses are displayed on the main screen, along with the total amount spent and a breakdown of expenses by category. To add a new expense, users select Add Expense and enter the required information.

After selecting Save, the new expense is added to the list, and the total amounts are updated automatically. Users can also tap an expense and confirm its deletion if they want to remove it. If they decide not to save an expense, they can select Cancel to return to the main screen.

The application uses local file storage to preserve expenses on the device, even after the application is closed.

The purpose of this project was to strengthen my skills in Java and Android development while learning how to build a functional mobile application. Through this project, I practiced working with multiple Android activities, user input, event handling, data structures, local file storage, data validation, and user interface design.

[Software Demo Video](https://youtu.be/TJE2CXKpnu0)

# Development Environment

I developed this application using **Android Studio** with the Android SDK and a **Pixel 7 Android emulator** for testing.

The application was developed using **Java**. I used Java classes and data structures such as `ArrayList` and `HashMap` to manage expenses and calculate totals. Android `Intent` objects are used to pass expense information between activities.

The application uses Android's built-in APIs for local file storage. Expenses are saved to a local text file on the device and loaded when the application starts.

The main Android components used in the project include:

* `Activity` for creating the application's two screens.
* `Intent` for communicating between `MainActivity` and `AddExpenseActivity`.
* `ArrayList` for storing expenses while the application is running.
* `HashMap` for calculating totals by category.
* `AlertDialog` for confirming expense deletion.
* Android internal file storage for persistent data.
* XML layouts for designing the user interface.

# Useful Websites

* [Android Developers - Activities](https://developer.android.com/guide/components/activities/intro-activities?hl=es-419)
* [Android Developers - Intents and Intent Filters](https://developer.android.com/guide/components/intents-filters?hl=es-419)
* [Android Developers - App Resources](https://developer.android.com/guide/topics/resources/providing-resources?hl=es-419)
* [Android Developers - Data and File Storage](https://developer.android.com/training/data-storage?hl=es-419)
* [Oracle Java Documentation](https://docs.oracle.com/en/java/)

# Future Work

* Add an option to edit an existing expense instead of only deleting it.
* Improve the organization of expenses by sorting them by date.
* Add monthly or weekly expense summaries.
* Improve the local data storage system by using a database such as Room instead of a text file.





