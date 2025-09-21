import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 性能分析工具类，用于测量和分析代码执行性能
 */
public class PerformanceAnalyzer {
    private static final Map<String, PerformanceData> performanceDataMap = new HashMap<>();
    private static final ThreadLocal<Map<String, Long>> threadLocalStartTimeMap = ThreadLocal.withInitial(HashMap::new);
    
    /**
     * 开始性能测量
     * @param operationName 操作名称，用于标识测量的操作
     */
    public static void start(String operationName) {
        long startTime = System.nanoTime();
        threadLocalStartTimeMap.get().put(operationName, startTime);
    }
    
    /**
     * 结束性能测量并记录数据
     * @param operationName 操作名称，必须与start方法中的名称对应
     * @return 操作执行时间（毫秒）
     */
    public static double end(String operationName) {
        Long startTime = threadLocalStartTimeMap.get().remove(operationName);
        if (startTime == null) {
            throw new IllegalArgumentException("Operation not started: " + operationName);
        }
        
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        double durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos) + 
                              (durationNanos % 1_000_000) / 1_000_000.0;
        
        // 记录性能数据
        PerformanceData data = performanceDataMap.computeIfAbsent(operationName, 
                               k -> new PerformanceData(operationName));
        data.addMeasurement(durationMillis);
        
        return durationMillis;
    }
    
    /**
     * 打印所有操作的性能统计信息
     */
    public static void printStatistics() {
        System.out.println("\n===== 性能分析统计 =====");
        for (PerformanceData data : performanceDataMap.values()) {
            System.out.printf("操作: %s\n", data.operationName);
            System.out.printf("  调用次数: %d\n", data.count);
            System.out.printf("  平均执行时间: %.3f ms\n", data.totalTime / data.count);
            System.out.printf("  最大执行时间: %.3f ms\n", data.maxTime);
            System.out.printf("  最小执行时间: %.3f ms\n", data.minTime);
            System.out.printf("  总执行时间: %.3f ms\n", data.totalTime);
            System.out.println("------------------------");
        }
        System.out.println("======================\n");
    }
    
    /**
     * 重置所有性能数据
     */
    public static void reset() {
        performanceDataMap.clear();
        threadLocalStartTimeMap.remove();
    }
    
    /**
     * 内部类，用于存储性能数据
     */
    private static class PerformanceData {
        private final String operationName;
        private long count = 0;
        private double totalTime = 0;
        private double maxTime = 0;
        private double minTime = Double.MAX_VALUE;
        
        public PerformanceData(String operationName) {
            this.operationName = operationName;
        }
        
        public void addMeasurement(double durationMillis) {
            count++;
            totalTime += durationMillis;
            maxTime = Math.max(maxTime, durationMillis);
            minTime = Math.min(minTime, durationMillis);
        }
    }
}