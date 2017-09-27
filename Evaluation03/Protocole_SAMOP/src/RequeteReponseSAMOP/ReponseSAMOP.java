/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteReponseSAMOP;

import Utils.Reponse;
import java.io.Serializable;

/**
 *
 * @author Julien
 */
public class ReponseSAMOP implements Reponse, Serializable
{
    public static int LOGIN_OK = 100;
    public static int LOGIN_KO = 200;
    public static int REPONSE_OK = 101;
    public static int REPONSE_KO = 201;
    private int CodeRetour;
    private Object Result;
    
    public ReponseSAMOP(int CodeRetour, Object res)
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
     * @return the Result
     */
    public Object getResult() {
        return Result;
    }

    /**
     * @param Result the Result to set
     */
    public void setResult(Object Result) {
        this.Result = Result;
    }
}
