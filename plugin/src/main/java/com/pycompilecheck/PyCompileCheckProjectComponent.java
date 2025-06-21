package com.pycompilecheck;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Project component that manages PyCompileCheck file watcher lifecycle.
 * Automatically starts file watching when a project is opened.
 */
public class PyCompileCheckProjectComponent implements StartupActivity {
    
    private static final Logger LOG = Logger.getInstance(PyCompileCheckProjectComponent.class);
    
    @Override
    public void runActivity(@NotNull Project project) {
        LOG.info("PyCompileCheck project component starting for project: " + project.getName());
        
        // Get the project service and start file watching
        PyCompileCheckProjectService service = project.getService(PyCompileCheckProjectService.class);
        if (service != null) {
            service.startFileWatching();
            LOG.info("PyCompileCheck file watcher started for project: " + project.getName());
        } else {
            LOG.warn("PyCompileCheck project service not found for project: " + project.getName());
        }
    }
} 