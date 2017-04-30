package com.xschen.works.glm.search;


import com.xschen.works.glm.search.events.NumericSolutionIterateListener;
import com.xschen.works.glm.search.events.NumericSolutionUpdatedListener;
import com.xschen.works.glm.search.solutions.NumericSolution;
import com.xschen.works.glm.search.solutions.NumericSolutionUpdateResult;

import java.util.HashSet;


/**
 * Created by xschen on 13/8/15.
 */
public class SearchListenerService {
    private HashSet<NumericSolutionIterateListener> iterateListeners;
    private HashSet<NumericSolutionUpdatedListener> updateListeners;

    public SearchListenerService(){
        iterateListeners = new HashSet<>();
        updateListeners = new HashSet<>();
    }

    public void addIterateListener(NumericSolutionIterateListener listener){
        iterateListeners.add(listener);
    }

    public void removeIterateListener(NumericSolutionIterateListener listener){
        iterateListeners.remove(listener);
    }

    public void addUpdateListener(NumericSolutionUpdatedListener listener){
        updateListeners.add(listener);
    }

    public void removeUpdateListener(NumericSolutionUpdatedListener listener){
        updateListeners.remove(listener);
    }

    public void notifySolutionUpdated(NumericSolution solution, NumericSolutionUpdateResult state, int iteration){
        for(NumericSolutionUpdatedListener listener : updateListeners){
            if(listener != null){
                listener.report(solution, state, iteration);
            }
        }
    }

    public void step(NumericSolution solution, NumericSolutionUpdateResult state, int iteration){
        for(NumericSolutionIterateListener listener : iterateListeners){
            if(listener != null){
                listener.report(solution, state, iteration);
            }
        }
    }
}
