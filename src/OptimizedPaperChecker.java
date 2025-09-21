import java.util.*;
import java.util.stream.Collectors;


/**
 * 性能优化的论文查重算法实现
 * 包含更高效的分词和相似度计算方法
 */
public class OptimizedPaperChecker {
    
    /**
     * 使用更高效的分词方法
     * 使用字符过滤和优化的循环来提高性能
     */
    public static List<String> optimizedTokenize(String text) {
        List<String> words = new ArrayList<>();
        int length = text.length();
        StringBuilder currentWord = new StringBuilder();
        
        // 直接遍历字符，避免正则表达式的开销
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (isWordCharacter(c)) {
                currentWord.append(c);
            } else {
                // 当遇到非单词字符时，处理当前收集的单词
                processWord(currentWord.toString(), words);
                currentWord.setLength(0); // 清空StringBuilder
            }
        }
        
        // 处理最后一个单词
        processWord(currentWord.toString(), words);
        
        return words;
    }
    
    /**
     * 判断字符是否为单词字符（中文字符、字母、数字）
     */
    private static boolean isWordCharacter(char c) {
        return (c >= '\u4e00' && c <= '\u9fa5') || // 中文字符
               (c >= 'a' && c <= 'z') ||          // 小写字母
               (c >= 'A' && c <= 'Z') ||          // 大写字母
               (c >= '0' && c <= '9');            // 数字
    }
    
    /**
     * 处理收集到的单词，进行分词并添加到结果列表
     */
    private static void processWord(String word, List<String> words) {
        if (word.isEmpty()) {
            return;
        }
        
        // 判断是否包含中文字符
        boolean containsChinese = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) >= '\u4e00' && word.charAt(i) <= '\u9fa5') {
                containsChinese = true;
                break;
            }
        }
        
        if (containsChinese) {
            // 对中文进行更高效的二元语法分词
            int wordLen = word.length();
            // 添加单字
            for (int i = 0; i < wordLen; i++) {
                words.add(word.substring(i, i + 1));
            }
            // 添加二元语法
            for (int i = 0; i < wordLen - 1; i++) {
                words.add(word.substring(i, i + 2));
            }
        } else {
            // 非中文直接添加
            words.add(word);
        }
    }
    
    /**
     * 优化的词频计算方法
     */
    public static Map<String, Integer> optimizedCalculateTF(List<String> words) {
        Map<String, Integer> tfMap = new HashMap<>(words.size() / 2); // 预分配容量以减少扩容
        for (String word : words) {
            tfMap.compute(word, (k, v) -> v == null ? 1 : v + 1);
        }
        return tfMap;
    }
    
    /**
     * 优化的余弦相似度计算方法
     * 使用更高效的集合操作和数学计算
     */
    public static double optimizedCalculateCosineSimilarity(Map<String, Integer> tf1, Map<String, Integer> tf2) {
        // 选择较小的集合进行迭代，减少循环次数
        Map<String, Integer> smallerMap = tf1.size() <= tf2.size() ? tf1 : tf2;
        Map<String, Integer> largerMap = tf1.size() <= tf2.size() ? tf2 : tf1;
        
        // 计算点积
        double dotProduct = 0;
        for (Map.Entry<String, Integer> entry : smallerMap.entrySet()) {
            String word = entry.getKey();
            Integer count2 = largerMap.get(word);
            if (count2 != null) {
                dotProduct += entry.getValue() * count2;
            }
        }
        
        // 计算向量长度（预先计算，避免重复计算）
        double magnitude1 = calculateVectorMagnitude(tf1);
        double magnitude2 = calculateVectorMagnitude(tf2);
        
        // 计算余弦相似度
        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0;
        }
        return dotProduct / (magnitude1 * magnitude2);
    }
    
    /**
     * 计算向量的模长
     */
    private static double calculateVectorMagnitude(Map<String, Integer> tfMap) {
        double magnitude = 0;
        for (int count : tfMap.values()) {
            magnitude += count * count;
        }
        return Math.sqrt(magnitude);
    }
    
    /**
     * 优化的整体相似度计算方法
     */
    public static double optimizedCalculateSimilarity(String original, String plagiarized) {
        List<String> originalWords = optimizedTokenize(original);
        List<String> plagiarizedWords = optimizedTokenize(plagiarized);
        
        Map<String, Integer> originalTF = optimizedCalculateTF(originalWords);
        Map<String, Integer> plagiarizedTF = optimizedCalculateTF(plagiarizedWords);
        
        double similarity = optimizedCalculateCosineSimilarity(originalTF, plagiarizedTF);
        return similarity * 100; // 转换为百分比
    }
    
    /**
     * 基于SimHash算法的文本相似度计算
     * 适用于大文本的高效相似度计算
     */
    public static double calculateSimilarityWithSimHash(String original, String plagiarized) {
        long originalHash = generateSimHash(original);
        long plagiarizedHash = generateSimHash(plagiarized);
        
        // 计算海明距离
        int hammingDistance = countBits(originalHash ^ plagiarizedHash);
        
        // 转换为相似度百分比（基于64位SimHash）
        return (1 - hammingDistance / 64.0) * 100;
    }
    
    /**
     * 生成文本的SimHash值
     */
    private static long generateSimHash(String text) {
        // 简化的SimHash实现
        List<String> tokens = optimizedTokenize(text);
        int[] vector = new int[64]; // 64位SimHash
        
        // 对每个token生成哈希并更新向量
        for (String token : tokens) {
            long hash = token.hashCode();
            for (int i = 0; i < 64; i++) {
                vector[i] += ((hash >> i) & 1) == 1 ? 1 : -1;
            }
        }
        
        // 将向量转换为哈希值
        long simHash = 0;
        for (int i = 0; i < 64; i++) {
            if (vector[i] > 0) {
                simHash |= 1L << i;
            }
        }
        
        return simHash;
    }
    
    /**
     * 计算二进制中1的个数（汉明重量）
     */
    private static int countBits(long value) {
        int count = 0;
        while (value != 0) {
            count++;
            value &= value - 1; // 清除最低位的1
        }
        return count;
    }
}