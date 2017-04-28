package com.xschen.works.glm.links;

/**
 * Created by xschen on 14/8/15.
 */
/// <summary>
/// Generally applicable to the following distribution:
///   1. Exponential / Gamma : Exponential-response shrinkedData, scale parameters
/// </summary>
public class InverseLinkFunction extends AbstractLinkFunction {

    @Override
    public LinkFunction makeCopy(){
        return new InverseLinkFunction();
    }

    @Override
    public double GetLink(double b)
    {
        return -1.0 / b;
    }

    @Override
    public double GetInvLink(double a)
    {
        return -1.0 / a;
    }

    @Override
    public double GetInvLinkDerivative(double a)
    {
        return -1.0 / (a * a);
    }
}


