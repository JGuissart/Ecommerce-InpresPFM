using Shared;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// Pour en savoir plus sur le modèle d'élément Page vierge, consultez la page http://go.microsoft.com/fwlink/?LinkId=391641

namespace WeatherAppWindows
{
    /// <summary>
    /// Une page vide peut être utilisée seule ou constituer une page de destination au sein d'un frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();

            this.NavigationCacheMode = NavigationCacheMode.Required;
        }

        /// <summary>
        /// Invoqué lorsque cette page est sur le point d'être affichée dans un frame.
        /// </summary>
        /// <param name="e">Données d'événement décrivant la manière dont l'utilisateur a accédé à cette page.
        /// Ce paramètre est généralement utilisé pour configurer la page.</param>
        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            // TODO: préparer la page pour affichage ici.

            // TODO: si votre application comporte plusieurs pages, assurez-vous que vous
            // gérez le bouton Retour physique en vous inscrivant à l’événement
            // Événement Windows.Phone.UI.Input.HardwareButtons.BackPressed.
            // Si vous utilisez le NavigationHelper fourni par certains modèles,
            // cet événement est géré automatiquement.
        }

        private void btnGetWeather_Click(object sender, RoutedEventArgs e)
        {
            Weather weather = Core.GetWeather(tbxZipCode.Text).Result;

            if (weather != null)
            {
                ResultsTitle.Text = weather.Title;
                tbkTemperature.Text = weather.Temperature;
                tbkVent.Text = weather.Wind;
                tbkVisibilite.Text = weather.Visibility;
                tbkHumidite.Text = weather.Humidity;
                tbkAube.Text = weather.Sunrise;
                tbkCrepuscule.Text = weather.Sunset;
            }
            else
                ResultsTitle.Text = "Couldn't find any results";
        }

        private void btnConvertToSIU_Click(object sender, RoutedEventArgs e)
        {
            // 1 mph = 1,60934 km/h
            // 1 °C = (°F - 32)/1.8
            // 1 mile = 1,60934 km

            tbkUniteTemperature.Text = "°C";
            tbkUniteVent.Text = "km/h";
            tbkUniteVisibilite.Text = "km";
            
            int iTemperatureF = Convert.ToInt32(tbkTemperature.Text);
            int iVitesseVentMPH = Convert.ToInt32(tbkVent.Text);
            int iVisibiliteM = Convert.ToInt32(tbkVisibilite.Text);

            double dTemperatureC = Convert.ToDouble(Math.Round((iTemperatureF - 32) / 1.8, 2));
            double dVitesseVentKMH = Convert.ToDouble(iVitesseVentMPH * 1.61);
            double dVisibiliteKM = Convert.ToDouble(iVisibiliteM * 1.61);

            tbkTemperature.Text = dTemperatureC.ToString();
            tbkVent.Text = dVitesseVentKMH.ToString();
            tbkVisibilite.Text = dVisibiliteKM.ToString();

            btnConvertToSIU.IsEnabled = false;
            btnConvertToSM.IsEnabled = true;
        }

        private void btnConvertToSM_Click(object sender, RoutedEventArgs e)
        {
            tbkUniteTemperature.Text = "°F";
            tbkUniteVent.Text = "mph";
            tbkUniteVisibilite.Text = "miles";

            double dTemperatureC = Convert.ToDouble(tbkTemperature.Text);
            double dVitesseVentKMH = Convert.ToDouble(tbkVent.Text);
            double dVisibiliteKM = Convert.ToDouble(tbkVisibilite.Text);

            int iTemperatureF = Convert.ToInt32((dTemperatureC * 1.8) + 32);
            int iVitesseVentMPH = Convert.ToInt32(dVitesseVentKMH / 1.61);
            int iVisibiliteM = Convert.ToInt32(dVisibiliteKM / 1.61);

            tbkTemperature.Text = iTemperatureF.ToString();
            tbkVent.Text = iVitesseVentMPH.ToString();
            tbkVisibilite.Text = iVisibiliteM.ToString();

            btnConvertToSIU.IsEnabled = true;
            btnConvertToSM.IsEnabled = false;
        }

        private void tbxZipCode_GotFocus(object sender, RoutedEventArgs e)
        {
            tbxZipCode.Text = "";
        }
    }
}
