/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur_compta;

import AccessBD.BeanBDAccess;
import AccessBD.BeanBDMySql;
/*import RequeteReponseBISAMAP.RequeteBISAMAP;
import RequeteReponseCHAMAP.RequeteCHAMAP;*/
import RequeteReponseSAMOP.RequeteSAMOP;
import Utils.ConsoleServeur;
import Utils.ListeTaches;
import Utils.ThreadServeur;
import Utils.ThreadServeurSSL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Julien
 */
public class MainForm extends javax.swing.JFrame implements ConsoleServeur
{
    private ThreadServeur _ThreadServeurApplicCompta = null;
    private ThreadServeur _ThreadServeurServeurTrafic = null;
    private ThreadServeurSSL _ThreadServeurApplicComptaSalary = null;
    private Properties PropertiesFileServeurCompta;
    private int NombreMaxClients;
    private String RequestSeparator;
    
    /**
     * Creates new form MainForm
     */
    public MainForm()
    {
        initComponents();
        LoadPropertiesFile();
    }
    
    private void LoadPropertiesFile()
    {
        File f = new File("Serveur_ComptaSSL.properties");
        if(!f.exists())
        {
            OutputStream os = null;
            try
            {
                os = new FileOutputStream(f);
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Properties propToCreate = new Properties();
            propToCreate.put("PORT_TRAFIC", "31016");
            propToCreate.put("PORT_COMPTA", "31017");
            propToCreate.put("PORT_SALARY", "31019");
            propToCreate.put("ADRESSE_IP_SERVEUR", "192.168.1.100");
            propToCreate.put("MAX_CLI", "3");
            propToCreate.put("SEP", "#");
            propToCreate.put("ADRESSE_IP_BD", "127.0.0.1");
            propToCreate.put("PORT_BD", "3306");
            propToCreate.put("SCHEMA_BD_TRAFIC", "bd_trafic");
            propToCreate.put("SCHEMA_BD_COMPTA", "bd_compta");
            
            try
            {
                propToCreate.store(os, "Serveur_ComptaSSL");
                os.flush();
            }
            catch (IOException ex)
            {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try
        {
            InputStream is = new FileInputStream(f);
            PropertiesFileServeurCompta = new Properties();
            PropertiesFileServeurCompta.load(is);
            tfdPortTrafic.setText(PropertiesFileServeurCompta.getProperty("PORT_TRAFIC"));
            tfdPortCompta.setText(PropertiesFileServeurCompta.getProperty("PORT_COMPTA"));
            tfdPortSalary.setText(PropertiesFileServeurCompta.getProperty("PORT_SALARY"));
            NombreMaxClients = Integer.valueOf(PropertiesFileServeurCompta.getProperty("MAX_CLI"));
            RequestSeparator = PropertiesFileServeurCompta.getProperty("SEP");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void TraceEvenements(String commentaire)
    {
        Vector ligne = new Vector();
        StringTokenizer parser = new StringTokenizer(commentaire, RequestSeparator);
        while (parser.hasMoreTokens())
            ligne.add(parser.nextToken());
        DefaultTableModel dtm = (DefaultTableModel)this.tblRequete.getModel();
        dtm.insertRow(dtm.getRowCount(), ligne);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblPortTrafic = new javax.swing.JLabel();
        tfdPortTrafic = new javax.swing.JTextField();
        btnDemarrerServeur = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRequete = new javax.swing.JTable();
        btnStopperServeur = new javax.swing.JButton();
        lblPortCompta = new javax.swing.JLabel();
        tfdPortCompta = new javax.swing.JTextField();
        lblPortSalary = new javax.swing.JLabel();
        tfdPortSalary = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblPortTrafic.setText("PORT_TRAFIC : ");

        btnDemarrerServeur.setText("Démarrer le serveur");
        btnDemarrerServeur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDemarrerServeurActionPerformed(evt);
            }
        });

        tblRequete.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Origine", "Requête", "Thread"
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
        jScrollPane1.setViewportView(tblRequete);

        btnStopperServeur.setText("Stopper le serveur");
        btnStopperServeur.setEnabled(false);
        btnStopperServeur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopperServeurActionPerformed(evt);
            }
        });

        lblPortCompta.setText("PORT_COMPTA :");

        lblPortSalary.setText("PORT_SALARY : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPortTrafic)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfdPortTrafic, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPortCompta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfdPortCompta, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPortSalary)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfdPortSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDemarrerServeur)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnStopperServeur))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPortSalary)
                        .addComponent(tfdPortSalary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPortCompta)
                        .addComponent(tfdPortCompta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDemarrerServeur)
                        .addComponent(btnStopperServeur))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPortTrafic)
                        .addComponent(tfdPortTrafic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDemarrerServeurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDemarrerServeurActionPerformed
        try
        {
            int PortTrafic = Integer.parseInt(this.tfdPortTrafic.getText());
            int PortCompta = Integer.parseInt(this.tfdPortCompta.getText());
            int PortSalary = Integer.parseInt(this.tfdPortSalary.getText());
            btnDemarrerServeur.setEnabled(false);
            btnStopperServeur.setEnabled(true);
            
            /* Instanciation et lancement du ThreadServeur du protocole CHAMAP */
            
            /*BeanBDAccess BeanTrafic = new BeanBDMySql(PropertiesFileServeurCompta.getProperty("ADRESSE_IP_BD"), PropertiesFileServeurCompta.getProperty("PORT_BD"), PropertiesFileServeurCompta.getProperty("SCHEMA_BD_TRAFIC"), "root", "");
            HashMap hmST = new HashMap();
            hmST.put(RequeteCHAMAP.BeanTrafic, BeanTrafic);
            this.setThreadServeurServeurTrafic(new ThreadServeur(PortTrafic, new ListeTaches(), this, 1, hmST));
            this.getThreadServeurServeurTrafic().start();*/
            
            /* Instanciation et lancement du ThreadServeur du protocole BISAMAP */
            BeanBDAccess BeanCompta = new BeanBDMySql(PropertiesFileServeurCompta.getProperty("ADRESSE_IP_BD"), PropertiesFileServeurCompta.getProperty("PORT_BD"), PropertiesFileServeurCompta.getProperty("SCHEMA_BD_COMPTA"), "root", "");
            /*HashMap hmAC = new HashMap();
            hmAC.put(RequeteBISAMAP.BeanCompta, BeanCompta);
            this.setThreadServeurApplicCompta(new ThreadServeur(PortCompta, new ListeTaches(), this, NombreMaxClients, hmAC));
            this.getThreadServeurApplicCompta().start();*/
            
            /* Instanciation et lancement du ThreadServeur du protocole SAMOP */
            HashMap hmACS = new HashMap();
            hmACS.put(RequeteSAMOP.BeanCompta, BeanCompta);
            this.setThreadServeurApplicComptaSalary(new ThreadServeurSSL(PortSalary, new ListeTaches(), this, NombreMaxClients, hmACS));
            this.getThreadServeurApplicComptaSalary().start();

            this.TraceEvenements("Main#Lancement serveur#Main");
        }
        catch(NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(this, "Le port entré est invalide.", "Erreur de saisie du port", JOptionPane.ERROR_MESSAGE);
        } 
        catch (SQLException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDemarrerServeurActionPerformed

    private void btnStopperServeurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopperServeurActionPerformed
        if(this.getThreadServeurApplicCompta() != null)
        {
            this.getThreadServeurApplicCompta().interrupt();
            this.getThreadServeurServeurTrafic().interrupt();
            this.getThreadServeurApplicComptaSalary().interrupt();
            this.TraceEvenements("Main#Arrêt du serveur#Main");
            btnDemarrerServeur.setEnabled(true);
            btnStopperServeur.setEnabled(false);
        }
    }//GEN-LAST:event_btnStopperServeurActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDemarrerServeur;
    private javax.swing.JButton btnStopperServeur;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPortCompta;
    private javax.swing.JLabel lblPortSalary;
    private javax.swing.JLabel lblPortTrafic;
    private javax.swing.JTable tblRequete;
    private javax.swing.JTextField tfdPortCompta;
    private javax.swing.JTextField tfdPortSalary;
    private javax.swing.JTextField tfdPortTrafic;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the _ThreadServeurApplicCompta
     */
    public ThreadServeur getThreadServeurApplicCompta() {
        return _ThreadServeurApplicCompta;
    }

    /**
     * @param _ThreadServeurApplicCompta the _ThreadServeurApplicCompta to set
     */
    public void setThreadServeurApplicCompta(ThreadServeur _ThreadServeurApplicCompta) {
        this._ThreadServeurApplicCompta = _ThreadServeurApplicCompta;
    }

    /**
     * @return the _ThreadServeurServeurTrafic
     */
    public ThreadServeur getThreadServeurServeurTrafic() {
        return _ThreadServeurServeurTrafic;
    }

    /**
     * @param _ThreadServeurServeurTrafic the _ThreadServeurServeurTrafic to set
     */
    public void setThreadServeurServeurTrafic(ThreadServeur _ThreadServeurServeurTrafic) {
        this._ThreadServeurServeurTrafic = _ThreadServeurServeurTrafic;
    }

    /**
     * @return the _ThreadServeurApplicComptaSalary
     */
    public ThreadServeurSSL getThreadServeurApplicComptaSalary() {
        return _ThreadServeurApplicComptaSalary;
    }

    /**
     * @param _ThreadServeurApplicComptaSalary the _ThreadServeurApplicComptaSalary to set
     */
    public void setThreadServeurApplicComptaSalary(ThreadServeurSSL _ThreadServeurApplicComptaSalary) {
        this._ThreadServeurApplicComptaSalary = _ThreadServeurApplicComptaSalary;
    }
}
