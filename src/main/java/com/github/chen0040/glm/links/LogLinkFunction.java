package com.github.chen0040.glm.links;

/**
 * Created by xschen on 14/8/15.
 */
/// <summary>
/// The poisson link function maps the constraint interval b to linear line a
/// The inverse poisson link function maps the linear line a to constraint interval b
///
/// Generally applicable to the following distribution:
///   1. Poisson: count of occurrences in a fixed amount of time/space (Poisson regressions)
/// </summary>
public class LogLinkFunction extends AbstractLinkFunction {

    @Override
    public LinkFunction makeCopy(){
        return new LogLinkFunction();
    }

    @Override
    public double GetLink(double b) {
        return Math.log(b);
    }

    @Override
    public double GetInvLink(double a) {
        return Math.exp(a);
    }

    @Override
    public double GetInvLinkDerivative(double a) {
        return Math.exp(a);
    }
}

