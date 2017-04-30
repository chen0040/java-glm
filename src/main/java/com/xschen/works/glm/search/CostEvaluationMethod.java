package com.xschen.works.glm.search;


/**
 * Created by xschen on 29/4/2017.
 */
public interface CostEvaluationMethod{

   double apply(double[] x, double[] lowerBounds, double[] upperBounds, Object constraint);
}

