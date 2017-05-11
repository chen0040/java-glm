package com.github.chen0040.glm.applications;


import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.glm.solvers.Glm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Random;


/**
 * Created by xschen on 5/5/2017.
 */
public class LinearRegressionUnitTest {

   private static final Logger logger = LoggerFactory.getLogger(LinearRegressionUnitTest.class);

   private static Random random = new Random();

   public static double rand(){
      return random.nextDouble();
   }


   public static double randn(){
      double u1 = rand();
      double u2 = rand();
      double r = Math.sqrt(-2.0 * Math.log(u1));
      double theta = 2.0 * Math.PI * u2;
      return r * Math.sin(theta);
   }

   @Test
   public void testSimple() {
      DataQuery.DataFrameQueryBuilder schema = DataQuery.blank()
              .newInput("x1")
              .newInput("x2")
              .newOutput("y")
              .end();

      // y = 4 + 0.5 * x1 + 0.2 * x2
      Sampler.DataSampleBuilder sampler = new Sampler()
              .forColumn("x1").generate((name, index) -> randn() * 0.3 + index)
              .forColumn("x2").generate((name, index) -> randn() * 0.3 + index * index)
              .forColumn("y").generate((name, index) -> 4 + 0.5 * index + 0.2 * index * index + randn() * 0.3)
              .end();

      DataFrame trainingData = schema.build();

      trainingData = sampler.sample(trainingData, 200);

      System.out.println(trainingData.head(10));

      DataFrame crossValidationData = schema.build();

      crossValidationData = sampler.sample(crossValidationData, 40);

      Glm glm = Glm.linear();
      glm.setSolverType(GlmSolverType.GlmIrlsQr);
      glm.fit(trainingData);

      for(int i = 0; i < crossValidationData.rowCount(); ++i){
         double predicted = glm.transform(crossValidationData.row(i));
         double actual = crossValidationData.row(i).target();
         logger.info("predicted: {}\texpected: {}", predicted, actual);
      }

      logger.info("Coefficients: {}", glm.getCoefficients());

   }
}
