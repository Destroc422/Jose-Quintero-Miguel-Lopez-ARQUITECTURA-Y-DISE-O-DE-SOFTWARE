package com.hermosacartagena.quality.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * ANALIZADOR DE MÉTRICAS DE CÓDIGO
 * =============================================
 *
 * Analizador de métricas de software para calcular:
 * - LOC (Lines of Code)
 * - WMC (Weighted Methods per Class)
 * - CBO (Coupling Between Objects)
 * - Cyclomatic Complexity
 * - Technical Debt
 *
 * @author Sistema de Gestión Turística
 * @version 3.0.0
 * @since 2026
 */
public class CodeMetricsAnalyzer {
    
    private static final String SOURCE_PATH = "src/main/java/com/hermosacartagena";
    private Map<String, ClassMetrics> classMetricsMap;
    private ProjectMetrics projectMetrics;
    
    public CodeMetricsAnalyzer() {
        this.classMetricsMap = new HashMap<>();
        this.projectMetrics = new ProjectMetrics();
    }
    
    /**
     * Analiza el proyecto completo y genera métricas
     */
    public void analyzeProject() {
        System.out.println("🔍 Iniciando análisis de métricas de código...");
        
        try {
            // 1. Encontrar todos los archivos Java
            List<File> javaFiles = findJavaFiles(SOURCE_PATH);
            System.out.println("📁 Encontrados " + javaFiles.size() + " archivos Java");
            
            // 2. Analizar cada archivo
            for (File javaFile : javaFiles) {
                analyzeJavaFile(javaFile);
            }
            
            // 3. Calcular métricas del proyecto
            calculateProjectMetrics();
            
            // 4. Generar reporte
            generateMetricsReport();
            
        } catch (IOException e) {
            System.err.println("❌ Error analizando archivos: " + e.getMessage());
        }
    }
    
    /**
     * Encuentra todos los archivos Java en el proyecto
     */
    private List<File> findJavaFiles(String path) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            return paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .map(Path::toFile)
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Analiza un archivo Java individual
     */
    private void analyzeJavaFile(File javaFile) {
        try {
            String content = Files.readString(javaFile.toPath());
            String className = extractClassName(javaFile.getName(), content);
            
            ClassMetrics metrics = new ClassMetrics(className);
            
            // Calcular LOC
            metrics.setLoc(calculateLOC(content));
            
            // Calcular WMC
            metrics.setWmc(calculateWMC(content));
            
            // Calcular CBO
            metrics.setCbo(calculateCBO(content));
            
            // Calcular Complejidad Ciclomática
            metrics.setCyclomaticComplexity(calculateCyclomaticComplexity(content));
            
            // Calcular Technical Debt
            metrics.setTechnicalDebt(calculateTechnicalDebt(metrics));
            
            classMetricsMap.put(className, metrics);
            
        } catch (IOException e) {
            System.err.println("❌ Error leyendo archivo " + javaFile.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Extrae el nombre de la clase del archivo
     */
    private String extractClassName(String fileName, String content) {
        String className = fileName.replace(".java", "");
        
        // Buscar declaración de clase
        String classPattern = "class\\s+(\\w+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(classPattern);
        java.util.regex.Matcher matcher = pattern.matcher(content);
        
        if (matcher.find()) {
            className = matcher.group(1);
        }
        
        return className;
    }
    
    /**
     * Calcula Lines of Code (LOC)
     */
    private int calculateLOC(String content) {
        String[] lines = content.split("\n");
        int loc = 0;
        
        for (String line : lines) {
            String trimmed = line.trim();
            // Excluir líneas vacías y comentarios
            if (!trimmed.isEmpty() && 
                !trimmed.startsWith("//") && 
                !trimmed.startsWith("/*") && 
                !trimmed.startsWith("*") && 
                !trimmed.startsWith("*/")) {
                loc++;
            }
        }
        
        return loc;
    }
    
    /**
     * Calcula Weighted Methods per Class (WMC)
     */
    private int calculateWMC(String content) {
        int wmc = 0;
        
        // Contar métodos y su complejidad
        String[] lines = content.split("\n");
        boolean inMethod = false;
        int methodComplexity = 1; // Complejidad base
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            // Detectar inicio de método
            if (trimmed.matches("(public|private|protected|default).*\\w+\\s*\\(.*\\)\\s*\\{")) {
                if (inMethod) {
                    wmc += methodComplexity;
                }
                inMethod = true;
                methodComplexity = 1;
            }
            
            // Calcular complejidad dentro del método
            if (inMethod) {
                methodComplexity += countComplexityOperators(trimmed);
            }
            
            // Detectar fin de método
            if (trimmed.equals("}") && inMethod) {
                wmc += methodComplexity;
                inMethod = false;
            }
        }
        
        return wmc;
    }
    
    /**
     * Cuenta operadores de complejidad
     */
    private int countComplexityOperators(String line) {
        int count = 0;
        
        String[] operators = {
            "if", "else", "while", "for", "switch", "case", "catch",
            "&&", "||", "?", ":", "==", "!=", "<", ">", "<=", ">="
        };
        
        for (String op : operators) {
            if (line.contains(op)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Calcula Coupling Between Objects (CBO)
     */
    private int calculateCBO(String content) {
        Set<String> dependencies = new HashSet<>();
        
        // Buscar imports
        String importPattern = "import\\s+(?:static\\s+)?[\\w.]+\\.([\\w]+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(importPattern);
        java.util.regex.Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            String dependency = matcher.group(1);
            if (!dependency.startsWith("java") && !dependency.startsWith("jakarta")) {
                dependencies.add(dependency);
            }
        }
        
        // Buscar referencias a otras clases
        String classReferencePattern = "\\b([A-Z][a-zA-Z0-9]*)\\b";
        pattern = java.util.regex.Pattern.compile(classReferencePattern);
        matcher = pattern.matcher(content);
        
        Set<String> localClasses = new HashSet<>();
        localClasses.add(extractClassName("", content));
        
        while (matcher.find()) {
            String ref = matcher.group(1);
            if (!localClasses.contains(ref) && !ref.equals("String") && !ref.equals("Integer") && !ref.equals("Long")) {
                dependencies.add(ref);
            }
        }
        
        return dependencies.size();
    }
    
    /**
     * Calcula Complejidad Ciclomática
     */
    private int calculateCyclomaticComplexity(String content) {
        int complexity = 1; // Complejidad base
        
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            String trimmed = line.trim();
            
            // Contar puntos de decisión
            if (trimmed.contains("if") || trimmed.contains("else") ||
                trimmed.contains("while") || trimmed.contains("for") ||
                trimmed.contains("switch") || trimmed.contains("case") ||
                trimmed.contains("catch") || trimmed.contains("&&") ||
                trimmed.contains("||")) {
                complexity++;
            }
        }
        
        return complexity;
    }
    
    /**
     * Calcula Technical Debt (estimado en horas)
     */
    private double calculateTechnicalDebt(ClassMetrics metrics) {
        double debt = 0;
        
        // Deuda por LOC (>500 líneas)
        if (metrics.getLoc() > 500) {
            debt += (metrics.getLoc() - 500) * 0.01; // 0.01 horas por línea extra
        }
        
        // Deuda por WMC (>10)
        if (metrics.getWmc() > 10) {
            debt += (metrics.getWmc() - 10) * 0.5; // 0.5 horas por punto extra
        }
        
        // Deuda por CBO (>8)
        if (metrics.getCbo() > 8) {
            debt += (metrics.getCbo() - 8) * 0.3; // 0.3 horas por acoplamiento extra
        }
        
        // Deuda por Complejidad Ciclomática (>10)
        if (metrics.getCyclomaticComplexity() > 10) {
            debt += (metrics.getCyclomaticComplexity() - 10) * 0.4; // 0.4 horas por punto extra
        }
        
        return debt;
    }
    
    /**
     * Calcula métricas del proyecto completo
     */
    private void calculateProjectMetrics() {
        projectMetrics.setTotalClasses(classMetricsMap.size());
        projectMetrics.setTotalLoc(classMetricsMap.values().stream()
            .mapToInt(ClassMetrics::getLoc).sum());
        projectMetrics.setAverageWmc(classMetricsMap.values().stream()
            .mapToInt(ClassMetrics::getWmc).average().orElse(0));
        projectMetrics.setAverageCbo(classMetricsMap.values().stream()
            .mapToInt(ClassMetrics::getCbo).average().orElse(0));
        projectMetrics.setTotalTechnicalDebt(classMetricsMap.values().stream()
            .mapToDouble(ClassMetrics::getTechnicalDebt).sum());
        projectMetrics.setAverageComplexity(classMetricsMap.values().stream()
            .mapToInt(ClassMetrics::getCyclomaticComplexity).average().orElse(0));
    }
    
    /**
     * Genera reporte de métricas
     */
    private void generateMetricsReport() {
        System.out.println("\n📊 REPORTE DE MÉTRICAS DE CÓDIGO");
        System.out.println("=" .repeat(50));
        
        // Métricas del proyecto
        System.out.println("\n🏗️ MÉTRICAS DEL PROYECTO:");
        System.out.println("Total Clases: " + projectMetrics.getTotalClasses());
        System.out.println("Total LOC: " + projectMetrics.getTotalLoc());
        System.out.println("WMC Promedio: " + String.format("%.2f", projectMetrics.getAverageWmc()));
        System.out.println("CBO Promedio: " + String.format("%.2f", projectMetrics.getAverageCbo()));
        System.out.println("Complejidad Promedio: " + String.format("%.2f", projectMetrics.getAverageComplexity()));
        System.out.println("Deuda Técnica Total: " + String.format("%.2f horas", projectMetrics.getTotalTechnicalDebt()));
        
        // Clases con mayor complejidad
        System.out.println("\n🔥 CLASES CON MAYOR COMPLEJIDAD:");
        classMetricsMap.entrySet().stream()
            .sorted(Map.Entry.<String, ClassMetrics>comparingByValue(
                Comparator.comparingInt(ClassMetrics::getWmc).reversed()))
            .limit(5)
            .forEach(entry -> {
                ClassMetrics metrics = entry.getValue();
                System.out.println(entry.getKey() + ":");
                System.out.println("  LOC: " + metrics.getLoc());
                System.out.println("  WMC: " + metrics.getWmc());
                System.out.println("  CBO: " + metrics.getCbo());
                System.out.println("  Complejidad: " + metrics.getCyclomaticComplexity());
                System.out.println("  Deuda Técnica: " + String.format("%.2f horas", metrics.getTechnicalDebt()));
                System.out.println();
            });
        
        // Alertas de calidad
        System.out.println("⚠️ ALERTAS DE CALIDAD:");
        classMetricsMap.values().stream()
            .filter(metrics -> metrics.getWmc() > 15 || metrics.getCbo() > 10 || metrics.getCyclomaticComplexity() > 15)
            .forEach(metrics -> {
                System.out.println("⚠️ " + metrics.getClassName() + " necesita refactorización:");
                if (metrics.getWmc() > 15) {
                    System.out.println("   - WMC muy alto: " + metrics.getWmc());
                }
                if (metrics.getCbo() > 10) {
                    System.out.println("   - Alto acoplamiento: " + metrics.getCbo());
                }
                if (metrics.getCyclomaticComplexity() > 15) {
                    System.out.println("   - Complejidad ciclomática alta: " + metrics.getCyclomaticComplexity());
                }
            });
    }
    
    public static void main(String[] args) {
        CodeMetricsAnalyzer analyzer = new CodeMetricsAnalyzer();
        analyzer.analyzeProject();
    }
    
    /**
     * Clase interna para métricas de clase
     */
    private static class ClassMetrics {
        private String className;
        private int loc;
        private int wmc;
        private int cbo;
        private int cyclomaticComplexity;
        private double technicalDebt;
        
        public ClassMetrics(String className) {
            this.className = className;
        }
        
        // Getters y Setters
        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }
        
        public int getLoc() { return loc; }
        public void setLoc(int loc) { this.loc = loc; }
        
        public int getWmc() { return wmc; }
        public void setWmc(int wmc) { this.wmc = wmc; }
        
        public int getCbo() { return cbo; }
        public void setCbo(int cbo) { this.cbo = cbo; }
        
        public int getCyclomaticComplexity() { return cyclomaticComplexity; }
        public void setCyclomaticComplexity(int cyclomaticComplexity) { this.cyclomaticComplexity = cyclomaticComplexity; }
        
        public double getTechnicalDebt() { return technicalDebt; }
        public void setTechnicalDebt(double technicalDebt) { this.technicalDebt = technicalDebt; }
    }
    
    /**
     * Clase interna para métricas del proyecto
     */
    private static class ProjectMetrics {
        private int totalClasses;
        private int totalLoc;
        private double averageWmc;
        private double averageCbo;
        private double averageComplexity;
        private double totalTechnicalDebt;
        
        // Getters y Setters
        public int getTotalClasses() { return totalClasses; }
        public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
        
        public int getTotalLoc() { return totalLoc; }
        public void setTotalLoc(int totalLoc) { this.totalLoc = totalLoc; }
        
        public double getAverageWmc() { return averageWmc; }
        public void setAverageWmc(double averageWmc) { this.averageWmc = averageWmc; }
        
        public double getAverageCbo() { return averageCbo; }
        public void setAverageCbo(double averageCbo) { this.averageCbo = averageCbo; }
        
        public double getAverageComplexity() { return averageComplexity; }
        public void setAverageComplexity(double averageComplexity) { this.averageComplexity = averageComplexity; }
        
        public double getTotalTechnicalDebt() { return totalTechnicalDebt; }
        public void setTotalTechnicalDebt(double totalTechnicalDebt) { this.totalTechnicalDebt = totalTechnicalDebt; }
    }
}
