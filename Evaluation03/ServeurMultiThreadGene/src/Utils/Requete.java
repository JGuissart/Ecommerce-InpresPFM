/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.net.Socket;

/**
 *
 * @author Julien
 */
public interface Requete
{
    public Runnable createRunnable (Socket s, ConsoleServeur cs, Object param);
}
