/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.icefaces.ace.component.dataexporter.DataExporter;

/**
 *
 * @author edwin
 */
@ManagedBean(name = "exportadorSfccbdmq")
@RequestScoped
public class ExportadorBean {

    /** Creates a new instance of ExportadorBean */
    public ExportadorBean() {
    }
      private DataExporter dataExporter= new DataExporter(); 
      /* @return the dataExporter
     */
    public DataExporter getDataExporter() {
        return dataExporter;
    }

    /**
     * @param dataExporter the dataExporter to set
     */
    public void setDataExporter(DataExporter dataExporter) {
//        dataExporter.setResource(null); 
        
        this.dataExporter = dataExporter;
    }

}
