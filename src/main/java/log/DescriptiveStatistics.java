/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;


/**
 *This class is to perform descriptive statistical analysis on a parameter.
 * @author aslanpour
 */
public class DescriptiveStatistics {

    public DescriptiveStatistics(double[] values) {
    }
    
    /**
     * Analyze the parameter in terms of descriptive statistics.
     * @param values
     * @param parameterName 
     */
    public static void analyze(double[] values, String parameterName){
        org.apache.commons.math3.stat.descriptive.DescriptiveStatistics descriptiveStat = new org.apache.commons.math3.stat.descriptive.DescriptiveStatistics(values);

        
        double count = values.length;
        double sum = descriptiveStat.getSum();
        double min = descriptiveStat.getMin();
        double max = descriptiveStat.getMax();
        double mean = descriptiveStat.getMean();
        double mode =  mode(values);
        double standardDeviation = descriptiveStat.getStandardDeviation();
        double skewness = descriptiveStat.getSkewness();
        double kurtosis = descriptiveStat.getKurtosis();
        double median = descriptiveStat.getPercentile(50);
        double percentile99 = descriptiveStat.getPercentile(99);
        
        
        System.out.println("Descriptive Statistics Results: \n");
        Log.printLine3(parameterName + "\n" +
                            "Count: " + count +
                            "\nSum: " + sum + 
                            "\nMin: " + min + 
                            "\nMax: " + max + 
                            "\nMean: " + mean + 
                            "\nMode: " + mode +
                            "\nStandard Deviation: " + standardDeviation + 
                            "\nSkewness: " + skewness + 
                            "\nKurtosis: " + kurtosis + 
                            "\nMedian: " + median +
                            "\n90th Percentile: " + percentile99);
    }
    
    /**
     * Find Mode value for a list of data
     * @param a
     * @return 
     */
    private static int mode(int a[]) {
    int maxValue = 0, maxCount = 0;

    for (int i = 0; i < a.length; ++i) {
        int count = 0;
        for (int j = 0; j < a.length; ++j) {
            if (a[j] == a[i]) ++count;
        }
        if (count > maxCount) {
            maxCount = count;
            maxValue = a[i];
        }
    }

    return maxValue;
    }
    
    /**
     * Find Mode value for a list of data
     * @param a
     * @return 
     */
    private static double mode(double a[]) {
        double maxValue = 0, maxCount = 0;

        for (int i = 0; i < a.length; ++i) {
            int count = 0;
            for (int j = 0; j < a.length; ++j) {
                if (a[j] == a[i]) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = a[i];
            }
        }
        return maxValue;
    }
}