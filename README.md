# Tucil1_13523090 - IQ Puzzle Solver

![CLI Demo](https://i.pinimg.com/originals/1c/ec/60/1cec60b076ed3e42a0a253548370a353.gif)


📌 **Note:** This App can run in both **Command Line Interface (CLI)** and **Graphic User Interface (GUI)** ₍^. .^₎⟆

---
## 💻 Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white) ![JavaFX](https://img.shields.io/badge/JavaFX-%23007396.svg?style=for-the-badge&logo=java&logoColor=white)

---
## 🔍 What Inside This Project?
![Puzzle](https://m.media-amazon.com/images/I/61ALalScvjL.jpg)

**IQ Puzzler Pro** is a board game produced by the Smart Games company. The objective of the game is for the player to fill the entire board with the available pieces (puzzle blocks).
### Game Components

#### 1. Board
The board serves as the playing area where all the puzzle blocks must be placed without any gaps or overlaps.

#### 2. Blocks / Pieces
Each puzzle block has a unique shape and must be arranged to completely fill the board. Players can rotate or reflect the blocks as needed.

### Game Rules & Objective
The game begins with an empty board. Players must strategically place each puzzle block, ensuring that:
- No blocks overlap (except in 3D challenges).
- All blocks fit perfectly within the board.
- The board is fully covered when the puzzle is completed.

### Project Goal
This project aims to find a valid solution to the IQ Puzzler Pro game using the **Brute Force algorithm**. If no solution exists, the program will indicate that the puzzle cannot be solved.


---

## 📦 Installation & Setup

### Requirements
- Git
- Java 21 or later
- Javafx
- Gradle 8.5 or later
- Any IDE (Vscode, Vim, Neovim, Sublime Text, etc)

### Running the Application

You can run the application in two modes: **Command-Line Interface (CLI)** or **Graphical User Interface (GUI)**.

#### **1. Windows**
##### **- Command Line Interface (CLI)**
###### JAR
1. Open a terminal
2. Clone the repository
      ```bash
   git clone https://github.com/Nayekah/Tucil1_13523090.git
   ```
3. Make Tucil1_13523090 as root directory
4. Run the following command to start the application in CLI mode:
   ```bash
   cd tucil1
   java -jar bin/tucil1.jar
   ```

###### Makefile
1. Open a terminal
2. Clone the repository
      ```bash
   git clone https://github.com/Nayekah/Tucil1_13523090.git
   ```
3. Make Tucil1_13523090 as root directory
4. Run the following command to start the application in CLI mode:
   ```bash
   cd tucil1
   make run
   ```

##### **- Graphical User Interface (GUI)**
###### Gradle
1. Open a terminal
2. Clone the repository
      ```bash
   git clone https://github.com/Nayekah/Tucil1_13523090.git
   ```
3. Make Tucil1_13523090 as root directory
4. Run the following command to start the application in CLI mode:
   ```bash
   cd tucil1
   gradlew.bat run
   ```
---

#### **2. Linux**
##### **- Command Line Interface (CLI)**
###### JAR
1. Open a terminal
2. Clone the repository
      ```bash
   git clone https://github.com/Nayekah/Tucil1_13523090.git
   ```
3. Make Tucil1_13523090 as root directory
4. Run the following command to start the application in CLI mode:
   ```bash
   cd tucil1
   java -jar bin/tucil1.jar
   ```

###### Makefile
1. Open a terminal
2. Clone the repository
      ```bash
   git clone https://github.com/Nayekah/Tucil1_13523090.git
   ```
3. Make Tucil1_13523090 as root directory
4. Run the following command to start the application in CLI mode:
   ```bash
   cd tucil1
   make run
   ```

##### **- Graphical User Interface (GUI)**
###### Gradle
1. Open a terminal
2. Clone the repository
      ```bash
   git clone https://github.com/Nayekah/Tucil1_13523090.git
   ```
3. Make Tucil1_13523090 as root directory
4. Run the following command to start the application in CLI mode:
   ```bash
   cd tucil1
   ./gradlew run
   ```
---

<div align="center">
This page is intentionally left blank
</div>

---

## ✨ How to Use

### Command Line Interface (CLI)
![cligif](https://raw.githubusercontent.com/Nayekah/Tucil1_13523090/main/tucil1/etc/cli.gif)

<br/>
<br/>
<br/>
<br/>

### Graphic User Interface (GUI)

#### Manual Input
![gmanual](https://raw.githubusercontent.com/Nayekah/Tucil1_13523090/main/tucil1/etc/guimanual.gif)

<br/>
<br/>

#### File Input
![gload](https://raw.githubusercontent.com/Nayekah/Tucil1_13523090/main/tucil1/etc/guiload.gif)

---


## 📝 Notes & Limitations
- The program may took several time to search for a solution
- The input case is well-handled (but may have some small bug)
- If you want to try it on CLI, make sure that the input file is in  .../Tucil1_13523090/tucil1/test/input/ (if you use GUI, the input file locations doesn't matter). Make sure that the file is in .txt
- You can save the solution to .txt or .png file and will be saved in .../Tucil1_13523090/tucil1/test/output/
- To generate input file, please refer to .../Tucil1_13523090/tucil1/test/input/format.txt
  
---
## 📱 Repository Structure
```
Tucil1_13523090/
├── .vscode/
│ └── settings.json
├── tucil1/
│ ├── bin/
│ │ ├── tucil1/src/
│ │ │ ├── Board$PieceOperation.class
│ │ │ ├── Board.class
│ │ │ ├── InputReader.class
│ │ │ ├── Main.class
│ │ │ ├── Pair.class
│ │ │ ├── Piece.class
│ │ │ └── Solve.class
│ │ ├── gradle-wrapper.jar
│ │ └── tucil1.jar
│ ├── doc/
│ │ └── Tucil1_K2_13523090_Nayaka Ghana Subrata.pdf
│ ├── etc/
│ │ ├── cli.gif
│ │ ├── guiload.gif
│ │ └── guimanual.gif
│ ├── gradle/
│ │ └── wrapper/
│ │ ├── gradle-wrapper.jar
│ │ └── gradle-wrapper.properties
│ ├── src/
│ │ ├── Board.java
│ │ ├── InputReader.java
│ │ ├── Main.java
│ │ ├── Pair.java
│ │ ├── Piece.java
│ │ ├── PuzzleSolverGUI.java
Tugas Kecil 1 - 13523090
IF - 2211 Strategi Algoritma 8
│ │ └── Solve.java
│ ├── test/
│ │ ├── input/
│ │ │ ├── 1.txt
│ │ │ ├── 2.txt
│ │ │ ├── 3.txt
│ │ │ ├── 4.txt
│ │ │ ├── 5.txt
│ │ │ ├── 6.txt
│ │ │ ├── 7.txt
│ │ │ └── 8.txt
│ │ └── solutions/
│ │ ├── solution1.png
│ │ ├── solution1.txt
│ │ ├── solution2.png
│ │ ├──solution 2.txt
│ │ ├── solution3.png
│ │ ├── solution 3.txt
│ │ ├── solution4.png
│ │ ├── solution4.txt
│ │ ├── solution5.png
│ │ ├── solution5.txt
│ │ ├── solution6.png
│ │ ├── solution6.txt
│ │ ├── solution7.png
│ │ ├── solution 7.txt
│ │ ├── solution8.png
│ │ └── solution8.txt
│ ├── Makefile
│ ├── build.gradle
│ ├── gradlew
│ ├── gradlew.bat
│ ├── manifest.txt
│ └── settings.gradle
└── README.md
```
---
## 📃 Miscellaneous
| No | Points | Yes | No |
| --- | --- | --- | --- |
| 1 | The program compiles successfully without errors | ✔️ | |
| 2 | Program successfully executed | ✔️ | |
| 3 | The solution provided by the program is correct and complies with the rules of the game | ✔️ | |
| 4 | The program can read the input .txt file and save the solution in the .txt file | ✔️ | |
| 5 | The program has a Graphical User Interface (GUI) | ✔️ | |
| 6 | The program can save the solution as an image file | ✔️ | |
| 7 | The program can solve the case of custom configuration | | ✔️ |
| 8 | The program can solve the case of Pyramid configuration (3D) | | ✔️ |
| 9 | Program created by myself | ✔️ | |

<br/>
<br/>
<br/>
<br/>

<div align="center">
Nayaka Ghana Subrata • © 2025 • 13523090
</div>
