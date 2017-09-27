/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteReponsePIDEP;

import Utils.Reponse;
import java.io.Serializable;

/**
 *
 * @author Julien
 */
public class ReponsePIDEP implements Reponse, Serializable
{
    public static int LOGIN_OK = 100;
    public static int LOGIN_KO = 200;
    public static int REPONSE_OK = 101;
    public static int REPONSE_KO = 201;
    private int CodeRetour;
    private Object _Result;

    public ReponsePIDEP(int CodeRetour, Object res)
    {
        this.CodeRetour = CodeRetour; 
        this.setResult(res);
    }

    @Override
    public int getCode()
    {
        return CodeRetour;
    }

    /**
     * @return the _Result
     */
    public Object getResult() {
        return _Result;
    }

    /**
     * @param _Result the _Result to set
     */
    public void setResult(Object _Result) {
        this._Result = _Result;
    }
    
}
