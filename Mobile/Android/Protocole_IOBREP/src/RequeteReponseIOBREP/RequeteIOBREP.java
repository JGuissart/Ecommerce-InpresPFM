/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteReponseIOBREP;

import AccessBD.BeanBDAccess;
import Queries.QuerySelect;
import Queries.QueryUpdate;
import Transporteurs.Bateaux;
import TransporteursSerializable.BateauxSerializable;
import Utils.ConsoleServeur;
import Utils.Convert;
import static Utils.Convert.ResultSetToString;
import Utils.Requete;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julien
 */
public class RequeteIOBREP implements Requete, Serializable
{
    public static String BeanCompta = "_$BDCOMPTA";
    public static String BeanContainers = "_$CSVCONTAINERS";
    public static String PropertiesFile = "_$PROPBATEAUX";
    public static int REQUEST_DEC = -1;
    public static int REQUEST_CON = 0;
    public static int REQUEST_LOGIN = 1;
    public static int REQUEST_GET_CONTAINERS = 2;
    public static int REQUEST_HANDLE_CONTAINER_OUT = 3;
    public static int REQUEST_END_CONTAINER_OUT = 4;
    public static int REQUEST_BOAT_ARRIVED = 5;
    public static int REQUEST_HANDLE_CONTAINER_IN = 6;
    public static int REQUEST_END_CONTAINER_IN = 7;
    
    private static String LoginPersonnel = "";
    private static ArrayList<String> ListContainersToBateau = new ArrayList<>(); // Liste remplie lors de l'appel de GET_CONTAINER
    private static ArrayList<String> ListContainersToParc = new ArrayList<>(); // Liste mémorisant les containers à déplacer du bateau vers le parc
    private static ArrayList<String> ListContainersSelected = new ArrayList<>(); // Liste mémorisant les containers à déplacer du parc vers le bateau
    private static String OrdreGetContainers = "";
    private static ArrayList<String> ListEmplacementForContainerToBateau = new ArrayList<>();
    
    private int RequestType;
    private String Parameters;
    private Socket CSocket;
    
    public RequeteIOBREP(int t, String param)
    {
        RequestType = t;
        this.setParameters(param);
    }
    
    public RequeteIOBREP(int t, String param, Socket s)
    {
        RequestType = t;
        this.setParameters(param); 
        CSocket = s;
    }
    
    @Override
    public Runnable createRunnable (final Socket s, final ConsoleServeur cs, final Object param)
    {
        if (RequestType == REQUEST_CON)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteConnexion(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_LOGIN)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteLogin(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_GET_CONTAINERS)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteGetContainers(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_HANDLE_CONTAINER_OUT)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteHandleContainerOut(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_END_CONTAINER_OUT)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteEndContainerOut(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_BOAT_ARRIVED)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteBoatArrived(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_HANDLE_CONTAINER_IN)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteHandleContainerIn(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_END_CONTAINER_IN)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteEndContainerIn(s, cs, param);
                }
            };
        }
        else 
            return null;
    }
    
    private void traiteRequeteConnexion(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        boolean cont = true;
        
        while(cont)
        {
            ObjectInputStream ois = null;
            RequeteIOBREP req = null;
            try
            { 
                System.out.println("Avant réception requête IOBREP");
                ois = new ObjectInputStream(SocketCli.getInputStream());
                req = (RequeteIOBREP)ois.readObject();
                System.out.println("Requete lue par le serveur, instance de " + req.getClass().getName());
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Erreur de def de classe [" + e.getMessage() + "]");
            }
            catch (IOException e)
            {
                System.err.println("Erreur ? [" + e.getMessage() + "]");
                System.exit(-1);
            }
            
            if(req.RequestType == RequeteIOBREP.REQUEST_DEC)
                cont = false;
            else
            {
                Runnable travail = req.createRunnable(SocketCli, cs, param);
                if (travail != null)
                {
                    travail.run();
                    System.out.println("Tâche envoyée");
                }
                else
                    System.out.println("Pas de tâche à envoyer");

                System.out.println("Après réception requête IOBREP");
            }
        }
    }

    private void traiteRequeteLogin(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        // Attend LOGIN#PASSWORD
        
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#LOGIN#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        System.out.println("TEST GROUPE : " + Thread.currentThread().getThreadGroup().getParent().getName());
        cs.TraceEvenements(comm);
        
        String[] log = this.getParameters().split("#");
        HashMap hm = (HashMap)param;
        BeanBDAccess bdCompta = (BeanBDAccess)hm.get(RequeteIOBREP.BeanCompta);
        QuerySelect qs = new QuerySelect();
        qs.AddSelect("*");
        qs.AddFrom("personnel");
        qs.AddWhere("Login = '" + log[0] + "' AND Password = '" + log[1] + "'");
        
        try 
        {
            ResultSet rs = bdCompta.Select(qs);
            ReponseIOBREP rep;
            if(!rs.first())
                rep = new ReponseIOBREP(ReponseIOBREP.LOGIN_KO, null);
            else
            {
                String strResult = Convert.ResultSetToString(rs) + "#" + Convert.CHAR_COL;
                rep = new ReponseIOBREP(ReponseIOBREP.LOGIN_OK, strResult);
                LoginPersonnel = log[0];
            }
            rs.close();
                
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteGetContainers(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        // Attend ORDRE#DESTINATION
        
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#GET_CONTAINERS#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
       
        boolean bValide = false;
 
        HashMap hm = (HashMap)param;
        BeanBDAccess bdContainers = (BeanBDAccess)hm.get(RequeteIOBREP.BeanContainers);
       
        String[] reponse = this.getParameters().split("#");
        QuerySelect qsContainers = new QuerySelect();
        qsContainers.AddSelect("IDCONTAINER");
        qsContainers.AddFrom("\"Containers.csv\"");

        switch (reponse[1])
        {
            case "RANDOM":
                qsContainers.AddWhere("DESTINATION = '" + reponse[0] + "'");
                OrdreGetContainers = reponse[1];
                bValide = true;
                break;
            case "FIRST":
                bValide = true;
                qsContainers.AddWhere("DESTINATION = '" + reponse[0] + "' ORDER BY DATEARRIVEE");
                OrdreGetContainers = reponse[1];
                break;
            default:
                OrdreGetContainers = "UNKNOWN";
                bValide = false;
                break;
        }
        
        ReponseIOBREP rep = null;

        if(!bValide)
            rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_KO, "BAD_ARGUMENT");
        else
        {
            try
            {
                ResultSet rs = bdContainers.Select(qsContainers);
                
                if(!rs.first())
                    rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_KO, "NO_DATA_FOUND");
                else
                {
                    ListContainersToBateau.clear(); // On clear la liste pour avoir la dernière version des containers se trouvant dans le fichier .csv
                    String strResult = ResultSetToString(rs) + "#" + Convert.CHAR_ROW;
                    System.out.println(strResult);
                    
                    rs.beforeFirst();
                    
                    while(rs.next())
                        ListContainersToBateau.add(rs.getString("IDCONTAINER"));
                    
                    rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_OK, strResult);
                }
                
                rs.close();
            }
            catch (SQLException ex)
            {
                Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try 
        {
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteHandleContainerOut(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        // Attend l'identifiant du container
        
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#HANDLE_CONTAINER_OUT#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        String idContainer = this.getParameters();
        ReponseIOBREP rep = null;
        
        if(OrdreGetContainers.equals("FIRST"))
        {
            if(!ListContainersToBateau.get(0).equals(idContainer))
                rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_KO, "NOT_THE_FIRST_CONTAINER");
            else
            {
                ListContainersSelected.add(idContainer);
                ListContainersToBateau.remove(0); // On retire le 1er élément pour le prochain HANDLE_CONTAINER_OUT
                rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_OK, "CONTAINER_SELECTED");
            }
        }
        else
        {
            ListContainersSelected.add(idContainer);
            ListContainersToBateau.remove(idContainer); // On retire l'élément idContainer pour le prochain HANDLE_CONTAINER_OUT
            rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_OK, "CONTAINER_SELECTD");
        }
        
        try 
        {
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteEndContainerOut(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        // N'attend rien du tout
        
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#END_CONTAINER_OUT#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        ReponseIOBREP rep = null;
        
        if(ListContainersSelected.size() > 0) // Si la liste contient des containers à chargé sur le bateau, on fait le traitement
        {
            Random r = new Random();
            int nombre = r.nextInt(200);

            if(nombre % 3 == 0) // % 3 arbitrairement
                rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_KO, "UNKNOWN_PROBLEM");
            else
            {
                HashMap hm = (HashMap)param;
                BeanBDAccess bdContainers = (BeanBDAccess)hm.get(RequeteIOBREP.BeanContainers);

                try
                {
                    for (String idContainer : ListContainersSelected)
                    {
                        System.out.println("idContainer = " + idContainer);
                        QueryUpdate qu = new QueryUpdate();
                        qu.setTable("\"Containers.csv\"");
                        qu.AddValue("IDCONTAINER", "NULL");
                        qu.AddWhere("IDCONTAINER = '" + idContainer + "'");
                        bdContainers.Update(qu);
                        bdContainers.Commit();
                    }
                }
                catch(SQLException ex)
                {
                    Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ListContainersSelected.clear(); // On clear la liste pour le prochain tour
                rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_OK, "CONTAINERS_ON_THE_BOAT");
            }
        }
        else
            rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_KO, "NO_CONTAINER_SELECTED");
        
        try 
        {
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteBoatArrived(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        // Attends IDBATEAU#DESTINATION
        
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#BOAT_ARRIVED#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        String[] reponse = this.getParameters().split("#");
        Bateaux bateauAmarre = new Bateaux(reponse[0], reponse[1]);
        
        BateauxSerializable bs = new BateauxSerializable(bateauAmarre);
        
        ReponseIOBREP rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_OK, null);

        try
        {
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteHandleContainerIn(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        // Attend l'identifiant du container
        
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#HANDLE_CONTAINER_IN#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponseIOBREP rep = null;
        
        QuerySelect qsContainers = new QuerySelect();
        qsContainers.AddSelect("EMPLACEMENT");
        qsContainers.AddFrom("\"Containers.csv\"");
        qsContainers.AddWhere("ETAT = 0 LIMIT 1"); // LIMIT 1 = le 1er tuple
        
        HashMap hm = (HashMap)param;
        BeanBDAccess bdContainers = (BeanBDAccess)hm.get(RequeteIOBREP.BeanContainers);
        
        try
        {
            ResultSet rs = bdContainers.Select(qsContainers);

            if(!rs.first())
                rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_KO, "NO_PLACE_AVAILABLE");
            else
            {
                rs.beforeFirst();
                
                while(rs.next())
                    ListEmplacementForContainerToBateau.add(rs.getString("EMPLACEMENT"));
                
                ListContainersToParc.add(this.getParameters());
                
                QueryUpdate quContainers = new QueryUpdate();
                quContainers.setTable("\"Containers.csv\"");
                quContainers.AddWhere("EMPLACEMENT = '" + rs.getString("EMPLACEMENT") + "'");
                quContainers.AddValue("ETAT", "1");
                bdContainers.Update(quContainers);
                bdContainers.Commit();

                rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_OK, rs.getString("EMPLACEMENT"));
            }
            
            rs.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try 
        {
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteEndContainerIn(Socket SocketCli, ConsoleServeur cs, Object param)
    {
        // N'attend rien du tout
        
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#END_CONTAINER_IN#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        ReponseIOBREP rep = null;
        
        HashMap hm = (HashMap)param;
        BeanBDAccess bdContainers = (BeanBDAccess)hm.get(RequeteIOBREP.BeanContainers);
        
        try
        {
            if(ListEmplacementForContainerToBateau.size() == ListContainersToParc.size())
            {
                System.out.println("Taille ListEmplacementForContainerToBateau et ListContainersToParc = " + ListEmplacementForContainerToBateau.size());
                
                for (int i = 0; i < ListEmplacementForContainerToBateau.size(); i++)
                {
                    System.out.println("IdContainer = " + ListContainersToParc.get(i));
                    System.out.println("Emplacement = " + ListEmplacementForContainerToBateau.get(i));
                    QueryUpdate qu = new QueryUpdate();
                    qu.setTable("\"Containers.csv\"");
                    qu.AddValue("IDCONTAINER", "'" + ListContainersToParc.get(i) + "'");
                    qu.AddValue("DESTINATION", "NULL");
                    qu.AddValue("ETAT", "2");
                    qu.AddWhere("EMPLACEMENT = '" + ListEmplacementForContainerToBateau.get(i) + "'");
                    bdContainers.Update(qu);
                    bdContainers.Commit();
                }
                
                rep = new ReponseIOBREP(ReponseIOBREP.REPONSE_OK, null);
            }           
        }
        catch(SQLException ex)
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try 
        {
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteIOBREP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the Parameters
     */
    public String getParameters() {
        return Parameters;
    }

    /**
     * @param Parameters the Parameters to set
     */
    public void setParameters(String Parameters) {
        this.Parameters = Parameters;
    }
}