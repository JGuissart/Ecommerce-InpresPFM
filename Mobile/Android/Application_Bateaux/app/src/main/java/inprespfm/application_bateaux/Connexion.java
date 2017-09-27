package inprespfm.application_bateaux;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import RequeteReponseIOBREP.ReponseIOBREP;
import RequeteReponseIOBREP.RequeteIOBREP;

public class Connexion extends AppCompatActivity
{
    private EditText etxtLogin;
    private EditText etxtPassword;
    private Button btnConnexion;

    public static Socket CSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        setTitle("Connexion");
        etxtLogin = (EditText)findViewById(R.id.R_etxtLogin);
        etxtPassword = (EditText)findViewById(R.id.R_etxtPassword);

        btnConnexion = (Button)findViewById(R.id.R_btnConnexion);
        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickConnexion(v);
            }
        });

        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    CSocket = new Socket(InetAddress.getByName("192.168.1.103"), 31019);
                    System.err.println("connexion");
                    RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_CON, null);
                    ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                }
                catch(Exception ex)
                {
                    Toast.makeText(Connexion.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
            }
        }).start();
    }


    public void onClickConnexion(View view)
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    ReponseIOBREP rep = null;
                    System.err.println("Je veux me connecter");

                    String message = etxtLogin.getText().toString() + "#" + etxtPassword.getText().toString();
                    RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_LOGIN, message);
                    ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();

                    ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                    rep = (ReponseIOBREP)ois.readObject();

                    if(rep.getCode() == ReponseIOBREP.LOGIN_OK)
                    {
                        Intent menu = new Intent(Connexion.this, Menu.class);
                        startActivity(menu);
                    }
                    else
                    {
                        Toast.makeText(Connexion.this, "Login ou mot de passe incorrecte !", Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex)
                {
                    Toast.makeText(Connexion.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
            }
        }).start();
    }
}
