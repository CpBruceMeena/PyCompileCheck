# PyCharm Integration Guide

## Simple External Tool Integration

Instead of creating a complex Java plugin, we can integrate PyCompileCheck with PyCharm using **External Tools** - a much simpler and more appropriate approach.

## Method 1: External Tool Configuration

### Step 1: Configure External Tool in PyCharm

1. Go to `File > Settings > Tools > External Tools`
2. Click the `+` button to add a new tool
3. Configure as follows:

```
Name: PyCompileCheck
Program: $ProjectFileDir$/run.sh
Arguments: 
Working directory: $ProjectFileDir$
```

### Step 2: Add to Context Menu

1. Go to `File > Settings > Appearance & Behavior > Menus and Toolbars`
2. Navigate to `Project View Popup Menu`
3. Add the PyCompileCheck tool to the context menu

### Step 3: Add Keyboard Shortcut (Optional)

1. Go to `File > Settings > Keymap`
2. Search for "PyCompileCheck"
3. Assign a keyboard shortcut (e.g., Ctrl+Shift+P)

## Method 2: File Watcher Integration

### Step 1: Create File Watcher

1. Go to `File > Settings > Tools > File Watchers`
2. Add a new watcher for Python files
3. Configure to run PyCompileCheck on file changes

## Method 3: Pre-commit Hook Integration

### Step 1: Create Pre-commit Hook

Create `.git/hooks/pre-commit`:
```bash
#!/bin/bash
./run.sh .
```

### Step 2: Make Executable
```bash
chmod +x .git/hooks/pre-commit
```

## Method 4: Custom Run Configuration

### Step 1: Create Run Configuration

1. Go to `Run > Edit Configurations`
2. Add new "Shell Script" configuration
3. Configure to run `./run.sh`

## Advantages of External Tool Approach

✅ **No Java Development Required**
✅ **Simpler Implementation**
✅ **Easier Maintenance**
✅ **Works with Any IDE**
✅ **No Plugin Distribution**
✅ **Faster Development**

## Usage Examples

### From Context Menu
- Right-click on project folder
- Select "PyCompileCheck"

### From Tools Menu
- Go to `Tools > External Tools > PyCompileCheck`

### From Keyboard Shortcut
- Press assigned shortcut (e.g., Ctrl+Shift+P)

## Integration with PyCharm Features

### 1. TODO Comments
- PyCompileCheck adds TODO comments
- PyCharm automatically highlights them in TODO tool window

### 2. File Monitoring
- PyCharm detects file changes
- External tool runs analysis

### 3. Console Output
- Analysis results appear in PyCharm console
- Error messages are clearly displayed

## Configuration File

Create `pycharm-config.xml` for easy import:

```xml
<application>
  <component name="ExternalToolsSettings">
    <option name="tools">
      <list>
        <ExternalTool>
          <option name="description" value="Run PyCompileCheck analysis" />
          <option name="group" value="PyCompileCheck" />
          <option name="name" value="PyCompileCheck" />
          <option name="options" value="" />
          <option name="parameters" value="" />
          <option name="program" value="$ProjectFileDir$/run.sh" />
          <option name="showConsole" value="ALWAYS" />
          <option name="showInEditor" value="false" />
          <option name="workingDirectory" value="$ProjectFileDir$" />
        </ExternalTool>
      </list>
    </option>
  </component>
</application>
```

## Why This Approach is Better

1. **No Plugin Development**: No need to learn Java or PyCharm plugin API
2. **Cross-Platform**: Works on any system with PyCharm
3. **Easy Distribution**: Just share the configuration
4. **Maintainable**: Simple shell script integration
5. **Flexible**: Can be used with other IDEs too

## Next Steps

1. **Remove Java Plugin Files**: Delete the `plugin/` directory
2. **Focus on Core Tool**: Improve the Python analysis functionality
3. **Add IDE Configs**: Create configuration files for other IDEs (VS Code, etc.)
4. **Documentation**: Update README with integration instructions

This approach is much more practical and aligns better with the project's goals! 