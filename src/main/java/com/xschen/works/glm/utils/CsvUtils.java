package com.xschen.works.glm.utils;


import java.io.*;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Created by xschen on 1/5/2017.
 */
public class CsvUtils {
   public static final String quoteSplitPM = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";



   public static boolean csv(String filename, String cvsSplitBy, Function<String[], Boolean> onLineReady, Consumer<Exception> onFailed){
      BufferedReader br = null;
      String line = "";
      if(cvsSplitBy==null) cvsSplitBy = ",";

      boolean success = true;
      try {

         br = new BufferedReader(new StringReader(filename));
         while ((line = br.readLine()) != null) {

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

      } catch (FileNotFoundException e) {
         success = false;
         if(onFailed != null) onFailed.accept(e);
         else e.printStackTrace();
      } catch (IOException e) {
         success = false;
         if(onFailed != null) onFailed.accept(e);
         else e.printStackTrace();
      } finally {
         if (br != null) {
            try {
               br.close();
            } catch (IOException e) {
               success = false;
               if(onFailed != null) onFailed.accept(e);
               else e.printStackTrace();
            }
         }
      }

      return success;
   }
}
