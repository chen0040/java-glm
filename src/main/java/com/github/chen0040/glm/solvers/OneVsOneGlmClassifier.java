package com.github.chen0040.glm.solvers;

import com.github.chen0040.glm.data.BasicDataFrame;
import com.github.chen0040.glm.data.DataFrame;
import com.github.chen0040.glm.data.DataRow;
import com.github.chen0040.glm.utils.TupleTwo;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Created by xschen on 8/20/2015 0020.
 */
public class OneVsOneGlmClassifier {
   protected List<TupleTwo<Glm, Glm>> classifiers;
   private double alpha = 0.1;
   private boolean shuffleData = false;
   private List<String> classLabels = new ArrayList<>();
   private Supplier<Glm> generator = () -> Glm.linear();

   private static String BINARY_LABEL = "success";

   public OneVsOneGlmClassifier(List<String> classLabels){
      this.classLabels.addAll(classLabels);
      classifiers = new ArrayList<>();
   }

   public OneVsOneGlmClassifier(){
      super();
      classifiers = new ArrayList<>();
   }


   public OneVsOneGlmClassifier(Supplier<Glm> binaryClassifierGenerator) {
      super();
      classifiers = new ArrayList<>();
      this.generator = binaryClassifierGenerator;
   }


   public boolean isShuffleData() {
      return shuffleData;
   }

   public void setShuffleData(boolean shuffleData) {
      this.shuffleData = shuffleData;
   }

   public double getAlpha() {
      return alpha;
   }

   public void setAlpha(double alpha) {
      this.alpha = alpha;
   }

   protected void createClassifiers(DataFrame dataFrame){
      classifiers = new ArrayList<>();

      if(classLabels.size()==0){
         classLabels.addAll(dataFrame.stream().map(DataRow::categoricalTarget).distinct().collect(Collectors.toList()));
      }
      for(int i=0; i < classLabels.size()-1; ++i){
         for(int j=i+1; j < classLabels.size(); ++j) {
            Glm svr1 = createClassifier(classLabels.get(i));
            Glm svr2 = createClassifier(classLabels.get(j));
            classifiers.add(new TupleTwo<>(svr1, svr2));
         }
      }
   }



   protected Glm createClassifier(String classLabel) {
      Glm svr = generator.get();
      svr.setName(classLabel);
      return svr;
   }

   protected double getClassifierScore(DataRow tuple, Glm classifier) {
      return classifier.transform(tuple);
   }

   protected List<DataFrame> split(DataFrame dataFrame, int n){
      List<DataFrame> miniFrames = new ArrayList<>();

      for(int i=0; i < n; ++i){
         miniFrames.add(new BasicDataFrame());
      }

      int index = 0;
      for(DataRow tuple : dataFrame) {
         int batchIndex = index % n;
         miniFrames.get(batchIndex).addRow(tuple);
         index++;
      }

      return miniFrames;
   }

   protected List<DataFrame> remerge(List<DataFrame> batches, int k){
      List<DataFrame> newBatches = new ArrayList<>();


      for(int i=0; i < batches.size(); ++i){

         DataFrame newBatch = new BasicDataFrame();

         for(int j=0; j < k; ++j){
            int d = (i + j) % batches.size();
            DataFrame batch = batches.get(d);
            for(DataRow tuple : batch){
               newBatch.addRow(tuple.makeCopy());
            }
         }

         newBatches.add(newBatch);
      }
      return newBatches;
   }


   public double transform(DataRow row) {
      String label = classify(row);
      return classLabels.indexOf(label);
   }

   public void fit(DataFrame dataFrame) {

      createClassifiers(dataFrame);

      if(shuffleData) {
         dataFrame.shuffle();
      }

      List<DataFrame> batches = split(dataFrame, classifiers.size());

      int k= Math.max(1, (int)alpha * batches.size());
      batches = remerge(batches, k);


      for(int i=0; i < classifiers.size(); ++i){
         TupleTwo<Glm, Glm> pair = classifiers.get(i);
         Glm classifier1 = pair._1();
         Glm classifier2 = pair._2();

         classifier1.fit(createBinaryBatch(batches.get(i), classifier1.getName()));
         classifier2.fit(createBinaryBatch(batches.get(i), classifier2.getName()));
      }

   }

   private DataFrame createBinaryBatch(DataFrame dataFrame, String classLabel){
      DataFrame binaryBatch = new BasicDataFrame();
      for(DataRow row  : dataFrame){
         String label = row.categoricalTarget();
         DataRow rowWithBinaryTargetOutput = row.makeCopy();
         rowWithBinaryTargetOutput.setTargetCell(BINARY_LABEL, label.equals(classLabel) ? 1.0 : 0.0);
         binaryBatch.addRow(rowWithBinaryTargetOutput);
      }
      return binaryBatch;
   }


   public String classify(DataRow row) {

      row = row.makeCopy();
      if(row.getTargetColumnNames().isEmpty()) {
        row.setTargetColumnNames(Collections.singletonList(BINARY_LABEL));
      }

      Map<String, Integer> scores = score(row);

      String predicatedClassLabel = null;
      int maxScore = 0;
      for(Map.Entry<String, Integer> entry : scores.entrySet()){
         String label = entry.getKey();
         int score = entry.getValue();
         if(score > maxScore){
            maxScore= score;
            predicatedClassLabel = label;
         }
      }

      if(predicatedClassLabel == null) {
         predicatedClassLabel = "NA";
      }

      return predicatedClassLabel;
   }


   public void reset() {
      classifiers.clear();
      classLabels.clear();
   }


   public List<String> getClassLabels() {
      return classLabels;
   }


   public Map<String, Integer> score(DataRow row) {

      Map<String, Integer> scores = new HashMap<>();

      for(int i=0; i < classifiers.size(); ++i){
         TupleTwo<Glm, Glm> pair = classifiers.get(i);
         Glm classifier1 = pair._1();
         Glm classifier2 = pair._2();

         double score1 = getClassifierScore(row, classifier1);
         double score2 = getClassifierScore(row, classifier2);

         if(score1 == score2) continue;

         String winningLabel;
         if(score1 > score2) {
            winningLabel = classifier1.getName();
         }
         else {
            winningLabel = classifier2.getName();
         }
         if(scores.containsKey(winningLabel)){
            scores.put(winningLabel, scores.get(winningLabel) + 1);
         }else {
            scores.put(winningLabel, 1);
         }
      }

      return scores;
   }
}
