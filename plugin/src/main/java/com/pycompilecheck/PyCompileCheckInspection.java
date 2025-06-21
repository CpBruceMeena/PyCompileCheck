package com.pycompilecheck;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * PyCompileCheck Inspection that provides real-time visual feedback.
 * Shows red/yellow underlines for changed files and lines.
 */
public class PyCompileCheckInspection extends LocalInspectionTool {

    private static final Logger LOG = Logger.getInstance(PyCompileCheckInspection.class);
    private static final String INSPECTION_GROUP = "PyCompileCheck";
    private static final String INSPECTION_NAME = "PyCompileCheck Analysis";
    private static final String INSPECTION_DISPLAY_NAME = "PyCompileCheck detected changes";

    @Override
    public @NotNull String getGroupDisplayName() {
        return INSPECTION_GROUP;
    }

    @Override
    public @NotNull String getDisplayName() {
        return INSPECTION_DISPLAY_NAME;
    }

    @Override
    public @NotNull String getShortName() {
        return "PyCompileCheck";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public @NotNull LocalInspectionToolSession createSession(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        return new LocalInspectionToolSession(file, manager, isOnTheFly);
    }

    @Override
    public @Nullable ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!isPythonFile(file)) {
            return null;
        }

        List<ProblemDescriptor> problems = new ArrayList<>();
        
        try {
            // Run PyCompileCheck analysis in background
            PyCompileCheckResult result = runPyCompileCheckAnalysis(file.getProject());
            
            if (result != null && result.hasChanges(file.getVirtualFile().getPath())) {
                // Add visual indicators for changed lines
                addChangeIndicators(file, manager, problems, result);
            }
            
        } catch (Exception e) {
            // Add error indicator
            ProblemDescriptor errorProblem = manager.createProblemDescriptor(
                file,
                "PyCompileCheck analysis failed: " + e.getMessage(),
                true,
                LocalQuickFix.EMPTY_ARRAY,
                ProblemHighlightType.ERROR
            );
            problems.add(errorProblem);
        }

        return problems.toArray(new ProblemDescriptor[0]);
    }

    private boolean isPythonFile(@NotNull PsiFile file) {
        return file.getName().endsWith(".py");
    }

    private void addChangeIndicators(@NotNull PsiFile file, 
                                   @NotNull InspectionManager manager,
                                   @NotNull List<ProblemDescriptor> problems,
                                   @NotNull PyCompileCheckResult result) {
        
        VirtualFile virtualFile = file.getVirtualFile();
        String filePath = virtualFile.getPath();
        
        if (result.hasContentChanges(filePath)) {
            // Add red underline for content changes
            ProblemDescriptor contentProblem = manager.createProblemDescriptor(
                file,
                "Content modified - PyCompileCheck detected changes",
                true,
                LocalQuickFix.EMPTY_ARRAY,
                ProblemHighlightType.ERROR
            );
            problems.add(contentProblem);
        }
        
        if (result.hasImportChanges(filePath)) {
            // Add yellow underline for import changes
            ProblemDescriptor importProblem = manager.createProblemDescriptor(
                file,
                "Imports modified - PyCompileCheck detected import changes",
                true,
                LocalQuickFix.EMPTY_ARRAY,
                ProblemHighlightType.WEAK_WARNING
            );
            problems.add(importProblem);
        }
        
        if (result.hasSizeChanges(filePath)) {
            // Add info indicator for size changes
            String sizeInfo = result.getSizeChangeInfo(filePath);
            ProblemDescriptor sizeProblem = manager.createProblemDescriptor(
                file,
                "File size changed: " + sizeInfo,
                true,
                LocalQuickFix.EMPTY_ARRAY,
                ProblemHighlightType.INFORMATION
            );
            problems.add(sizeProblem);
        }
    }

    private @Nullable PyCompileCheckResult runPyCompileCheckAnalysis(@NotNull Project project) {
        try {
            // Get the path to the Python integration script
            String projectBasePath = project.getBasePath();
            if (projectBasePath == null) {
                return null;
            }

            // Find PyCompileCheck installation (you can configure this path)
            String pycompilecheckPath = findPyCompileCheckPath();
            if (pycompilecheckPath == null) {
                return null;
            }

            String integrationScript = pycompilecheckPath + "/plugin/pycharm_integration.py";
            
            // Run the Python analysis
            ProcessBuilder pb = new ProcessBuilder(
                "python3", integrationScript, projectBasePath
            );
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // Read the output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // Parse the analysis results
                return parseAnalysisResults(output.toString(), projectBasePath);
            } else {
                return null;
            }
            
        } catch (Exception e) {
            return null;
        }
    }

    private @Nullable String findPyCompileCheckPath() {
        // Try common installation paths
        String[] possiblePaths = {
            "/opt/pycompilecheck",
            System.getProperty("user.home") + "/pycompilecheck",
            "C:\\pycompilecheck"
        };
        
        for (String path : possiblePaths) {
            if (java.nio.file.Files.exists(Paths.get(path, "main.py"))) {
                return path;
            }
        }
        
        return null;
    }

    private @NotNull PyCompileCheckResult parseAnalysisResults(@NotNull String output, @NotNull String projectPath) {
        PyCompileCheckResult result = new PyCompileCheckResult();
        
        try {
            // Look for structured output markers
            int startIndex = output.indexOf("PYCOMPILECHECK_OUTPUT_START");
            int endIndex = output.indexOf("PYCOMPILECHECK_OUTPUT_END");
            
            if (startIndex != -1 && endIndex != -1) {
                // Extract JSON data between markers
                String jsonData = output.substring(
                    startIndex + "PYCOMPILECHECK_OUTPUT_START".length(),
                    endIndex
                ).trim();
                
                // Parse JSON
                com.google.gson.Gson gson = new com.google.gson.Gson();
                AnalysisOutput analysisOutput = gson.fromJson(jsonData, AnalysisOutput.class);
                
                if (analysisOutput != null && analysisOutput.changes != null) {
                    for (ChangeData change : analysisOutput.changes) {
                        String fullPath = projectPath + "/" + change.file;
                        result.addChange(
                            fullPath,
                            change.has_content_changes,
                            change.has_import_changes,
                            change.has_size_changes,
                            change.size_change_info != null ? change.size_change_info : ""
                        );
                    }
                }
            }
        } catch (Exception e) {
            // Fallback: create basic result if parsing fails
            LOG.warn("Failed to parse PyCompileCheck output: " + e.getMessage());
        }
        
        return result;
    }

    // Data classes for JSON parsing
    private static class AnalysisOutput {
        String status;
        String project_path;
        ChangeData[] changes;
        long timestamp;
    }
    
    private static class ChangeData {
        String file;
        boolean has_content_changes;
        boolean has_import_changes;
        boolean has_size_changes;
        String size_change_info;
        long last_modified;
        long size;
    }

    /**
     * Result class to hold PyCompileCheck analysis results
     */
    public static class PyCompileCheckResult {
        private final java.util.Map<String, ChangeInfo> changes = new java.util.HashMap<>();

        public boolean hasChanges(String filePath) {
            return changes.containsKey(filePath);
        }

        public boolean hasContentChanges(String filePath) {
            ChangeInfo info = changes.get(filePath);
            return info != null && info.hasContentChanges;
        }

        public boolean hasImportChanges(String filePath) {
            ChangeInfo info = changes.get(filePath);
            return info != null && info.hasImportChanges;
        }

        public boolean hasSizeChanges(String filePath) {
            ChangeInfo info = changes.get(filePath);
            return info != null && info.hasSizeChanges;
        }

        public String getSizeChangeInfo(String filePath) {
            ChangeInfo info = changes.get(filePath);
            return info != null ? info.sizeChangeInfo : "";
        }

        public void addChange(String filePath, boolean content, boolean imports, boolean size, String sizeInfo) {
            changes.put(filePath, new ChangeInfo(content, imports, size, sizeInfo));
        }

        private static class ChangeInfo {
            final boolean hasContentChanges;
            final boolean hasImportChanges;
            final boolean hasSizeChanges;
            final String sizeChangeInfo;

            ChangeInfo(boolean content, boolean imports, boolean size, String sizeInfo) {
                this.hasContentChanges = content;
                this.hasImportChanges = imports;
                this.hasSizeChanges = size;
                this.sizeChangeInfo = sizeInfo;
            }
        }
    }
} 