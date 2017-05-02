package com.github.chen0040.glm.data;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by xschen on 29/4/2017.
 */
public class DataColumn implements Serializable {

   private int sourceColumnIndex;
   private String columnName;
   private Set<Integer> levels = new HashSet<>();

   public DataColumn makeCopy() {
      DataColumn clone = new DataColumn();
      clone.copy(this);
      return clone;
   }

   public void copy(DataColumn that) {
      this.sourceColumnIndex = that.sourceColumnIndex;
      this.columnName = that.columnName;
      this.levels.clear();
      this.levels.addAll(that.levels);
   }


   public void setSourceColumnIndex(int key) {
      this.sourceColumnIndex = key;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public String getColumnName() {
      return columnName;
   }

   public void setLevels(Set<Integer> set) {
      levels = set;
   }

   @Override
   public String toString(){
      return columnName;
   }


   public String summary() {
      StringBuilder sb = new StringBuilder();
      sb.append(columnName).append(":discrete=").append(levels.size());
      return sb.toString();
   }
}
