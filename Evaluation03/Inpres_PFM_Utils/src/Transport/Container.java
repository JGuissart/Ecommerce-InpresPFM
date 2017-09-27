/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

/**
 *
 * @author Julien
 */
public class Container 
{
    private String Identifiant;
    private String Destination;
    private String NatureContenu;
    private String DangersParticuliers;
    
    public Container()
    {
        this.Identifiant = null;
        this.Destination = null;
        this.NatureContenu = null;
        this.DangersParticuliers = null;
    }
    
    public Container(String Identifiant, String Destination)
    {
        this.Identifiant = Identifiant;
        this.Destination = Destination;
    }
    
    public Container(String Identifiant, String Destination, String NatureContenu, String DangersParticuliers)
    {
        this.Identifiant = Identifiant;
        this.Destination = Destination;
        this.NatureContenu = NatureContenu;
        this.DangersParticuliers = DangersParticuliers;
    }

    /**
     * @return the Identifiant
     */
    public String getIdentifiant() {
        return Identifiant;
    }

    /**
     * @param Identifiant the Identifiant to set
     */
    public void setIdentifiant(String Identifiant) {
        this.Identifiant = Identifiant;
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

    /**
     * @return the NatureContenu
     */
    public String getNatureContenu() {
        return NatureContenu;
    }

    /**
     * @param NatureContenu the NatureContenu to set
     */
    public void setNatureContenu(String NatureContenu) {
        this.NatureContenu = NatureContenu;
    }

    /**
     * @return the DangersParticuliers
     */
    public String getDangersParticuliers() {
        return DangersParticuliers;
    }

    /**
     * @param DangersParticuliers the DangersParticuliers to set
     */
    public void setDangersParticuliers(String DangersParticuliers) {
        this.DangersParticuliers = DangersParticuliers;
    }
}
