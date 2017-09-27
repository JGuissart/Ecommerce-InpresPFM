/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TransporteursSerializable;

import Transporteurs.Bateaux;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julien
 */
public final class BateauxSerializable
{
    private ArrayList<Bateaux> ArrayListBateaux;
    private FileOutputStream fos;
    private String sep;
    private String cheminFichier;
    
    public BateauxSerializable()
    {
        sep = System.getProperty("file.separator");
        cheminFichier = System.getProperty("user.home") + sep + "Documents" + sep + "NetBeansProjects" + sep + "RTI" + sep + "Serveur_Bateaux" + sep + "BateauxAmarres.dat";

        Deserialize();
    }
    
    public BateauxSerializable(Bateaux b)
    {
        sep = System.getProperty("file.separator");
        cheminFichier = System.getProperty("user.home") + sep + "Documents" + sep + "NetBeansProjects" + sep + "RTI" + sep + "Serveur_Bateaux" + sep + "BateauxAmarres.dat";

        Deserialize();
        ArrayListBateaux.add(b);
        Serialize();
    }
    
    public BateauxSerializable(ArrayList<Bateaux> ListBateaux)
    {
        sep = System.getProperty("file.separator");
        cheminFichier = System.getProperty("user.home") + sep + "Documents" + sep + "NetBeansProjects" + sep + "RTI" + sep + "Serveur_Bateaux" + sep + "BateauxAmarres.dat";

        Deserialize();
        for(Bateaux b : ListBateaux)
        {
            if(!ArrayListBateaux.contains(b))
                ArrayListBateaux.add(b);
        }
        
        Serialize();
    }
    
    private void Serialize()
    {
        try
        {
            fos = new FileOutputStream(cheminFichier);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(getArrayListBateaux());
            oos.flush();
            oos.close();
        }
        catch(NotSerializableException e)
        {
            System.out.println("Composante non Serializable ! : " + e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public void Serialize(ArrayList<Bateaux> arrayListBateau)
    {
        try
        {
            fos = new FileOutputStream(cheminFichier);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(arrayListBateau);
            oos.flush();
            oos.close();
        }
        catch(NotSerializableException e)
        {
            System.out.println("Composante non Serializable ! : " + e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void Deserialize()
    {
        try 
        {
            FileInputStream fis = new FileInputStream(cheminFichier);
            ObjectInputStream ois;
            try 
            {
                ois = new ObjectInputStream(fis);
                try 
                {
                    setArrayListBateaux((ArrayList<Bateaux>)ois.readObject());
                } 
                catch (ClassNotFoundException ex) 
                {
                    System.out.println("BateauxSerializable Error Class Not Found > " + ex.getMessage());
                }
                ois.close();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(BateauxSerializable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch (FileNotFoundException ex) 
        {
            System.out.println("Aucun bateaux amarrés au port.");
            ArrayListBateaux = new ArrayList<>();
        }
    }

    /**
     * @return the ArrayListBateaux
     */
    public ArrayList<Bateaux> getArrayListBateaux() {
        return ArrayListBateaux;
    }

    /**
     * @param ArrayListBateaux the ArrayListBateaux to set
     */
    public void setArrayListBateaux(ArrayList<Bateaux> ArrayListBateaux) {
        this.ArrayListBateaux = ArrayListBateaux;
    }
}
