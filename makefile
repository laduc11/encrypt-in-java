# Define directories
SRC_DIR = src
LIB_DIR = lib
BIN_DIR = bin

# Find all .java files in the src directory
SRC_FILES := $(wildcard $(SRC_DIR)/*.java)

# Output directory for compiled .class files
CLASS_FILES := $(patsubst $(SRC_DIR)/%.java, $(BIN_DIR)/%.class, $(SRC_FILES))

# Classpath (include lib directory if there are jars)
CLASSPATH := $(LIB_DIR)/*:$(BIN_DIR)

# Main class to run
MAIN_CLASS = App

# Default target
.PHONY: all
all: build run

# Build target
build: $(CLASS_FILES)

# Create the bin directory if it doesn't exist
$(BIN_DIR):
	mkdir -p $(BIN_DIR)

# Compile all .java files
$(BIN_DIR)/%.class: $(SRC_DIR)/%.java | $(BIN_DIR)
	javac -d $(BIN_DIR) -cp $(CLASSPATH) $<

# Run the program
.PHONY: run
run:
	java -cp $(CLASSPATH) $(MAIN_CLASS)

# Clean the bin directory
.PHONY: clean
clean:
	del $(BIN_DIR)
