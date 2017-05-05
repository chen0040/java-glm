package com.github.chen0040.glm.data;


import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by xschen on 1/5/2017.
 */
public class BasicDataFrame implements DataFrame {

   private final List<DataRow> rows = new ArrayList<>();
   private final List<InputDataColumn> inputDataColumns = new ArrayList<>();
   private boolean locked = false;

   @Override public int rowCount() {
      return rows.size();
   }


   @Override public DataRow row(int i) {
      return rows.get(i);
   }


   @Override public List<InputDataColumn> inputColumns() {
      return inputDataColumns;
   }

   @Override public void unlock(){
      locked = false;
   }

   @Override
   public boolean isLocked() {
      return locked;
   }


   @Override public void lock() {
      Map<String, Set<Double>> counts = new HashMap<>();
      for(DataRow row : rows){
         List<String> keys = row.getColumnNames();
         for(String key: keys) {
            Set<Double> set;

            if(counts.containsKey(key)){
               set = counts.get(key);
            } else {
               set = new HashSet<>();
               counts.put(key, set);
            }

            set.add(row.getCell(key));
         }
      }

      for(Map.Entry<String, Set<Double>> entry : counts.entrySet()){
         Set<Double> set = entry.getValue();
         InputDataColumn inputDataColumn = new InputDataColumn();
         inputDataColumn.setColumnName(entry.getKey());
         if(set.size() < rowCount() / 3) {
            inputDataColumn.setLevels(set);
         }
         inputDataColumns.add(inputDataColumn);
      }

      List<String> inputColumns = inputDataColumns.stream().map(InputDataColumn::getColumnName).collect(Collectors.toList());
      inputColumns.sort(String::compareTo);
      for(int i=0; i < rowCount(); ++i) {
         DataRow row = row(i);
         row.setColumnNames(inputColumns);
      }

      locked = true;
   }


   @Override public DataRow newRow() {
      return new BasicDataRow();
   }


   @Override public void addRow(DataRow row) {
      if(locked) {
         throw new RuntimeException("Data frame is currently locked, please unlock first");
      }
      rows.add(row);
   }

}
