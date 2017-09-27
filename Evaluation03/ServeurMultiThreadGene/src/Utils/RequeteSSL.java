/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import javax.net.ssl.SSLSocket;

/**
 *
 * @author Julien
 */
public interface RequeteSSL 
{
    public Runnable createRunnable (SSLSocket s, ConsoleServeur cs, Object param);
}
