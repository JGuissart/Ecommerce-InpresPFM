package inprespfm.application_bateaux;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;

import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import static org.achartengine.ChartFactory.getBarChartIntent;

/**
 * Created by Julien on 19-11-15.
 */
public class HistogrammeRepartitionContainer
{
    private static DatabaseHandler sqlLiteConnection;
    private static SQLiteDatabase DB;

    public static Intent getIntent(Context context)
    {
        sqlLiteConnection = new DatabaseHandler(context, "DataDocker.bd", null, 3);
        DB = sqlLiteConnection.getReadableDatabase();

        String selectQueryIn = "SELECT strftime('%W', DateMouvement), COUNT(DateMouvement), DateMouvement, Destination FROM MOUVEMENTS WHERE TypeMouvement ='IN' GROUP BY Destination,strftime('%W', DateMouvement) ORDER BY 1, 4";
        String selectQueryOut = "SELECT strftime('%W', DateMouvement), COUNT(DateMouvement),DateMouvement, Destination FROM MOUVEMENTS WHERE TypeMouvement ='OUT' GROUP BY Destination,strftime('%W', DateMouvement) ORDER BY 1, 4";


        Cursor cursorIn = DB.rawQuery(selectQueryIn, null);
        Cursor cursorOut = DB.rawQuery(selectQueryOut, null);

        CategorySeries serie = new CategorySeries("Chargé");
        CategorySeries serie2 = new CategorySeries("Déchargé");
        XYMultipleSeriesRenderer msr = new XYMultipleSeriesRenderer();
        int i = 0;
        int maxY = 0;

        if (cursorIn.moveToFirst())
        {
            do
            {
                System.out.println(cursorIn.getString(0) + " - " + cursorIn.getString(2) +  " - " + cursorIn.getString(3) + " - " +cursorIn.getString(1));
                serie.add(cursorIn.getInt(1));

                if(cursorIn.getInt(1) > maxY)
                {
                    maxY = cursorIn.getInt(1);
                }

                msr.addXTextLabel(i+1, "___" + cursorIn.getString(0) + "/" + cursorIn.getString(3));
                i++;
            }
            while (cursorIn.moveToNext());
        }
        Double j = Double.valueOf("0");
        if (cursorOut.moveToFirst())
        {
            do
            {
                System.out.println(cursorOut.getString(0) + " - " + cursorOut.getString(1));
                serie2.add(cursorOut.getInt(1));

                if(cursorOut.getInt(1) > maxY)
                    maxY = cursorOut.getInt(1);

                msr.addXTextLabel(j + 1, "___" + cursorOut.getString(0) + "/" + cursorOut.getString(3));
                //}
                j++;
            }
            while (cursorOut.moveToNext());
        }

        XYMultipleSeriesDataset msds = new XYMultipleSeriesDataset();
        msds.addSeries(serie.toXYSeries());
        msds.addSeries(serie2.toXYSeries());

        // Histogramme 1
        XYSeriesRenderer sr1 = new XYSeriesRenderer();
        sr1.setDisplayChartValues(true);
        sr1.setChartValuesSpacing((float) 1);
        sr1.setColor(Color.CYAN);
        sr1.setChartValuesTextSize(50);
        // Histogramme 2
        XYSeriesRenderer sr2 = new XYSeriesRenderer();
        sr2.setDisplayChartValues(true);
        sr2.setChartValuesSpacing((float) 1);
        sr2.setColor(Color.GREEN);
        sr2.setChartValuesTextSize(50);

        msr.addSeriesRenderer(sr1);
        msr.addSeriesRenderer(sr2);
        msr.setChartTitle("Répartition du nombre de containers\nchargés ou déchargés par semaine\n par destination");
        msr.setYTitle("Nombres de containers");
        msr.setAxisTitleTextSize(50);
        msr.setShowAxes(true);

        msr.setChartTitleTextSize(60);
        msr.setAxesColor(Color.BLACK);
        msr.setLabelsColor(Color.GRAY);
        msr.setShowLegend(true);
        msr.setLegendTextSize(50);

        msr.setMargins(new int[]{10, 50, 250, 50});
        msr.setXLabelsAlign(Paint.Align.CENTER);
        msr.setXLabelsAngle(90);
        msr.setLabelsTextSize(50);
        msr.setXLabelsColor(Color.RED);
        msr.setXLabels(0);
        msr.setYLabels(0);

        if(i > j)
            msr.setXAxisMax(i + 1);
        else
            msr.setXAxisMax(j + 1);

        msr.setYAxisMax(maxY + 5);
        msr.setXAxisMin(0);
        msr.setYAxisMin(0);

        msr.setBarSpacing(0.5);
        msr.setLegendHeight(50);

        msr.setZoomEnabled(false);
        msr.setClickEnabled(false);

        msr.setFitLegend(true);
        msr.setPanEnabled(true, false);
        msr.setZoomEnabled(false, false);

        msr.setShowLabels(true);
        msr.setYLabelsAlign(Paint.Align.LEFT);

        Intent intent =  getBarChartIntent(context, msds, msr, BarChart.Type.DEFAULT);
        return intent;
    }
}
