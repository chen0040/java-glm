package com.github.chen0040.glm.data;


import java.util.List;


/**
 * Created by xschen on 28/4/2017.
 */
public interface DataRow {
   double target();

   double[] toArray();

   void put(String name, double value);

   List<String> columnNames();

   List<String> targetColumnNames();

   double get(String key);

   void target(String name, Double value);

   boolean containsColumn(String columnName);
}
