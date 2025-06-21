package com.pycompilecheck;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * File watcher service that monitors Python file changes and triggers
 * PyCompileCheck analysis in real-time.
 */
public class PyCompileCheckFileWatcher {
    
    private static final Logger LOG = Logger.getInstance(PyCompileCheckFileWatcher.class);
    private final Project project;
    private final VirtualFileListener fileListener;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService analysisExecutor;
    private volatile boolean isAnalysisPending = false;
    
    public PyCompileCheckFileWatcher(@NotNull Project project) {
        this.project = project;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.analysisExecutor = Executors.newSingleThreadExecutor();
        
        this.fileListener = new VirtualFileAdapter() {
            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                handleFileChange(event.getFile());
            }
            
            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {
                handleFileChange(event.getFile());
            }
            
            @Override
            public void fileDeleted(@NotNull VirtualFileEvent event) {
                handleFileChange(event.getFile());
            }
        };
    }
    
    public void startWatching() {
        VirtualFileManager.getInstance().addVirtualFileListener(fileListener);
        LOG.info("PyCompileCheck file watcher started for project: " + project.getName());
    }
    
    public void stopWatching() {
        VirtualFileManager.getInstance().removeVirtualFileListener(fileListener);
        scheduler.shutdown();
        analysisExecutor.shutdown();
        LOG.info("PyCompileCheck file watcher stopped for project: " + project.getName());
    }
    
    private void handleFileChange(@NotNull VirtualFile file) {
        // Only watch Python files in the current project
        if (!isPythonFile(file) || !isInProject(file)) {
            return;
        }
        
        LOG.info("PyCompileCheck detected change in file: " + file.getPath());
        
        // Debounce analysis to avoid running too frequently
        if (!isAnalysisPending) {
            isAnalysisPending = true;
            
            // Schedule analysis with a delay to allow for multiple rapid changes
            scheduler.schedule(() -> {
                runAnalysis();
                isAnalysisPending = false;
            }, 2, TimeUnit.SECONDS);
        }
    }
    
    private boolean isPythonFile(@NotNull VirtualFile file) {
        return file.getName().endsWith(".py");
    }
    
    private boolean isInProject(@NotNull VirtualFile file) {
        VirtualFile projectDir = project.getBaseDir();
        if (projectDir == null) {
            return false;
        }
        
        String filePath = file.getPath();
        String projectPath = projectDir.getPath();
        
        return filePath.startsWith(projectPath);
    }
    
    private void runAnalysis() {
        analysisExecutor.submit(() -> {
            try {
                // Run the analysis in background
                String projectPath = project.getBasePath();
                if (projectPath == null) {
                    return;
                }
                
                // Find PyCompileCheck installation
                String pycompilecheckPath = findPyCompileCheckPath();
                if (pycompilecheckPath == null) {
                    LOG.warn("PyCompileCheck installation not found");
                    return;
                }
                
                String integrationScript = pycompilecheckPath + "/plugin/pycharm_integration.py";
                
                // Run analysis
                ProcessBuilder pb = new ProcessBuilder(
                    "python3", integrationScript, projectPath
                );
                pb.redirectErrorStream(true);
                
                Process process = pb.start();
                
                // Read output
                StringBuilder output = new StringBuilder();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    LOG.info("PyCompileCheck analysis completed successfully");
                    
                    // Trigger inspection refresh to show visual indicators
                    ApplicationManager.getApplication().invokeLater(() -> {
                        refreshInspections();
                    });
                } else {
                    LOG.warn("PyCompileCheck analysis failed with exit code: " + exitCode);
                }
                
            } catch (Exception e) {
                LOG.error("Error running PyCompileCheck analysis", e);
            }
        });
    }
    
    private @NotNull String findPyCompileCheckPath() {
        // Try common installation paths
        String[] possiblePaths = {
            "/opt/pycompilecheck",
            System.getProperty("user.home") + "/pycompilecheck",
            "C:\\pycompilecheck"
        };
        
        for (String path : possiblePaths) {
            if (java.nio.file.Files.exists(java.nio.file.Paths.get(path, "main.py"))) {
                return path;
            }
        }
        
        // Return default path
        return "/opt/pycompilecheck";
    }
    
    private void refreshInspections() {
        // Trigger inspection refresh to show new visual indicators
        // This will cause the inspection to re-run and show underlines
        project.getMessageBus().syncPublisher(
            com.intellij.openapi.project.ProjectTopics.PROJECT_ROOTS
        ).rootsChanged();
    }
} 