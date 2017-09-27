/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Julien
 */
public class ThreadServeur extends Thread
{
    /**
     * @param args the command line arguments
     */
    private int Port;
    private int NombreMaxClients;
    private SourceTaches TachesAExecuter;
    private ConsoleServeur GUIApplication;
    private ServerSocket SSocket = null;
    private Object Param;
    
    public ThreadServeur(int Port, SourceTaches TachesAExecuter, ConsoleServeur GUIApplication, int NombreMaxClients, Object Param)
    {
        this.Port = Port; 
        this.TachesAExecuter = TachesAExecuter; 
        this.GUIApplication = GUIApplication;
        this.NombreMaxClients = NombreMaxClients;
        this.Param = Param;
    }
    
    @Override
    public void run()
    {
        try
        {
            SSocket = new ServerSocket(Port);
        }
        catch (IOException e)
        {
            System.err.println("Erreur de port d'écoute ! ? [" + e + "]");
            System.exit(1);
        }
        
        // Démarrage du pool de threads
        for (int i = 0; i < NombreMaxClients; i++) // NombreMaxClients une propriété du fichier de config
        {
            ThreadClient thr = new ThreadClient (TachesAExecuter, "Thread du pool n°" + String.valueOf(i));
            thr.start();
        }
        // Mise en attente du serveur
        Socket CSocket = null;
        while (!isInterrupted())
        {
            try
            {
                System.out.println("************ Serveur en attente sur le port " + Port +  " ************");
                CSocket = SSocket.accept();
                GUIApplication.TraceEvenements(CSocket.getRemoteSocketAddress().toString() + "#accept#thread serveur");
            }
            catch (IOException e)
            {
                System.err.println("Erreur d'accept ! ? [" + e.getMessage() + "]");
                System.exit(1);
            }
            
            ObjectInputStream ois = null;
            Requete req = null;
            try
            {
                ois = new ObjectInputStream(CSocket.getInputStream());
                req = (Requete)ois.readObject();
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
