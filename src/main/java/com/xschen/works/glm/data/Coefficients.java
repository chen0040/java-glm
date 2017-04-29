package com.xschen.works.glm.data;


/**
 * Created by memeanalytics on 18/8/15.
 */
public class Coefficients {
    private double[] values;
    private Level[] descriptors;

    public void copy(Coefficients rhs){
        values = rhs.values == null ? null : rhs.values.clone();
        descriptors = null;
        if(rhs.descriptors != null){
            descriptors = new Level[rhs.descriptors.length];
            for(int i=0; i < rhs.descriptors.length; ++i){
                descriptors[i] = rhs.descriptors[i] == null ? null : (Level)rhs.descriptors[i].makeCopy();
            }
        }
    }

    public Coefficients makeCopy(){
        Coefficients clone = new Coefficients();
        clone.copy(this);
        return clone;
    }

    public Coefficients() {
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values=values;
    }

    public Level[] getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(Level[] descriptors){
        this.descriptors = descriptors;
    }

    public int size() {
        return values==null ? 0 : values.length;
    }

    @Override
    public String toString() {
        if(values==null || descriptors==null){
            return "(null)";
        }
        StringBuilder sb = new StringBuilder();


        sb.append("{");
        sb.append(String.format("\"(Intercepter)\":%f, ", values[0]));
        for (int i = 1; i < values.length; ++i){
            sb.append(String.format(", \"%s\":%f", descriptors[i-1].toString(), values[i]));
        }
        sb.append("}");
        return sb.toString();
    }
}
