package com.pycompilecheck;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Project service that manages PyCompileCheck file watcher for each project.
 */
public class PyCompileCheckProjectService {
    
    private final Project project;
    private PyCompileCheckFileWatcher fileWatcher;
    
    public PyCompileCheckProjectService(@NotNull Project project) {
        this.project = project;
    }
    
    public void startFileWatching() {
        if (fileWatcher == null) {
            fileWatcher = new PyCompileCheckFileWatcher(project);
            fileWatcher.startWatching();
        }
    }
    
    public void stopFileWatching() {
        if (fileWatcher != null) {
            fileWatcher.stopWatching();
            fileWatcher = null;
        }
    }
    
    public boolean isWatching() {
        return fileWatcher != null;
    }
} 