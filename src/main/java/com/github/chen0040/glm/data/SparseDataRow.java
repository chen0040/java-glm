package com.github.chen0040.glm.data;


import java.util.List;


/**
 * Created by xschen on 2/5/2017.
 */
public class SparseDataRow implements DataRow {

   @Override public double target() {
      return 0;
   }


   @Override public double[] toArray() {
      return new double[0];
   }


   @Override public void put(String name, double value) {

   }


   @Override public List<String> columnNames() {
      return null;
   }


   @Override public List<String> targetColumnNames() {
      return null;
   }


   @Override public double get(String key) {
      return 0;
   }


   @Override public void target(String name, Double value) {

   }


   @Override public boolean containsColumn(String columnName) {
      return false;
   }
}
