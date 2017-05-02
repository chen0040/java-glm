package com.github.chen0040.glm.data;


import com.github.chen0040.glm.utils.NumberUtils;

import java.util.*;


/**
 * Created by xschen on 1/5/2017.
 */
public class DenseDataFrame implements DataFrame {

   private final List<DataRow> rows = new ArrayList<>();
   private final List<DataColumn> dataColumns = new ArrayList<>();

   @Override public int rowCount() {
      return rows.size();
   }


   @Override public DataRow row(int i) {
      return rows.get(i);
   }


   @Override public List<DataColumn> columns() {
      return dataColumns;
   }


   @Override public void inspect() {
      Map<String, Set<Integer>> counts = new HashMap<>();
      for(DataRow row : rows){
         List<String> keys = row.columnNames();
         for(String key: keys) {
            Set<Integer> set;

            if(counts.containsKey(key)){
               set = counts.get(key);
            } else {
               set = new HashSet<>();
               counts.put(key, set);
            }

            set.add(NumberUtils.toInt(row.get(key)));

         }
      }

      for(Map.Entry<String, Set<Integer>> entry : counts.entrySet()){
         Set<Integer> set = entry.getValue();
         DataColumn dataColumn = new DataColumn();
         dataColumn.setColumnName(entry.getKey());
         if(set.size() < rowCount() / 3) {
            dataColumn.setLevels(set);
         }
         dataColumns.add(dataColumn);
      }

      for(int i=0; i < rowCount(); ++i) {
         DataRow row = row(i);
         dataColumns.stream().filter(c -> !row.containsColumn(c.getColumnName())).forEach(c -> {
            row.put(c.getColumnName(), 0.0);
         });
      }
   }


   @Override public DataRow newRow() {
      return new DenseDataRow();
   }


   @Override public void addRow(DataRow row) {
      rows.add(row);
   }

}
