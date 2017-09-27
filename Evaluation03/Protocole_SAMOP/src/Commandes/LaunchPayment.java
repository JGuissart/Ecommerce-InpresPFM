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
public class LaunchPayment implements Serializable
{
    private String Login;
    private String Mois;
    private String Annee;

    public LaunchPayment(String Login, String Mois, String Annee) {
        this.Login = Login;
        this.Mois = Mois;
        this.Annee = Annee;
    }

    /**
     * @return the Login
     */
    public String getLogin() {
        return Login;
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
