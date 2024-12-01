# Directories
SRC_DIR = src
BIN_DIR = bin
LIB_DIR = lib

# Classpath
CP = $(LIB_DIR)/*

# Source files
SOURCES = $(wildcard $(SRC_DIR)/*.java)

# Output files
CLASSES = $(SOURCES:$(SRC_DIR)/%.java=$(BIN_DIR)/%.class)

# Default target
.PHONY: all
all: build run

# Build the project
.PHONY: build
build:
ifeq ($(OS),Windows_NT)
	powershell -Command "if (!(Test-Path -Path 'bin')) { New-Item -ItemType Directory -Path 'bin' }"
else
	mkdir -p bin
endif
	javac -d $(BIN_DIR) -cp "$(CP)" $(SOURCES)

# Run the project
.PHONY: run
run:
ifeq ($(OS),Windows_NT)
	java -cp "$(BIN_DIR);$(CP)" App
else
	java -cp "$(BIN_DIR):$(CP)" App
endif

# Clean the build directory
.PHONY: clean
clean:
ifeq ($(OS),Windows_NT)
	powershell -Command "Get-ChildItem -Path 'bin/*.class' | Remove-Item -Force"
	powershell -Command "Get-ChildItem -Path 'data/ciphertext/*.des' | Remove-Item -Force"
else
	rm -f bin/*.class
	rm -f data/ciphertext/*.des
endif
