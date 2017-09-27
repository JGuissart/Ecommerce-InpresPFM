using System;
using Android.App;
using Android.Content;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.OS;
using Shared;

namespace WeatherApp
{
    [Activity(Label = "WeatherApp", MainLauncher = true, Icon = "@drawable/icon")]
    public class MainActivity : Activity
    {
        protected override void OnCreate(Bundle bundle)
        {
            base.OnCreate(bundle);

            SetContentView(Resource.Layout.Main);

            Button btnRechercher = FindViewById<Button>(Resource.Id.btnRechercher);

            btnRechercher.Click += delegate
            {
                Weather weather = Core.GetWeather(FindViewById<EditText>(Resource.Id.edtxtCodePostal).Text).Result;

                if (weather != null)
                {
                    FindViewById<TextView>(Resource.Id.txtvMeteo).Text = weather.Title;
                    FindViewById<TextView>(Resource.Id.txtvTemperature).Text = weather.Temperature;
                    FindViewById<TextView>(Resource.Id.txtvVent).Text = weather.Wind;
                    FindViewById<TextView>(Resource.Id.txtvVisibilite).Text = weather.Visibility;
                    FindViewById<TextView>(Resource.Id.txtvHumidite).Text = weather.Humidity;
                    FindViewById<TextView>(Resource.Id.txtvAube).Text = weather.Sunrise;
                    FindViewById<TextView>(Resource.Id.txtvCrepuscule).Text = weather.Sunset;
                }
                else
                    FindViewById<TextView>(Resource.Id.txtvMeteo).Text = "Couldn't find any results";
            };

            Button btnConvertToSIU = FindViewById<Button>(Resource.Id.btnConvertToSIU);
            Button btnConvertToSM = FindViewById<Button>(Resource.Id.btnConvertToSM);

            btnConvertToSIU.Click += delegate
            {
                FindViewById<TextView>(Resource.Id.txtvUniteTemperature).Text = "°C";
                FindViewById<TextView>(Resource.Id.txtvUniteVent).Text = "km/h";
                FindViewById<TextView>(Resource.Id.txtvUniteVisibilite).Text = "km";

                int iTemperatureF = Convert.ToInt32(FindViewById<TextView>(Resource.Id.txtvTemperature).Text);
                int iVitesseVentMPH = Convert.ToInt32(FindViewById<TextView>(Resource.Id.txtvVent).Text);
                int iVisibiliteM = Convert.ToInt32(FindViewById<TextView>(Resource.Id.txtvVisibilite).Text);

                double dTemperatureC = Convert.ToDouble(Math.Round((iTemperatureF - 32) / 1.8, 2));
                double dVitesseVentKMH = Convert.ToDouble(iVitesseVentMPH * 1.61);
                double dVisibiliteKM = Convert.ToDouble(iVisibiliteM * 1.61);

                FindViewById<TextView>(Resource.Id.txtvTemperature).Text = dTemperatureC.ToString();
                FindViewById<TextView>(Resource.Id.txtvVent).Text = dVitesseVentKMH.ToString();
                FindViewById<TextView>(Resource.Id.txtvVisibilite).Text = dVisibiliteKM.ToString();

                btnConvertToSIU.Enabled = false;
                btnConvertToSM.Enabled = true;
            };

            btnConvertToSM.Click += delegate
            {
                FindViewById<TextView>(Resource.Id.txtvUniteTemperature).Text = "°F";
                FindViewById<TextView>(Resource.Id.txtvUniteVent).Text = "mph";
                FindViewById<TextView>(Resource.Id.txtvUniteVisibilite).Text = "miles";

                double dTemperatureC = Convert.ToDouble(FindViewById<TextView>(Resource.Id.txtvTemperature).Text);
                double dVitesseVentKMH = Convert.ToDouble(FindViewById<TextView>(Resource.Id.txtvVent).Text);
                double dVisibiliteKM = Convert.ToDouble(FindViewById<TextView>(Resource.Id.txtvVisibilite).Text);

                int iTemperatureF = Convert.ToInt32((dTemperatureC * 1.8) + 32);
                int iVitesseVentMPH = Convert.ToInt32(dVitesseVentKMH / 1.61);
                int iVisibiliteM = Convert.ToInt32(dVisibiliteKM / 1.61);

                FindViewById<TextView>(Resource.Id.txtvTemperature).Text = iTemperatureF.ToString();
                FindViewById<TextView>(Resource.Id.txtvVent).Text = iVitesseVentMPH.ToString();
                FindViewById<TextView>(Resource.Id.txtvVisibilite).Text = iVisibiliteM.ToString();

                btnConvertToSIU.Enabled = true;
                btnConvertToSM.Enabled = false;
            };
        }
    }
}

