/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transporteurs;

import java.io.Serializable;

/**
 *
 * @author Julien
 */
public class Bateaux implements Serializable
{
    private String IdBateau;
    private String Destination;
    
    public Bateaux()
    {
        IdBateau = null;
        Destination = null;
    }
    
    public Bateaux(String IdBateau, String Destination)
    {
        this.IdBateau = IdBateau;
        this.Destination = Destination;
    }

    /**
     * @return the IdBateau
     */
    public String getIdBateau() {
        return IdBateau;
    }

    /**
     * @param idBateau the IdBateau to set
     */
    public void setIdBateau(String idBateau) {
        this.IdBateau = idBateau;
    }

    /**
     * @return the Destination
     */
    public String getDestination() {
        return Destination;
    }

    /**
     * @param Destination the Destination to set
     */
    public void setDestination(String Destination) {
        this.Destination = Destination;
    }
}
