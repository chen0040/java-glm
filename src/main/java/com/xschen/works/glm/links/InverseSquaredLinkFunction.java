package com.xschen.works.glm.links;

/**
 * Created by xschen on 14/8/15.
 */
/// <summary>
/// Generally applicable to:
///   1. Inverse Gaussian
/// </summary>
public class InverseSquaredLinkFunction extends AbstractLinkFunction {

    @Override
    public LinkFunction makeCopy(){
        return new InverseSquaredLinkFunction();
    }

    @Override
    public double GetLink(double b) {
        return -1.0 / (b * b);
    }

    @Override
    public double GetInvLink(double a) {
        return Math.sqrt(-a);
    }

    @Override
    public double GetInvLinkDerivative(double a) {
        return -1.0 / Math.sqrt(-a);
    }
}

