package com.github.chen0040.glm.data;


import com.github.chen0040.glm.enums.DataFileType;
import com.github.chen0040.glm.utils.CsvUtils;
import com.github.chen0040.glm.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Created by xschen on 1/5/2017.
 */



public class DataFrameBuilder {

   public interface DataFrameQueryBuilder {
      default DataFrameQueryBuilder selectColumn(String columName, int columnIndex) {
         return selectColumn(columName, columnIndex, StringUtils::parseDouble);
      }
      DataFrameQueryBuilder selectColumn(String columName, int columnIndex, Function<String, Double> columnTransformer);
      default DataFrameQueryBuilder selectTargetColumn(String columnName, int columnIndex) {
         return selectTargetColumn(columnName, columnIndex, StringUtils::parseDouble);
      }
      DataFrameQueryBuilder selectTargetColumn(String columName, int columnIndex, Function<String, Double> columnTransformer);
      DataFrame build();
   }

   public interface SourceBuilder {
      DataFrameQueryBuilder csv(InputStream inputStream, String splitter);
      DataFrameQueryBuilder heartScale(InputStream inputStream);
   }

   private static class DataFrameColumn {
      private int index;
      private Function<String, Double> transformer;
      private String columnName;

      public DataFrameColumn(String columnName, int index, Function<String, Double> transformer){
         this.columnName = columnName;
         this.index = index;
         this.transformer = transformer;
      }
   }

   private static class DataFrameBuilderX implements SourceBuilder, DataFrameQueryBuilder {

      private final List<DataFrameColumn> columns = new ArrayList<>();
      private final List<DataFrameColumn> targetColumns = new ArrayList<>();
      private InputStream dataInputStream;
      private String csvSplitter;
      private DataFileType fileType;

      private static final Logger logger = LoggerFactory.getLogger(DataFrameBuilderX.class);

      @Override public DataFrameQueryBuilder selectColumn(String columnName, int columnIndex, Function<String, Double> columnTransformer) {
         columns.add(new DataFrameColumn(columnName, columnIndex, columnTransformer));
         return this;
      }


      @Override public DataFrameQueryBuilder selectTargetColumn(String columnName, int columnIndex, Function<String, Double> columnTransformer) {
         targetColumns.add(new DataFrameColumn(columnName, columnIndex, columnTransformer));
         return this;
      }


      @Override public DataFrame build() {
         final DenseDataFrame dataFrame = new DenseDataFrame();

         if(fileType == DataFileType.Csv) {
            CsvUtils.csv(dataInputStream, csvSplitter, (words) -> {
               DataRow row = dataFrame.newRow();
               for (int i = 0; i < words.length; ++i) {
                  for (DataFrameColumn c : columns) {
                     if (c.index == i) {
                        row.put(c.columnName, c.transformer.apply(words[i]));
                     }
                  }
                  for (DataFrameColumn c : targetColumns) {
                     if (c.index == i) {
                        row.target(c.columnName, c.transformer.apply(words[i]));
                     }
                  }
               }
               dataFrame.addRow(row);
               return true;
            }, (e) -> {
               logger.error("Failed to read csv file", e);
            });
         } else {
            List<DataRow> rows = CsvUtils.readHeartScale(dataInputStream);
            for(DataRow row : rows){
               dataFrame.addRow(row);
            }
         }

         dataFrame.inspect();

         return dataFrame;
      }


      @Override public DataFrameQueryBuilder csv(InputStream inputStream, String splitter) {
         dataInputStream = inputStream;
         csvSplitter = splitter;
         fileType = DataFileType.Csv;
         return this;
      }


      @Override public DataFrameQueryBuilder heartScale(InputStream inputStream) {
         dataInputStream = inputStream;
         fileType = DataFileType.HeartScale;
         return this;
      }
   }

   public static DataFrameQueryBuilder csv(String path, String splitter) throws FileNotFoundException {
      return new DataFrameBuilderX().csv(new FileInputStream(path), splitter);
   }

   public static DataFrameQueryBuilder heartScale(String path) throws  FileNotFoundException {
      return new DataFrameBuilderX().heartScale(new FileInputStream(path));
   }

   public static DataFrameQueryBuilder csv(InputStream inputStream, String splitter) {
      return new DataFrameBuilderX().csv(inputStream, splitter);
   }

   public static DataFrameQueryBuilder heartScale(InputStream inputStream) {
      return new DataFrameBuilderX().heartScale(inputStream);
   }
}
