# Personal Finance Manager (Java + Swing + SQLite)

A complete desktop application for tracking personal income, expenses,
budgets and spending reports — built with core Java, Swing (GUI), and
SQLite (local database, no server required).

## Features

- **User accounts** — register/login, passwords stored as salted SHA-256 hashes (never in plain text)
- **Transactions** — add, edit, delete income/expense entries with category, amount, date, description
- **Categories** — built-in defaults (Salary, Rent, Food, etc.) plus your own custom categories
- **Budgets** — set a monthly spending limit per category; see "OK / Near limit / OVER BUDGET" status
- **Overview dashboard** — current month's total income, total expense, net balance
- **Reports** — category-wise expense breakdown for any month, shown as a bar chart
- **Persistent storage** — all data saved locally in a SQLite database file (`data/finance_manager.db`), so your data is still there next time you open the app

## Project Structure

```
PersonalFinanceManager/
├── README.md
├── PersonalFinanceManager.jar     <- ready-to-run application (recommended)
├── libs/
│   └── sqlite-jdbc.jar            <- SQLite JDBC driver (needed only if compiling from source)
└── src/com/financemanager/
    ├── Main.java                  <- application entry point
    ├── db/
    │   └── DatabaseManager.java   <- creates/opens the SQLite database and tables
    ├── model/
    │   ├── User.java
    │   ├── Category.java
    │   ├── Transaction.java
    │   └── Budget.java
    ├── dao/                       <- database access layer (SQL lives here)
    │   ├── UserDAO.java
    │   ├── CategoryDAO.java
    │   ├── TransactionDAO.java
    │   └── BudgetDAO.java
    ├── util/
    │   └── PasswordUtil.java      <- salted password hashing
    └── ui/                        <- all Swing screens
        ├── LoginFrame.java
        ├── DashboardFrame.java
        ├── OverviewPanel.java
        ├── TransactionsPanel.java
        ├── AddEditTransactionDialog.java
        ├── CategoriesPanel.java
        ├── BudgetsPanel.java
        └── ReportsPanel.java
```

---

## OPTION A — Just run it (fastest, recommended)

You only need a **Java Runtime (JRE) 17 or newer** installed on your computer.

### Step 1: Check Java is installed
Open a terminal / command prompt and run:
```
java -version
```
If you see a version number (17 or higher), you're good. If not, install a
JDK first — see "Installing Java" below.

### Step 2: Run the application
From the folder containing `PersonalFinanceManager.jar`, run:
```
java -jar PersonalFinanceManager.jar
```
A login window will appear. Click **"Create Account"**, pick a username and
password, then log in. A `data/` folder with your SQLite database will be
created automatically in the same directory the first time you run it — you
don't need to set up any database yourself.

That's it — the whole app, GUI and database driver, is bundled inside that
one `.jar` file.

---

## OPTION B — Compile and run from source (if you want to edit the code)

This is the path to take for a college project submission, since you'll
want to show you can build/compile it yourself and possibly modify it.

### Step 1: Install a JDK (Java Development Kit — not just the JRE)
- **Windows/Mac/Linux:** Download and install **JDK 17 or newer** from
  [https://adoptium.net](https://adoptium.net) (free, "Eclipse Temurin" builds).
- Verify installation:
  ```
  javac -version
  ```
  This should print something like `javac 17.0.x` or higher.

### Step 2: Get the project folder
Unzip the project so you have this structure on your computer:
```
PersonalFinanceManager/
├── libs/sqlite-jdbc.jar
└── src/com/financemanager/...
```

### Step 3: Compile

**On Windows (Command Prompt), from inside the `PersonalFinanceManager` folder:**
```
mkdir bin
javac -cp libs\sqlite-jdbc.jar -d bin (dir /s /b src\*.java)
```
If the command above gives an error on older Windows versions, use this
two-step alternative instead:
```
mkdir bin
dir /s /b src\*.java > sources.txt
javac -cp libs\sqlite-jdbc.jar -d bin @sources.txt
```

**On macOS/Linux (Terminal), from inside the `PersonalFinanceManager` folder:**
```
mkdir -p bin
javac -cp libs/sqlite-jdbc.jar -d bin $(find src -name "*.java")
```

Either way, this compiles every `.java` file into `.class` files inside the
new `bin/` folder. No output/errors means it worked.

### Step 4: Run

**Windows:**
```
java -cp "bin;libs\sqlite-jdbc.jar" com.financemanager.Main
```

**macOS/Linux:**
```
java -cp "bin:libs/sqlite-jdbc.jar" com.financemanager.Main
```

(Note: Windows uses `;` to separate classpath entries, macOS/Linux use `:`.)

---

## OPTION C — Import into an IDE (IntelliJ IDEA / Eclipse)

This is the easiest way to browse, edit, and run the code with a debugger,
and is a good option if your college expects an IDE-based demo.

### IntelliJ IDEA
1. Open IntelliJ → **File → Open** → select the `PersonalFinanceManager` folder.
2. Right-click the `src` folder → **Mark Directory as → Sources Root**.
3. Go to **File → Project Structure → Libraries → + → Java** → select `libs/sqlite-jdbc.jar` → Apply.
4. Open `src/com/financemanager/Main.java`, click the green ▶ Run arrow next to `public static void main`.

### Eclipse
1. **File → New → Java Project** → name it `PersonalFinanceManager` → uncheck "Use default location" if needed and point it at the unzipped folder, OR create a new project and copy the `src` folder in.
2. Right-click the project → **Build Path → Configure Build Path → Libraries → Add External JARs** → select `libs/sqlite-jdbc.jar`.
3. Right-click `Main.java` → **Run As → Java Application**.

---

## Installing Java (if you don't have it yet)

1. Go to [https://adoptium.net](https://adoptium.net)
2. Download the **JDK** (not just JRE) for your operating system — pick version 17 or 21 (LTS releases).
3. Run the installer, keeping default options.
4. Restart your terminal/command prompt, then confirm with `java -version` and `javac -version`.

---

## How to Use the App (Walkthrough)

1. **First launch:** click "Create Account", choose a username/password, then log in.
2. **Overview tab:** shows this month's income, expenses, and balance (starts at $0 until you add transactions).
3. **Transactions tab:** click "Add Transaction" → choose Income or Expense, pick a category, enter amount/date/description → Save. Edit or delete any row using the buttons above the table.
4. **Categories tab:** add your own categories (e.g., "Gym Membership", "Side Hustle Income") if the defaults don't cover something you need.
5. **Budgets tab:** type a month (e.g., `2026-09`), pick an expense category, set a limit, click "Set Budget". The table shows how much you've spent so far and whether you're near or over the limit.
6. **Reports tab:** type a month and click "Generate Report" to see a bar chart of your expenses broken down by category for that month.

## Troubleshooting

| Problem | Likely Cause / Fix |
|---|---|
| `java: command not found` | Java isn't installed or not on your PATH — reinstall from adoptium.net and restart your terminal |
| `Error: Unable to access jarfile` | You're not in the same folder as `PersonalFinanceManager.jar` — `cd` into that folder first |
| Compile errors mentioning `org.sqlite` | The `-cp libs/sqlite-jdbc.jar` part of the compile command is missing or the path is wrong |
| App window doesn't show any data after restarting | Make sure you're running the app from the *same* folder each time — the `data/finance_manager.db` file is created relative to your current directory |
| "That username is already taken" | Someone (maybe you, previously) already registered that username — pick another, or delete `data/finance_manager.db` to start fresh (**this erases all data**) |

