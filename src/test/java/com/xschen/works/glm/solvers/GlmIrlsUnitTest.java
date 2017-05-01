package com.xschen.works.glm.solvers;

import com.xschen.works.glm.data.DataFrame;
import com.xschen.works.glm.data.DataFrameBuilder;
import com.xschen.works.glm.enums.GlmDistributionFamily;
import com.xschen.works.glm.enums.GlmSolverType;
import com.xschen.works.glm.utils.FileUtils;
import com.xschen.works.glm.utils.StringUtils;
import org.testng.annotations.Test;

import java.util.function.BiFunction;

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
                .selectTargetColumn(column_use)
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
