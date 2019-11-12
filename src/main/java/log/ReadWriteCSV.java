/*
 * This project analyzes the Milano Weather Station Data. 
 * This data contains the information about the temperature and relative humidity gathered 
 * during around 2 months in 2013 in Milano, Lambrate street.
 * 1) We 1) analyze the distribution of the data (temperature and relative humidity) 
 * in terms of its centrality and shape, 
 * 2) try to find a relationship between temperature and relative humidity using regression, 
 * and 3) propose a prediction method for this purpose based on artificial neural networks.
 */
package log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author aslanpour
 */
public class ReadWriteCSV {
   
    /**
     * Read a CSV file
     * @param filePath
     * @param file
     * @param labeled
     * @return 
     */
    public static ArrayList readCSV(String filePath,String file, boolean labeled){
        ArrayList dataList = new ArrayList<>();
        BufferedReader csvReader;
        try {
            csvReader = new BufferedReader(new FileReader(filePath + file));
            try {
                String line;
                if (labeled)
                    csvReader.readLine();
                while ( (line = csvReader.readLine()) != null ) {
                    String[] rowStr = line.split(",");
                    
                    ArrayList<Double> row = new ArrayList<Double>();
                    for (int i =0; i < rowStr.length;i++){
                        row.add(Double.valueOf(rowStr[i]));
                    }
                    
                    dataList.add(row);
                } 
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            e.printStackTrace(); 
        }
        System.out.println("The file: " + file + " was read");
        return dataList;

    }
    
    /**
     * Write an array of an item to a CSV file
     * @param data
     * @param filePath
     * @param fileName
     * @throws IOException 
     */
    public static void writeCSV( double[] data, String filePath,String fileName) throws IOException{
        FileWriter csvWriter = new FileWriter(filePath + fileName);
                
        for (int i = 0; i < data.length; i++) {
           csvWriter.append(data[i] + "\n");
        }
        
        csvWriter.flush();
        csvWriter.close();
    }
    
    public static void writeCSV(ArrayList dataList, String filePath, String fileName) throws IOException{
        FileWriter csvWriter = new FileWriter(filePath + fileName);
                
        for (int i = 0; i < dataList.size(); i++) {
            ArrayList<Double> row = (ArrayList<Double>)dataList.get(i);
            
           for (int j = 0; j< row.size(); j++){
               if (j <row.size() - 1)
                csvWriter.append(row.get(j) + ",");
               else
                   csvWriter.append(String.valueOf(row.get(j)));
           }
           csvWriter.append("\n");
        }
        
        csvWriter.flush();
        csvWriter.close();
    }
    
    /**
     * Write an array of an item to a CSV file
     * @param data
     * @param filePath
     * @param fileName
     * @param lable
     * @throws IOException 
     */
    public static void writeCSV(double[] data, String filePath, String fileName, String lable) throws IOException{
        FileWriter csvWriter = new FileWriter(filePath + fileName);
        csvWriter.append(lable + "\n");
        
        for (int i = 0; i < data.length; i++) {
           csvWriter.append(data[i] + "\n");
        }
        
        csvWriter.flush();
        csvWriter.close();
    }
    
    /**
     * Pick an item array from a dataset
     * @param dataList
     * @param index
     * @return 
     */
    public static double[] pickAnItemList(ArrayList dataList, int index){
        double[] itemList = new double[dataList.size()];
        
        for(int i = 0; i < dataList.size();i++){
            ArrayList<Double> row = (ArrayList<Double>)dataList.get(i);
            itemList[i] = row.get(index);
        }
        
        return itemList;
    }
}
