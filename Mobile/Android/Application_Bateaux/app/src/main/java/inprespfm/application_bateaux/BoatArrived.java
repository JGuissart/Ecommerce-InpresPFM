package inprespfm.application_bateaux;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import RequeteReponseIOBREP.ReponseIOBREP;
import RequeteReponseIOBREP.RequeteIOBREP;

public class BoatArrived extends AppCompatActivity
{
    private EditText etxtIdBateau;
    private EditText etxtDestination;
    private Button btnSignaler;
    private Socket CSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_arrived);

        setTitle("Arrivée d'un bateau");

        etxtIdBateau = (EditText)findViewById(R.id.R_etxtIdBateau);
        etxtDestination = (EditText)findViewById(R.id.R_etxtDestination);

        btnSignaler = (Button)findViewById(R.id.R_btnSignaler);
        btnSignaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignaler(v);
            }
        });
    }

    public void onClickSignaler(View view)
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                ReponseIOBREP rep = null;
                CSocket = Connexion.CSocket;
                String message = etxtIdBateau.getText().toString()+"#"+ etxtDestination.getText().toString();
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_BOAT_ARRIVED, message);

                try
                {
                    ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();

                    ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                    rep = (ReponseIOBREP) ois.readObject();
                }
                catch(IOException ex)
                {
                    /*Toast.makeText(Connexion.this, ex.getMessage(), Toast.LENGTH_LONG).show();*/
                    System.err.println("Erreur[" + ex + "]");
                }
                catch(ClassNotFoundException ex)
                {
                    /*Toast.makeText(Connexion.this, ex.getMessage(), Toast.LENGTH_LONG).show();*/
                    System.err.println("Erreur[" + ex + "]");
                }

                if(rep.getCode() == ReponseIOBREP.REPONSE_OK)
                {
                    Intent handleIn = new Intent(BoatArrived.this, HandleContainerIn.class);
                    handleIn.putExtra("etxtDestination", etxtDestination.getText().toString());
                    startActivity(handleIn);
                }
                else
                {
                    /*Toast.makeText(Connexion.this, "Login ou mot de passe incorrecte !", Toast.LENGTH_LONG).show();*/
                }
            }
        }).start();
    }
}
