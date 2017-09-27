/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MailPackage;

import applic_mail.MainForm;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Julien
 */
public class ThreadPolling extends Thread
{
    public Store store;
    public Folder f;
    public MainForm Caller;
    public Message[] ArrayMessages;
    
    public ThreadPolling(Store store, MainForm Parent) 
    {   
        this.store = store;
        Caller = Parent;
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                load();
                Thread.sleep(Integer.parseInt(Caller.getPropertiesMail().getProperty("ATTENTE_POLLING")));
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(ThreadPolling.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void load()
    {
        try 
        {
            System.out.println("---------- Mise à jour des messages ----------");
            
            if(f == null)
                f = store.getFolder("INBOX");
            else
            {
                if(f.isOpen())
                    f.close(true);
            }
            
            f.open(Folder.READ_ONLY);
            
            ArrayMessages = f.getMessages();
            
            System.out.println("Nombre de messages : " + f.getMessageCount());
            
            Caller.getLabelNombreMessage().setText("Nombre de messages : " + f.getMessageCount());
            
            DefaultTableModel dtm = (DefaultTableModel)Caller.getTablelInbox().getModel();
            Vector v = null;
            
            dtm.setRowCount(0);
            
            for (int i = 0; i < ArrayMessages.length; i++)
            {
                v = new Vector();
                v.add(i);
                v.add(ArrayMessages[i].getFrom()[0]);
                v.add(ArrayMessages[i].getSubject());
                v.add(ArrayMessages[i].getSentDate().toString());
                dtm.addRow(v); 
            }
            
            Caller.CurrentInbox = ArrayMessages;
            System.out.println("Fin des messages");
        }
        catch (Exception ex)
        {
            Logger.getLogger(ThreadPolling.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
