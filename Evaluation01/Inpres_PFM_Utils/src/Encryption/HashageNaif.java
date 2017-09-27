/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Encryption;

/**
 *
 * @author Julien
 */
public class HashageNaif
{
    public static int GetMessageDigest(String Message)
    {
        int SaltedDigest = 0;
        
        for(int i = 0; i < Message.length(); i++)
            SaltedDigest += (int)Message.charAt(i);
        
        return SaltedDigest % 67;
    }
}
