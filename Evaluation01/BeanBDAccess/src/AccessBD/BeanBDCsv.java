/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AccessBD;

/**
 *
 * @author Julien
 */
public class BeanBDCsv extends BeanBDAccess
{
    public BeanBDCsv()
    {
        this.DEFAULT_SELECT = "*";
        this.DEFAULT_FROM = "information_schema.tables";
        this.DEFAULT_WHERE = "";
        this.CONNECT = "jdbc:jstels:csv:" + System.getProperty("user.dir") + "?separator=,";
        this.DRIVER = "jstels.jdbc.csv.CsvDriver2";
        this.LIMIT_ONE = "WHERE LIMIT 1";
        this.Connect();
    }
}
