/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applic_mail;

import MailPackage.PieceJointe;
import MailPackage.ThreadPolling;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author Julien
 */
public class MainForm extends javax.swing.JFrame
{
    private Properties PropertiesMail;
    private String Serveur;
    static String Charset;
    static String HostIn;
    static String HostOut;
    static String AdresseServeur;
    public String Expediteur;
    public Folder folder = null;
    public Store store;
    public Session session;
    public Message[] CurrentInbox;
    public boolean MultiPart = false;
    public ThreadPolling TP;
	
    public MainForm()
    {
        initComponents();
        LoadPropertiesFile();
    }
    
    public MainForm(String Serveur)
    {
        initComponents();
        this.Serveur = Serveur;
        LoadPropertiesFile();
    }
    
    private void LoadPropertiesFile()
    {
        File f = new File("Applic_Mail.properties");
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
            propToCreate.put("HOST_IN_GMAIL", "pop.gmail.com");
            propToCreate.put("HOST_OUT_GMAIL", "smtp.gmail.com");
            propToCreate.put("HOST_IN_U2", "u2.tech.hepl.local");
            propToCreate.put("HOST_OUT_U2", "u2.tech.hepl.local");
            propToCreate.put("SERVEUR_U2", "10.59.26.134");
            propToCreate.put("HOST_IN_INXS", "inxs.aileinfo");
            propToCreate.put("HOST_OUT_INXS", "inxs.aileinfo");
            propToCreate.put("SERVEUR_INXS", "10.43.4.165");
            propToCreate.put("CHARSET", "iso-8859-1");
            propToCreate.put("ATTENTE_POLLING", "300000"); // 5 minutes
            
            try
            {
                propToCreate.store(os, "Applic_Mail");
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
            PropertiesMail = new Properties();
            getPropertiesMail().load(is);
            
            switch(Serveur)
            {
                case "U2":
                    HostIn = getPropertiesMail().getProperty("HOST_IN_U2");
                    HostOut = getPropertiesMail().getProperty("HOST_OUT_U2");
                    Charset = getPropertiesMail().getProperty("CHARSET");
                    AdresseServeur = getPropertiesMail().getProperty("SERVEUR_U2");
                    break;
                    
                case "INXS":
                    HostIn = getPropertiesMail().getProperty("HOST_IN_INXS");
                    HostOut = getPropertiesMail().getProperty("HOST_OUT_INXS");
                    Charset = getPropertiesMail().getProperty("CHARSET");
                    Serveur = getPropertiesMail().getProperty("SERVEUR_INXS");
                    break;
                    
                case "Gmail":
                    HostIn = getPropertiesMail().getProperty("HOST_IN_GMAIL");
                    HostOut = getPropertiesMail().getProperty("HOST_OUT_GMAIL");
                    Charset = getPropertiesMail().getProperty("CHARSET");
                    break;
                    
                default:
                    HostIn = getPropertiesMail().getProperty("HOST_IN_GMAIL");
                    HostOut = getPropertiesMail().getProperty("HOST_OUT_GMAIL");
                    Charset = getPropertiesMail().getProperty("CHARSET");
                    break;
            }
            System.out.println("Après le switch");
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
    
    public boolean Connect(String login, String password)
    {
        boolean connect = true;
        
        try
        {
            System.out.println("HostIn = " + HostIn);
            System.out.println("HostOut = " + HostOut);
            Properties prop = System.getProperties();
            System.out.println("Création d'une session mail");
            
            prop.put("mail.pop3.host", HostIn);
            prop.put("mail.smtp.host", HostOut);
            prop.put("mail.disable.top", true);
            prop.put("file.encoding", Charset);
            
            if(Serveur.equals("Gmail"))
            {
                prop.put("mail.smtp.port", "587");
                prop.put("mail.smtp.auth", "true");
                prop.put("mail.smtp.starttls.enable", "true");
                session = Session.getInstance(prop, new javax.mail.Authenticator() {
                   @Override
                   protected PasswordAuthentication getPasswordAuthentication() {
                      return new PasswordAuthentication("inpres.guissart", "applicmail123");
                   }
                });
                store = session.getStore("pop3s");
            }
            else
            {
                session = Session.getDefaultInstance(prop, null);
                store = session.getStore("pop3");
            }
            
            store.connect(HostIn, login, password);
            TP = new ThreadPolling(store, this);
            TP.start(); // Lance la méthode run qui appelle la méthode Load() -> MAJ des mails reçus dans la JTable
        }
        catch (NoSuchProviderException ex) 
        {
            connect = false;
            System.out.println("NoSuchProviderException");
            System.err.println(ex.getMessage());
        }
        catch (MessagingException ex) 
        {
            connect = false;
            System.out.println("MessagingException");
            System.err.println(ex.getMessage());
        }
       
        Expediteur = login;
        return connect;     
    }
    
    public boolean Send(String destinataire, String sujet, String message, ArrayList<PieceJointe> listPiecesJointes)
    {
        boolean bEnvoiOk = true;
        System.out.println("Entree dans le send");
        
        try
        {
            MimeMessage MM = new MimeMessage(session); // Création du message qui sera envoyé
            MM.setFrom(new InternetAddress(Expediteur));
            MM.setRecipient(Message.RecipientType.TO, new InternetAddress(destinataire));
            MM.setSubject(sujet);
            MM.setSentDate(new Date());
            
            System.out.println("Debut du multipart");
            Multipart MP = new MimeMultipart();
            
            System.out.println("Ajout du texte");
            MimeBodyPart MBP = new MimeBodyPart();
            MBP.setText(message);
            MP.addBodyPart(MBP);
            
            if(MultiPart == true)
            {
                System.out.println("Ajout des pièces jointes dans le mail ...");
                for(PieceJointe PJ : listPiecesJointes)
                {
                    MBP = JoindrePiece(PJ);
                    MP.addBodyPart(MBP);
                }
            }
            
            MM.setContent(MP);
            
            System.out.println("Envoi du message ...");
            Transport.send(MM);
            System.out.println("Message envoyé !");
        }
        catch (Exception ex)
        {
            bEnvoiOk = false;
            System.out.println("!!!!! Erreur envoi : " + ex.getMessage() + " !!!!!");
        }
        
        return bEnvoiOk;
    }
    
    public MimeBodyPart JoindrePiece(PieceJointe pieceJointe)
    {
        MimeBodyPart MBP = null;
        System.out.println("Path pièce jointe = " + pieceJointe.getPath());
        try 
        {
            MBP = new MimeBodyPart();
            DataSource ds = new FileDataSource(pieceJointe.getPath());
            MBP.setDataHandler(new DataHandler(ds));
            MBP.setFileName(pieceJointe.getName());
        }
        catch (MessagingException ex)
        {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return MBP;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnAfficher = new javax.swing.JButton();
        lblNombreMessages = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        lblInpresMail = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInbox = new javax.swing.JTable();
        lblInbox = new javax.swing.JLabel();
        btnNouveauMessage = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnAfficher.setText("Afficher");
        btnAfficher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAfficherActionPerformed(evt);
            }
        });

        lblNombreMessages.setText("Nombre de messages :");

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        lblInpresMail.setFont(new java.awt.Font("Segoe Keycaps", 3, 18)); // NOI18N
        lblInpresMail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInpresMail.setText("INPRES MAIL");

        tblInbox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "FROM", "SUBJECT", "DATE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInbox.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblInbox);

        lblInbox.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        lblInbox.setText("INBOX");

        btnNouveauMessage.setText("Nouveau Message");
        btnNouveauMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNouveauMessageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAfficher))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNombreMessages, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNouveauMessage))
                    .addComponent(lblInpresMail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInbox, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInpresMail)
                .addGap(25, 25, 25)
                .addComponent(lblInbox)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombreMessages)
                    .addComponent(btnNouveauMessage))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnAfficher))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAfficherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAfficherActionPerformed
        if(this.tblInbox.getSelectedRow() > -1)
        {
            int i = this.tblInbox.getSelectedRow();
            Message ToDisplay = CurrentInbox[i];
            DetailsMessageForm DMF = new DetailsMessageForm(ToDisplay);
            DMF.setVisible(true);
        }
    }//GEN-LAST:event_btnAfficherActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        TP.load();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnNouveauMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNouveauMessageActionPerformed
        WriteMessageForm WMF = new WriteMessageForm(this);
        WMF.setVisible(true);
    }//GEN-LAST:event_btnNouveauMessageActionPerformed

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
    private javax.swing.JButton btnAfficher;
    private javax.swing.JButton btnNouveauMessage;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInbox;
    private javax.swing.JLabel lblInpresMail;
    private javax.swing.JLabel lblNombreMessages;
    private javax.swing.JTable tblInbox;
    // End of variables declaration//GEN-END:variables

    public JLabel getLabelNombreMessage()
    {
        return lblNombreMessages;
    }
    
    public JTable getTablelInbox()
    {
        return tblInbox;
    }

    /**
     * @return the PropertiesMail
     */
    public Properties getPropertiesMail() {
        return PropertiesMail;
    }

}
