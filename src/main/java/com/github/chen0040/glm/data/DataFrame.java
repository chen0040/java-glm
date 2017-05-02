package com.github.chen0040.glm.data;


import java.util.List;


/**
 * Created by xschen on 28/4/2017.
 */
public interface DataFrame {
   int rowCount();

   DataRow row(int i);

   List<DataColumn> columns();

   void unlock();

   boolean isLocked();

   void lock();

   DataRow newRow();

   void addRow(DataRow row);
}
