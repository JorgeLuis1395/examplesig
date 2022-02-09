/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.auxiliares.sfccbdmq;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.SelectItem;
import org.entidades.sfccbdmq.Cargosxorganigrama;
import org.entidades.sfccbdmq.Codigos;

/**
 *
 * @author edwin
 */
public class Combos implements Serializable {
     private static final long serialVersionUID = 1L;

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        if (entities==null){
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(0, "--- Seleccione uno ---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }
    public static SelectItem[] comboStrings(List<String> entities, boolean selectOne) {
        if (entities==null){
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "---");
            i++;
        }
        for (String x : entities) {
            items[i++] = new SelectItem(x, x);
        }
        return items;
    }
    public static SelectItem[] comboToStrings(List<?> entities, boolean selectOne) {
        if (entities==null){
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x.toString(), x.toString());
        }
        return items;
    }
    
    public static SelectItem[] getSelectItems2(List<Cargosxorganigrama> entities, boolean selectOne) {
        if (entities == null) {
            return null;
        }
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "---");
            i++;
        }
        for (Cargosxorganigrama x : entities) {
            items[i++] = new SelectItem(x, x.toString2());
        }
        return items;
    }

    
}
