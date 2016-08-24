/*
 *  NON ancora utilizzata 
 */
package Servizi;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 *  Classe che permette la realizzazione di un grafico per confutare i dati.
 * 
 * @author Esteban Lombardozzi
 */
public class GraficoRandomSelecter extends ApplicationFrame {

    /**
     * Costruttore che permette la visualizzazione di un grafico 
     * @param titolo Targhetta che descrive il grafico
     * @param datiRandomSelecter
     * @throws IOException 
     */
    public GraficoRandomSelecter(final String titolo, Double[] datiRandomSelecter) throws IOException {

        super(titolo);

        final XYDataset dataset = createDataset(datiRandomSelecter);

        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setPreferredSize(new java.awt.Dimension(700, 470));

        setContentPane(chartPanel);

        ChartUtilities.saveChartAsPNG(new File("/home/gianni/Documenti/output/graficoRandomS.png"), chart, 1024, 768);
        
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    /**
     *
     * Creazione del dataset da utilizzare per la generazione del grafi
     *
     * Ogni grafico ha un suo dataset specifico
     *
     * @return un dataset di default.
     *
     */
    private XYDataset createDataset(Double[] datiRandomSelecter) {

        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries dataRandomSelecter = riempiDataRandomSelecter("Valore RandomSelecter", datiRandomSelecter);

     //   XYSeries dataCont = riempiDataContatore("Contatore ", datiRandomSelecter);

     //   dataset.addSeries(dataCont);
        dataset.addSeries(dataRandomSelecter);

        return dataset;

    }

    /**
     * Creo la prima serie di dati contenenti i la media della fitness di ogni
     * popolazione
     *
     * @param label targhetta da stampare sul grafo
     * @return serie di valori per riempimento grafo
     */
    private XYSeries riempiDataRandomSelecter(String label, Double[] datiRandomSelecter) {

        XYSeries serie = new XYSeries(label);

        for (int i = 0; i < datiRandomSelecter.length; i++) {

            serie.add(i, datiRandomSelecter[i]);

        }

        return serie;

    }

    /**
     * Creo la serie di dati che rappresentano la fitness della linea
     *
     * @param label targhetta da stampare sul grafo
     * @param datiFitness dati contenenti il valore della media del valore di
     * fitness di ogni popolazione
     * @param fitnessLinea valore di fitness della linea da generare
     * @return serie di valori per riempimento grafo
     */
    private XYSeries riempiDataContatore(String label, Double[] datiRandomSelecter) {

        XYSeries serie = new XYSeries(label);

        for (int i = 0; i < datiRandomSelecter.length; i++) {

            serie.add(i,i );

        }

        return serie;

    }

    /**
     *
     * Metodo deputato alla creazione del grafico.
     *
     * @param dataset il dataset creato dal metodo createDataset
     *
     * @return il grafico.
     *
     */
    private JFreeChart createChart(final XYDataset dataset) {

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Grafico a Linea Random Selecter", //titolo

                "Individui Selezionati", //label asse delle X

                "Valore Random Selecter", //label asse dell Y

                dataset, // sorgente dei dati

                PlotOrientation.VERTICAL, //orientamento del grafico

                true, // mostra la legenda

                true, //usa i tooltip

                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);

        plot.setRenderer(renderer);

        renderer.setBaseShapesVisible(true);

        renderer.setBaseShapesFilled(true);

        NumberFormat format = NumberFormat.getNumberInstance();

        format.setMaximumFractionDigits(2);

        XYItemLabelGenerator generator
                = new StandardXYItemLabelGenerator(
                        StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT,
                        format, format);

        renderer.setBaseItemLabelGenerator(generator);

        renderer.setBaseItemLabelsVisible(true);

        return chart;

    }


}
