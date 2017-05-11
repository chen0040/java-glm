package com.github.chen0040.glm.solvers;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.data.evaluators.BinaryClassifierEvaluator;
import com.github.chen0040.glm.utils.FileUtils;
import com.github.chen0040.data.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;


/**
 * Created by xschen on 15/8/15.
 */
public class GlmUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(GlmUnitTest.class);
    private DataFrame frame;

    @BeforeMethod
    public void setUp() throws IOException {
        int column_use = 3;
        int column_livch = 4;
        int column_age = 5;
        int column_urban = 6;

        boolean skipFirstLine = true;
        String columnSplitter = ",";
        InputStream inputStream = FileUtils.getResource("contraception.csv");
        frame = DataQuery.csv(columnSplitter, skipFirstLine)
                .from(inputStream)
                .selectColumn(column_livch).transform(cell -> cell.equals("1") ? 1.0 : 0.0).asInput("livch1")
                .selectColumn(column_livch).transform(cell -> cell.equals("2") ? 1.0 : 0.0).asInput("livch2")
                .selectColumn(column_livch).transform(cell -> cell.equals("3+") ? 1.0 : 0.0).asInput("livch3")
                .selectColumn(column_age).asInput("age")
                .selectColumn(column_age).transform(cell -> Math.pow(StringUtils.parseDouble(cell), 2)).asInput("age^2")
                .selectColumn(column_urban).transform(cell -> cell.equals("Y") ? 1.0 : 0.0).asInput("urban")
                .selectColumn(column_use).transform(cell -> cell.equals("Y") ? 1.0 : 0.0).asOutput("use")
                .build();

        for(int i=0; i < 10; ++i){
            logger.info("row[{}]: {}", i, frame.row(i));
        }
    }

    @Test
    public void test_logistic_irls() {


        Glm glm = Glm.logistic();
        glm.setSolverType(GlmSolverType.GlmIrls);
        glm.fit(frame);

        BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();

        for(int i = 0; i < frame.rowCount(); ++i){
            boolean predicted = glm.transform(frame.row(i)) > 0.5;
            boolean actual = frame.row(i).target() > 0.5;
            logger.info("predicted(Irls): {}\texpected: {}", predicted, actual);
            evaluator.evaluate(actual, predicted);
        }

        logger.info("Coefficients(Irls): {}", glm.getCoefficients());
        evaluator.report();
    }

    @Test
    public void test_logistic_irls_qr() {
        Glm glm = Glm.logistic();
        glm.setSolverType(GlmSolverType.GlmIrlsQr);
        glm.fit(frame);

        BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();
        for(int i = 0; i < frame.rowCount(); ++i){
            boolean predicted = glm.transform(frame.row(i)) > 0.5;
            boolean actual = frame.row(i).target() > 0.5;
            logger.info("predicted(IrlsQr): {}\texpected: {}", predicted, actual);
            evaluator.evaluate(actual, predicted);
        }

        logger.info("Coefficients(IrlsQr): {}", glm.getCoefficients());
        evaluator.report();
    }

    @Test
    public void test_logistic_irls_svd() {
        Glm glm = Glm.logistic();
        glm.setSolverType(GlmSolverType.GlmIrlsSvd);
        glm.fit(frame);

        BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();
        for(int i = 0; i < frame.rowCount(); ++i){
            boolean predicted = glm.transform(frame.row(i)) > 0.5;
            boolean actual = frame.row(i).target() > 0.5;
            logger.info("predicted(IrlsSvd): {}\texpected: {}", predicted, actual);
            evaluator.evaluate(actual, predicted);
        }

        logger.info("Coefficients(IrlsSvd): {}", glm.getCoefficients());
        evaluator.report();
    }
}
