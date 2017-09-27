/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AccessBD;

import Queries.QueryDelete;
import Queries.QueryInsert;
import Queries.QuerySelect;
import Queries.QueryUpdate;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julien
 */
public abstract class BeanBDAccess implements Serializable
{
    protected String DEFAULT_SELECT = "*";
    protected String DEFAULT_FROM = "dual";
    protected String DEFAULT_WHERE = "";
    protected String CONNECT = "";
    protected String DRIVER = "";
    protected String LIMIT_ONE = "";
    private Connection _Connection;
    
    public boolean Connect()
    {
        try 
        {
            System.out.println("Bean = " + System.getProperty("user.dir"));
            Class.forName(DRIVER).newInstance();
            System.out.println("Driver charge !");
            this.setConnection(DriverManager.getConnection(CONNECT));
            System.out.println("Driver connecte !");
            this.getConnection().setAutoCommit(true);
            
            return true;
        }
        catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException ex)
        {
            System.err.println("Erreur BeanBDAccess");
            Logger.getLogger(BeanBDAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean Connect(String Connect)
    {
        try 
        {
            Class.forName(DRIVER).newInstance();
            System.out.println("Driver charge !");
            this.setConnection(DriverManager.getConnection(CONNECT + Connect, "", ""));
            this.getConnection().setAutoCommit(false);
            
            return true;
        }
        catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException ex)
        {
            System.err.println("Erreur BeanBDAccess");
            Logger.getLogger(BeanBDAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean Connect(String Connect, String Login, String Password)
    {
        try 
        {
            Class.forName(DRIVER).newInstance();
            System.out.println("Driver charge !");
            this.setConnection(DriverManager.getConnection(CONNECT + Connect, Login, Password));
            this.getConnection().setAutoCommit(false);
            
            return true;
        }
        catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException ex)
        {
            System.err.println("Erreur BeanBDAccess");
            Logger.getLogger(BeanBDAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public synchronized ResultSet Select(QuerySelect query) throws SQLException
    {
        String select, from, where;
        
        if(query.getSelect().isEmpty())
            select = "SELECT " + this.DEFAULT_SELECT;
        else
            select = "SELECT " + query.getSelect();
        
        if(query.getFrom().isEmpty())
            from = "FROM " + this.DEFAULT_FROM;
        else
            from = "FROM " + query.getFrom();
        
        if(query.getWhere().isEmpty())
            where = this.DEFAULT_WHERE;
        else
            where = "WHERE " + query.getWhere();
        
        String QueryString = select + " " + from + " " + where;
        
        // PreparedStatement pour setInt, setString, ...
        Statement stmt = this.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        System.out.println("Query Select : " + QueryString);

        return stmt.executeQuery(QueryString);
    }
    
    public ResultSet Select(String select, String from, String where) throws SQLException
    {
        return this.Select(new QuerySelect(select, from, where));
    }
    
    public synchronized int Insert(QueryInsert query) throws SQLException
    {
        String QueryString = "INSERT INTO " + query.getTable() + " (" + query.getCols() + ") VALUES(" + query.getValues() + ")";
        
        // PreparedStatement pour setInt, setString, ...
        Statement stmt = this.getConnection().createStatement();
        System.out.println("Query Insert : " + QueryString);
        
        return stmt.executeUpdate(QueryString);
    }
    
    public int Insert(String table, String cols, String values) throws SQLException
    {
        return this.Insert(new QueryInsert(table, cols, values));
    }
    
    public synchronized int Update(QueryUpdate query) throws SQLException
    {
        String QueryString;
        
        if(!query.getWhere().equals("")) 
            QueryString = "UPDATE " + query.getTable() + " SET " + query.getValues() + " WHERE " + query.getWhere();
        else
            QueryString = "UPDATE " + query.getTable() + " SET " + query.getValues();
        
        System.out.println("Query Update : " + QueryString);
        
        // PreparedStatement pour setInt, setString, ...
        Statement stmt = this.getConnection().createStatement();
        
        return stmt.executeUpdate(QueryString);
    }
    
    public int Update(String table, String cols, String values) throws SQLException
    {
        return this.Update(new QueryUpdate(table, cols, values));
    }
    
    public int Update(String table, String cols, String values, String where) throws SQLException
    {
        return this.Update(new QueryUpdate(table, cols, values, where));
    }
    
    public synchronized int Delete(QueryDelete query) throws SQLException
    {
        String QueryString;
        
        if(!query.getWhere().equals("")) 
            QueryString = "DELETE FROM " + query.getTable() + " WHERE " + query.getWhere();
        else
            QueryString = "DELETE FROM " + query.getTable();
        
        System.out.println("Query Delete : " + QueryString);
        
        // PreparedStatement pour setInt, setString, ...
        Statement stmt = this.getConnection().createStatement();
        
        return stmt.executeUpdate(QueryString);
    }
    
    public int Delete(String table) throws SQLException
    {
        return this.Delete(new QueryDelete(table));
    }
    
    public int Delete(String table, String where) throws SQLException
    {
        return this.Delete(new QueryDelete(table, where));
    }
    
    public void Disconnect() throws SQLException
    {
        if(this.getConnection() != null)
            this.getConnection().close();
        this.setConnection(null);
    }
    
    public synchronized void Commit() throws SQLException
    {
        if(this.getConnection() != null) 
            this.getConnection().commit();
    }
    
    public synchronized void Rollback() throws SQLException
    {
        if(this.getConnection() != null) 
            this.getConnection().rollback();
    }
    
    public void setAutoCommit(boolean autocommit) throws SQLException
    {
        if(this.getConnection() != null) 
            this.getConnection().setAutoCommit(autocommit);
    }

    /**
     * @return the _Connection
     */
    public Connection getConnection() {
        return _Connection;
    }

    /**
     * @param _Connection the _Connection to set
     */
    public void setConnection(Connection _Connection) {
        this._Connection = _Connection;
    }
}
