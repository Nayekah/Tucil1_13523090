JAVAC = javac
JAR = jar
BIN_DIR = bin
SRC_DIR = src
MAIN_CLASS = tucil1.src.Main
JAR_FILE = $(BIN_DIR)/tucil1.jar
MANIFEST_FILE = manifest.txt

SOURCES = $(shell find $(SRC_DIR) -name "*.java" ! -name "PuzzleSolverGUI.java")

.PHONY: all run clean

all: $(JAR_FILE)

$(JAR_FILE): $(SOURCES)
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(SOURCES)
	$(JAR) cfm $(JAR_FILE) $(MANIFEST_FILE) -C $(BIN_DIR) .

run: all
	java -jar $(JAR_FILE)

clean:
	rm -rf $(BIN_DIR)/*