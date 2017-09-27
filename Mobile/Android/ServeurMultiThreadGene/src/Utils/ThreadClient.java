/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 *
 * @author Julien
 */
public class ThreadClient extends Thread
{
    private SourceTaches tachesAExecuter;
    private String nom;
    private Runnable tacheEnCours;
    
    public ThreadClient(SourceTaches st, String n)
    {
        tachesAExecuter = st;
        nom = n;
    }
    
    @Override
    public void run()
    {
        while (!isInterrupted())
        {
            try
            {
                System.out.println("Thread client avant get");
                tacheEnCours = tachesAExecuter.getTache();
            }
            catch (InterruptedException e)
            {
                System.out.println("Interruption : " + e.getMessage());
            }
            System.out.println("Avant lancement de la tâche du threadclient");
            tacheEnCours.run();
            System.out.println("Après lancement de la tâche du threadclient");
        }
    }
}
