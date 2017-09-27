/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commandes;

import java.io.Serializable;

/**
 *
 * @author Julien
 */
public class LaunchPayments implements Serializable
{
    private String Mois;
    private String Annee;

    public LaunchPayments(String Mois, String Annee) {
        this.Mois = Mois;
        this.Annee = Annee;
    }

    /**
     * @return the Mois
     */
    public String getMois() {
        return Mois;
    }

    /**
     * @return the Annee
     */
    public String getAnnee() {
        return Annee;
    }
}
