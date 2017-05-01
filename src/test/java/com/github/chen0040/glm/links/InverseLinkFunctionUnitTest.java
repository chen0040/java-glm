package com.github.chen0040.glm.links;


import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


/**
 * Created by xschen on 28/4/2017.
 */
public class InverseLinkFunctionUnitTest {

   @Test
   public void test_getLink(){
      InverseLinkFunction f = new InverseLinkFunction();
      double b = 10.0;
      assertThat(f.GetLink(b)).isCloseTo(-0.1, within(0.1));
   }

}
