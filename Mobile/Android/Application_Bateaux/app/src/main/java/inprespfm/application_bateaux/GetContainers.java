package inprespfm.application_bateaux;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import RequeteReponseIOBREP.ReponseIOBREP;
import RequeteReponseIOBREP.RequeteIOBREP;

public class GetContainers extends AppCompatActivity
{
    private Socket CSocket;
    private ListView lstContainers;
    private Spinner spnrOrdre;
    private EditText etxtDestination;
    private Button btnValider;
    private ReponseIOBREP rep;
    private ArrayList<String> ListContainers;
    private String Message;
    private ArrayAdapter<String> AdapterListContainers;
    private String[] ArrayStringTuples;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_containers);

        setTitle("Déchargement");

        etxtDestination = (EditText) findViewById(R.id.R_etxtDestination);
        lstContainers = (ListView) findViewById(R.id.R_lstContainers);
        spnrOrdre = (Spinner) findViewById(R.id.R_SpnrOrdre);
        btnValider = (Button) findViewById(R.id.R_btnValider);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickValider(v);
            }
        });
        btnValider = (Button)findViewById(R.id.R_btnTerminer);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTerminer(v);
            }
        });

        //Creation du spinner
        List<String> list = new ArrayList<String>();
        list.add("RANDOM");
        list.add("FIRST");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        spnrOrdre.setAdapter(dataAdapter);

        ListContainers = new ArrayList<String>();
        AdapterListContainers = new ArrayAdapter<String>(GetContainers.this, android.R.layout.simple_list_item_1, ListContainers);

        if(ListContainers == null)
            System.out.println("container null -----------------");
        else
            System.out.println("container pas null -----------------");

        if(lstContainers == null)
            System.out.println("lstContainers null -----------------");
        else
            System.out.println("lstContainers pas null -----------------");
    }

    public void onClickValider(View view)
    {
        Message = etxtDestination.getText().toString() + "#" + spnrOrdre.getSelectedItem().toString();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params)
            {
                ReponseIOBREP rep = null;
                CSocket = Connexion.CSocket;

                System.out.println("Message:" + Message);
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_GET_CONTAINERS, Message);

                try
                {
                    ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();

                    ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                    rep = (ReponseIOBREP) ois.readObject();


                    System.out.println("Réception: " + (String)rep.getResult());
                    String Results = (String) rep.getResult();
                    String[] Splitage = Results.split("#");
                    String SepRow = Splitage[1];

                    ArrayStringTuples = Splitage[0].split(SepRow);

                    AdapterListContainers.addAll(Arrays.asList(ArrayStringTuples));

                    publishProgress();
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


            protected void onProgressUpdate(Void... text)
            {
                System.out.println("on_progress +++++");
                lstContainers.setAdapter(AdapterListContainers);
            }
        }.execute();

        lstContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                final String strItem = (String) parent.getItemAtPosition(position);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params)
                    {
                        ReponseIOBREP rep = null;
                        RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_HANDLE_CONTAINER_OUT, strItem);

                        try
                        {
                            ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                            oos.writeObject(req);
                            oos.flush();

                            ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                            rep = (ReponseIOBREP) ois.readObject();
                        }
                        catch (IOException ex)
                        {
                            /*Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();*/
                            System.err.println("Erreur[" + ex + "]");
                        }
                        catch (ClassNotFoundException ex)
                        {
                            /*Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();*/
                            System.err.println("Erreur[" + ex + "]");
                        }

                        if (rep.getCode() == ReponseIOBREP.REPONSE_OK)
                        {
                            ListContainers.remove(strItem);
                        }
                        else
                        {
                            /*Toast.makeText(GetContainers.this, "Impossible à sortir !", Toast.LENGTH_LONG).show();*/
                        }
                        publishProgress();

                        return null;
                    }

                    protected void onProgressUpdate(Void... text) {
                        AdapterListContainers.notifyDataSetChanged();
                    }
                }.execute();


            }
        });
    }

    public void onClickTerminer(View view)
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params)
            {
                ReponseIOBREP rep = null;
                RequeteIOBREP req = new RequeteIOBREP(RequeteIOBREP.REQUEST_END_CONTAINER_OUT, null);

                try
                {
                    ObjectOutputStream oos = new ObjectOutputStream(CSocket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();

                    ObjectInputStream ois = new ObjectInputStream(CSocket.getInputStream());
                    rep = (ReponseIOBREP) ois.readObject();

                    if(rep.getCode() == ReponseIOBREP.REPONSE_OK)
                    {
                        Intent endOut = new Intent(GetContainers.this,Menu.class);
                        startActivity(endOut);
                    }
                    else
                    {
                            /*Toast.makeText(GetContainers.this, "Impossible à sortir !", Toast.LENGTH_LONG).show();*/
                    }
                }
                catch (IOException ex)
                {
                    /*Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();*/
                    System.err.println("Erreur[" + ex + "]");
                }
                catch (ClassNotFoundException ex)
                {
                    /*Toast.makeText(GetContainers.this, ex.getMessage(), Toast.LENGTH_LONG).show();*/
                    System.err.println("Erreur[" + ex + "]");
                }
                return null;
            }
        }.execute();
    }
}
