/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applic_data_mining;

/**
 *
 * @author Julien
 */
public class Mais
{
    private int Individu;
    private String Hauteur;
    private String Masse;
    private String NombreGrains;
    private String MasseGrains;
    private String Couleur;
    private String GerminationEpi;
    private String Enracinement;
    private String Verse;
    private String Attaque;
    private String Parcelle;
    private String HauteurJ7;
    private String VerseTraitement;
    private String NombreJoursAttaque;
    private String CensureDroite;
    
    public Mais(String tuple)
    {
        String[] arrayTuple = tuple.split("\t");
        this.Individu = Integer.parseInt(arrayTuple[0]);
        this.Hauteur = arrayTuple[1];
        this.Masse = arrayTuple[2];
        this.NombreGrains = arrayTuple[3];
        this.MasseGrains = arrayTuple[4];
        this.Couleur = arrayTuple[5];
        this.GerminationEpi = arrayTuple[6];
        this.Enracinement = arrayTuple[7];
        this.Verse = arrayTuple[8];
        this.Attaque = arrayTuple[9];
        this.Parcelle = arrayTuple[10];
        this.HauteurJ7 = arrayTuple[11];
        this.VerseTraitement = arrayTuple[12];
        this.NombreJoursAttaque = arrayTuple[13];
        this.CensureDroite = arrayTuple[14];
    }

    /**
     * @return the Individu
     */
    public int getIndividu() {
        return Individu;
    }

    /**
     * @param Individu the Individu to set
     */
    public void setIndividu(int Individu) {
        this.Individu = Individu;
    }

    /**
     * @return the Hauteur
     */
    public String getHauteur() {
        return Hauteur;
    }

    /**
     * @param Hauteur the Hauteur to set
     */
    public void setHauteur(String Hauteur) {
        this.Hauteur = Hauteur;
    }

    /**
     * @return the Masse
     */
    public String getMasse() {
        return Masse;
    }

    /**
     * @param Masse the Masse to set
     */
    public void setMasse(String Masse) {
        this.Masse = Masse;
    }

    /**
     * @return the NombreGrains
     */
    public String getNombreGrains() {
        return NombreGrains;
    }

    /**
     * @param NombreGrains the NombreGrains to set
     */
    public void setNombreGrains(String NombreGrains) {
        this.NombreGrains = NombreGrains;
    }

    /**
     * @return the MasseGrains
     */
    public String getMasseGrains() {
        return MasseGrains;
    }

    /**
     * @param MasseGrains the MasseGrains to set
     */
    public void setMasseGrains(String MasseGrains) {
        this.MasseGrains = MasseGrains;
    }

    /**
     * @return the Couleur
     */
    public String getCouleur() {
        return Couleur;
    }

    /**
     * @param Couleur the Couleur to set
     */
    public void setCouleur(String Couleur) {
        this.Couleur = Couleur;
    }

    /**
     * @return the GerminationEpi
     */
    public String getGerminationEpi() {
        return GerminationEpi;
    }

    /**
     * @param GerminationEpi the GerminationEpi to set
     */
    public void setGerminationEpi(String GerminationEpi) {
        this.GerminationEpi = GerminationEpi;
    }

    /**
     * @return the Enracinement
     */
    public String getEnracinement() {
        return Enracinement;
    }

    /**
     * @param Enracinement the Enracinement to set
     */
    public void setEnracinement(String Enracinement) {
        this.Enracinement = Enracinement;
    }

    /**
     * @return the Verse
     */
    public String getVerse() {
        return Verse;
    }

    /**
     * @param Verse the Verse to set
     */
    public void setVerse(String Verse) {
        this.Verse = Verse;
    }

    /**
     * @return the Attaque
     */
    public String getAttaque() {
        return Attaque;
    }

    /**
     * @param Attaque the Attaque to set
     */
    public void setAttaque(String Attaque) {
        this.Attaque = Attaque;
    }

    /**
     * @return the Parcelle
     */
    public String getParcelle() {
        return Parcelle;
    }

    /**
     * @param Parcelle the Parcelle to set
     */
    public void setParcelle(String Parcelle) {
        this.Parcelle = Parcelle;
    }

    /**
     * @return the HauteurJ7
     */
    public String getHauteurJ7() {
        return HauteurJ7;
    }

    /**
     * @param HauteurJ7 the HauteurJ7 to set
     */
    public void setHauteurJ7(String HauteurJ7) {
        this.HauteurJ7 = HauteurJ7;
    }

    /**
     * @return the VerseTraitement
     */
    public String getVerseTraitement() {
        return VerseTraitement;
    }

    /**
     * @param VerseTraitement the VerseTraitement to set
     */
    public void setVerseTraitement(String VerseTraitement) {
        this.VerseTraitement = VerseTraitement;
    }

    /**
     * @return the NombreJoursAttaque
     */
    public String getNombreJoursAttaque() {
        return NombreJoursAttaque;
    }

    /**
     * @param NombreJoursAttaque the NombreJoursAttaque to set
     */
    public void setNombreJoursAttaque(String NombreJoursAttaque) {
        this.NombreJoursAttaque = NombreJoursAttaque;
    }

    /**
     * @return the CensureDroite
     */
    public String getCensureDroite() {
        return CensureDroite;
    }

    /**
     * @param CensureDroite the CensureDroite to set
     */
    public void setCensureDroite(String CensureDroite) {
        this.CensureDroite = CensureDroite;
    }
}
