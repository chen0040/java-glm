package com.github.chen0040.glm.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by xschen on 1/5/2017.
 */
public class DenseDataRow implements DataRow {

   private final Map<String, Double> targets = new HashMap<>();
   private final Map<String, Double> values = new HashMap<>();
   private List<String> columns = new ArrayList<>();
   private List<String> targetColumns = new ArrayList<>();

   @Override public double target() {
      if(targetColumns.isEmpty()){
         buildTargetColumns();
      }
      return targets.get(targetColumns.get(0));
   }



   @Override
   public void target(String name, Double value) {
      targets.put(name, value);
   }


   @Override public double[] toArray() {
      if(columns.isEmpty()){
         buildColumns();
      }
      double[] result = new double[columns.size()];
      for(int i=0; i < columns.size(); ++i) {
         result[i] = values.get(columns.get(i));
      }
      return result;
   }

   private void buildColumns(){
      List<String> cols = values.keySet().stream().collect(Collectors.toList());
      cols.sort(String::compareTo);
      columns.addAll(cols);
   }

   private void buildTargetColumns(){
      List<String> cols = targets.keySet().stream().collect(Collectors.toList());
      cols.sort(String::compareTo);
      targetColumns.addAll(cols);
   }


   @Override public void put(String name, double value) {
      values.put(name, value);
   }


   @Override public List<String> columnNames() {
      if(columns.isEmpty()) {
         buildColumns();
      }
      return columns;
   }

   @Override
   public List<String> targetColumnNames() {
      if(targetColumns.isEmpty()){
         buildTargetColumns();
      }
      return targetColumns;
   }


   @Override public double get(String key) {
      return values.get(key);
   }

   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder();
      List<String> keys = columnNames();
      for(int i=0; i < keys.size(); ++i){
         if(i != 0){
            sb.append(", ");
         }
         sb.append(keys.get(i)).append(":").append(values.get(keys.get(i)));
      }
      sb.append(" => ");
      keys = targetColumnNames();
      for(int i=0; i < keys.size(); ++i){
         if(i != 0){
            sb.append(", ");
         }
         sb.append(keys.get(i)).append(":").append(targets.get(keys.get(i)));
      }
      return sb.toString();
   }
}
