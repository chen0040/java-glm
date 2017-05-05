package com.github.chen0040.glm.utils;


import com.github.chen0040.glm.data.DataRow;
import com.github.chen0040.glm.data.BasicDataRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by xschen on 1/5/2017.
 */
public class CsvUtils {
   public static final String quoteSplitPM = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
   private static final Logger logger = LoggerFactory.getLogger(CsvUtils.class);

   public static double atof(String s)
   {
      double d = Double.valueOf(s).doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d))
      {
         System.err.print("NaN or Infinity in input\n");
         System.exit(1);
      }
      return(d);
   }

   public static int atoi(String s)
   {
      int value = 0;
      try {
         value = Integer.parseInt(s);
      }catch(NumberFormatException ex){
         value = 0;
      }
      return value;
   }

   public static List<DataRow> readHeartScale(InputStream inputStream){
      try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
         List<String> lines = reader.lines().collect(Collectors.toList());
         return lines.stream()
                 .filter(line -> !StringUtils.isEmpty(line))
                 .map(line -> {

                    StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

                    String label = st.nextToken();
                    HashMap<Integer, Double> row = new HashMap<>();

                    int m = st.countTokens() / 2;
                    for (int j = 0; j < m; j++) {
                       int index = atoi(st.nextToken()) - 1;
                       double value = atof(st.nextToken());

                       row.put(index, value);
                    }

                    DataRow dataRow = new BasicDataRow();

                    for(Map.Entry<Integer, Double> entry : row.entrySet()){
                       dataRow.setCell(entry.getKey() + "", entry.getValue());
                    }

                    if(label.equals("-1")){
                       dataRow.setTargetCell("label", -1.0);
                    } else {
                       dataRow.setTargetCell("label", 1.0);
                    }
                    return dataRow;
                 })
                 .collect(Collectors.toList());
      }
      catch (IOException e) {
         logger.error("Failed to read the heartScale data", e);
      }

      return new ArrayList<>();


   }

   public static boolean csv(InputStream inputStream, String cvsSplitBy, boolean skipFirstLine, Function<String[], Boolean> onLineReady, Consumer<Exception> onFailed){

      String line;
      if(cvsSplitBy==null) cvsSplitBy = ",";

      boolean success = true;
      boolean firstLine = true;
      try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
         while ((line = br.readLine()) != null) {

            if(firstLine) {
               firstLine = false;
               if(skipFirstLine) {
                  continue;
               }
            }

            line = line.trim();

            if(line.equals("")) continue;

            boolean containsQuote = false;
            if(line.contains("\"")){
               containsQuote = true;
               cvsSplitBy = cvsSplitBy + quoteSplitPM;
            }

            String[] values = line.split(cvsSplitBy);

            if(containsQuote){
               for(int i=0; i < values.length; ++i){
                  values[i] = StringUtils.stripQuote(values[i]);
               }
            }

            if(onLineReady != null){
               onLineReady.apply(values);
            }

         }

      }
      catch (IOException e) {
         success = false;
         if(onFailed != null) onFailed.accept(e);
         else e.printStackTrace();
      }

      return success;
   }
}
