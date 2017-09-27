/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteReponseSAMOP;

import AccessBD.BeanBDAccess;
import Commandes.AskPayments;
import Commandes.LaunchPayment;
import Commandes.LaunchPayments;
import Queries.QuerySelect;
import Queries.QueryUpdate;
import Utils.ConsoleServeur;
import Utils.RequeteSSL;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Julien
 */
public class RequeteSAMOP implements RequeteSSL, Serializable
{
    public static String BeanCompta = "_$BDCOMPTA";
    public static int REQUEST_DEC = -1;
    public static int REQUEST_CON = 0;
    public static int REQUEST_LOGIN = 1;
    public static int REQUEST_LAUNCH_PAYMENT = 2;
    public static int REQUEST_LAUNCH_PAYMENTS = 3;
    public static int REQUEST_ASK_PAYMENTS = 4;
    
    private int RequestType;
    private Object Parameters;
    private SSLSocket CSocket;
    
    public RequeteSAMOP(int t, Object param)
    {
        RequestType = t;
        this.setParameters(param);
    }
    
    public RequeteSAMOP(int t, Object param, byte[] signatureComptable)
    {
        RequestType = t;
        this.setParameters(param);
    }
    
    public RequeteSAMOP(int t, Object param, SSLSocket s)
    {
        RequestType = t;
        this.setParameters(param);
        CSocket = s;
    }
    
    @Override
    public Runnable createRunnable(SSLSocket s, ConsoleServeur cs, Object param)
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
        else if (RequestType == REQUEST_LAUNCH_PAYMENT)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteLaunchPayment(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_LAUNCH_PAYMENTS)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteLaunchPayments(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_ASK_PAYMENTS)
        {
            return new Runnable()
            {
                @Override
                public void run()
                {
                    traiteRequeteAskPayments(s, cs, param);
                }
            };
        }
        else 
            return null;
    }
    
    private void traiteRequeteConnexion(SSLSocket SocketCli, ConsoleServeur cs, Object param)
    {
        boolean cont = true;
        
        while(cont)
        {
            ObjectInputStream ois = null;
            RequeteSAMOP req = null;
            try
            { 
                System.out.println("Avant réception requête SAMOP");
                ois = new ObjectInputStream(SocketCli.getInputStream());
                req = (RequeteSAMOP)ois.readObject();
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
            
            if(req.RequestType == RequeteSAMOP.REQUEST_DEC)
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

                System.out.println("Après réception requête SAMOP");
            }
        }
    }

    private void traiteRequeteLogin(SSLSocket SocketCli, ConsoleServeur cs, Object param)
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#LOGIN#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        System.out.println("TEST GROUPE : " + Thread.currentThread().getThreadGroup().getParent().getName());
        cs.TraceEvenements(comm);
        
        ReponseSAMOP rep = null;
        String strParameters = (String)this.getParameters();
        String[] arrayParameters = strParameters.split("#");
        String strLogin = arrayParameters[0];
        String strPassword = arrayParameters[1];
        
        /*
            REQUETE
                démarrage de l'application : un chef compatble se fait reconnaître
                paramètres : nom, password (un mécanisme de digest n'est plus nécessaire, puisque l'on se base sur SSL)
            REPONSE
                oui ou non – validation au moyen de la base BD_ COMPTA
        */
        
        try 
        {
            /* Récupération du mot de passe dans la BD */
            
            HashMap hm = (HashMap)param;
            
            BeanBDAccess bdCompta = (BeanBDAccess)hm.get(RequeteSAMOP.BeanCompta);
            QuerySelect qs = new QuerySelect();
            qs.AddSelect("Password, Fonction");
            qs.AddFrom("personnel");
            qs.AddWhere("Login = '" + strLogin + "'");
            
            ResultSet rs = bdCompta.Select(qs);
            
            if(!rs.first()) // S'il n'y a pas de résultat pour le login passé en paramètre
                rep = new ReponseSAMOP(ReponseSAMOP.LOGIN_KO, "UNKNOWN_USERNAME");
            else
            {
                if(!rs.getString("Fonction").equals("Master") && !rs.getString("Fonction").equals("Chef-comptable"))
                    rep = new ReponseSAMOP(ReponseSAMOP.LOGIN_KO, "PERMISSION_DENIED");
                else
                {
                    if(!strPassword.equals(rs.getString("Password")))
                        rep = new ReponseSAMOP(ReponseSAMOP.LOGIN_KO, "INVALID_PASSWORD");
                    else
                        rep = new ReponseSAMOP(ReponseSAMOP.LOGIN_OK, null);
                }
            }
            
            ObjectOutputStream oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteLaunchPayment(SSLSocket SocketCli, ConsoleServeur cs, Object param)
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#LAUNCH_PAYMENT#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponseSAMOP rep = null;
        
        /*
            REQUETE
                lance la liquidation du salaire d'un employé bien précis
                paramètres : nom de l'employé
            REPONSE
                nom-montant OU "employee not found"
        */
        
        try 
        {
            LaunchPayment lp = (LaunchPayment) this.getParameters();
            HashMap hm = (HashMap)param;
            BeanBDAccess bdCompta = (BeanBDAccess)hm.get(RequeteSAMOP.BeanCompta);
            
            QuerySelect qs = new QuerySelect("Personnel NATURAL JOIN Salaires");
            qs.AddSelect("*");
            qs.AddWhere("Login = '" + lp.getLogin() + "'");
            
            ResultSet rs = bdCompta.Select(qs);
            
            if(!rs.first())
                rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_KO, "UNKNOWN_USERNAME");
            else
            {
                qs = new QuerySelect("Salaires");
                qs.AddSelect("SalaireValide, SalaireVerse");
                qs.AddWhere("Login = '" + lp.getLogin() + "' AND Mois = '" + lp.getMois()+ "' AND Annee = '" + lp.getAnnee()+ "'");
                
                rs = bdCompta.Select(qs);
                
                if(!rs.first())
                    rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_KO, "Il n'y a pas de salaire calculé pour l'employé, le mois et l'année donnés !");
                else
                {
                    if(rs.getShort("SalaireValide") == 0)
                        rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_KO, "La salaire n'a pas été validé pour l'employé, le mois et l'année donnés !");
                    else
                    {
                        if(rs.getShort("SalaireVerse") == 1)
                            rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_KO, "Le salaire pour l'employé, le mois et l'année donnés a déjà été liquidé !");
                        else
                        {
                            QueryUpdate qu = new QueryUpdate("Salaires");
                            qu.AddValue("SalaireVerse", "1");
                            qu.AddWhere("Login = '" + lp.getLogin() + "' AND Mois = '" + lp.getMois()+ "' AND Annee = '" + lp.getAnnee()+ "'");
                            bdCompta.Update(qu);
                            bdCompta.Commit();
                            
                            rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_OK, "Le salaire pour l'employé, le mois et l'année donnés a bien été liquidé !");
                        }
                    }
                }
            }
            
            ObjectOutputStream oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteLaunchPayments(SSLSocket SocketCli, ConsoleServeur cs, Object param)
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#LAUNCH_PAYMENTS#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponseSAMOP rep = null;
        
        /*
            REQUETE
                lance la liquidation de tous les salaires validés
                paramètres : -
            REPONSE
                liste des paiements réalisés (nom - montant)
        */
        
        try 
        {
            LaunchPayments lp = (LaunchPayments) this.getParameters();
            HashMap hm = (HashMap)param;
            BeanBDAccess bdCompta = (BeanBDAccess)hm.get(RequeteSAMOP.BeanCompta);
            
            QuerySelect qs = new QuerySelect("Personnel NATURAL JOIN Salaires");
            qs.AddSelect("Nom, Prenom, MontantBrut, RetraitONSS, RetraitPrecompte");
            qs.AddWhere("Mois = '" + lp.getMois()+ "' AND Annee = '" + lp.getAnnee()+ "' AND SalaireValide = 1 AND SalaireVerse = 0");
            ResultSet rs = bdCompta.Select(qs);
            
            if(!rs.first())
                rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_KO, "Il n'y a pas de salaire validé et non-envoyé pour le mois et l'année donné !");
            else
            {
                ArrayList<String> listRetour = new ArrayList<>();
                rs.beforeFirst();
                while(rs.next())
                {
                    Double salaireNet = rs.getDouble("MontantBrut") - rs.getDouble("RetraitONSS") - rs.getDouble("RetraitPrecompte");
                    String strNom = rs.getString("Prenom") + " " + rs.getString("Nom") + " - " + String.valueOf(new BigDecimal(salaireNet.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    listRetour.add(strNom);
                }
                QueryUpdate qu = new QueryUpdate("Salaires");
                qu.AddValue("SalaireVerse", "1");
                qu.AddWhere("Mois = '" + lp.getMois()+ "' AND Annee = '" + lp.getAnnee()+ "' AND SalaireVerse = 0");
                
                bdCompta.Update(qu);
                bdCompta.Commit();
                
                rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_OK, listRetour);
            }
            
            ObjectOutputStream oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteAskPayments(SSLSocket SocketCli, ConsoleServeur cs, Object param)
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#ASK_PAYMENTS#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        cs.TraceEvenements(comm);
        
        ReponseSAMOP rep = null;
        
        /*
            REQUETE
                demande la liste des paiements de salaires réalisés (nom - montant) pour un mois donné
                paramètres : le mois considéré
            REPONSE
                la liste OU "no payment made for ths month"
        */
        
        try 
        {
            AskPayments ap = (AskPayments) this.getParameters();
            HashMap hm = (HashMap)param;
            BeanBDAccess bdCompta = (BeanBDAccess)hm.get(RequeteSAMOP.BeanCompta);
            
            QuerySelect qs = new QuerySelect("Personnel NATURAL JOIN Salaires");
            qs.AddSelect("Nom, Prenom, MontantBrut, RetraitONSS, RetraitPrecompte");
            qs.AddWhere("Mois = '" + ap.getMois()+ "' AND Annee = '" + ap.getAnnee()+ "' AND SalaireVerse = 1");
            ResultSet rs = bdCompta.Select(qs);
            
            if(!rs.first())
                rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_KO, "Il n'y a pas de salaire envoyé pour le mois et l'année donné !");
            else
            {
                ArrayList<String> listRetour = new ArrayList<>();
                rs.beforeFirst();
                while(rs.next())
                {
                    Double salaireNet = rs.getDouble("MontantBrut") - rs.getDouble("RetraitONSS") - rs.getDouble("RetraitPrecompte");
                    String strNom = rs.getString("Prenom") + " " + rs.getString("Nom") + " - " + String.valueOf(new BigDecimal(salaireNet.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    listRetour.add(strNom);
                }
                
                rep = new ReponseSAMOP(ReponseSAMOP.REPONSE_OK, listRetour);
            }
            
            ObjectOutputStream oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RequeteSAMOP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the Parameters
     */
    public Object getParameters() {
        return Parameters;
    }

    /**
     * @param Parameters the Parameters to set
     */
    public void setParameters(Object Parameters) {
        this.Parameters = Parameters;
    }
}
