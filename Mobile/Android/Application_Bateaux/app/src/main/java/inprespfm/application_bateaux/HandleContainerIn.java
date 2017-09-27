
package inprespfm.application_bateaux;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import RequeteReponseIOBREP.ReponseIOBREP;
import RequeteReponseIOBREP.RequeteIOBREP;

public class HandleContainerIn extends AppCompatActivity
{
    private Button btnDecharger;
    private Button btnTerminer;
    private Socket CSocket;
    private EditText etxtIdBateau;
    private String Destination;
    private ReponseIOBREP rep;
    private ArrayList<String> ListContainers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_container_in);

        setTitle("Identification du container");

        etxtIdBateau = (EditText)findViewById(R.id.R_etxtIdBateau);

        Bundle extras = getIntent().getExtras();
        Destination = extras.getString("Destination");

        btnDecharger = (Button)findViewById(R.id.R_btnDecharger);
        btnDecharger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDecharger(v);
            }
        });

        btnTerminer = (Button)findViewById(R.id.R_btnTerminer);
        btnTerminer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                onClickTerminer(v);
            }
        });

        ListContainers = new ArrayList<>();
    }

    public void onClickDecharger(View view)
    {
        final String strIdentifiant = etxtIdBateau.getText().toString();
        new AsyncTask<Void, Integer, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                CSocket = Connexion.CSocket;
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_HANDLE_CONTAINER_IN, null);

                try
                {
                    ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();

                    ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                    rep = (ReponseIOBREP) ois.readObject();
                    System.out.println(rep.getCode());
                    if (rep.getCode() == ReponseIOBREP.REPONSE_OK)
                    {
                        String strContainer = strIdentifiant + "#" + (String)rep.getResult();
                        ListContainers.add(strContainer);
                        System.out.println("Ajouté -----------------------");
                    }
                    else
                    {
                        if (rep.getCode() == ReponseIOBREP.REPONSE_KO)
                        {
                            //Toast.makeText(HandleContainerIn.this, "Impossible à sortir !", Toast.LENGTH_LONG).show();
                            System.out.println("Pas ajouté -----------------------");
                        }
                    }
                }
                catch (IOException ex)
                {
                    //Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
                catch (ClassNotFoundException ex)
                {
                    //Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
                catch (Exception ex)
                {
                    //Toast.makeText(Connexion.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
                return null;
            }
        }.execute();
    }


    public void onClickTerminer(View view)
    {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params)
            {
                String message = "";

                for(int i = 0; i < ListContainers.size(); i++)
                    message = message + ListContainers.get(i) + "#";

                System.out.println("Message : " + message);
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_END_CONTAINER_IN, message);

                try
                {
                    ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();

                    ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                    rep = (ReponseIOBREP) ois.readObject();

                    if(rep.getCode() == ReponseIOBREP.REPONSE_OK)
                    {
                        Intent endIn = new Intent(HandleContainerIn.this,Menu.class);
                        startActivity(endIn);
                    }
                    else
                        Toast.makeText(HandleContainerIn.this, "Erreur lors de l'execution du End_Container_In !", Toast.LENGTH_LONG).show();
                }
                catch (IOException ex)
                {
                    //Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
                catch (ClassNotFoundException ex)
                {
                    //Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
                catch (Exception ex)
                {
                    //Toast.makeText(Connexion.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    System.err.println("Erreur[" + ex + "]");
                }
                return null;
            }
        }.execute();
    }
}
