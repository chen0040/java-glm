package com.xschen.works.glm.search;


import com.xschen.works.glm.search.solutions.NumericSolutionUpdateResult;


/**
 * Created by xschen on 29/4/2017.
 */
public interface TerminationEvaluationMethod {
   boolean shouldTerminate(NumericSolutionUpdateResult state, int iteration);
}
