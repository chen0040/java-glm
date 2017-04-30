package com.xschen.works.glm.search.events;


import com.xschen.works.glm.search.solutions.NumericSolution;
import com.xschen.works.glm.search.solutions.NumericSolutionUpdateResult;


/**
 * Created by xschen on 12/8/15.
 */
public interface NumericSolutionIterateListener {
    void report(NumericSolution solution, NumericSolutionUpdateResult state, int iteration);

}
