import java.util.*;

/**
 * 主程序类，用于运行性能测试和验证算法功能
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("===== 论文查重算法测试 =====");
        
        // 测试基本功能
        testBasicFunctionality();
        
        // 测试多种算法
        testDifferentAlgorithms();
        
        // 性能测试
        testPerformance();
        
        // 优化算法与原始算法性能比较
        compareOptimizedAndOriginal();
        
        // 大规模数据测试
        testLargeScaleData();
        
        System.out.println("===== 测试完成 =====");
    }
    
    /**
     * 测试基本功能
     */
    private static void testBasicFunctionality() {
        System.out.println("\n--- 基本功能测试 ---");
        
        String original = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String plagiarized = "今天是周天，天气晴朗，我晚上要去看电影。";
        String different = "这是一段完全不同的文本内容，与原文没有任何关系。";
        
        // 测试相同文本
        double sameSimilarity = PaperChecker.calculateSimilarity(original, original);
        System.out.printf("相同文本相似度: %.2f%%\n", sameSimilarity);
        
        // 测试相似文本
        double similarSimilarity = PaperChecker.calculateSimilarity(original, plagiarized);
        System.out.printf("相似文本相似度: %.2f%%\n", similarSimilarity);
        
        // 测试不同文本
        double differentSimilarity = PaperChecker.calculateSimilarity(original, different);
        System.out.printf("不同文本相似度: %.2f%%\n", differentSimilarity);
    }
    
    /**
     * 测试多种算法
     */
    private static void testDifferentAlgorithms() {
        System.out.println("\n--- 多种算法测试 ---");
        
        String original = "今天是星期天，天气晴，今天晚上我要去看电影。";
        String plagiarized = "今天是周天，天气晴朗，我晚上要去看电影。";
        
        double cosineSimilarity = PaperChecker.calculateSimilarityWithCosine(original, plagiarized);
        double simpleMatchSimilarity = PaperChecker.calculateSimilarityWithSimpleMatch(original, plagiarized);
        double jaccardSimilarity = PaperChecker.calculateSimilarityWithJaccard(original, plagiarized);
        double simHashSimilarity = OptimizedPaperChecker.calculateSimilarityWithSimHash(original, plagiarized);
        
        System.out.printf("余弦相似度: %.2f%%\n", cosineSimilarity);
        System.out.printf("简单匹配相似度: %.2f%%\n", simpleMatchSimilarity);
        System.out.printf("Jaccard相似度: %.2f%%\n", jaccardSimilarity);
        System.out.printf("SimHash相似度: %.2f%%\n", simHashSimilarity);
    }
    
    /**
     * 性能测试
     */
    private static void testPerformance() {
        System.out.println("\n--- 性能测试 ---");
        
        // 创建中等长度的测试文本
        StringBuilder originalBuilder = new StringBuilder();
        StringBuilder plagiarizedBuilder = new StringBuilder();
        
        for (int i = 0; i < 1000; i++) {
            originalBuilder.append("今天是星期天，天气晴，今天晚上我要去看电影。");
            plagiarizedBuilder.append(i % 5 == 0 ? "今天天气不错，适合外出活动。" : "今天是周天，天气晴朗，我晚上要去看电影。");
        }
        
        String original = originalBuilder.toString();
        String plagiarized = plagiarizedBuilder.toString();
        
        // 运行主程序并查看性能分析结果
        try {
            PerformanceAnalyzer.reset();
            PaperChecker.main(new String[]{"orig.txt", "orig_add.txt", "ans.txt"});
        } catch (Exception e) {
            System.out.println("主程序运行出错: " + e.getMessage());
        }
    }
    
    /**
     * 比较优化算法与原始算法
     */
    private static void compareOptimizedAndOriginal() {
        System.out.println("\n--- 优化算法与原始算法比较 ---");
        
        // 创建较长的测试文本
        StringBuilder originalBuilder = new StringBuilder();
        StringBuilder plagiarizedBuilder = new StringBuilder();
        
        for (int i = 0; i < 2000; i++) {
            originalBuilder.append("今天是星期天，天气晴，今天晚上我要去看电影。");
            plagiarizedBuilder.append(i % 5 == 0 ? "今天天气不错，适合外出活动。" : "今天是周天，天气晴朗，我晚上要去看电影。");
        }
        
        String original = originalBuilder.toString();
        String plagiarized = plagiarizedBuilder.toString();
        
        // 测试原始算法性能
        long startTime = System.currentTimeMillis();
        double originalResult = PaperChecker.calculateSimilarityWithCosine(original, plagiarized);
        long originalTime = System.currentTimeMillis() - startTime;
        
        // 测试优化算法性能
        startTime = System.currentTimeMillis();
        double optimizedResult = OptimizedPaperChecker.optimizedCalculateSimilarity(original, plagiarized);
        long optimizedTime = System.currentTimeMillis() - startTime;


        // 测试SimHash算法性能
        startTime = System.currentTimeMillis();
        double simHashResult = OptimizedPaperChecker.calculateSimilarityWithSimHash(original, plagiarized);
        long simHashTime = System.currentTimeMillis() - startTime;
        
        System.out.println("===== 算法性能比较 ====");
        System.out.printf("原始余弦相似度算法: %d ms, 结果: %.2f%%\n", originalTime, originalResult);
        System.out.printf("优化余弦相似度算法: %d ms, 结果: %.2f%%\n", optimizedTime, optimizedResult);
        System.out.printf("SimHash算法: %d ms, 结果: %.2f%%\n", simHashTime, simHashResult);
        
        // 计算性能提升
        double improvement = (double)(originalTime - optimizedTime) / originalTime * 100;
        System.out.printf("优化算法性能提升: %.2f%%\n", improvement);
    }
    
    /**
     * 大规模数据测试
     */
    private static void testLargeScaleData() {
        System.out.println("\n--- 大规模数据测试 ---");
        
        // 创建大规模测试数据
        StringBuilder originalBuilder = new StringBuilder();
        StringBuilder plagiarizedBuilder = new StringBuilder();
        
        for (int i = 0; i < 10000; i++) {
            originalBuilder.append("这是一段用于大规模测试的文本内容，包含各种不同的词汇和句子结构。");
            if (i % 7 != 0) { // 约70%的内容保持一致
                plagiarizedBuilder.append("这是一段用于大规模测试的文本内容，包含各种不同的词汇和句子结构。");
            } else {
                plagiarizedBuilder.append("这是替换后的不同内容，用于测试算法在大规模数据下的表现和准确性。");
            }
        }
        
        String original = originalBuilder.toString();
        String plagiarized = plagiarizedBuilder.toString();
        
        // 测试优化算法
        long startTime = System.currentTimeMillis();
        double optimizedResult = OptimizedPaperChecker.optimizedCalculateSimilarity(original, plagiarized);
        long optimizedTime = System.currentTimeMillis() - startTime;
        
        // 测试SimHash算法
        startTime = System.currentTimeMillis();
        double simHashResult = OptimizedPaperChecker.calculateSimilarityWithSimHash(original, plagiarized);
        long simHashTime = System.currentTimeMillis() - startTime;
        
        System.out.println("===== 大规模数据测试结果 ====");
        System.out.printf("优化余弦相似度算法: %d ms, 结果: %.2f%%\n", optimizedTime, optimizedResult);
        System.out.printf("SimHash算法: %d ms, 结果: %.2f%%\n", simHashTime, simHashResult);
        System.out.printf("预期相似度约为70%%，因为约30%%的内容被替换\n");
    }
}