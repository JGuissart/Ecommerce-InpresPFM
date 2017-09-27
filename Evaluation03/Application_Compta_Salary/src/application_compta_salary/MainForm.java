/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application_compta_salary;

import Commandes.AskPayments;
import Commandes.LaunchPayment;
import Commandes.LaunchPayments;
import RequeteReponseSAMOP.ReponseSAMOP;
import RequeteReponseSAMOP.RequeteSAMOP;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Julien
 */
public class MainForm extends javax.swing.JFrame 
{
    private SSLSocket CSocket = null;
    private Properties PropertiesSalary;
    private int Port;
    private String AdresseServeur;
    private String Separator;
    private String PathKeyStore;
    private String UtilisateurConnecte;
    private String PasswordKeyStore;
    private String PasswordKey;
    private DefaultListModel dlmLaunchPayments;
    private DefaultListModel dlmAskPayments;
    private DefaultComboBoxModel dcbmMoisEmploye;
    private DefaultComboBoxModel dcbmAnneeEmploye;
    private DefaultComboBoxModel dcbmMoisLaunchPayments;
    private DefaultComboBoxModel dcbmAnneeLaunchPayments;
    private DefaultComboBoxModel dcbmMoisAskPayments;
    private DefaultComboBoxModel dcbmAnneeAskPayments;

    /**
     * Creates new form MainForm
     */
    public MainForm()
    {
        initComponents();
        dlmLaunchPayments = new DefaultListModel();
        dlmAskPayments = new DefaultListModel();
        lstLaunchPayments.setModel(dlmLaunchPayments);
        lstAskPayments.setModel(dlmAskPayments);
        LoadPropertiesFile();
        FillComboBoxesDates();
        ConnexionServeur();
    }
    
    private void LoadPropertiesFile()
    {
        File f = new File("ApplicSalary.properties");
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
            propToCreate.put("ADRESSE_IP_SERVEUR", "192.168.1.100");
            propToCreate.put("PORT_SALARY", "31019");
            propToCreate.put("SEP", "#");
            propToCreate.put("PATH_KEYSTORE", "C:\\makecert\\KS_ApplicSalary");
            propToCreate.put("PASSWORD_KEYSTORE", "azerty");
            propToCreate.put("PASSWORD_KEY", "azerty");
            
            try
            {
                propToCreate.store(os, "ApplicSalary");
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
            PropertiesSalary = new Properties();
            PropertiesSalary.load(is);
            AdresseServeur = PropertiesSalary.getProperty("ADRESSE_IP_SERVEUR");
            Port = Integer.valueOf(PropertiesSalary.getProperty("PORT_SALARY"));
            Separator = PropertiesSalary.getProperty("SEP");
            PathKeyStore = PropertiesSalary.getProperty("PATH_KEYSTORE");
            PasswordKeyStore = PropertiesSalary.getProperty("PASSWORD_KEYSTORE");
            PasswordKey = PropertiesSalary.getProperty("PASSWORD_KEY");
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("FileNotFoundException");
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            System.err.println("IOException LoadPropertiesFile");
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void ConnexionServeur()
    {
        try 
        {
            // 1. Keystore
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(PathKeyStore), PasswordKeyStore.toCharArray());
            // 2. Contexte
            SSLContext ctx = SSLContext.getInstance("SSLv3");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, PasswordKey.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            // 3. Factory
            SSLSocketFactory socketFactory = ctx.getSocketFactory();
            // 4. Socket
            CSocket = (SSLSocket)socketFactory.createSocket(AdresseServeur, Port);
            
            RequeteSAMOP req = new RequeteSAMOP(RequeteSAMOP.REQUEST_CON, null);
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(CSocket.getOutputStream());
            oos.writeObject(req);
            oos.flush();
            
            menuConnexion.setEnabled(true);
        }
        catch(IOException | NoSuchAlgorithmException | KeyStoreException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnrecoverableKeyException | KeyManagementException | CertificateException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void FillComboBoxesDates()
    {
        dcbmMoisEmploye = new DefaultComboBoxModel();
        dcbmMoisLaunchPayments = new DefaultComboBoxModel();
        dcbmMoisAskPayments = new DefaultComboBoxModel();
        
        for(int i = 1; i < 13; i++)
        {
            if(i < 10)
            {
                dcbmMoisEmploye.addElement("0" + String.valueOf(i));
                dcbmMoisLaunchPayments.addElement("0" + String.valueOf(i));
                dcbmMoisAskPayments.addElement("0" + String.valueOf(i));
            }
            else
            {
                dcbmMoisEmploye.addElement(String.valueOf(i));
                dcbmMoisLaunchPayments.addElement(String.valueOf(i));
                dcbmMoisAskPayments.addElement(String.valueOf(i));
            }
        }
        dcbmAnneeEmploye = new DefaultComboBoxModel();
        dcbmAnneeEmploye.addElement("2015");
        dcbmAnneeEmploye.addElement("2016");
        dcbmAnneeLaunchPayments = new DefaultComboBoxModel();
        dcbmAnneeLaunchPayments.addElement("2015");
        dcbmAnneeLaunchPayments.addElement("2016");
        dcbmAnneeAskPayments = new DefaultComboBoxModel();
        dcbmAnneeAskPayments.addElement("2015");
        dcbmAnneeAskPayments.addElement("2016");
        
        cbxMoisByEmploye.setModel(dcbmMoisEmploye);
        cbxAnneeByEmploye.setModel(dcbmAnneeEmploye);
        cbxMoisPayments.setModel(dcbmMoisLaunchPayments);
        cbxAnneePayments.setModel(dcbmAnneeLaunchPayments);
        cbxMoisAsk.setModel(dcbmMoisAskPayments);
        cbxAnneeAsk.setModel(dcbmAnneeAskPayments);
    }
    
    private void FillListSalairesLiquides(ArrayList<String> listSalaires)
    {
        dlmLaunchPayments.clear();
        for(int i = 0; i < listSalaires.size(); i++)
            dlmLaunchPayments.addElement(listSalaires.get(i));
    }
    
    private void FillListSalaires(ArrayList<String> listSalaires)
    {
        dlmAskPayments.clear();
        for(int i = 0; i < listSalaires.size(); i++)
            dlmAskPayments.addElement(listSalaires.get(i));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jLabel1 = new javax.swing.JLabel();
        lblBonjour = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tfdLogin = new javax.swing.JTextField();
        btnLiquidationEmploye = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        btnLiquidationSalaires = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstLaunchPayments = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        btnAfficherListeSalaires = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstAskPayments = new javax.swing.JList();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cbxMoisByEmploye = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        cbxAnneeByEmploye = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        cbxMoisPayments = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        cbxAnneePayments = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        cbxMoisAsk = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cbxAnneeAsk = new javax.swing.JComboBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuConnexion = new javax.swing.JMenu();
        miSeConnecter = new javax.swing.JMenuItem();

        jMenu1.setText("jMenu1");

        jMenuItem1.setText("jMenuItem1");

        jMenu2.setText("jMenu2");

        jMenu5.setText("jMenu5");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Application_Compta_Salary");

        lblBonjour.setText("Veuillez vous connecter !");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Lancer la liquidation du salaire pour un employé");

        jLabel4.setText("Login de l'employé : ");

        btnLiquidationEmploye.setText("Lancer la liquidation du salaire");
        btnLiquidationEmploye.setEnabled(false);
        btnLiquidationEmploye.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiquidationEmployeActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Lancer la liquidation de tous les salaires validés");

        btnLiquidationSalaires.setText("Lancer la liquidation de tous les salaires validés");
        btnLiquidationSalaires.setEnabled(false);
        btnLiquidationSalaires.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLiquidationSalairesActionPerformed(evt);
            }
        });

        lstLaunchPayments.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstLaunchPayments);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Liste des salaires liquidés : ");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Afficher les salaires liquidés pour un mois donné");

        btnAfficherListeSalaires.setText("Afficher les salaires liquidés pour un mois donné");
        btnAfficherListeSalaires.setEnabled(false);
        btnAfficherListeSalaires.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAfficherListeSalairesActionPerformed(evt);
            }
        });

        lstAskPayments.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstAskPayments);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Liste des salaires liquidés pour un mois donné : ");

        jLabel11.setText("Mois : ");

        cbxMoisByEmploye.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel12.setText("Année : ");

        cbxAnneeByEmploye.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel13.setText("Mois : ");

        cbxMoisPayments.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel14.setText("Année : ");

        cbxAnneePayments.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel15.setText("Mois : ");

        cbxMoisAsk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel16.setText("Année : ");

        cbxAnneeAsk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        menuConnexion.setText("Connexion");
        menuConnexion.setEnabled(false);

        miSeConnecter.setText("Se connecter");
        miSeConnecter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSeConnecterActionPerformed(evt);
            }
        });
        menuConnexion.add(miSeConnecter);

        jMenuBar1.add(menuConnexion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfdLogin))
                    .addComponent(btnLiquidationEmploye, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLiquidationSalaires, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator3)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAfficherListeSalaires, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxMoisByEmploye, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxAnneeByEmploye, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxMoisPayments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxAnneePayments, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblBonjour)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxMoisAsk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxAnneeAsk, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBonjour)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfdLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cbxMoisByEmploye, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(cbxAnneeByEmploye, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLiquidationEmploye)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cbxMoisPayments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(cbxAnneePayments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLiquidationSalaires)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cbxMoisAsk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(cbxAnneeAsk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAfficherListeSalaires)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miSeConnecterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSeConnecterActionPerformed
        ConnectionForm cf = new ConnectionForm(this, true);
        cf.setVisible(true);
        
        String strRequest = cf.getLogin() + Separator + cf.getPassword();
        try 
        {
            ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
            RequeteSAMOP req = new RequeteSAMOP(RequeteSAMOP.REQUEST_LOGIN, strRequest);
            oos.writeObject(req);
            oos.flush();
            
            /* Attente de réponse */
            ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
            ReponseSAMOP rep = (ReponseSAMOP)ois.readObject();
            
            if(rep.getCode() == ReponseSAMOP.LOGIN_OK)
            {
                lblBonjour.setText("Bonjour " + cf.getLogin() + ", que voulez-vous faire ?");
                btnLiquidationEmploye.setEnabled(true);
                btnLiquidationSalaires.setEnabled(true);
                btnAfficherListeSalaires.setEnabled(true);
            }
            else
                JOptionPane.showMessageDialog(this, (String)rep.getResult(), "Erreur lors de la connexion au serveur", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_miSeConnecterActionPerformed

    private void btnLiquidationEmployeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiquidationEmployeActionPerformed
        if(!tfdLogin.getText().isEmpty())
        {
            LaunchPayment lp = new LaunchPayment(tfdLogin.getText(), (String)cbxMoisByEmploye.getSelectedItem(), (String)cbxAnneeByEmploye.getSelectedItem());
            try 
            {
                ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                RequeteSAMOP req = new RequeteSAMOP(RequeteSAMOP.REQUEST_LAUNCH_PAYMENT, lp);
                oos.writeObject(req);
                oos.flush();

                /* Attente de réponse */
                ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                ReponseSAMOP rep = (ReponseSAMOP)ois.readObject();

                if(rep.getCode() == ReponseSAMOP.REPONSE_OK)
                    JOptionPane.showMessageDialog(this, (String)rep.getResult(), "Liquidation d'un salaire", JOptionPane.INFORMATION_MESSAGE);
                else
                    JOptionPane.showMessageDialog(this, (String)rep.getResult(), "Erreur lors de la liquidation d'un salaire", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException ex)
            {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (ClassNotFoundException ex)
            {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "Insérez le login d'un employé", "Erreur", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnLiquidationEmployeActionPerformed

    private void btnLiquidationSalairesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLiquidationSalairesActionPerformed
        dlmLaunchPayments.clear();
        LaunchPayments lp = new LaunchPayments((String)cbxMoisPayments.getSelectedItem(), (String)cbxAnneePayments.getSelectedItem());
        try 
        {
            ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
            RequeteSAMOP req = new RequeteSAMOP(RequeteSAMOP.REQUEST_LAUNCH_PAYMENTS, lp);
            oos.writeObject(req);
            oos.flush();

            /* Attente de réponse */
            ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
            ReponseSAMOP rep = (ReponseSAMOP)ois.readObject();

            if(rep.getCode() == ReponseSAMOP.REPONSE_OK)
            {
                ArrayList<String> listSalaires = (ArrayList<String>)rep.getResult();
                FillListSalairesLiquides(listSalaires);
            }
            else
                JOptionPane.showMessageDialog(this, (String)rep.getResult(), "Erreur lors de la liquidation des salaires", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLiquidationSalairesActionPerformed

    private void btnAfficherListeSalairesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAfficherListeSalairesActionPerformed
        dlmAskPayments.clear();
        AskPayments ap =  new AskPayments((String)cbxMoisAsk.getSelectedItem(), (String)cbxAnneeAsk.getSelectedItem());
        try 
        {
            ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
            RequeteSAMOP req = new RequeteSAMOP(RequeteSAMOP.REQUEST_ASK_PAYMENTS, ap);
            oos.writeObject(req);
            oos.flush();

            /* Attente de réponse */
            ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
            ReponseSAMOP rep = (ReponseSAMOP)ois.readObject();

            if(rep.getCode() == ReponseSAMOP.REPONSE_OK)
            {
                ArrayList<String> listSalaires = (ArrayList<String>)rep.getResult();
                FillListSalaires(listSalaires);
            }
            else
                JOptionPane.showMessageDialog(this, (String)rep.getResult(), "Erreur lors de la récupération des salaires liquidés", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAfficherListeSalairesActionPerformed

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
    private javax.swing.JButton btnAfficherListeSalaires;
    private javax.swing.JButton btnLiquidationEmploye;
    private javax.swing.JButton btnLiquidationSalaires;
    private javax.swing.JComboBox cbxAnneeAsk;
    private javax.swing.JComboBox cbxAnneeByEmploye;
    private javax.swing.JComboBox cbxAnneePayments;
    private javax.swing.JComboBox cbxMoisAsk;
    private javax.swing.JComboBox cbxMoisByEmploye;
    private javax.swing.JComboBox cbxMoisPayments;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblBonjour;
    private javax.swing.JList lstAskPayments;
    private javax.swing.JList lstLaunchPayments;
    private javax.swing.JMenu menuConnexion;
    private javax.swing.JMenuItem miSeConnecter;
    private javax.swing.JTextField tfdLogin;
    // End of variables declaration//GEN-END:variables
}
