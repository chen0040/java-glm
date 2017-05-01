package com.github.chen0040.glm.solvers;


import com.github.chen0040.glm.data.Coefficients;
import com.github.chen0040.glm.data.DataFrame;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.glm.metrics.GlmStatistics;
import com.github.chen0040.glm.data.DataRow;
import com.github.chen0040.glm.enums.GlmDistributionFamily;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by xschen on 16/8/15.
 */
@Getter
@Setter
public class GlmSolver {

    private static final Logger logger = LoggerFactory.getLogger(GlmSolver.class);

    private Glm solver;
    private GlmDistributionFamily distributionFamily;
    private GlmSolverType solverType;
    private Coefficients coefficients;
    
    public void copy(GlmSolver that){
        solver = that.solver == null ? null : that.solver.makeCopy();
        distributionFamily = that.distributionFamily;
        solverType = that.solverType;
        coefficients = that.coefficients == null ? null : that.coefficients.makeCopy();
    }

    public GlmSolver makeCopy(){
        GlmSolver clone = new GlmSolver();
        clone.copy(this);

        return clone;
    }

    public GlmSolver(GlmSolverType solverType, GlmDistributionFamily distributionFamily){
        this.solverType = solverType;
        this.distributionFamily = distributionFamily;
        this.coefficients = new Coefficients();
    }

    public GlmSolver(){
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

    protected Glm createSolver(double[][] A, double[] b){
        if(solverType == GlmSolverType.GlmNaive){
            return new Glm(distributionFamily, A, b);
        } else if(solverType == GlmSolverType.GlmIrlsQr){
            return new GlmIrlsQrNewton(distributionFamily, A, b);
        } else if(solverType == GlmSolverType.GlmIrls){
            return new GlmIrls(distributionFamily, A, b);
        } else if(solverType == GlmSolverType.GlmIrlsSvd){
            return new GlmIrlsSvdNewton(distributionFamily, A, b);
        }
        return null;
    }

    public void fit(DataFrame batch) {
        int m = batch.rowCount();
        double[][] X = new double[m][];

        coefficients.setDescriptors(batch.descriptors());

        double[] y = new double[m];
        for(int i=0; i < m; ++i){
            DataRow tuple = batch.row(i);
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
            coefficients.setValues(x_best);
        }
    }


    public GlmStatistics showStatistics(){
        return solver != null ? solver.getStatistics() : null;
    }

    public Coefficients getCoefficients(){
        return coefficients;
    }
}