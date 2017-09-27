/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author Julien
 */
public class ThreadServeurSSL extends Thread
{
    /**
     * @param args the command line arguments
     */
    private int Port;
    private int NombreMaxClients;
    private SourceTaches TachesAExecuter;
    private ConsoleServeur GUIApplication;
    private SSLServerSocket SSocket = null;
    private Object Param;
    private String PathKeystore;
    private String PasswordKeystore;
    private String PasswordKey;
    
    public ThreadServeurSSL(int Port, SourceTaches TachesAExecuter, ConsoleServeur GUIApplication, int NombreMaxClients, Object Param)
    {
        this.Port = Port; 
        this.TachesAExecuter = TachesAExecuter; 
        this.GUIApplication = GUIApplication;
        this.NombreMaxClients = NombreMaxClients;
        this.Param = Param;
        LoadPropertiesFile();
    }
    
    private void LoadPropertiesFile()
    {
        File f = new File("ThreadServeurSSL.properties");
        if(!f.exists())
        {
            OutputStream os = null;
            try
            {
                os = new FileOutputStream(f);
            }
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Properties propToCreate = new Properties();
            propToCreate.put("PATH_KEYSTORE", "C:\\makecert\\KS_ServeurSalary");
            propToCreate.put("PASSWORD_KEYSTORE", "azerty");
            propToCreate.put("PASSWORD_KEY", "azerty");
            
            try
            {
                propToCreate.store(os, "ThreadServeurSSL");
                os.flush();
            }
            catch (IOException ex)
            {
                Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try
        {
            InputStream is = new FileInputStream(f);
            Properties prop = new Properties();
            prop.load(is);
            PathKeystore = prop.getProperty("PATH_KEYSTORE");
            PasswordKeystore = prop.getProperty("PASSWORD_KEYSTORE");
            PasswordKey = prop.getProperty("PASSWORD_KEY");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run()
    {
        try
        {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream (PathKeystore), PasswordKeystore.toCharArray());
            // 2. Contexte
            SSLContext ctx = SSLContext.getInstance("SSLv3");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, PasswordKey.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            // 3. Factory
            SSLServerSocketFactory fact = ctx.getServerSocketFactory();
            // 4. Socket
            SSocket = (SSLServerSocket) fact.createServerSocket(Port);
        }
        catch (IOException e)
        {
            System.err.println("Erreur de port d'écoute ! ? [" + e + "]");
            System.exit(1);
        } catch (KeyStoreException ex) {
            Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(ThreadServeurSSL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Démarrage du pool de threads
        for (int i = 0; i < NombreMaxClients; i++) // NombreMaxClients une propriété du fichier de config
        {
            ThreadClient thr = new ThreadClient (TachesAExecuter, "Thread du pool n°" + String.valueOf(i));
            thr.start();
        }
        // Mise en attente du serveur
        SSLSocket CSocket = null;
        while (!isInterrupted())
        {
            try
            {
                System.out.println("************ Serveur en attente sur le port " + Port +  " ************");
                CSocket = (SSLSocket) SSocket.accept();
                GUIApplication.TraceEvenements(CSocket.getRemoteSocketAddress().toString() + "#accept#thread serveur");
            }
            catch (IOException e)
            {
                System.err.println("Erreur d'accept ! ? [" + e.getMessage() + "]");
                System.exit(1);
            }
            
            ObjectInputStream ois = null;
            RequeteSSL req = null;
            try
            {
                ois = new ObjectInputStream(CSocket.getInputStream());
                req = (RequeteSSL)ois.readObject();
                System.out.println("Requete lue par le serveur, instance de " + req.getClass().getName());
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Erreur de def de classe [" + e.getMessage() + "]");
            }
            catch (IOException e)
            {
                System.err.println("Erreur ? [" + e.getMessage() + "]");
            }
            System.err.println("Avant req.createRunnable");
            Runnable travail = req.createRunnable(CSocket, GUIApplication, Param);
            System.err.println("Apres req.createRunnable");
            
            if (travail != null)
            {
                TachesAExecuter.recordTache(travail);
                System.out.println("Travail mis dans la file");
            }
            else 
                System.out.println("Pas de mise en file");
        }
    }
}
