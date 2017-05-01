package com.github.chen0040.glm.search.events;


import com.github.chen0040.glm.search.solutions.NumericSolution;
import com.github.chen0040.glm.search.solutions.NumericSolutionUpdateResult;


/**
 * Created by xschen on 12/8/15.
 */
public interface NumericSolutionIterateListener {
    void report(NumericSolution solution, NumericSolutionUpdateResult state, int iteration);

}
