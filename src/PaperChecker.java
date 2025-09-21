import java.io.*;
import java.util.*;
import java.text.DecimalFormat;


public class PaperChecker {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -jar main.jar [原文文件] [抄袭版论文的文件] [答案文件]");
            System.exit(1);
        }

        String originalFilePath = args[0];
        String plagiarizedFilePath = args[1];
        String outputFilePath = args[2];

        try {
            // 开始测量整个程序执行时间
            PerformanceAnalyzer.start("整个程序执行");
            
            String originalText = readFile(originalFilePath);
            String plagiarizedText = readFile(plagiarizedFilePath);
            double similarity = calculateSimilarity(originalText, plagiarizedText);
            writeFile(outputFilePath, similarity);
            
            // 结束测量并打印性能统计
            PerformanceAnalyzer.end("整个程序执行");
            PerformanceAnalyzer.printStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String readFile(String filePath) throws IOException {
        PerformanceAnalyzer.start("文件读取");
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" ");
            }
        }
        PerformanceAnalyzer.end("文件读取");
        return content.toString().trim();
    }

    static void writeFile(String filePath, double similarity) throws IOException {
        PerformanceAnalyzer.start("文件写入");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            DecimalFormat df = new DecimalFormat("0.00");
            bw.write(df.format(similarity));
        }
        PerformanceAnalyzer.end("文件写入");
    }

    static double calculateSimilarity(String original, String plagiarized) {
        // 默认使用余弦相似度算法
        return calculateSimilarityWithCosine(original, plagiarized);
    }
    
    // 使用余弦相似度的算法
    static double calculateSimilarityWithCosine(String original, String plagiarized) {
        PerformanceAnalyzer.start("余弦相似度计算");
        
        // 分词
        PerformanceAnalyzer.start("分词处理");
        List<String> originalWords = tokenize(original);
        List<String> plagiarizedWords = tokenize(plagiarized);
        PerformanceAnalyzer.end("分词处理");
        
        // 计算TF
        PerformanceAnalyzer.start("词频计算");
        Map<String, Integer> originalTF = calculateTF(originalWords);
        Map<String, Integer> plagiarizedTF = calculateTF(plagiarizedWords);
        PerformanceAnalyzer.end("词频计算");
        
        // 计算余弦相似度
        PerformanceAnalyzer.start("余弦相似度核心计算");
        double similarity = calculateCosineSimilarity(originalTF, plagiarizedTF);
        PerformanceAnalyzer.end("余弦相似度核心计算");
        
        PerformanceAnalyzer.end("余弦相似度计算");
        return similarity * 100; // 转换为百分比
    }
    
    // 使用简单词频匹配的算法
    static double calculateSimilarityWithSimpleMatch(String original, String plagiarized) {
        PerformanceAnalyzer.start("简单匹配算法");
        Set<String> originalWords = new HashSet<>(Arrays.asList(original.split("\\s+")));
        Set<String> plagiarizedWords = new HashSet<>(Arrays.asList(plagiarized.split("\\s+")));

        originalWords.retainAll(plagiarizedWords);

        double similarity = (double) originalWords.size() / Math.max(original.split("\\s+").length, plagiarized.split("\\s+").length);
        PerformanceAnalyzer.end("简单匹配算法");
        return similarity * 100; // 转换为百分比
    }
    
    // 使用Jaccard相似度的算法
    static double calculateSimilarityWithJaccard(String original, String plagiarized) {
        PerformanceAnalyzer.start("Jaccard相似度算法");
        Set<String> originalWords = new HashSet<>(Arrays.asList(original.split("\\s+")));
        Set<String> plagiarizedWords = new HashSet<>(Arrays.asList(plagiarized.split("\\s+")));
        
        Set<String> intersection = new HashSet<>(originalWords);
        intersection.retainAll(plagiarizedWords);
        
        Set<String> union = new HashSet<>(originalWords);
        union.addAll(plagiarizedWords);
        
        double jaccardIndex = (double) intersection.size() / union.size();
        PerformanceAnalyzer.end("Jaccard相似度算法");
        return jaccardIndex * 100; // 转换为百分比
    }
    
    // 分词函数，支持更细致的中文分词
    static List<String> tokenize(String text) {
        List<String> words = new ArrayList<>();
        // 简单分词，将文本按空格、标点符号等分割
        String[] tokens = text.split("[^\u4e00-\u9fa5a-zA-Z0-9]+");
        for (String token : tokens) {
            if (!token.isEmpty()) {
                // 对于中文文本，进一步进行单字或双字分词
                if (isChineseText(token)) {
                    // 双字分词（二元语法）
                    for (int i = 0; i < token.length() - 1; i++) {
                        words.add(token.substring(i, i + 2));
                    }
                    // 保留单字
                    for (int i = 0; i < token.length(); i++) {
                        words.add(token.substring(i, i + 1));
                    }
                } else {
                    words.add(token);
                }
            }
        }
        return words;
    }
    
    // 判断是否为中文文本
    static boolean isChineseText(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) >= '\u4e00' && text.charAt(i) <= '\u9fa5') {
                return true;
            }
        }
        return false;
    }
    
    // 计算词频
    static Map<String, Integer> calculateTF(List<String> words) {
        Map<String, Integer> tfMap = new HashMap<>();
        for (String word : words) {
            tfMap.put(word, tfMap.getOrDefault(word, 0) + 1);
        }
        return tfMap;
    }
    
    // 计算余弦相似度
    static double calculateCosineSimilarity(Map<String, Integer> tf1, Map<String, Integer> tf2) {
        // 计算点积
        double dotProduct = 0;
        for (String word : tf1.keySet()) {
            if (tf2.containsKey(word)) {
                dotProduct += tf1.get(word) * tf2.get(word);
            }
        }
        
        // 计算向量长度
        double magnitude1 = 0;
        for (int count : tf1.values()) {
            magnitude1 += count * count;
        }
        magnitude1 = Math.sqrt(magnitude1);
        
        double magnitude2 = 0;
        for (int count : tf2.values()) {
            magnitude2 += count * count;
        }
        magnitude2 = Math.sqrt(magnitude2);
        
        // 计算余弦相似度
        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0;
        }
        return dotProduct / (magnitude1 * magnitude2);
    }
}