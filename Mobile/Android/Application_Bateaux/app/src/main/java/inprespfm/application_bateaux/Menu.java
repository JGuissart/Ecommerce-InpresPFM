package inprespfm.application_bateaux;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import RequeteReponseIOBREP.RequeteIOBREP;

public class Menu extends AppCompatActivity
{
    private Button btnChargement;
    private Button btnDechargement;
    private Button btnDeconnexion;
    private Button btnContInOutJour;
    private Button btnContInOutWeekDest;
    private Button btnMeanTimeInOutDocker;
    private Socket CSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setTitle("Menu");
        btnChargement = (Button)findViewById(R.id.R_btnChargement);
        btnChargement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChargement(v);
            }
        });
        btnDechargement = (Button)findViewById(R.id.R_btnDechargement);
        btnDechargement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDechargement(v);
            }
        });
        btnContInOutJour = (Button)findViewById(R.id.R_btnContInOutJour);
        btnContInOutJour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContInOutJour(v);
            }
        });
        btnContInOutWeekDest = (Button)findViewById(R.id.R_btnContInOutWeekDest);
        btnContInOutWeekDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContInOutWeekDest(v);
            }
        });
        btnMeanTimeInOutDocker = (Button)findViewById(R.id.R_btnContInOutWeekDest);
        btnMeanTimeInOutDocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMeanTimeInOutDocker(v);
            }
        });
        btnDeconnexion = (Button)findViewById(R.id.R_btnDeconnexion);
        btnDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeconnexion(v);
            }
        });
    }

    public void onClickChargement(View view)
    {
        Intent chargement = new Intent(Menu.this, GetContainers.class);
        startActivity(chargement);
    }

    public void onClickDechargement(View view)
    {
        Intent dechargement = new Intent(Menu.this, BoatArrived.class);
        startActivity(dechargement);
    }

    public void onClickContInOutJour(View view)
    {
        Intent histogrammeJour = HistogrammeContainerChargesDecharges.getIntent(Menu.this);
        startActivity(histogrammeJour);
    }

    public void onClickContInOutWeekDest(View view)
    {
        Intent histogrammeRepartion = HistogrammeRepartitionContainer.getIntent(Menu.this);
        startActivity(histogrammeRepartion);
    }

    public void onClickMeanTimeInOutDocker(View view)
    {
        Intent histogrammeTempsMoyen = HistogrammeTempsMoyenChargementDechargement.getIntent(Menu.this);
        startActivity(histogrammeTempsMoyen);
    }

    public void onClickDeconnexion(View view)
    {
        CSocket = Connexion.CSocket;
        RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_DEC, null);

        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
            oos.writeObject(req);
            oos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Intent logout = new Intent(Menu.this, Connexion.class);
        startActivity(logout);
    }
}
