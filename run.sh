#!/bin/bash

# Validate the project path if provided
if [ $# -gt 0 ] && [ ! -d "$1" ]; then
    echo "Error: Project path does not exist: $1"
    exit 1
fi

echo "Analyzing project at: ${1:-.}"
echo "Starting analysis..."
python main.py "$@" 