/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import java.util.ArrayList;

/**
 *
 * @author Julien
 */
public class Camion
{
    private String Immatriculation;
    private ArrayList<Container> ListContainers;
    
    public Camion()
    {
        this.Immatriculation = null;
        this.ListContainers = new ArrayList<>();
    }
    
    public Camion(String Immatriculation)
    {
        this.Immatriculation = Immatriculation;
        this.ListContainers = new ArrayList<>();
    }
    
    public Camion(String Immatriculation, Container container)
    {
        this.Immatriculation = Immatriculation;
        this.ListContainers = new ArrayList<>();
        this.ListContainers.add(container);
    }
    
    public Camion(String Immatriculation, ArrayList<Container> ListContainers)
    {
        this.Immatriculation = Immatriculation;
        this.ListContainers = new ArrayList<>();
        this.ListContainers.addAll(ListContainers);
    }
    
    public void AddContainer(Container c)
    {
        this.ListContainers.add(c);
    }
    
    public void RemoveContainer(Container c)
    {
        this.ListContainers.remove(c);
    }
    
    public int CountContainers()
    {
        return this.ListContainers.size();
    }

    /**
     * @return the Immatriculation
     */
    public String getImmatriculation() {
        return Immatriculation;
    }

    /**
     * @param Immatriculation the Immatriculation to set
     */
    public void setImmatriculation(String Immatriculation) {
        this.Immatriculation = Immatriculation;
    }

    /**
     * @return the ListContainers
     */
    public ArrayList<Container> getListContainers() {
        return ListContainers;
    }

    /**
     * @param ListContainers the ListContainers to set
     */
    public void setListContainers(ArrayList<Container> ListContainers) {
        this.ListContainers = ListContainers;
    }
}
