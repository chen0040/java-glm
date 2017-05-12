package com.github.chen0040.glm.solvers;


import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.glm.metrics.GlmStatistics;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.glm.enums.GlmDistributionFamily;
import com.github.chen0040.data.utils.CollectionUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;


/**
 * Created by xschen on 16/8/15.
 */
@Getter
@Setter
public class Glm {

    private static final Logger logger = LoggerFactory.getLogger(Glm.class);

    private GlmAlgorithm solver;
    private GlmDistributionFamily distributionFamily;
    private GlmSolverType solverType;
    private Coefficients coefficients;
    private String name;


    public void copy(Glm that){
        solver = that.solver == null ? null : that.solver.makeCopy();
        distributionFamily = that.distributionFamily;
        solverType = that.solverType;
        coefficients = that.coefficients == null ? null : that.coefficients.makeCopy();
    }

    public Glm makeCopy(){
        Glm clone = new Glm();
        clone.copy(this);

        return clone;
    }

    public Glm(GlmSolverType solverType, GlmDistributionFamily distributionFamily){
        this.solverType = solverType;
        this.distributionFamily = distributionFamily;
        this.coefficients = new Coefficients();
    }

    public Glm(){
        this(GlmSolverType.GlmIrls,GlmDistributionFamily.Normal);
    }

    public GlmDistributionFamily getDistributionFamily() {
        return distributionFamily;
    }

    public void setDistributionFamily(GlmDistributionFamily distributionFamily) {
        this.distributionFamily = distributionFamily;
    }

    public GlmSolverType getSolverType() {
        return solverType;
    }

    public void setSolverType(GlmSolverType solverType) {
        this.solverType = solverType;
    }
    
    public double transform(DataRow tuple) {
        double[] x0 = tuple.toArray();
        double[] x = new double[x0.length+1];
        x[0]=1;
        for(int i=0; i < x0.length; ++i){
            x[i+1] = x0[i];
        }

        return solver.predict(x);
    }

    protected GlmAlgorithm createSolver(double[][] A, double[] b){
        if(solverType == GlmSolverType.GlmNaive){
            return new GlmAlgorithm(distributionFamily, A, b);
        } else if(solverType == GlmSolverType.GlmIrlsQr){
            return new GlmAlgorithmIrlsQrNewton(distributionFamily, A, b);
        } else if(solverType == GlmSolverType.GlmIrls){
            return new GlmAlgorithmIrls(distributionFamily, A, b);
        } else if(solverType == GlmSolverType.GlmIrlsSvd){
            return new GlmAlgorithmIrlsSvdNewton(distributionFamily, A, b);
        }
        return null;
    }

    public void fit(DataFrame dataFrame) {
        int m = dataFrame.rowCount();
        double[][] X = new double[m][];

        coefficients.setDescriptors(dataFrame.rowArrayDescriptors());

        double[] y = new double[m];
        for(int i=0; i < m; ++i){
            DataRow tuple = dataFrame.row(i);
            double[] x_i = tuple.toArray();

            double[] x_prime = new double[x_i.length+1];
            x_prime[0] = 1;
            for(int j=0; j < x_i.length; ++j) {
                x_prime[j+1] = x_i[j];
            }
            X[i] = x_prime;


            y[i] = tuple.target();
        }

        solver = createSolver(X, y);
        double[] x_best = solver.solve();


        if(x_best == null){
            throw new RuntimeException("The solver failed");
        }else{
            coefficients.setValues(CollectionUtils.toList(x_best));
        }
    }


    public GlmStatistics showStatistics(){
        return solver != null ? solver.getStatistics() : null;
    }

    public Coefficients getCoefficients(){
        return coefficients;
    }


    public static Glm logistic() {
        Glm glm = new Glm();
        glm.setDistributionFamily(GlmDistributionFamily.Binomial);
        return glm;
    }

    public static Glm linear() {
        Glm glm = new Glm();
        glm.setDistributionFamily(GlmDistributionFamily.Normal);
        return glm;
    }


    public String getName() {
        return name;
    }


    public static OneVsOneGlmClassifier oneVsOne() {
        return new OneVsOneGlmClassifier();
    }

    public static OneVsOneGlmClassifier oneVsOne(Supplier<Glm> binaryClassifierGenerator) {
        return new OneVsOneGlmClassifier(binaryClassifierGenerator);
    }
}
