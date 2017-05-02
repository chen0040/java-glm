package com.github.chen0040.glm.data;


import com.github.chen0040.glm.utils.FileUtils;
import com.github.chen0040.glm.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;


/**
 * Created by xschen on 2/5/2017.
 */
public class DataFrameBuilderUnitTest {

   private Logger logger = LoggerFactory.getLogger(DataFrameBuilderUnitTest.class);

   @Test
   public void test_csv() throws IOException {

      int column_use = 3;
      int column_livch = 4;
      int column_age = 5;
      int column_urban = 6;

      DataFrame frame = DataFrameBuilder.csv(FileUtils.getResourceFile("contraception.csv"), ",")
              .selectColumn("livch", column_livch)
              .selectColumn("age", column_age)
              .selectColumn("age^2", column_age, age -> Math.pow(StringUtils.parseDouble(age), 2))
              .selectColumn("urban", column_urban, label -> label.equals("Y") ? 1.0 : 0.0)
              .selectTargetColumn("use", column_use, label -> label.equals("Y") ? 1.0 : 0.0)
              .build();

      for(int i=0; i < 10; ++i){
         logger.info("row[{}]: {}", i, frame.row(i));
      }

      logger.info("row count: {}", frame.rowCount());

   }

   @Test
   public void test_heartScale() throws IOException {
      DataFrame frame = DataFrameBuilder.heartScale(FileUtils.getResourceFile("heart_scale.txt")).build();

      for(int i=0; i < 10; ++i){
         logger.info("row[{}]: {}", i, frame.row(i));
      }

      logger.info("row count: {}", frame.rowCount());
   }
}
