/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceimp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HUBERTBAKIEWICZ
 */
public final class inventList extends javax.swing.JDialog {

    public int dial = 0;
    //public String[] data_j;

    /**
     * Creates new form inventList
     */
    public inventList(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //setColumn(jTable1,data_j );
    }

    public void setColu(String[] jjj) {
        setColumn(jTable1, jjj);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "DokId ", "Data ", "Nr Dokumentu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(10);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jLabel1.setText("Wybierz dokument inwentaryzacji");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        dial++;
        int row = jTable1.rowAtPoint(evt.getPoint());
        String DokId = jTable1.getModel().getValueAt(row, 0).toString();
        String DataDok = jTable1.getModel().getValueAt(row, 1).toString();
        String NrDok = jTable1.getModel().getValueAt(row, 2).toString();

        //System.out.println("click : " + evt.getClickCount());
        if (evt.getClickCount() == 2) {
            try {
                genInvent(DokId);
                this.dispose();
            } catch (SQLException ex) {
                AppMain.log.logEvent(Level.SEVERE, "Bład przy generowaniu inwentaryzacji", ex);
            }

        }
    }//GEN-LAST:event_jTable1MouseClicked

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    public DefaultTableModel setColumn(JTable tab, String[] jar) {

        DefaultTableModel tableModel = (DefaultTableModel) tab.getModel();
        tableModel.setRowCount(0);
        //String res = null;

        int count = 0;

        for (String jar1 : jar) {
            String[] data = jar1.split(";");
            // System.out.println(s);
            count++;

            tableModel.addRow(data);
            //if (rs.getString("PrinterStatus").equals("0")) {
            //    tableModel.setValueAt(false, tableModel.getRowCount() - 1, 5);
            // } else {
            //     tableModel.setValueAt(true, tableModel.getRowCount() - 1, 5);
            // }

//System.out.println(Boolean.parseBoolean(rs.getString("PrinterStatus")));
            tab.setModel(tableModel);
            tableModel.fireTableDataChanged();
        }

        //if (count > 0) {
        //   msg = "Liczba rekordów: " + String.valueOf(count);
        //   log.logEvent(Level.INFO, msg);
        // }
        return tableModel;
    }

    @SuppressWarnings("null")
    public void genInvent(String dokid) throws SQLException {
        String INV = "", EMP = "";
        String tmpLine = "", lin2 = "";
        AppMain.log.logEvent(Level.INFO, "Rozpoczynam generację Inwentaryzacji, DokiId " + dokid);
        String inv_date = AppMain.dbm.getDateDok(dokid);
        //inv_date += inv_date.replace("-", "");
        //Emp_JACXXXX.txt Inv_JACXXXX.txt
        // AppMain.dbm.getInwentPoz(dokid);
        String gt[] = AppMain.dbm.getInwentPoz(dokid);
        try {
            int lineCounter = 1, emp_counter = 1;
            for (String invPoz : gt) {
                String[] poz = invPoz.split(";");
                tmpLine = invPoz;
                if (tmpLine.contains("360365166663")) {
                    System.out.println("priceimp.inventList.genInvent()");
                }
                //
                for (int i = 0; i < Integer.parseInt(poz[2]); i++) {

                    lin2 = AppMain.store_id.substring(2, 6) + inv_date + addZero(String.valueOf(emp_counter), 4) + "0" + poz[3] + "\n";

                    INV += lin2;

                    //EMP += AppMain.store_id.substring(2, 6) + emp_counter + poz[1] + poz[2] + "\n";
                    if (lineCounter == 999) {
                        EMP += AppMain.store_id.substring(2, 6) + addZero(String.valueOf(emp_counter), 4) + 999999 + "\n";
                        emp_counter++;
                        lineCounter = 0;

                    }
                    lineCounter++;
                    lin2 = "";
                }

            }

            if (lineCounter > 0) {
                EMP += AppMain.store_id.substring(2, 6) + addZero(String.valueOf(emp_counter), 4) + addZero(String.valueOf(lineCounter-1), 3) + addZero(String.valueOf(lineCounter-1), 3) + "\n";
            }

            PrintWriter out_inv;
            PrintWriter out_emp;
            try {
                out_inv = new PrintWriter(AppMain.inv_output + "\\Inv_JAC" + AppMain.store_id + ".txt");
                out_emp = new PrintWriter(AppMain.inv_output + "\\Emp_JAC" + AppMain.store_id + ".txt");
                out_inv.print(INV);
                out_emp.print(EMP);
                out_inv.flush();
                out_emp.flush();
                AppMain.log.logEvent(Level.INFO, "zapisano plik : " + "Emp_JAC" + AppMain.store_id + ".txt, w lokalizacji : " + AppMain.inv_output);
                AppMain.log.logEvent(Level.INFO, "zapisano plik : " + "Inv_JAC" + AppMain.store_id + ".txt, w lokalizacji : " + AppMain.inv_output);
            } catch (FileNotFoundException ex) {
                AppMain.log.logEvent(Level.SEVERE, "genInvent", ex);
            }
        } catch (NullPointerException ex) {
            AppMain.log.logEvent(Level.SEVERE, "Bład w lini -> " + tmpLine, ex);
        }
    }

    private String repetLine(String line, int count) {
        String t = "";
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                t = t + line + "\n";
            }
        }
        return t;

    }

    private String addZero(String txt, Integer cunt) {

        while (txt.length() < cunt) {
            txt = "0" + txt;
        }

        return txt;
    }
}
