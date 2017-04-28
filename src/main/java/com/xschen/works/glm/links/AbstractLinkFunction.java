package com.xschen.works.glm.links;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * Created by xschen on 14/8/15.
 */
public abstract class AbstractLinkFunction implements LinkFunction {
    public abstract double GetLink(double constraint_interval_value);
    public abstract double GetInvLink(double real_line_value);
    public abstract double GetInvLinkDerivative(double real_line_value);

    public LinkFunction makeCopy(){
        throw new NotImplementedException();
    }
}


