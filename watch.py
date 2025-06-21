#!/usr/bin/env python3
"""
File watcher for PyCompileCheck.
Continuously monitors Python files and runs analysis when changes are detected.
"""

import time
import os
import sys
from pathlib import Path
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
import subprocess
import threading

class PyCompileCheckHandler(FileSystemEventHandler):
    def __init__(self, project_path: str):
        self.project_path = Path(project_path)
        self.last_run = 0
        self.debounce_time = 2  # Wait 2 seconds after last change before running
        
    def on_modified(self, event):
        if event.is_directory:
            return
            
        if event.src_path.endswith('.py'):
            # Debounce: only run if enough time has passed since last change
            current_time = time.time()
            if current_time - self.last_run > self.debounce_time:
                self.last_run = current_time
                print(f"Change detected in {event.src_path}")
                self.run_analysis()
    
    def run_analysis(self):
        """Run PyCompileCheck analysis in a separate thread."""
        def run():
            try:
                result = subprocess.run(
                    ['./run.sh'], 
                    cwd=self.project_path,
                    capture_output=True,
                    text=True
                )
                if result.returncode == 0:
                    print("PyCompileCheck analysis completed successfully")
                else:
                    print(f"PyCompileCheck analysis failed: {result.stderr}")
            except Exception as e:
                print(f"Error running PyCompileCheck: {e}")
        
        # Run in background thread to avoid blocking file operations
        thread = threading.Thread(target=run)
        thread.daemon = True
        thread.start()

def main():
    if len(sys.argv) > 1:
        project_path = sys.argv[1]
    else:
        project_path = '.'
    
    print(f"Starting PyCompileCheck file watcher for: {project_path}")
    print("Press Ctrl+C to stop")
    
    event_handler = PyCompileCheckHandler(project_path)
    observer = Observer()
    observer.schedule(event_handler, project_path, recursive=True)
    observer.start()
    
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
        print("\nStopping file watcher...")
    
    observer.join()

if __name__ == "__main__":
    main() 