package com.xschen.works.glm.search.solutions;


/**
 * Created by xschen on 29/4/2017.
 */
public class NumericSolutionUpdateResult {
   private double improvement;
   private boolean improved;

   public NumericSolutionUpdateResult(double improvement, boolean improved){
      this.improvement = improvement;
      this.improved = improved;
   }

   public boolean improved(){
      return improved;
   }

   public double improvement(){
      return improvement;
   }
}
