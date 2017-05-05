package com.github.chen0040.glm.data;


import com.github.chen0040.glm.enums.DataFileType;
import com.github.chen0040.glm.utils.CsvUtils;
import com.github.chen0040.glm.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * Created by xschen on 1/5/2017.
 */



public class DataQuery {

   public interface DataFrameQueryBuilder {
      DataColumnBuilder selectColumn(int columnIndex);
      DataFrame build();
   }

   public interface DataColumnBuilder {
      DataColumnBuilder transform(Function<String, Double> columnTransformer);
      DataFrameQueryBuilder asInput(String columnName);
      DataFrameQueryBuilder asOutput(String columnName);
   }

   public interface FormatBuilder {
      SourceBuilder csv(String splitter, boolean skipFirstLine);
      SourceBuilder libsvm();
   }

   public interface SourceBuilder {
      DataFrameQueryBuilder from(InputStream inputStream);
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

   private static class DataFrameBuilderX implements SourceBuilder, DataFrameQueryBuilder, DataColumnBuilder, FormatBuilder {

      private final List<DataFrameColumn> inputColumns = new ArrayList<>();
      private final List<DataFrameColumn> outputColumns = new ArrayList<>();
      private InputStream dataInputStream;
      private String csvSplitter;
      private DataFileType fileType;
      private boolean skipFirstLine = false;

      private static final Logger logger = LoggerFactory.getLogger(DataFrameBuilderX.class);

      private DataFrameColumn selected = null;

      @Override public DataColumnBuilder selectColumn(int columnIndex) {
         selected = new DataFrameColumn("", columnIndex, StringUtils::parseDouble);
         return this;
      }

      @Override public DataFrame build() {
         final BasicDataFrame dataFrame = new BasicDataFrame();

         if(fileType == DataFileType.Csv) {
            CsvUtils.csv(dataInputStream, csvSplitter, skipFirstLine, (words) -> {
               DataRow row = dataFrame.newRow();
               for (int i = 0; i < words.length; ++i) {
                  for (DataFrameColumn c : inputColumns) {
                     if (c.index == i) {
                        row.setCell(c.columnName, c.transformer.apply(words[i]));
                     }
                  }
                  for (DataFrameColumn c : outputColumns) {
                     if (c.index == i) {
                        row.setTargetCell(c.columnName, c.transformer.apply(words[i]));
                     }
                  }
               }
               dataFrame.addRow(row);
               return true;
            }, (e) -> logger.error("Failed to read csv file", e));
         } else {
            List<DataRow> rows = CsvUtils.readHeartScale(dataInputStream);
            if(inputColumns.isEmpty() && outputColumns.isEmpty()) {
               rows.forEach(dataFrame::addRow);
            } else if(inputColumns.isEmpty() || outputColumns.isEmpty()) {
               throw new RuntimeException("data frame should not have either empty input columns or empty output columns");
            } else {
               for (DataRow row : rows) {
                  DataRow newRow = dataFrame.newRow();
                  for (DataFrameColumn c : inputColumns) {
                     newRow.setCell(c.columnName, c.transformer.apply("" + row.getCell(c.columnName)));
                  }
                  for (DataFrameColumn c : outputColumns) {
                     newRow.setTargetCell(c.columnName, c.transformer.apply("" + row.getTargetCell(c.columnName)));
                  }
                  dataFrame.addRow(newRow);
               }
            }
         }

         dataFrame.lock();

         return dataFrame;
      }


      @Override public SourceBuilder csv(String splitter, boolean skipFirstLine) {
         this.skipFirstLine = skipFirstLine;
         csvSplitter = splitter;
         fileType = DataFileType.Csv;
         return this;
      }

      @Override public DataFrameQueryBuilder from(InputStream inputStream) {
         dataInputStream = inputStream;
         return this;
      }


      @Override public SourceBuilder libsvm() {
         fileType = DataFileType.HeartScale;
         return this;
      }


      @Override public DataColumnBuilder transform(Function<String, Double> columnTransformer) {
         selected.transformer = columnTransformer;
         return this;
      }


      @Override public DataFrameQueryBuilder asInput(String columnName) {
         selected.columnName = columnName;
         inputColumns.add(selected);
         selected = null;
         return this;
      }


      @Override public DataFrameQueryBuilder asOutput(String columnName) {
         selected.columnName = columnName;
         outputColumns.add(selected);
         selected = null;
         return this;
      }
   }


   public static SourceBuilder libsvm() {
      return new DataFrameBuilderX().libsvm();
   }

   public static SourceBuilder csv(String splitter, boolean skipFirstLine) {
      return new DataFrameBuilderX().csv(splitter, skipFirstLine);
   }
}
