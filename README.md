# PyCompileCheck

A Python-based static analysis tool for Django projects that provides **real-time visual feedback** in PyCharm with red/yellow underlines for changed files.

## 🎯 Features

- **Real-time Visual Feedback**: Red/yellow underlines for changed files in PyCharm
- **Background File Monitoring**: Automatically runs analysis when files are saved
- **Change Detection**: Tracks content, import, and size changes
- **Smart TODO Comments**: Adds detailed TODO comments to modified files
- **Metadata Management**: Maintains structured metadata store for project analysis
- **Hybrid Architecture**: Java (UI) + Python (analysis) for optimal performance

## 🏗️ Architecture

```
Java Plugin (PyCharm Integration)
├── Real-time visual indicators (red/yellow underlines)
├── Background file monitoring
├── Inspection integration
└── Project lifecycle management
    ↓ calls
Python Backend (Analysis Engine)
├── Core analysis logic
├── Change detection algorithms
└── Metadata management
```

## 🚀 Quick Start

### Option 1: Command Line (Simple)
```bash
# Clone and setup
git clone https://github.com/CpBruceMeena/PyCompileCheck.git
cd PyCompileCheck
chmod +x run.sh

# Run analysis
./run.sh [project_path]
```

### Option 2: PyCharm Plugin (Recommended)
For **real-time visual feedback** with red/yellow underlines:

1. **Build the Plugin**:
   ```bash
   cd plugin
   ./gradlew buildPlugin
   ```

2. **Install PyCompileCheck Backend**:
   ```bash
   sudo git clone https://github.com/CpBruceMeena/PyCompileCheck.git /opt/pycompilecheck
   chmod +x /opt/pycompilecheck/run.sh
   chmod +x /opt/pycompilecheck/plugin/pycharm_integration.py
   ```

3. **Install in PyCharm**:
   - `File > Settings > Plugins`
   - Install from disk: `plugin/build/distributions/pycompilecheck-plugin-1.0.0.jar`
   - Restart PyCharm

4. **Usage**:
   - Open any Python project
   - Save a file (Ctrl+S)
   - Watch for red/yellow underlines automatically!

## 📊 How It Works

### 1. **File Monitoring**
- Plugin monitors all Python files in real-time
- Detects file saves, creates, and deletes
- Debounces rapid changes (2-second delay)

### 2. **Background Analysis**
- Runs PyCompileCheck analysis in background thread
- Calls Python analysis engine
- Parses structured JSON output

### 3. **Visual Feedback**
- Updates inspection results in real-time
- Shows red underlines for content changes
- Shows yellow underlines for import changes
- Displays tooltips with change details

### 4. **Change Detection**
- Compares current state with previous analysis
- Detects: content changes, size changes, import modifications
- Generates specific TODO comments for each change type

## 🎨 Visual Indicators

### Red Underlines
- **Content changes** detected
- **File modifications** found
- **Critical changes** that need attention

### Yellow Underlines
- **Import changes** detected
- **Non-critical modifications**
- **Warnings** about changes

### Information Indicators
- **File size changes**
- **Metadata updates**
- **Analysis status**

## 📁 Project Structure

```
PyCompileCheck/
├── main.py                              # Core Python analysis engine
├── run.sh                               # Command-line interface
├── setup.py                             # Global installation
├── requirements.txt                     # Python dependencies
├── README.md                            # This file
├── PRD.md                               # Product Requirements Document
├── plugin/                              # PyCharm plugin
│   ├── build.gradle                     # Plugin build configuration
│   ├── pycharm_integration.py          # Python integration script
│   ├── README-HYBRID.md                # Detailed plugin documentation
│   └── src/main/
│       ├── java/com/pycompilecheck/    # Java plugin source
│       │   ├── PyCompileCheckInspection.java      # Visual indicators
│       │   ├── PyCompileCheckFileWatcher.java     # File monitoring
│       │   ├── PyCompileCheckProjectService.java  # Project service
│       │   └── PyCompileCheckProjectComponent.java # Lifecycle
│       └── resources/META-INF/
│           └── plugin.xml              # Plugin configuration
└── .pycompilecheck/                     # Analysis metadata (auto-generated)
    └── metadata.json
```

## 🔧 Configuration

### Automatic Configuration
The plugin automatically:
- ✅ Starts file watching when a project is opened
- ✅ Monitors Python files for changes
- ✅ Runs analysis in background
- ✅ Shows visual indicators

### Manual Configuration (Optional)
If you need to customize the PyCompileCheck path, the plugin automatically detects:
- `/opt/pycompilecheck` (default)
- `~/pycompilecheck`
- `C:\pycompilecheck` (Windows)

## 🔍 Usage Examples

### Command Line
```bash
# Analyze current directory
./run.sh

# Analyze specific project
./run.sh /path/to/project
```

### PyCharm Plugin
1. **Automatic**: Save any Python file → watch for underlines
2. **Manual**: `Code > Inspect Code` → check PyCompileCheck group
3. **Console**: View analysis logs in PyCharm console

## 🛠️ Troubleshooting

### Plugin Issues
```bash
# Check installation
ls /opt/pycompilecheck/main.py

# Fix permissions
chmod +x /opt/pycompilecheck/run.sh
chmod +x /opt/pycompilecheck/plugin/pycharm_integration.py

# Test manually
python3 /opt/pycompilecheck/plugin/pycharm_integration.py /path/to/project
```

### Build Issues
```bash
# Build plugin
cd plugin
./gradlew buildPlugin

# Test in PyCharm
./gradlew runIde
```

## 🎯 Benefits

✅ **Real-time feedback** - No manual triggering needed
✅ **Visual indicators** - Clear red/yellow underlines
✅ **Background processing** - Doesn't block IDE
✅ **Python analysis** - Leverages existing logic
✅ **IDE integration** - Native PyCharm experience
✅ **Cross-platform** - Works on Windows, macOS, Linux

## 📚 Documentation

- **Plugin Setup**: See `plugin/README-HYBRID.md` for detailed plugin documentation
- **Product Requirements**: See `PRD.md` for feature specifications
- **Development**: See plugin source code for customization

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.