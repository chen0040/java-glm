package com.github.chen0040.glm.applications;


import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.data.utils.TupleTwo;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.data.evaluators.BinaryClassifierEvaluator;
import com.github.chen0040.glm.solvers.Glm;
import com.github.chen0040.glm.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
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
   public void testSimple() throws IOException {

      InputStream inputStream = FileUtils.getResource("heart_scale.txt");
      DataFrame dataFrame = DataQuery.libsvm().from(inputStream).build();

      for(int i=0; i < dataFrame.rowCount(); ++i){
         DataRow row = dataFrame.row(i);
         String targetColumn = row.getTargetColumnNames().get(0);
         row.setTargetCell(targetColumn, row.getTargetCell(targetColumn) == -1 ? 0 : 1);
      }

      TupleTwo<DataFrame, DataFrame> miniFrames = dataFrame.shuffle().split(0.9);
      DataFrame trainingData = miniFrames._1();
      DataFrame crossValidationData = miniFrames._2();

      Glm algorithm = Glm.logistic();
      algorithm.setSolverType(GlmSolverType.GlmIrlsQr);
      algorithm.fit(trainingData);

      double threshold = 1.0;
      for(int i = 0; i < trainingData.rowCount(); ++i){
         double prob = algorithm.transform(trainingData.row(i));
         if(trainingData.row(i).target() == 1 && prob < threshold){
            threshold = prob;
         }
      }
      logger.info("threshold: {}",threshold);


      BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();

      for(int i = 0; i < crossValidationData.rowCount(); ++i){
         double prob = algorithm.transform(crossValidationData.row(i));
         boolean predicted = prob > 0.5;
         boolean actual = crossValidationData.row(i).target() > 0.5;
         evaluator.evaluate(actual, predicted);
         logger.info("probability of positive: {}", prob);
         logger.info("predicted: {}\texpected: {}", predicted, actual);
      }

      evaluator.report();



   }


}
