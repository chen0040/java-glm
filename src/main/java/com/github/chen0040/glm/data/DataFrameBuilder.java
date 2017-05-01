package com.github.chen0040.glm.data;


import com.github.chen0040.glm.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Created by xschen on 1/5/2017.
 */



public class DataFrameBuilder {

   public interface DataFrameQueryBuilder {
      default DataFrameQueryBuilder selectColumn(int columnIndex) {
         return selectColumn(columnIndex, StringUtils::parseDouble);
      }
      DataFrameQueryBuilder selectColumn(int columnIndex, Function<String, Double> columnTransformer);
      default DataFrameQueryBuilder selectTargetColumn(int columnIndex) {
         return selectTargetColumn(columnIndex, StringUtils::parseDouble);
      }
      DataFrameQueryBuilder selectTargetColumn(int columnIndex, Function<String, Double> columnTransformer);
      DataFrame build();
   }

   public interface SourceBuilder {
      DataFrameQueryBuilder csv(String path);
   }

   private static class DataFrameColumn {
      private int index;
      private Function<String, Double> transformer;

      public DataFrameColumn(int index, Function<String, Double> transformer){
         this.index = index;
         this.transformer = transformer;
      }
   }

   private static class DataFrameBuilderX implements SourceBuilder, DataFrameQueryBuilder {

      private final List<DataFrameColumn> columns = new ArrayList<>();
      private final List<DataFrameColumn> targetColumns = new ArrayList<>();
      private String dataSource;

      @Override public DataFrameQueryBuilder selectColumn(int columnIndex, Function<String, Double> columnTransformer) {
         columns.add(new DataFrameColumn(columnIndex, columnTransformer));
         return this;
      }


      @Override public DataFrameQueryBuilder selectTargetColumn(int columnIndex, Function<String, Double> columnTransformer) {
         targetColumns.add(new DataFrameColumn(columnIndex, columnTransformer));
         return this;
      }


      @Override public DataFrame build() {
         return null;
      }


      @Override public DataFrameQueryBuilder csv(String path) {
         dataSource = path;
         return this;
      }
   }

   public static DataFrameQueryBuilder csv(String path) {
      return new DataFrameBuilderX().csv(path);
   }
}
