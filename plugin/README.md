# PyCompileCheck PyCharm Plugin

This plugin integrates PyCompileCheck into PyCharm IDE, providing seamless static analysis for Python projects.

## How It Works

The plugin references the PyCompileCheck project from a central location and can analyze any project in PyCharm.

## Setup Instructions

### 1. **Install PyCompileCheck Project**

First, clone or download the PyCompileCheck project to a central location:
```bash
git clone https://github.com/CpBruceMeena/PyCompileCheck.git /opt/pycompilecheck
# or any other location you prefer
```

### 2. **Configure PyCharm External Tool**

1. Open PyCharm
2. Go to `File > Settings > Tools > External Tools`
3. Click the `+` button to add a new tool
4. Configure as follows:

```
Name: PyCompileCheck
Program: /opt/pycompilecheck/plugin/pycharm_integration.py
Arguments: $ProjectFileDir$
Working directory: $ProjectFileDir$
```

**Important**: Replace `/opt/pycompilecheck` with the actual path where you installed PyCompileCheck.

### 3. **Configure File Watcher (Optional)**

For automatic analysis when files are saved:

1. Go to `File > Settings > Tools > File Watchers`
2. Click the `+` button to add a new watcher
3. Configure as follows:

```
Name: PyCompileCheck
File type: Python
Program: /opt/pycompilecheck/plugin/pycharm_integration.py
Arguments: $ProjectFileDir$
Working directory: $ProjectFileDir$
```

### 4. **Add to Context Menu**

1. Go to `File > Settings > Appearance & Behavior > Menus and Toolbars`
2. Navigate to `Project View Popup Menu`
3. Add the PyCompileCheck tool to the context menu

## Usage

### Manual Analysis
- Right-click on your project folder in PyCharm
- Select `External Tools > PyCompileCheck`
- Check the console for analysis results

### Automatic Analysis
- If you set up the file watcher, analysis will run automatically when you save Python files
- Check the TODO tool window for any issues detected

### Keyboard Shortcut
- Go to `File > Settings > Keymap`
- Search for "PyCompileCheck"
- Assign a keyboard shortcut (e.g., `Ctrl+Shift+P`)

## How It References the PyCompileCheck Project

The plugin works by:

1. **Central Installation**: PyCompileCheck is installed in a central location (e.g., `/opt/pycompilecheck`)
2. **Path Reference**: PyCharm's external tool points to the integration script in the PyCompileCheck project
3. **Dynamic Import**: The integration script dynamically imports PyCompileCheck modules from the central location
4. **Project Analysis**: It analyzes the current PyCharm project using the central PyCompileCheck installation

## File Structure

```
/opt/pycompilecheck/                    # Central PyCompileCheck installation
├── main.py                            # Core analysis engine
├── run.sh                             # Command-line interface
├── plugin/
│   ├── pycompilecheck_plugin.py       # Main plugin logic
│   ├── pycharm_integration.py         # PyCharm integration script
│   └── pycharm-plugin-config.xml      # Configuration template
└── ...

Your Project/                           # Any PyCharm project
├── .pycompilecheck/                   # Analysis results (created automatically)
│   └── metadata.json
├── your_python_files.py
└── ...
```

## Troubleshooting

### Plugin Not Found
- Ensure the path to `pycharm_integration.py` is correct
- Check that PyCompileCheck is properly installed

### Import Errors
- Verify that PyCompileCheck's main modules are accessible
- Check Python path configuration

### No Analysis Results
- Ensure your project contains Python files
- Check the console for error messages
- Verify the working directory is set correctly

## Advantages of This Approach

✅ **Central Installation**: Install PyCompileCheck once, use everywhere
✅ **No Project Dependencies**: Don't need to add PyCompileCheck to each project
✅ **Easy Updates**: Update PyCompileCheck in one place
✅ **IDE Integration**: Seamless integration with PyCharm
✅ **Automatic Analysis**: Can run automatically on file changes

## Configuration Examples

### For Different Operating Systems

**macOS/Linux:**
```
Program: /opt/pycompilecheck/plugin/pycharm_integration.py
```

**Windows:**
```
Program: C:\pycompilecheck\plugin\pycharm_integration.py
```

### For Different Python Versions

If you have multiple Python versions, you can specify the interpreter:
```
Program: /usr/bin/python3
Arguments: /opt/pycompilecheck/plugin/pycharm_integration.py $ProjectFileDir$
``` 