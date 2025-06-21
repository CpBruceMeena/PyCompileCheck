#!/usr/bin/env python3
# TODO: PyCompileCheck detected changes: content modified, size changed from 5288 to 5411 bytes
"""
Main entry point for PyCompileCheck.
Handles project analysis and metadata management.
"""

import os
import sys
import json
import hashlib
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

class ProjectAnalyzer:
    def __init__(self, project_path: str):
        self.project_path = Path(project_path)
        self.metadata_dir = self.project_path / ".pycompilecheck"
        self.metadata_file = self.metadata_dir / "metadata.json"
        self.current_metadata: Dict[str, Dict] = {}
        self.previous_metadata: Dict[str, Dict] = {}
        
    def setup(self) -> None:
        """Initialize the metadata directory and files."""
        self.metadata_dir.mkdir(exist_ok=True)
        if self.metadata_file.exists():
            with open(self.metadata_file, 'r') as f:
                self.previous_metadata = json.load(f)
    
    def calculate_file_hash(self, file_path: Path) -> str:
        """Calculate SHA-256 hash of a file."""
        sha256_hash = hashlib.sha256()
        with open(file_path, "rb") as f:
            for byte_block in iter(lambda: f.read(4096), b""):
                sha256_hash.update(byte_block)
        return sha256_hash.hexdigest()
    
    def analyze_file(self, file_path: Path) -> Dict:
        """Analyze a single Python file and return its metadata."""
        stats = file_path.stat()
        return {
            "last_modified": stats.st_mtime,
            "size": stats.st_size,
            "hash": self.calculate_file_hash(file_path),
            "imports": self._detect_imports(file_path),
            "last_analyzed": datetime.now().isoformat()
        }
    
    def _detect_imports(self, file_path: Path) -> List[str]:
        """Detect imports in a Python file."""
        imports = []
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                for line in f:
                    line = line.strip()
                    if line.startswith(('import ', 'from ')):
                        imports.append(line)
        except Exception as e:
            print(f"Error reading imports from {file_path}: {e}")
        return imports
    
    def analyze_project(self) -> None:
        """Analyze the entire project and detect changes."""
        for root, _, files in os.walk(self.project_path):
            for file in files:
                if file.endswith('.py'):
                    file_path = Path(root) / file
                    rel_path = str(file_path.relative_to(self.project_path))
                    
                    # Skip metadata directory
                    if '.pycompilecheck' in rel_path:
                        continue
                    
                    # Analyze file
                    self.current_metadata[rel_path] = self.analyze_file(file_path)
                    
                    # Check for changes
                    if rel_path in self.previous_metadata:
                        prev = self.previous_metadata[rel_path]
                        curr = self.current_metadata[rel_path]
                        
                        changes = []
                        if prev['hash'] != curr['hash']:
                            changes.append("content modified")
                        if prev['size'] != curr['size']:
                            changes.append(f"size changed from {prev['size']} to {curr['size']} bytes")
                        if prev['imports'] != curr['imports']:
                            changes.append("imports modified")
                        
                        if changes:
                            self.add_todo_comment(file_path, changes)
    
    def add_todo_comment(self, file_path: Path, changes: List[str]) -> None:
        """Add a TODO comment to the file indicating what changed."""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                lines = f.readlines()
            
            # Create the change message
            change_msg = f"# TODO: PyCompileCheck detected changes: {', '.join(changes)}"
            
            # Add the comment after any existing shebang or encoding declarations
            insert_pos = 0
            for i, line in enumerate(lines):
                if not line.strip().startswith(('#!', '# -*-', '# coding')):
                    insert_pos = i
                    break
            
            # Insert the comment
            lines.insert(insert_pos, f"{change_msg}\n")
            
            # Write back to file
            with open(file_path, 'w', encoding='utf-8') as f:
                f.writelines(lines)
                
        except Exception as e:
            print(f"Error adding TODO comment to {file_path}: {e}")
    
    def save_metadata(self) -> None:
        """Save the current metadata to file."""
        with open(self.metadata_file, 'w') as f:
            json.dump(self.current_metadata, f, indent=2)

def main():
    # If no argument is given, use current directory
    if len(sys.argv) == 1:
        project_path = '.'
    elif len(sys.argv) == 2:
        project_path = sys.argv[1]
    else:
        print("Usage: python main.py [project_path]")
        sys.exit(1)
    
    analyzer = ProjectAnalyzer(project_path)
    analyzer.setup()
    analyzer.analyze_project()
    analyzer.save_metadata()

if __name__ == "__main__":
    main() 