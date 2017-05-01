package com.github.chen0040.glm.data;


/**
 * Created by xschen on 28/4/2017.
 */
public interface DataFrame {
   int rowCount();

   DataRow row(int i);

   Level[] descriptors();
}
