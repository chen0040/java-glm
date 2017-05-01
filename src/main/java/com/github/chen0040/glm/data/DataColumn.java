package com.github.chen0040.glm.data;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by xschen on 29/4/2017.
 */
public class DataColumn implements Serializable {

   private int columnIndex;
   private String columnName;
   private Set<Integer> levels = new HashSet<>();

   public DataColumn makeCopy() {
      DataColumn clone = new DataColumn();
      clone.copy(this);
      return clone;
   }

   public void copy(DataColumn that) {
      this.columnIndex = that.columnIndex;
      this.columnName = that.columnName;
      this.levels.clear();
      this.levels.addAll(that.levels);
   }


   public void setColumnIndex(int key) {
      this.columnIndex = key;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setLevels(Set<Integer> set) {
      levels = set;
   }

   @Override
   public String toString(){
      return columnName;
   }
}
