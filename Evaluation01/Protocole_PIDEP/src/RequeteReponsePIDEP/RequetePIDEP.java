/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteReponsePIDEP;

import AccessBD.BeanBDAccess;
import Queries.QueryInsert;
import Queries.QuerySelect;
import Utils.ConsoleServeur;
import Utils.Requete;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.apache.commons.math3.stat.inference.TTest;

/**
 *
 * @author Julien
 */
public class RequetePIDEP implements Requete, Serializable
{
    public static String BeanTrafic = "_$BDTRAFIC";
    public static String BeanCompta = "_$BDCOMPTA";
    public static String BeanDecisions = "_$BDDECISION";
    public static int REQUEST_DEC = -1;
    public static int REQUEST_CON = 0;
    public static int REQUEST_LOGIN = 1;
    public static int REQUEST_GET_STAT_DESCR_CONT = 2;
    public static int REQUEST_GET_GR_CONT_REP = 3;
    public static int REQUEST_GET_GR_CONT_COMP = 4;
    public static int REQUEST_GET_STAT_INFER_TEST_CONF = 5;
    public static int REQUEST_GET_STAT_INFER_TEST_HOMOG = 6;
    public static int REQUEST_GET_STAT_INFER_TEST_HOMOG_ANOVA = 7;
    public static int REQUEST_DATABASE = 8;
    
    private int RequestType;
    private Object _Parameters;
    private Socket CSocket;
    
    public RequetePIDEP(int RequestType, Object param)
    {
        this.RequestType = RequestType;
        this.setParameters(param);
    }
    public RequetePIDEP(int RequestType, Object param, Socket CSocket)
    {
        this.RequestType = RequestType;
        this.setParameters(param); 
        this.CSocket = CSocket;
    }

    @Override
    public Runnable createRunnable(final Socket s, final ConsoleServeur cs, final Object param)
    {
        if (RequestType == REQUEST_CON)
        {
            return new Runnable()
            {
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
                public void run()
                {
                    traiteRequeteLogin(s, cs, param);
                }
 
            };
        }
        else if (RequestType == REQUEST_GET_STAT_DESCR_CONT)
        {
            return new Runnable()
            {
                public void run()
                {
                    traiteRequeteGetStatDescrCont(s, cs, param);
                }

            };
        }
        else if (RequestType == REQUEST_GET_GR_CONT_REP)
        {
            return new Runnable()
            {
                public void run()
                {
                    traiteRequeteGetGrContRep(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_GET_GR_CONT_COMP)
        {
            return new Runnable()
            {
                public void run()
                {
                    traiteRequeteGetGrContComp(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_GET_STAT_INFER_TEST_CONF)
        {
            return new Runnable()
            {
                public void run()
                {
                    traiteRequeteGetStatInferTestConf(s, cs, param);
                }
  
            };
        }
        else if (RequestType == REQUEST_GET_STAT_INFER_TEST_HOMOG)
        {
            return new Runnable()
            {
                public void run()
                {
                    traiteRequeteGetStatInferTestHomog(s, cs, param);
                }

            };
        }
        else if (RequestType == REQUEST_GET_STAT_INFER_TEST_HOMOG_ANOVA)
        {
            return new Runnable()
            {
                public void run()
                {
                    traiteRequeteGetStatInferTestHomogAnova(s, cs, param);
                }
            };
        }
        else if (RequestType == REQUEST_DATABASE)
        {
            return new Runnable()
            {
                public void run()
                {
                    traiteRequeteDatabase(s, cs, param);
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
            RequetePIDEP req = null;
            try
            { 
                System.out.println("Avant réception requête PIDEP");
                ois = new ObjectInputStream(SocketCli.getInputStream());
                req = (RequetePIDEP)ois.readObject();
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
            
            if(req.RequestType == RequetePIDEP.REQUEST_DEC)
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

                System.out.println("Après réception requête PIDEP");
            }
        }
    }
    
    private void traiteRequeteLogin(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#LOGIN#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        System.out.println("TEST GROUPE : " + Thread.currentThread().getThreadGroup().getParent().getName());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        
        try 
        {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(SocketCli.getInputStream()));
            
            /* Récupération info du réseaux */
            
            String strLogin = dis.readUTF();
            Long lDate = dis.readLong();
            Double dRandom = dis.readDouble();
            Integer iLongueurDigest = dis.readInt();
            byte[] DigestDistant = new byte[iLongueurDigest];
            dis.readFully(DigestDistant);
            
            /* Récupération du mot de passe dans la BD */
            
            HashMap hm = (HashMap)param;
            
            BeanBDAccess bdCompta = (BeanBDAccess)hm.get(RequetePIDEP.BeanCompta);
            QuerySelect qs = new QuerySelect();
            qs.AddSelect("Password");
            qs.AddFrom("personnel");
            qs.AddWhere("Login = '" + strLogin + "'");
            
            String strPasswordClair = "";
            
            ResultSet rs = bdCompta.Select(qs);
            
            if(!rs.first())
                rep = new ReponsePIDEP(ReponsePIDEP.LOGIN_KO, "UNKNOWN_USERNAME");
            else
            {
                rs.first();
                strPasswordClair = rs.getString("Password");
                
                /* Confection du digest local */
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream bdos = new DataOutputStream(baos);
                bdos.writeLong(lDate);
                bdos.writeDouble(dRandom);

                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(strPasswordClair.getBytes());
                md.update(baos.toByteArray());

                byte[] DigestLocal = md.digest();

                if(MessageDigest.isEqual(DigestLocal, DigestDistant))
                    rep = new ReponsePIDEP(ReponsePIDEP.LOGIN_OK, null);
                else
                    rep = new ReponsePIDEP(ReponsePIDEP.LOGIN_KO, "BAD_PASSWORD");
            }
            
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (IOException ex) 
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteGetStatDescrCont(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#GET_STAT_DESCR_CONT#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        
        String strParametres = (String)this.getParameters();
        String[] arrayParametres = strParametres.split("#");
        int iNombreContainers = Integer.valueOf(arrayParametres[0]);
        String strTypeMouvement = arrayParametres[1];
        String strAnnee = arrayParametres[2];
        
        HashMap hm = (HashMap)param;
        BeanBDAccess bdTrafic = (BeanBDAccess)hm.get(RequetePIDEP.BeanTrafic);
        BeanBDAccess bdDecisions = (BeanBDAccess)hm.get(RequetePIDEP.BeanDecisions);
        
        try 
        {
            QuerySelect qsPoids = new QuerySelect();
            qsPoids.AddFrom("Mouvement");
            qsPoids.AddSelect("Poids");
            if(strTypeMouvement.equals("IN"))
                qsPoids.AddWhere("SUBSTR(DateArrivee, 7) = '" + strAnnee + "' ORDER BY RAND() LIMIT " + iNombreContainers);
            else
            {
                if(strTypeMouvement.equals("OUT"))
                    qsPoids.AddWhere("DateDepart IS NOT NULL AND SUBSTR(DateArrivee, 7) = '" + strAnnee + "' ORDER BY RAND() LIMIT " + iNombreContainers);
            }

            double[] arrayPoids = new double[iNombreContainers];
        
            ResultSet rs = bdTrafic.Select(qsPoids);
            
            if(!rs.first())
                rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "NO_CONTAINERS");
            else
            {
                rs.last(); // On se positionne à la fin du ResultSet
                if(rs.getRow() == iNombreContainers)
                {
                    rs.beforeFirst();
                    int i = 0;
                    while(i < iNombreContainers && rs.next())
                    {
                        arrayPoids[i] = rs.getDouble("Poids");
                        i++;
                    }

                    DescriptiveStatistics ds = new DescriptiveStatistics(arrayPoids);

                    Double dMoyenne = ds.getMean();
                    Double dMediane = ds.getPercentile(50);
                    Double dEcartType = ds.getStandardDeviation();
                    double[] dMode = StatUtils.mode(arrayPoids);

                    String strReponse = dMoyenne.toString() + "#" + dMediane.toString() + "#" + dEcartType.toString();
                    String strModeBd = "";

                    for(int j = 0; j < dMode.length; j++)
                    {
                        if(j == 0)
                        {
                            strModeBd += String.valueOf(dMode[j]);
                            strReponse += "#" + String.valueOf(dMode[j]);
                        }
                        else
                        {
                            strModeBd += " - " + String.valueOf(dMode[j]);
                            strReponse += "_" + String.valueOf(dMode[j]);
                        }
                    }

                    System.out.println("Réponse au serveur = " + strReponse);
                    
                    QueryInsert qi = new QueryInsert();
                    qi.setTable("StatistiquesDescriptives");
                    qi.AddValue("TypeMouvement", "'" + strTypeMouvement + "'");
                    qi.AddValue("Moyenne", "'" + dMoyenne.toString() + "'");
                    qi.AddValue("Mediane", "'" + dMediane.toString() + "'");
                    qi.AddValue("EcartType", "'" + dEcartType.toString() + "'");
                    qi.AddValue("Mode", "'" + strModeBd + "'");
                    
                    bdDecisions.Insert(qi);
                    bdDecisions.Commit();

                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_OK, strReponse);
                }
                else
                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "TOO_MANY_CONTAINERS");
            }
            
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteGetGrContRep(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#GET_GR_CONT_REP#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        
        try 
        {
            String strParametre = (String)this.getParameters();
            String[] arrayParametres = strParametre.split("#");

            HashMap hm = (HashMap)param;
            BeanBDAccess bdTrafic = (BeanBDAccess)hm.get(RequetePIDEP.BeanTrafic);

            QuerySelect qsDestination = new QuerySelect();
            qsDestination.AddFrom("Mouvement");
            qsDestination.AddSelect("DISTINCT Destination");

            if(arrayParametres[0].equals("MOIS"))
                qsDestination.AddWhere("SUBSTR(DateArrivee, 4, 2) = '" + arrayParametres[1] + "'");
            else
                qsDestination.AddWhere("SUBSTR(DateArrivee, 7) = '" + arrayParametres[1] + "'");
        
            ArrayList<String> listDestinations = new ArrayList<>();
            ArrayList<Short> listCount = new ArrayList<>();
            ResultSet rsDestination = bdTrafic.Select(qsDestination);
            
            if(!rsDestination.first())
                rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "NO_CONTAINERS");
            else
            {
                rsDestination.beforeFirst();
                
                while(rsDestination.next())
                    listDestinations.add(rsDestination.getString("Destination"));
                
                for(int i = 0; i < listDestinations.size(); i++)
                {
                    System.out.println("Destination = " + listDestinations.get(i));
                    QuerySelect qsCount = new QuerySelect();
                    qsCount.AddFrom("Mouvement");
                    qsCount.AddSelect("COUNT(*)");
                    if(arrayParametres[0].equals("MOIS"))
                        qsCount.AddWhere("SUBSTR(DateArrivee, 4, 2) = '" + arrayParametres[1] + "' AND Destination = '" + listDestinations.get(i) + "'");
                    else
                        qsCount.AddWhere("SUBSTR(DateArrivee, 7) = '" + arrayParametres[1] + "' AND Destination = '" + listDestinations.get(i) + "'");
                    
                    ResultSet rsCount = bdTrafic.Select(qsCount);
                    if(rsCount.first())
                        listCount.add(rsCount.getShort(1));
                    rsCount.close();
                    System.out.println("Nombre = " + listCount.get(i));
                }
                
                for(int i = 0; i < listCount.size(); i++)
                    System.out.println("Il y a " + listCount.get(i) + " containers pour la destination " + listDestinations.get(i));
            
                if(listDestinations.size() == listCount.size())
                {
                    HashMap<String, Object> hmList = new HashMap<>();
                    hmList.put("COUNT", listCount);
                    hmList.put("DESTINATION", listDestinations);

                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_OK, hmList);
                }
                else
                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "UNKNOWN_ERROR");
            }
            rsDestination.close();
            
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteGetGrContComp(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#GET_GR_CONT_COMP#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        
        try 
        {
            String strParametre = (String)this.getParameters();

            HashMap hm = (HashMap)param;
            BeanBDAccess bdTrafic = (BeanBDAccess)hm.get(RequetePIDEP.BeanTrafic);

            QuerySelect qs = new QuerySelect();
            qs.AddFrom("Mouvement");
            qs.AddSelect("Destination, COUNT(IdContainer), QUARTER(STR_TO_DATE(DateArrivee, '%d/%m/%y'))");
            qs.AddWhere("SUBSTR(DateArrivee, 7) = '" + strParametre + "' GROUP BY 3, 1");
            
            ResultSet rs = bdTrafic.Select(qs);
            
            if(!rs.first())
                rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "NO_CONTAINERS");
            else
            {
                ArrayList<String> listDestinations = new ArrayList<>();
                ArrayList<Short> listTrimestres = new ArrayList<>();
                ArrayList<Short> listCount = new ArrayList<>();
            
                rs.beforeFirst();
                
                while(rs.next())
                {
                    listDestinations.add(rs.getString("Destination"));
                    listCount.add(rs.getShort(2));
                    listTrimestres.add(rs.getShort(3));
                    System.out.println(rs.getString(1) + " - " + rs.getString(2) + " - " + rs.getString(3));
                }
                rs.close();
                
                if(listDestinations.size() == listCount.size() && listCount.size() == listTrimestres.size())
                {
                    HashMap<String, Object> hmList = new HashMap<>();
                    hmList.put("COUNT", listCount);
                    hmList.put("TRIMESTRE", listTrimestres);
                    hmList.put("DESTINATION", listDestinations);

                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_OK, hmList);
                }
                else
                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "UNKNOWN_ERROR");
            }
            
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    private void traiteRequeteGetStatInferTestConf(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#GET_STAT_INFER_TEST_CONF#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        
        int iNombreContainers = Integer.valueOf((String)this.getParameters());
        HashMap hm = (HashMap)param;
        BeanBDAccess bdTrafic = (BeanBDAccess)hm.get(RequetePIDEP.BeanTrafic);
        BeanBDAccess bdDecisions = (BeanBDAccess)hm.get(RequetePIDEP.BeanDecisions);
        String strReponse = "";
                
        try
        {       
            QuerySelect qs = new QuerySelect();
            qs.AddSelect("DATEDIFF(STR_TO_DATE(DateDepart, '%d/%m/%y'), STR_TO_DATE(DateArrivee, '%d/%m/%y'))");
            qs.AddFrom("Mouvement");
            qs.AddWhere("DateDepart IS NOT NULL ORDER BY RAND() LIMIT " + iNombreContainers);

            double[] arrayEchantillons = new double[iNombreContainers];
            
            ResultSet rs = bdTrafic.Select(qs);

            if(!rs.first())
                rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "Il n'y a aucun résultat retourné ...");
            else
            {
                rs.last();

                if(rs.getRow() < iNombreContainers)
                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "Le nombre de containers demandé est trop grand ...");
                else
                {
                    rs.beforeFirst();
                    int i = 0;
                    while(i < iNombreContainers && rs.next())
                        arrayEchantillons[i] = rs.getDouble(1);
                    rs.close();
                    
                    TTest t = new TTest();
                    double pValue = t.tTest(10, arrayEchantillons); // 1er paramètre: la moyenne, le 2e: l'échantillon

                    String strResultatHypothese = "";
                    strReponse = pValue + "#";

                    if(pValue < 0.025)
                    {
                        strResultatHypothese = "L''hypothèse selon laquelle le temps moyen"
                                + " de stationnement d''un container est de 10 jours est rejetée.";
                        strReponse += "L'hypothèse nulle est rejetée.";
                    }
                    else
                    {
                        strResultatHypothese = "L''hypothèse selon laquelle le temps moyen"
                                + " de stationnement d''un container est de 10 jours est acceptée.";
                        strReponse += "L'hypothèse nulle est acceptée.";
                    }
                    
                    QueryInsert qi = new QueryInsert();
                    qi.setTable("StatsInferConformite");
                    qi.AddValue("pValue", String.valueOf(pValue));
                    qi.AddValue("TailleEchantillon", String.valueOf(iNombreContainers));
                    qi.AddValue("ReponseHypothese", "'" + strResultatHypothese + "'");

                    bdDecisions.Insert(qi);
                    bdDecisions.Commit();
                    
                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_OK, strReponse);
                }
            }

            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteGetStatInferTestHomog(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#GET_STAT_INFER_TEST_HOMOG#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        
        String strParameters = (String)this.getParameters();
        String[] arrayParameters = strParameters.split("#");
        String strDestination1 = arrayParameters[0];
        String strDestination2 = arrayParameters[1];
        int iNombreContainers = Integer.valueOf(arrayParameters[2]);
        
        System.out.println("Nombre de containers = " + iNombreContainers);
        
        HashMap hm = (HashMap)param;
        BeanBDAccess bdTrafic = (BeanBDAccess)hm.get(RequetePIDEP.BeanTrafic);
        BeanBDAccess bdDecisions = (BeanBDAccess)hm.get(RequetePIDEP.BeanDecisions);
        String strReponse = "";
                
        try
        {       
            QuerySelect qsDestination1 = new QuerySelect();
            qsDestination1.AddSelect("DATEDIFF(STR_TO_DATE(DateDepart, '%d/%m/%y'), STR_TO_DATE(DateArrivee, '%d/%m/%y'))");
            qsDestination1.AddFrom("Mouvement");
            qsDestination1.AddWhere("Destination = '" + strDestination1 + "' AND DateDepart IS NOT NULL ORDER BY RAND() LIMIT " + iNombreContainers);
            ResultSet rsDestination1 = bdTrafic.Select(qsDestination1);

            if(!rsDestination1.first())
                rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "Il n'y a aucun résultat retourné pour la destination " + strDestination1);
            else
            {
                rsDestination1.last();

                if(rsDestination1.getRow() < iNombreContainers)
                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "L'échantillon demandé est trop grand pour la destination " + strDestination1);
                else
                {
                    QuerySelect qsDestination2 = new QuerySelect();
                    qsDestination2.AddSelect("DATEDIFF(STR_TO_DATE(DateDepart, '%d/%m/%y'), STR_TO_DATE(DateArrivee, '%d/%m/%y'))");
                    qsDestination2.AddFrom("Mouvement");
                    qsDestination2.AddWhere("Destination = '" + strDestination2 + "' AND DateDepart IS NOT NULL ORDER BY RAND() LIMIT " + iNombreContainers);
                    ResultSet rsDestination2 = bdTrafic.Select(qsDestination2);
                    
                    if(!rsDestination2.first())
                        rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "Il n'y a aucun résultat retourné pour la destination " + strDestination2);
                    else
                    {
                        rsDestination2.last();
                        
                        if(rsDestination2.getRow() < iNombreContainers)
                            rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "L'échantillon demandé est trop grand pour la destination " + strDestination2);
                        else
                        {
                            double[] arrayEchantillons1 = new double[iNombreContainers];
                            double[] arrayEchantillons2 = new double[iNombreContainers];
                            rsDestination1.beforeFirst();
                            rsDestination2.beforeFirst();
                            int i = 0;
                            while(i < iNombreContainers && rsDestination1.next() && rsDestination2.next())
                            {
                                arrayEchantillons1[i] = rsDestination1.getDouble(1);
                                arrayEchantillons2[i] = rsDestination2.getDouble(1);
                            }
                            rsDestination1.close();
                            rsDestination2.close();

                            TTest t = new TTest();
                            double pValue = t.tTest(arrayEchantillons1, arrayEchantillons2);

                            String strResultatHypothese = "";
                            strReponse = pValue + "#";

                            if(pValue < 0.025)
                            {
                                strResultatHypothese = "L''hypothèse selon laquelle le temps moyen"
                                        + " de stationnement d''un container à destination de " + strDestination1 + " est le même "
                                        + " qu''un container à destination de " + strDestination2 + " est rejetée.";
                                strReponse += "L'hypothèse nulle est rejetée.";
                            }
                            else
                            {
                                strResultatHypothese = "L''hypothèse selon laquelle le temps moyen"
                                        + " de stationnement d''un container à destination de " + strDestination1 + " est le même "
                                        + " qu''un container à destination de " + strDestination2 + " est acceptée.";
                                strReponse += "L'hypothèse nulle est acceptée.";
                            }
                            
                            QueryInsert qi = new QueryInsert();
                            qi.setTable("StatsInferHomogeneite");
                            qi.AddValue("pValue", String.valueOf(pValue));
                            qi.AddValue("TailleEchantillon", String.valueOf(iNombreContainers));
                            qi.AddValue("ReponseHypothese", "'" + strResultatHypothese + "'");
                            qi.AddValue("Destination1", "'" + strDestination1 + "'");
                            qi.AddValue("Destination2", "'" + strDestination2 + "'");

                            bdDecisions.Insert(qi);
                            bdDecisions.Commit();
                            
                            rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_OK, strReponse);
                        }
                    }
                }
            }

            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteGetStatInferTestHomogAnova(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        System.out.println("Début de traiteRequete : adresse distante = " + adresseDistante);
        String comm = adresseDistante + "#GET_STAT_INFER_TEST_HOMOG_ANOVA#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        
        String strParameters = (String)this.getParameters();
        int iNombreContainers = Integer.valueOf(strParameters);
        Boolean bError = false;
        
        HashMap hm = (HashMap)param;
        BeanBDAccess bdTrafic = (BeanBDAccess)hm.get(RequetePIDEP.BeanTrafic);
        BeanBDAccess bdDecisions = (BeanBDAccess)hm.get(RequetePIDEP.BeanDecisions);
        String strReponse = "";
                
        try
        {       
            QuerySelect qs = new QuerySelect();
            qs.AddSelect("Destination, DATEDIFF(STR_TO_DATE(DateDepart, '%d/%m/%y'), STR_TO_DATE(DateArrivee, '%d/%m/%y'))");
            qs.AddFrom("Mouvement");
            qs.AddWhere("DateDepart IS NOT NULL ORDER BY Destination, RAND()");
            ResultSet rs = bdTrafic.Select(qs);

            if(!rs.first())
                rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "Il n'y a aucun résultat retourné ... C'est pas coquet tout ça.");
            else
            {
                rs.last();

                if(rs.getRow() < iNombreContainers)
                    rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "L'échantillon demandé est trop grand.");
                else
                {
                    ArrayList<double[]> listTemps = new ArrayList<>();
                    double[] arrayEchantillons = null;
                    rs.first();
                    String strDestinationCourante = rs.getString("Destination");
                    rs.beforeFirst();
                    
                    while(!rs.isAfterLast())
                    {
                        int i;
                        arrayEchantillons = new double[iNombreContainers];
                        
                        for(i = 0; i < iNombreContainers && rs.next() && strDestinationCourante.equals(rs.getString("Destination")); i++)
                            arrayEchantillons[i] = rs.getDouble(2);
                        
                        if(i < iNombreContainers)
                        {
                            rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_KO, "Il n'y a pas assez de containers pour la destination " + strDestinationCourante);
                            bError = true;
                            break;
                        }
                        else
                        {
                            listTemps.add(arrayEchantillons);
                            while(strDestinationCourante.equals(rs.getString("Destination")) && rs.next()); // On boucle jusqu'à la prochaine destination
                            if(rs.isAfterLast()) // Si on est arrivé à la fin, on sort de la boucle
                                break;
                            strDestinationCourante = rs.getString("Destination");
                            rs.previous(); // On revient un tuple en arrière à cause du rs.next() de la boucle for()
                        }
                    }
                    
                    rs.close();
                    
                    if(!bError)
                    {
                        OneWayAnova OWA = new OneWayAnova();
                        double pValue = OWA.anovaPValue(listTemps);
                        
                        String strResultatHypothese = "";
                        strReponse = pValue + "#";

                        if(pValue < 0.025)
                        {
                            strResultatHypothese = "L''hypothèse selon laquelle le temps moyen "
                                    + "de stationnement d''un container est le même pour toutes les destinations est rejetée.";
                            strReponse += "L'hypothèse nulle est rejetée.";
                        }
                        else
                        {
                            strResultatHypothese = "L''hypothèse selon laquelle le temps moyen "
                                    + "de stationnement d''un container est le même pour toutes les destinations est acceptée.";
                            strReponse += "L'hypothèse nulle est acceptée.";
                        }
                            
                        QueryInsert qi = new QueryInsert();
                        qi.setTable("StatsInferAnova");
                        qi.AddValue("pValue", String.valueOf(pValue));
                        qi.AddValue("TailleEchantillon", String.valueOf(iNombreContainers));
                        qi.AddValue("ReponseHypothese", "'" + strResultatHypothese + "'");
                        qi.AddValue("NombreDestinations", String.valueOf(listTemps.size()));

                        bdDecisions.Insert(qi);
                        bdDecisions.Commit();
                        
                        rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_OK, strReponse);
                    }
                }
            }

            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(RequetePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void traiteRequeteDatabase(Socket SocketCli, ConsoleServeur cs, Object param) 
    {
        String adresseDistante = SocketCli.getRemoteSocketAddress().toString();
        String comm = adresseDistante + "#DATABASE#Thread Client : " + String.valueOf(Thread.currentThread().getId());
        cs.TraceEvenements(comm);
        
        ReponsePIDEP rep = null;
        HashMap hm = (HashMap)param;
        BeanBDAccess bdTrafic = (BeanBDAccess)hm.get(RequetePIDEP.BeanTrafic);
        
        System.err.println("Valeur du paramètre = " + this.getParameters());
        String strParameters = (String)this.getParameters();
        String[] strRequestDb = strParameters.split("#");
        String strSelect = strRequestDb[0];
        String strFrom = strRequestDb[1];
        String strWhere = "";
        if(!strRequestDb[2].equals("NULL"))
            strWhere = strRequestDb[2];
        
        QuerySelect qs = new QuerySelect(strSelect, strFrom);
        if(!strWhere.isEmpty())
            qs.AddWhere(strWhere);
        try
        {
            ResultSet rs = bdTrafic.Select(qs);
            
            DefaultComboBoxModel dcbm1 = new DefaultComboBoxModel();
            DefaultComboBoxModel dcbm2 = new DefaultComboBoxModel();
            while(rs.next())
            {
                dcbm1.addElement(rs.getString(1));
                dcbm2.addElement(rs.getString(1));
            }
            rs.close();
            
            HashMap<String, Object> hashModel = new HashMap<>();
            hashModel.put("DCBM1", dcbm1);
            hashModel.put("DCBM2", dcbm2);
            
            rep = new ReponsePIDEP(ReponsePIDEP.REPONSE_OK, hashModel);
            
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(SocketCli.getOutputStream());
            oos.writeObject(rep);
            oos.flush();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(ReponsePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ReponsePIDEP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the _Parameters
     */
    public Object getParameters() {
        return _Parameters;
    }

    /**
     * @param _Parameters the _Parameters to set
     */
    public void setParameters(Object _Parameters) {
        this._Parameters = _Parameters;
    }
}
