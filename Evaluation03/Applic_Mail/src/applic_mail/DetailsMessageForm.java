/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applic_mail;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Julien
 */
public class DetailsMessageForm extends javax.swing.JFrame
{
    private Multipart MultiPart;
    private DefaultListModel dlm;
    private ArrayList<Part> ListPart = null;
    public static String Sortie = System.getProperty("user.dir") + System.getProperty("file.separator") + "Attachments" + System.getProperty("file.separator");
    /**
     * Creates new form DetailsMessageForm
     */
    public DetailsMessageForm()
    {
        initComponents();
        ListPart = new ArrayList();
        dlm = new DefaultListModel();
        this.lstPiecesJointes.setModel(dlm);
    }
    
    public DetailsMessageForm(Message message)
    {
        try 
        {
            initComponents();
            dlm = new DefaultListModel();
            ListPart = new ArrayList();
            this.lstPiecesJointes.setModel(dlm);
            
            this.tfdFrom.setText(message.getFrom()[0].toString());
            this.tfdSubject.setText(message.getSubject());
            this.tfdDate.setText(message.getSentDate().toString());
            
            if (message.isMimeType("text/plain"))
            {
                System.out.println("Text plain");
                this.txtarMessage.setText((String)message.getContent());
            }
            else
            {
                System.out.println("Multipart");
                MultiPart = (Multipart)message.getContent();
                RecupererContenu();
            }
        }
        catch (MessagingException | IOException ex) 
        {
            Logger.getLogger(DetailsMessageForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void RecupererContenu()
    {
        try
        {
            System.out.println("Nombre de parts du mail : " + MultiPart.getCount());
            
            for (int j = 0; j < MultiPart.getCount(); j++)
            {
                System.out.println("--Composante n° " + j);
                Part p = MultiPart.getBodyPart(j);
                String d = p.getDisposition();
                
                if (p.isMimeType("text/plain"))
                    this.txtarMessage.setText((String)p.getContent());
                
                if (d != null && d.equalsIgnoreCase(Part.ATTACHMENT))
                {   
                    ListPart.add(p);
                    String nf = p.getFileName();
                    dlm.addElement(nf);  
                }
            } 
        }
        catch (MessagingException ex)
        {
            Logger.getLogger(DetailsMessageForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(DetailsMessageForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblMessage = new javax.swing.JLabel();
        btnRetour = new javax.swing.JButton();
        lblPiecesJointes = new javax.swing.JLabel();
        lblFrom = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstPiecesJointes = new javax.swing.JList();
        lblDate = new javax.swing.JLabel();
        lblSubject = new javax.swing.JLabel();
        btnRecuperer = new javax.swing.JButton();
        tfdSubject = new javax.swing.JTextField();
        tfdDate = new javax.swing.JTextField();
        tfdFrom = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtarMessage = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblMessage.setText("Message :");

        btnRetour.setText("Retour");
        btnRetour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetourActionPerformed(evt);
            }
        });

        lblPiecesJointes.setText("Pièces Jointes : ");

        lblFrom.setText("From :");

        jScrollPane2.setViewportView(lstPiecesJointes);

        lblDate.setText("Date :");

        lblSubject.setText("Subject :");

        btnRecuperer.setText("Recupérer");
        btnRecuperer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecupererActionPerformed(evt);
            }
        });

        tfdSubject.setEditable(false);

        tfdDate.setEditable(false);

        tfdFrom.setEditable(false);

        txtarMessage.setEditable(false);
        txtarMessage.setColumns(20);
        txtarMessage.setRows(5);
        jScrollPane1.setViewportView(txtarMessage);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblSubject, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tfdFrom)
                                .addGap(18, 18, 18)
                                .addComponent(lblDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tfdDate, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tfdSubject)))
                    .addComponent(btnRetour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMessage)
                        .addGap(505, 505, 505)
                        .addComponent(lblPiecesJointes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnRecuperer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFrom)
                    .addComponent(lblDate)
                    .addComponent(tfdDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfdFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSubject)
                    .addComponent(tfdSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMessage)
                    .addComponent(lblPiecesJointes))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRecuperer)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(btnRetour)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRetourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetourActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnRetourActionPerformed

    private void btnRecupererActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecupererActionPerformed
        for(Part p : ListPart)
        {
            try 
            {
                if(p.getFileName().equals((String)this.lstPiecesJointes.getSelectedValue())) // On check si le part courant est celui sur lequel on a cliqué
                {
                    InputStream is = null;
                    ByteArrayOutputStream baos = null;
                    try 
                    {
                        is = p.getInputStream();
                        baos = new ByteArrayOutputStream();

                        int c;
                        while ((c = is.read()) != -1) // -1 si on a atteint la fin du flux
                            baos.write(c);

                        baos.flush();

                        String nf = p.getFileName();

                        FileOutputStream fos = new FileOutputStream(Sortie + nf);
                        baos.writeTo(fos); // On récupère la pièce jointe grâce au FileOutputStream dans lequel on a spécifié le chemin et le nom de celle-ci
                        fos.close();
                        System.out.println("Pièce attachée " + nf + " récupérée");

                        JOptionPane.showMessageDialog(this, "Fichier récupéré dans " + Sortie + nf, "Récuperation d'une pièce jointe", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (IOException | MessagingException | HeadlessException ex)
                    {
                        JOptionPane.showMessageDialog(this, "Erreur lors de la récupération d'une pièce jointe !", "Récuperation d'une pièce jointe", JOptionPane.ERROR_MESSAGE);
                    }
                    finally 
                    {
                        try
                        {
                            is.close();
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger(DetailsMessageForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } 
            catch (MessagingException ex)
            {
                Logger.getLogger(DetailsMessageForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnRecupererActionPerformed

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
            java.util.logging.Logger.getLogger(DetailsMessageForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DetailsMessageForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DetailsMessageForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetailsMessageForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DetailsMessageForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRecuperer;
    private javax.swing.JButton btnRetour;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblFrom;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblPiecesJointes;
    private javax.swing.JLabel lblSubject;
    private javax.swing.JList lstPiecesJointes;
    private javax.swing.JTextField tfdDate;
    private javax.swing.JTextField tfdFrom;
    private javax.swing.JTextField tfdSubject;
    private javax.swing.JTextArea txtarMessage;
    // End of variables declaration//GEN-END:variables
}
