package com.github.chen0040.glm.solvers;


import Jama.Matrix;
import Jama.SingularValueDecomposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by xschen on 24/5/2017.
 * Moore-Penrose Pseudoinverse in JAMA
 */
public class Matrices {
   private static final Logger logger = LoggerFactory.getLogger(Matrices.class);
   /**
    * The difference between 1 and the smallest exactly representable number
    * greater than one. Gives an upper bound on the relative error due to
    * rounding of floating point numbers.
    */
   public static double MACHEPS = 2E-16;

   /**
    * Updates MACHEPS for the executing machine.
    */
   public static void updateMacheps() {
      MACHEPS = 1;
      do
         MACHEPS /= 2;
      while (1 + MACHEPS / 2 != 1);
   }

   /**
    * Computes the Moore–Penrose pseudoinverse using the SVD method.
    *
    * Modified version of the original implementation by Kim van der Linde.
    */
   public static Matrix pinv(Matrix x) {
      int rows = x.getRowDimension();
      int cols = x.getColumnDimension();
      if (rows < cols) {
         Matrix result = pinv(x.transpose());
         if (result != null)
            result = result.transpose();
         return result;
      }
      SingularValueDecomposition svdX = new SingularValueDecomposition(x);
      if (svdX.rank() < 1)
         return null;
      double[] singularValues = svdX.getSingularValues();
      double tol = Math.max(rows, cols) * singularValues[0] * MACHEPS;
      double[] singularValueReciprocals = new double[singularValues.length];
      for (int i = 0; i < singularValues.length; i++)
         if (Math.abs(singularValues[i]) >= tol)
            singularValueReciprocals[i] =  1.0 / singularValues[i];
      double[][] u = svdX.getU().getArray();
      double[][] v = svdX.getV().getArray();
      int min = Math.min(cols, u[0].length);
      double[][] inverse = new double[cols][rows];
      for (int i = 0; i < cols; i++)
         for (int j = 0; j < u.length; j++)
            for (int k = 0; k < min; k++)
               inverse[i][j] += v[i][k] * singularValueReciprocals[k] * u[j][k];
      return new Matrix(inverse);
   }


   public static Matrix inverse(Matrix A) {
      Matrix result;
      try{
         result = A.inverse();
      } catch(Exception ex){
         logger.warn("inverse fail due to singularity, try Moore-Penrose Pseudoinverse in JAMA", ex);
         result = pinv(A);
      }
      return result;
   }
}
