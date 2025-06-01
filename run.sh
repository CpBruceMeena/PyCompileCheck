#!/bin/bash

# Function to validate project path
validate_path() {
    if [ ! -d "$1" ]; then
        echo "Error: Project path does not exist"
        return 1
    fi
    return 0
}

# Ask for project path
echo "Welcome to PyCompileCheck!"
echo "------------------------"
read -p "Please enter the project path to analyze: " PROJECT_PATH

# Validate the path
while ! validate_path "$PROJECT_PATH"; do
    read -p "Please enter a valid project path: " PROJECT_PATH
done

echo "Analyzing project at: $PROJECT_PATH"

# Run the Python script
echo "Starting analysis..."
python main.py "$PROJECT_PATH" 