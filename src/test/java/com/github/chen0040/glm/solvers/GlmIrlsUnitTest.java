package com.github.chen0040.glm.solvers;

import com.github.chen0040.glm.data.DataFrame;
import com.github.chen0040.glm.data.DataFrameBuilder;
import com.github.chen0040.glm.enums.GlmDistributionFamily;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.glm.utils.StringUtils;

import static org.junit.Assert.assertTrue;


/**
 * Created by xschen on 15/8/15.
 */
public class GlmIrlsUnitTest {
    //@Test
    public void test_logisticRegression(){


        int column_use = 3;
        int column_livch = 4;
        int column_age = 5;
        int column_urban = 6;

        DataFrame frame = DataFrameBuilder.csv("contraception.csv")
                .selectColumn(column_livch)
                .selectColumn(column_age)
                .selectColumn(column_age, age -> Math.pow(StringUtils.parseDouble(age), 2))
                .selectColumn(column_urban)
                .selectTargetColumn(column_use, label -> label.equals("Y") ? 1.0 : 0.0)
                .build();



        GlmSolver classifier = new GlmSolver();
        classifier.setDistributionFamily(GlmDistributionFamily.Binomial);
        classifier.setSolverType(GlmSolverType.GlmIrls);
        classifier.fit(frame);


        for(int i = 0; i < frame.rowCount(); ++i){
            System.out.println(String.format("predicted(Irls): %.2f\texpected: %d",
                    classifier.transform(frame.row(i)),
                    frame.row(i).target()));
        }

        System.out.println("Coefficients(Irls): "+classifier.getCoefficients());
    }
}
