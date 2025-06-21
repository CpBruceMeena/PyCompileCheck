#!/usr/bin/env python3
"""
PyCharm Integration Script for PyCompileCheck
This script can be called from PyCharm's external tools to run PyCompileCheck analysis.
Provides structured output for Java plugin integration.
"""

import sys
import os
import json
from pathlib import Path

# Add the PyCompileCheck directory to the Python path
# This script assumes it's in the plugin directory, so we need to go up to the root
script_dir = Path(__file__).parent
pycompilecheck_dir = script_dir.parent
sys.path.insert(0, str(pycompilecheck_dir))

try:
    from pycompilecheck_plugin import run_analysis, run_from_cli
except ImportError:
    # Fallback: try to import directly from main
    try:
        from main import ProjectAnalyzer
        
        def run_analysis(project_path):
            analyzer = ProjectAnalyzer(project_path)
            analyzer.setup()
            analyzer.analyze_project()
            analyzer.save_metadata()
            return True
            
    except ImportError:
        print("Error: Could not import PyCompileCheck modules")
        sys.exit(1)


def main():
    """Main entry point for PyCharm integration."""
    
    # Get project path from command line arguments
    if len(sys.argv) > 1:
        project_path = sys.argv[1]
    else:
        # Default to current directory
        project_path = "."
    
    # Validate project path
    if not os.path.isdir(project_path):
        print(f"Error: Project path does not exist: {project_path}")
        sys.exit(1)
    
    print(f"PyCompileCheck: Analyzing project at {project_path}")
    
    try:
        # Try to run analysis using the plugin
        success = run_analysis(project_path)
        
        if success:
            print("PyCompileCheck: Analysis completed successfully")
            
            # Generate structured output for Java plugin
            generate_structured_output(project_path)
            
            sys.exit(0)
        else:
            print("PyCompileCheck: Analysis failed")
            sys.exit(1)
            
    except Exception as e:
        print(f"PyCompileCheck: Error during analysis: {e}")
        sys.exit(1)


def generate_structured_output(project_path):
    """Generate structured output that the Java plugin can parse."""
    try:
        # Read the metadata file
        metadata_file = Path(project_path) / ".pycompilecheck" / "metadata.json"
        if not metadata_file.exists():
            return
        
        with open(metadata_file, 'r') as f:
            metadata = json.load(f)
        
        # Generate change information for each file
        changes = []
        for file_path, file_data in metadata.items():
            change_info = {
                "file": file_path,
                "has_content_changes": False,
                "has_import_changes": False,
                "has_size_changes": False,
                "size_change_info": "",
                "last_modified": file_data.get("last_modified", 0),
                "size": file_data.get("size", 0)
            }
            
            # Check for changes (this is simplified - you can enhance based on your logic)
            # For now, we'll mark all files as having potential changes
            change_info["has_content_changes"] = True
            
            changes.append(change_info)
        
        # Output structured data for Java plugin
        output_data = {
            "status": "success",
            "project_path": project_path,
            "changes": changes,
            "timestamp": os.path.getmtime(metadata_file)
        }
        
        # Print structured output that Java can parse
        print("PYCOMPILECHECK_OUTPUT_START")
        print(json.dumps(output_data, indent=2))
        print("PYCOMPILECHECK_OUTPUT_END")
        
    except Exception as e:
        print(f"Error generating structured output: {e}")


if __name__ == "__main__":
    main() 