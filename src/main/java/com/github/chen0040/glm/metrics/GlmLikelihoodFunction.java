package com.github.chen0040.glm.metrics;


import com.github.chen0040.glm.data.DataFrame;
import com.github.chen0040.glm.data.DataRow;
import com.github.chen0040.glm.enums.GlmDistributionFamily;
import com.github.chen0040.glm.maths.Mean;
import com.github.chen0040.glm.maths.StdDev;


/**
 * Created by xschen on 15/8/15.
 */
/// <summary>
/// The likelihood function for the linear model
///
/// </summary>
public class GlmLikelihoodFunction
{
    /// <summary>
    /// Return the likelihood value of the fitted regressions model
    /// </summary>
    /// <param name="shrinkedData"></param>
    /// <param name="beta_hat">estimated predictor coefficient in the fitted regressions model</param>
    /// <returns></returns>
    public static double getLikelihood(GlmDistributionFamily distribution, DataFrame data, double[] beta_hat)
    {
        switch (distribution)
        {
            case Normal:
                return GetLikelihood_Normal(data, beta_hat);
            default:
                return Double.NaN;
        }
    }

    private static double GetLikelihood_Normal(DataFrame data, double[] beta_hat)
    {
        int N = data.rowCount();
        int k = beta_hat.length;
        double residual_sum_of_squares = 0;

        double[] y = new double[N];
        for(int i=0; i < N; ++i)
        {
            y[i] = data.row(i).target();
        }

        double sigma = StdDev.apply(y, Mean.apply(y)) / (N - k - 1);

        for (int i = 0; i < N; ++i)
        {
            double linear_predictor = 0;
            DataRow rec = data.row(i);
            double[] values = rec.toArray();
            for (int j = 0; j < k; ++j)
            {
                linear_predictor += values[j] * beta_hat[j];
            }
            double residual = rec.target() - linear_predictor;
            residual_sum_of_squares += residual*residual;
        }

        return Math.exp(-residual_sum_of_squares / (2 * sigma)) / Math.sqrt(2 * Math.PI * sigma);
    }
}

