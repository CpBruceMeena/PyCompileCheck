# PyCompileCheck Hybrid Plugin (Java + Python)

This plugin provides **real-time visual feedback** in PyCharm with red/yellow underlines for changed files, using a hybrid approach combining Java (UI) and Python (analysis).

## 🎯 Features

- ✅ **Real-time visual feedback** with red/yellow underlines
- ✅ **Background file monitoring** - runs automatically when files are saved
- ✅ **Tooltips** showing change details
- ✅ **Inspection integration** - appears in PyCharm's inspection results
- ✅ **Python analysis engine** - leverages existing PyCompileCheck logic

## 🏗️ Architecture

```
Java Plugin (PyCharm Integration)
├── PyCompileCheckInspection.java     # Visual indicators (underlines)
├── PyCompileCheckFileWatcher.java    # Real-time file monitoring
├── PyCompileCheckProjectService.java # Project lifecycle management
└── PyCompileCheckProjectComponent.java # Auto-start/stop
    ↓ calls
Python Backend (Analysis Engine)
├── main.py                           # Core analysis logic
├── ProjectAnalyzer                   # Change detection
└── pycharm_integration.py           # Structured output
```

## 🚀 Installation

### Step 1: Build the Java Plugin

```bash
cd plugin
./gradlew buildPlugin
```

The plugin JAR will be created at: `plugin/build/distributions/pycompilecheck-plugin-1.0.0.jar`

### Step 2: Install PyCompileCheck Python Backend

```bash
# Clone PyCompileCheck to a central location
sudo git clone https://github.com/CpBruceMeena/PyCompileCheck.git /opt/pycompilecheck

# Make scripts executable
chmod +x /opt/pycompilecheck/run.sh
chmod +x /opt/pycompilecheck/plugin/pycharm_integration.py
```

### Step 3: Install Plugin in PyCharm

1. **Open PyCharm**
2. **Go to Settings**: `File > Settings > Plugins`
3. **Install Plugin**: Click gear icon → `Install Plugin from Disk`
4. **Select JAR**: Choose `pycompilecheck-plugin-1.0.0.jar`
5. **Restart PyCharm**

## 🔧 Configuration

### Automatic Configuration

The plugin automatically:
- ✅ Starts file watching when a project is opened
- ✅ Monitors Python files for changes
- ✅ Runs analysis in background
- ✅ Shows visual indicators

### Manual Configuration (Optional)

If you need to customize the PyCompileCheck path:

1. **Find your PyCompileCheck installation**:
   - Default: `/opt/pycompilecheck`
   - Windows: `C:\pycompilecheck`
   - Custom: Any path where you installed PyCompileCheck

2. **The plugin will automatically detect**:
   - `/opt/pycompilecheck`
   - `~/pycompilecheck`
   - `C:\pycompilecheck`

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

## 📊 How It Works

### 1. **File Monitoring**
- Plugin monitors all Python files in the project
- Detects file saves, creates, and deletes
- Debounces rapid changes (2-second delay)

### 2. **Background Analysis**
- Runs PyCompileCheck analysis in background thread
- Calls Python script: `pycharm_integration.py`
- Parses structured JSON output

### 3. **Visual Feedback**
- Updates inspection results in real-time
- Shows underlines on changed lines
- Displays tooltips with change details

### 4. **Structured Communication**
```
Java Plugin → Python Script → Analysis → JSON Output → Java Plugin → Visual Indicators
```

## 🔍 Usage

### Automatic Operation
1. **Open any Python project** in PyCharm
2. **Save a Python file** (Ctrl+S)
3. **Watch for underlines** - they appear automatically
4. **Hover over underlines** for tooltips

### Manual Trigger
- **Run Inspection**: `Code > Inspect Code`
- **Check PyCompileCheck group** in results
- **View all detected changes**

### Console Output
- **View PyCharm console** for analysis logs
- **Check Event Log** for plugin status
- **Monitor background processes**

## 🛠️ Troubleshooting

### Plugin Not Appearing
```bash
# Check plugin installation
File > Settings > Plugins > Installed
# Look for "PyCompileCheck" in the list
```

### No Visual Indicators
```bash
# Check PyCompileCheck installation
ls /opt/pycompilecheck/main.py
# Should exist and be accessible
```

### Analysis Not Running
```bash
# Check Python script permissions
chmod +x /opt/pycompilecheck/plugin/pycharm_integration.py

# Test manual execution
python3 /opt/pycompilecheck/plugin/pycharm_integration.py /path/to/project
```

### Permission Issues
```bash
# Fix permissions
sudo chown -R $USER:$USER /opt/pycompilecheck
chmod +x /opt/pycompilecheck/run.sh
chmod +x /opt/pycompilecheck/plugin/pycharm_integration.py
```

## 📁 File Structure

```
/opt/pycompilecheck/                    # Python Backend
├── main.py                            # Core analysis
├── run.sh                             # CLI interface
├── plugin/
│   └── pycharm_integration.py         # Integration script
└── ...

PyCharm Plugin (JAR)
├── PyCompileCheckInspection.java      # Visual indicators
├── PyCompileCheckFileWatcher.java     # File monitoring
├── PyCompileCheckProjectService.java  # Project service
└── PyCompileCheckProjectComponent.java # Lifecycle
```

## 🔄 Development

### Building the Plugin
```bash
cd plugin
./gradlew buildPlugin
./gradlew runIde  # Test in PyCharm
```

### Debugging
```bash
# Check PyCharm logs
Help > Show Log in Explorer
# Look for PyCompileCheck entries
```

### Customizing
- **Change detection logic**: Modify `main.py`
- **Visual indicators**: Update `PyCompileCheckInspection.java`
- **File monitoring**: Adjust `PyCompileCheckFileWatcher.java`

## 🎯 Benefits

✅ **Real-time feedback** - No manual triggering needed
✅ **Visual indicators** - Clear red/yellow underlines
✅ **Background processing** - Doesn't block IDE
✅ **Python analysis** - Leverages existing logic
✅ **IDE integration** - Native PyCharm experience
✅ **Cross-platform** - Works on Windows, macOS, Linux

## 🚀 Next Steps

1. **Install the plugin** following the steps above
2. **Test with a Python project** - save files and watch for underlines
3. **Customize detection logic** - modify the Python analysis as needed
4. **Extend visual indicators** - add more types of feedback

The hybrid approach gives you the best of both worlds: **real-time visual feedback** with **powerful Python analysis**! 