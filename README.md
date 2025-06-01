# PyCompileCheck

A Python-based static analysis tool for Django projects that helps track code changes and maintain code quality.

## Features

- **Change Detection**: Automatically detects and tracks changes in Python files
- **Smart TODO Comments**: Adds detailed TODO comments to modified files, explaining what changed
- **Import Analysis**: Tracks changes in import statements
- **Metadata Management**: Maintains a structured metadata store for project analysis
- **File Tracking**: Monitors file size, modification times, and content changes

## Installation

1. Clone the repository:
```bash
git clone https://github.com/CpBruceMeena/PyCompileCheck.git
cd PyCompileCheck
```

2. Make the run script executable:
```bash
chmod +x run.sh
```

## Usage

Run the analyzer on your project:
```bash
./run.sh
```

When prompted, enter the path to your Python project. The tool will:
1. Analyze all Python files in the project
2. Detect any changes since the last analysis
3. Add TODO comments to modified files
4. Store metadata about the project structure

## How It Works

1. **Project Analysis**:
   - Scans all Python files in the project directory
   - Calculates file hashes for change detection
   - Tracks file metadata (size, modification time)

2. **Change Detection**:
   - Compares current state with previous analysis
   - Detects content modifications
   - Tracks import statement changes
   - Monitors file size changes

3. **TODO Comments**:
   - Adds detailed TODO comments to modified files
   - Places comments after shebang/encoding declarations
   - Includes specific information about what changed

4. **Metadata Storage**:
   - Stores analysis results in `.pycompilecheck` directory
   - Maintains history of file changes
   - Tracks project structure

## Example

When changes are detected, the tool adds comments like:
```python
# TODO: PyCompileCheck detected changes: content modified, size changed from 1024 to 2048 bytes, imports modified
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.