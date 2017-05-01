package com.github.chen0040.glm.search;


import com.github.chen0040.glm.search.solutions.NumericSolutionUpdateResult;


/**
 * Created by xschen on 29/4/2017.
 */
public interface TerminationEvaluationMethod {
   boolean shouldTerminate(NumericSolutionUpdateResult state, int iteration);
}
