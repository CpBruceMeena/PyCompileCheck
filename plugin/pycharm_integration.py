#!/usr/bin/env python3
"""
PyCharm Integration Script for PyCompileCheck
This script can be called from PyCharm's external tools to run PyCompileCheck analysis.
"""

import sys
import os
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
            sys.exit(0)
        else:
            print("PyCompileCheck: Analysis failed")
            sys.exit(1)
            
    except Exception as e:
        print(f"PyCompileCheck: Error during analysis: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main() 