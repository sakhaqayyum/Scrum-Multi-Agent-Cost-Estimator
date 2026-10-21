# Scrum Multi-Agent Cost Estimator
<p>
<img src="https://github.com/user-attachments/assets/1d96fa8e-1d57-40a8-8c39-36c8678a65f6" alt="Image 1" style="margin-right: 10px; margin-bottom: 10px;> 
 <img src="https://github.com/user-attachments/assets/42da7c51-e0aa-45bc-ac07-536f55d78e27" alt="Image 1" style="margin-right: 10px; margin-bottom: 10px;" > 
 <img src="https://github.com/user-attachments/assets/6519d4c3-45a0-4aae-abe4-fbba978e7b2e" alt="Image 1" style="margin-right: 10px; margin-bottom: 10px;> 
 <img src="https://github.com/user-attachments/assets/58bb7419-8ae7-46f3-8129-df5398d9627b" alt="Image 1" style="margin-right: 10px; margin-bottom: 10px;> 
 <img src="https://github.com/user-attachments/assets/f0f68827-31a6-4d90-871e-8bc885857505" alt="Image 1" style="margin-right: 10px; margin-bottom: 10px;> 

   
  </p>





A Java console application that reads a Software Requirements Specification (SRS), splits it into individual requirements, assigns each one to a specialised agent, and estimates the **hours, cost, and number of sprints** for the project using Scrum-style planning.

---

## Table of Contents

- [Features](#features)
- [How It Works](#how-it-works)
- [Agents](#agents)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Example](#example)
- [Writing Good SRS Input](#writing-good-srs-input)
- [Optional: MySQL Storage](#optional-mysql-storage)
- [Limitations](#limitations)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- Extracts individual requirements from plain SRS text
- Identifies the **verb** and **noun** of each requirement
- Distributes tasks to specialised agents (Development, Security/Test, Database, Analysis)
- Estimates **hours** per task and calculates **cost** from your hourly rate
- Groups tasks into **sprints** (default planning assumption: 5 tasks per sprint)
- Prints a full task breakdown and an estimation summary
- Runs without a database (`ConsoleNoDb`); MySQL saving is optional

---

## How It Works

```
SRS text  ->  Requirement Agent  ->  Task Distribution  ->  Estimation  ->  Sprint Planning  ->  Report
```

1. **Input**: you enter the project name, hourly rate, and paste the SRS text, ending with a line containing only `END`.
2. **Requirement extraction**: each line or sentence becomes one requirement.
3. **Analysis**: the verb and noun are detected for each requirement.
4. **Agent assignment**: the task goes to the best-matching agent.
5. **Estimation**: hours are assigned (higher for sensitive tasks such as authentication and payments), then `cost = hours x hourly rate`.
6. **Sprint planning**: tasks are divided into sprints of 5 tasks each.
7. **Output**: a per-task breakdown and a summary of total tasks, hours, cost, and sprints.

---

## Agents

| Agent | Responsibility |
|---|---|
| Development Agent | Feature development (booking, ordering, tracking, management screens) |
| Security/Test Agent | Authentication, security, and testing related tasks |
| Database Agent | Data storage and database related tasks |
| Analysis Agent | Default agent when no specific verb or keyword matches |

---

## Project Structure

> Adjust this section to match your actual folders and files.

```
scrum-cost-estimator/
├── src/
│   └── com/
│       └── scrum/
│           ├── ConsoleNoDb.java        # Main entry point (no database)
│           ├── RequirementAgent.java   # Extracts requirements, verbs and nouns
│           └── ...                     # Other agents / models / utilities
├── out/                                # Compiled .class files (ignored by Git)
├── .gitignore
└── README.md
```

---

## Requirements

- **Java JDK 11 or higher** (check with `java -version`)
- A terminal (PowerShell, CMD, Bash) or VS Code
- *(Optional)* MySQL, only if you want to save estimates to a database

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/scrum-cost-estimator.git
cd scrum-cost-estimator
```

### 2. Compile

**Windows (PowerShell):**

```powershell
mkdir out -Force
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)
```

**Linux / macOS:**

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
```

### 3. Run

```bash
java -cp out com.scrum.ConsoleNoDb
```

---

## Usage

When the program starts, it asks for three things:

1. **Project name**, for example `eHealthcare`
2. **Hourly development rate**, for example `40.00`
3. **SRS text**: paste your requirements, then type `END` on its own line

---

## Example

**Input**

```
=== Scrum Multi-Agent Cost Estimation System (no database) ===
Project name: eHealthcare
Hourly development rate (e.g. 40.00): 40
Paste the SRS text. Enter a line containing only END to finish:
The system shall authenticate users with OTP.
The system shall allow patients to book doctor appointments.
The system shall process online payments.
The system shall schedule video consultations through Google Meet.
The system shall allow patients to order medicine.
The system shall track medicine delivery by riders.
The system shall allow pharmacists to manage medicine stock.
The system shall allow administrators to manage doctors and users.
The system shall store data in Firebase.
END
```

**Output (excerpt)**

```
--- Extracted requirements and task distribution (eHealthcare) ---

1. The system shall authenticate users with OTP.
   Verb: authenticate | Noun: otp | Agent: Security/Test Agent
   Hours: 12.00 | Cost: 480.00 | Sprint: 1

2. The system shall allow patients to book doctor appointments.
   Verb: allow | Noun: patients | Agent: Development Agent
   Hours: 4.00 | Cost: 160.00 | Sprint: 1

...

9. The system shall store data in Firebase.
   Verb: store | Noun: data | Agent: Database Agent
   Hours: 4.00 | Cost: 160.00 | Sprint: 2

--- Estimation summary ---
Total tasks: 9
Total estimated hours: 52.00
Total estimated cost: 2080.00
Estimated sprints: 2 (planning assumption: 5 tasks/sprint)
(Database save skipped - MySQL not installed)
```

---

## Writing Good SRS Input

The estimator works best when **each feature is on its own line**, written as a short requirement.

**Recommended format**

```
The system shall <verb> <what> ...
```

**Good**

```
The system shall authenticate users with OTP.
The system shall process online payments.
```

**Not recommended** (a project description in paragraph form)

```
The app is a healthcare solution that provides OTP authentication, doctor
appointments and online payments...
```

A paragraph is treated as only a few sentences, so many features get merged into one task and the estimate will be too low. Split your features onto separate lines for an accurate result.

---

## Optional: MySQL Storage

The default `ConsoleNoDb` version does not need a database. If MySQL is not installed, you will see:

```
(Database save skipped - MySQL not installed)
```

To enable saving estimates, install MySQL, create a database, and configure the connection settings in the database-enabled entry class of this project.

---

## Limitations

- Verb detection matches against a fixed list of known verbs; unknown verbs fall back to the default **Analysis Agent**
- Each line or sentence counts as exactly one requirement
- Hour values are rule-based estimates, not a replacement for team planning or story points
- Sprint count assumes 5 tasks per sprint

---

## Roadmap

- [ ] Recognise more verbs (provides, connects, supports, ...)
- [ ] Split comma-separated features into separate tasks
- [ ] Configurable tasks-per-sprint and hour rules
- [ ] Export report to CSV / PDF
- [ ] Simple GUI or web interface
- [ ] Full MySQL integration with saved project history

---

## Contributing

Contributions are welcome.

1. Fork the repository
2. Create a branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Author

**Your Name**
GitHub: [@sakhaqayyum@gmail.com](sakhaqayyum@gmail.com)
