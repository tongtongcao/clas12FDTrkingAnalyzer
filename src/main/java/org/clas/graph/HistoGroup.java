package org.clas.graph;

import java.util.List;
import org.jlab.groot.data.IDataSet;
import org.jlab.groot.data.TDirectory;
import org.jlab.groot.group.DataGroup;

/**
 *
 * @author Tongtong
 */
public class HistoGroup extends DataGroup{
    
    public HistoGroup(String str) {
        super(str);
    }
        
    public HistoGroup(String str, int ncols, int nrows) {
        super(str, ncols, nrows);
    }   
}
