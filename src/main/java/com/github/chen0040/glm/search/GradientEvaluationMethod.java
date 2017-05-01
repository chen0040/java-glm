package com.github.chen0040.glm.search;

/**
 * Created by xschen on 12/8/15.
 */
public interface GradientEvaluationMethod {
    void apply(double[] x, double[] Vf, double[] lowerBounds, double[] upperBounds, Object constraint);
}
