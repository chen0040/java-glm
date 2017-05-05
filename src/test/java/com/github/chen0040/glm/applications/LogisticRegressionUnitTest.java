package com.github.chen0040.glm.applications;


import com.github.chen0040.glm.data.DataFrame;
import com.github.chen0040.glm.data.DataQuery;
import com.github.chen0040.glm.data.Sampler;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.glm.evaluators.BinaryClassifierEvaluator;
import com.github.chen0040.glm.solvers.Glm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Random;


/**
 * Created by xschen on 5/5/2017.
 */
public class LogisticRegressionUnitTest {

   private static final Logger logger = LoggerFactory.getLogger(LogisticRegressionUnitTest.class);

   private static Random random = new Random();

   public static double rand(){
      return random.nextDouble();
   }

   public static double rand(double lower, double upper){
      return rand() * (upper - lower) + lower;
   }

   public static double randn(){
      double u1 = rand();
      double u2 = rand();
      double r = Math.sqrt(-2.0 * Math.log(u1));
      double theta = 2.0 * Math.PI * u2;
      return r * Math.sin(theta);
   }

   // unit testing based on example from http://scikit-learn.org/stable/auto_examples/svm/plot_oneclass.html#
   @Test
   public void testSimple(){

      DataQuery.DataFrameQueryBuilder schema = DataQuery.blank()
              .newInput("c1")
              .newInput("c2")
              .newOutput("anomaly")
              .end();

      Sampler.DataSampleBuilder negativeSampler = new Sampler()
              .forColumn("c1").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? -2 : 2))
              .forColumn("c2").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? -2 : 2))
              .forColumn("anomaly").generate((name, index) -> 0.0)
              .end();

      Sampler.DataSampleBuilder positiveSampler = new Sampler()
              .forColumn("c1").generate((name, index) -> rand(-4, 4))
              .forColumn("c2").generate((name, index) -> rand(-4, 4))
              .forColumn("anomaly").generate((name, index) -> 1.0)
              .end();

      DataFrame trainingData = schema.build();

      trainingData = negativeSampler.sample(trainingData, 200);
      trainingData = positiveSampler.sample(trainingData, 200);

      System.out.println(trainingData.head(10));

      DataFrame crossValidationData = schema.build();

      crossValidationData = negativeSampler.sample(crossValidationData, 40);
      crossValidationData = positiveSampler.sample(crossValidationData, 40);

      Glm algorithm = Glm.logistic();
      algorithm.setSolverType(GlmSolverType.GlmIrlsQr);
      algorithm.fit(trainingData);

      BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();

      for(int i = 0; i < crossValidationData.rowCount(); ++i){
         boolean predicted = algorithm.transform(crossValidationData.row(i)) > 0.5;
         boolean actual = crossValidationData.row(i).target() > 0.5;
         evaluator.evaluate(actual, predicted);
         logger.info("predicted: {}\texpected: {}", predicted, actual);
      }

      evaluator.report();



   }


}
