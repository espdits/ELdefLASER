/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dellapenna.research.ldr;

import Servizi.GraficoRandomSelecter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author marco
 */
public class Popolazione {

    //Variabile che conta le mosse da effettuare per creare la prima popolazione
    final int contatoreMosse = 24;

    //Variabile che imposta la dimensione della popolazione
    final int dimPopolazione = 1000;
    
    //Variabile da impostare che determina la lunghezza della linea ( veloce per test ) 
    final int lungLinea = 50;

    // numero che influisce sulla funzione di fitness come la differenza dalla posizione da modificare
    // e il quadrato modificato 
    final double scartoPOS = 0.00125;
    //numero di individui da prelevare deve essere un numero abbastanza piccolo 
    // e in rapporto alla dimensione della popolazione antecedente e costante per 
    // tutte le successive ( sempre pari ) 
    final int selELit = 6;

    //ArrayList salvataggio prima popolazione 
    private HashMap<Integer, LineaDeformabile> primaPOP = new HashMap<>();
    
    private HashMap<Integer, LineaDeformabile> matingPool = new HashMap<>();
    
    private HashMap<Integer, LineaDeformabile> newPop = new HashMap<>();
    
    private HashMap<Integer, LineaDeformabile> oldPopolazione = new HashMap<>();
    
    
    
    //Soglia Probabilità di CrossOver deve essere dell'ordine di 10^-1
    final Double sogliaCross = 0.7;

    //Soglia Probabilità di Mutazione 0.001 a 0.01
    final Double sogliaMutazione = 0.01;
    
    // Tipo di modifiche attuabbili alla mutazione (  numero modifiche attuabili - 1 )
    int numMod = 3;
            
    //Generatori randomici
    Random rq = new Random(); //per le prove
    Random rm = new Random(); //per le prove

    
    
    

    
    /**
     * Metodo che crea la prima popolazione, dove ogni individuo ha subito un
     * numero di mosse prestabilito, salvandola in un file esterno.
     *
     * @throws java.io.IOException I/O eccezzione momentaneamente non gestita
     */
    public void creaPopolazione() throws IOException {

        for (int i = 0; i < dimPopolazione; i++) {
            //Creo nuova linea deformabile ( individuo )
            LineaDeformabile individuo = new LineaDeformabile();

            //applico 5 mosse randomiche ammissibili
            for (int j = 0; j < contatoreMosse; j++) {
                // posizione quadrato da deformare
                int quadrato_da_deformare;
                Mossa mossa_da_applicare;

                //sè è deformabile il quadrato selezionato deformo
                do { //Seleziono un quadrato da modificare
                    quadrato_da_deformare = rq.nextInt(individuo.lunghezza_linea);

                    // Seleziono un tipo di modifica da effettuare
                    switch (rm.nextInt(4)) {
                        default:

                        case 0:
                            mossa_da_applicare = Mossa.a_d;
                            break;
                        case 1:
                            mossa_da_applicare = Mossa.a_s;
                            break;
                        case 2:
                            mossa_da_applicare = Mossa.b_d;
                            break;
                        case 3:
                            mossa_da_applicare = Mossa.b_s;
                            break;
                    }

                } while (!(individuo.isDeformabile(mossa_da_applicare, quadrato_da_deformare)));
                // Deformo un quadrato della linea deformabile
                individuo.deforma(mossa_da_applicare, quadrato_da_deformare);

            }
            // Aggiungo un individuo ogni 5 trasformazioni

            //Setto nome individuo, per controlli futuri
            individuo.setName(String.valueOf(i));
            
            primaPOP.put(i,individuo);

        }
   
    } 

    /**
     * Metodo che valuta la fitness di un individuo rispetto la linea data
     *
     * @param linea foram che si vuole ottenere
     * @param lineaDeformabile individuo in esame
     * @return val_fitness valore della funzione di fitness
     */
    public Double valFitness(Linea linea, LineaDeformabile lineaDeformabile) {

        Double val_fitness = 0.0;
        // Per ogni quadrato modificato della linea deformabile
        ArrayList<Quadrato> quadrati_non_schedati;
        quadrati_non_schedati = new ArrayList<>();
        ArrayList<Integer> posizioni_quadrati_non_schedati;
        posizioni_quadrati_non_schedati = new ArrayList<>();

        for (Map.Entry entry_lineaDef : lineaDeformabile.getQuadratiDeformati().entrySet()) {
            //Quadrato LINEA deformabile
            Quadrato quadrato_lineaDef;
            //quadrato in esame della Linea Deformabile
            quadrato_lineaDef = (Quadrato) entry_lineaDef.getValue();

            // prendo posizione del quadrato in esame
            int posQLD;
            posQLD = (int) entry_lineaDef.getKey();
            //se ci sono quadrati limitrofi nella linea a quello selezionato della linea deformabile
            // se il quadrato è nella medesima posizione 
            if (linea.getQuadratiDeformati().containsKey(posQLD)) {
                //Quadrato della linea da confutare
                Quadrato quadrato_linea;
                quadrato_linea = linea.getQuadratiDeformati().get(posQLD);
                Double auxFit;
                auxFit = val_fitness;
                val_fitness = val_fitness + checkAndValutation(quadrato_linea, quadrato_lineaDef);
                // FLAG di selezione che aiutano al calcolo della funzione di fitness 
                quadrato_linea.flagSelezione = true;
                quadrato_lineaDef.flagSelezione = true;

            } // se è nella posizione -1 
            else if (linea.getQuadratiDeformati().containsKey(posQLD - 1)) {
                //Quadrato della linea da confutare
                Quadrato quadrato_linea;
                quadrato_linea = linea.getQuadratiDeformati().get(posQLD - 1);
                Double auxFit;
                auxFit = val_fitness;

                val_fitness = val_fitness + ((checkAndValutation(quadrato_linea, quadrato_lineaDef)) * 0.7);
                // FLAG di selezione che aiutano al calcolo della funzione di fitness 
                quadrato_linea.flagSelezione = true;
                quadrato_lineaDef.flagSelezione = true;

            } //se è nella posizione +1
            else if (linea.getQuadratiDeformati().containsKey(posQLD + 1)) {
                //Quadrato della linea da confutare
                Quadrato quadrato_linea;
                quadrato_linea = linea.getQuadratiDeformati().get(posQLD + 1);
                Double auxFit;
                auxFit = val_fitness;
                val_fitness = val_fitness + ((checkAndValutation(quadrato_linea, quadrato_lineaDef)) * 0.7);
                // FLAG di selezione che aiutano al calcolo della funzione di fitness 
                quadrato_linea.flagSelezione = true;
                quadrato_lineaDef.flagSelezione = true;

            } //non c'è nessun quadrato nelle posizioni limitrofi al quadrato della linea deformabile selezionatas
            else { // imposto a false il quadrato non schedato ( può essere anche obsoleta sta cosa ) 
                quadrato_lineaDef.flagSelezione = false;
                //metto il quadrato dentro un array li quadrati non schedati.
                quadrati_non_schedati.add(quadrato_lineaDef);
                //metto la posizione del medesimo quadrato in un array di posizioni dei quadrati
                posizioni_quadrati_non_schedati.add(posQLD);

            }
        }

        int aux_dif, aux_pos;
        aux_dif = 0;
        aux_pos = 0;

        for (Map.Entry entry_linea : linea.getQuadratiDeformati().entrySet()) {
            //variabile provvisoria di appogio differenza e posizioni da scorrere

            // quadrato da associare al primo quadrato dell'array list
            Quadrato quadrato_candidato;
            quadrato_candidato = (Quadrato) entry_linea.getValue();
            // se il flag di selezione è uguale a false del quadrato candidato all'associazione
            if (aux_pos < posizioni_quadrati_non_schedati.size() && aux_pos >= 0 && quadrato_candidato.flagSelezione == false) {

                // calcolo differenza di posizione associo il primo quadrato della linea al primo della linea deformabile
                aux_dif = Math.abs((posizioni_quadrati_non_schedati.get(aux_pos)) - (int) entry_linea.getKey());
                //System.out.println("Differenza aux " + (int) entry_linea.getKey() + " valore : " + aux_dif);

                //Devo aggiornare l'attuale valore di val_fitness
                // Sottraggo l'aux_dif e lo moltiplico per un peso in modo che vada ad
                // influire al valore di fitness
                //il range di valori può essere anche negativo
                val_fitness = val_fitness - (aux_dif * scartoPOS);
                //System.out.println("val fitness con sottrazione " + val_fitness);

                // il quadrato candidato diverrà un quadrato selezionato
                quadrato_candidato.flagSelezione = true;

                //aumento posizione per valutare il successivo quadrato
                aux_pos++;
            }

        }

        //resetto la variabile booooleaNA di ogni quadrato della linea
        for (Map.Entry entry_linea : linea.getQuadratiDeformati().entrySet()) {
            Quadrato reset = (Quadrato) entry_linea.getValue();
            reset.flagSelezione = false;
        }

        return val_fitness;
    }

    /**
     * vedi il tipo di trasformazione dei due quadrati di entrambe le linee e
     * assegna un valore di fitness
     *
     * @param quadrato_linea quadrato selezionato dalla linea
     * @param quadrato_lineaDef quadrato selezionato dalla linea deformabile
     * @return double val_fitness provvisorio senza riduzione di posizione.
     */
    private Double checkAndValutation(Quadrato quadrato_linea, Quadrato quadrato_lineaDef) {
        Double val_fitness;
        val_fitness = 0.0;
        switch (quadrato_linea.nome_def) {
            default:
                System.err.println("Non ci deve stare qui");
                return 0.0;
            case "UL":
                switch (quadrato_lineaDef.nome_def) {
                    default:
                        System.err.println("Non ci deve stare qui");
                        return 0.0;
                    case "UL":
                        //aggiorno fitness
                        return val_fitness = 0.8;

                    case "UR":
                        //aggiorno fitness
                        return val_fitness = 0.15;

                    case "LL":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                    case "LR":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                }

            case "LL":
                switch (quadrato_lineaDef.nome_def) {
                    default:
                        System.err.println("Non ci deve stare qui");
                        return 0.0;

                    case "LL":
                        //aggiorno fitness
                        return val_fitness = 0.8;

                    case "LR":
                        //aggiorno fitness
                        return val_fitness = 0.15;

                    case "UL":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                    case "UR":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                }

            case "UR":
                switch (quadrato_lineaDef.nome_def) {
                    default:
                        System.err.println("Non ci deve stare qui");
                        return 0.0;
                    case "UR":
                        //aggiorno fitness
                        return val_fitness = 0.8;

                    case "UL":
                        //aggiorno fitness
                        return val_fitness = 0.15;

                    case "LL":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                    case "LR":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                }

            //       break;
            case "LR":
                switch (quadrato_lineaDef.nome_def) {
                    default:
                        System.err.println("Non ci deve stare qui");
                        return 0.0;
                    case "LR":
                        //aggiorno fitness
                        return val_fitness = 0.8;

                    case "LL":
                        //aggiorno fitness
                        return val_fitness = 0.15;

                    case "UR":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                    case "UL":
                        //aggiorno fitness
                        return val_fitness = 0.025;

                }

        }

    }

    /**
     * Ritorna la prima popolazione
     *
     * @return primaPOP arraylist contenente la prima popolazione
     */
    public HashMap getPrimaPOP() {
        return primaPOP;
    }
    
    /**
     * Ritorna la nuova popolazione
     * @return newPrimaPop ritorna la nuova popolazione
     */
    public HashMap getNewPop(){
        return newPop;
    }

    /**
     * ritorna il numero di mosse da effettuare sulla linea deformabile
     *
     * @return numero mosse da fare sulla linea deformabile
     */
    public int getContatoreMosse() {
        return contatoreMosse;
    }

    /**
     * Metodo che crea una generazione successiva a quella data come input
     *
     */
    public void nextPopolazione() throws Exception {

        matingPool.clear();
        newPop.clear();
     
     
        
        // prima selezione ELITARISMO

        elitarism();

        
        // creazione mating pool e roulette wheel selection
        rouletteWheelSelection();

        // Applico parent selection o direttamente crossover e mutazione?
        //Applico direttamente crossover e mutazione
        crossover_mutazione();
        
        //mutazione beta
        mutazione();
         
    }

    /**
     * Elitarismo prende i migliori individui e li copia direttamente nella
     * popolazione successiva
     *
     * @param oldPopolazione vecchia popolazione da cui estrarre gli individui
     * @return newPopolazione primo riempimento del vettore della nuova
     * popolazione
     */
    private void elitarism() {

        ArrayList<LineaDeformabile> arrayValFitness = new ArrayList<>();
        
        ArrayList<Integer> arrayPos = new ArrayList<>();

        for(Map.Entry entry_lineaDef: oldPopolazione.entrySet()){
            //prendo la lineaDeformabile su cui lavorare
            LineaDeformabile lineaWork = (LineaDeformabile) entry_lineaDef.getValue();
            Integer posLW = (Integer) entry_lineaDef.getKey();
            // devo salvare le chiavi per poter poi prendere le linee deformabili con valore di fitness più alto.
            arrayValFitness.add(lineaWork);
       
        }
        
        // ordino l'arrayList dei valori di fitness in ordine crescente
        Collections.sort(arrayValFitness, new Comparator<LineaDeformabile>() {

        @Override
        public int compare(LineaDeformabile o1, LineaDeformabile o2) {
            return (o1.getVal_fitness()).compareTo(o2.getVal_fitness());
            }
        });
        
        // ordino l'arrayList dei valori di fintess in ordine decrescente
        Collections.reverse(arrayValFitness);
        
        // aggiungo i primi "selElit" elementi direttamente nella nuova popolazione
        for (int i = 0; i < selELit; i++) {

            //aggiungo gli elementi con fitness più alto nella nuova popolazione
            newPop.put(i,arrayValFitness.get(i));

        }

        //Salvo le posizioni delle linee deformabili da eliminare
        //per ogni linea deformabile presente nela nuova popolazione
        for (Map.Entry lineaDef : newPop.entrySet()) {
            LineaDeformabile lDef = (LineaDeformabile) lineaDef.getValue();
            boolean selector = true;
            //se la vecchia popolazione contiene la linea della nuova popolazione
            if (oldPopolazione.containsValue(lDef)) {
                // per ogni lineaDeformabile della vecchia popolazione
                for (Map.Entry entry_lineaDefOld : oldPopolazione.entrySet()) {
                    //selector è usato per evitare che venga scelta due la stessa linea da cancellare
                    if(selector) {
                        Integer pos = (Integer) entry_lineaDefOld.getKey();
                        LineaDeformabile lineaDefOld = (LineaDeformabile) entry_lineaDefOld.getValue();
                        // se la lineaDef della vecchia popolazione è uguale a quella presente nella nuova
                        // e array delle posizioni da eliminare non contiene la posizione che si sta cancellando
                        if (lineaDefOld.equals(lDef) && !(arrayPos.contains(pos))) {
                            arrayPos.add(pos);
                            selector=false;
                        }
                    }
                }
            }
        }
         
        // rimuovo dalla vecchia popolazione un numero preciso "selELit" di individui ( lineeDeformabili )
        for(Integer num: arrayPos){
            oldPopolazione.remove(num);
        }
    }

    /**
     * Seleziona gli individui per l'accoppiamento
     *
     * @param newPopolazione nuova popolazione da riempire con i restanti
     * elementi dopo l'elitarismo.
     * @param oldPopolazione vecchia popolazione con eliminazione degli
     * individui di Elite
     * @return matingPool piscina d'accoppiamento degli individui
     *
     */
    private void rouletteWheelSelection() throws IOException {

        Random r, rDiv;
        r = new Random();
        rDiv = new Random();
        

        //Somma fitness di tutta la popolazione
        Double deltaS;
        deltaS = 0.0;

        //Variabile di controllo numero indidui
        int contPOP = 0;

        // fitness MINIMO per effettuare la normalizzazione
        Double fitnessMIN;

        //Array di valori di fitness non normalizzati
        Double fitnessUPD[];
        fitnessUPD = new Double[oldPopolazione.size()];

        //Selezionatore di individuo è quel numero che esce e seleziona l'individuo.
        Double randomSelecter;
        randomSelecter = 0.0;

        //Array di numeri normalizzati "non negativi" i quali vengono assegnati alla lineaDeformabile 
        //per la selezione ( probabilità di selezione ).
        Double probSel[];
        probSel = new Double[oldPopolazione.size()];
        
        //Numeri della roulette
        Double roulSel[];
        roulSel = new Double[oldPopolazione.size()+1];
        
        //Numeri per graficare il numero random
        Double vectRandomSelecter[];
        vectRandomSelecter = new Double[oldPopolazione.size()];
        
        //Array per ordinare la fitness degli individuo rimaneti
        ArrayList<LineaDeformabile> arrayValFitness;
        arrayValFitness=new ArrayList<>();
        
        //riempio l'arrayList con le linee deformabili della vecchia popolazione
        for(Map.Entry entry_lineaDeformabile : oldPopolazione.entrySet()){
            LineaDeformabile lineaDef = (LineaDeformabile) entry_lineaDeformabile.getValue();
            arrayValFitness.add(lineaDef);
        }
        
        // svuoto la oldPopolazione
        oldPopolazione.clear();
        
        
        // Ordino in ordine crescente arrayList dei valori di fitness
        Collections.sort(arrayValFitness, new Comparator<LineaDeformabile>() {

        @Override
        public int compare(LineaDeformabile o1, LineaDeformabile o2) {
            return (o1.getVal_fitness()).compareTo(o2.getVal_fitness());
            }
        });
        
        // Ordino in ordine decrescente l'arrayList dei valori di Fitness
        Collections.reverse(arrayValFitness);
        
        //riempimento oldPopolazione in ordine decrescente
        
        // INDICE per dar la chiave ( Il più alto si accoppierà se sopra ad una certa
        // soglia sempre con il secondo più alto ) va bene ciò? ( more Randomicità !?!?!? ) 
        int i=0;
        for(LineaDeformabile lineaDef: arrayValFitness){           
            oldPopolazione.put(i, lineaDef);
            i++;
        }
        
        
        //TUTTO CIÒ PER PRENDERE IL VALORE DI FITNESS PIÙ PICCOLO E NORMALIZZARE
        // I DATI DELLA FUNZIONE DI FITNESS IN MANIERA TALE DI ESSERE NON NEGATIVI
        
        
        //ArrayList ordinato e prendo ultimo elemento che è il piu piccolo per effettuare una statistica
        // con valori tutti > 0
        fitnessMIN = oldPopolazione.get(oldPopolazione.size()-1).getVal_fitness();
        
        //indice di supporto  per salvataggio fitness vecchio ( non normalizzato ) 
        i = 0;
        
        //aggiorno le fitness di tutto l'arraylist
        for(Map.Entry entry_lineadef: oldPopolazione.entrySet()){
            LineaDeformabile lineaDef = (LineaDeformabile) entry_lineadef.getValue();
            fitnessUPD[i] = lineaDef.getVal_fitness();
            Double aux_fitness;
            aux_fitness = lineaDef.getVal_fitness();
            
            //se è negativo cambio segno 
            if(fitnessMIN<0){
                 lineaDef.setVal_fitness(aux_fitness + -fitnessMIN + 0.00001);
            }
            //altrimenti potrei fare anche nulla ma per flusso di logica faccio aggiungere ugualmente.
            else{
                 lineaDef.setVal_fitness(aux_fitness + fitnessMIN + 0.00001);
            }
            i++;
        }

        //Somme della fitness di tutta la popolazione
        for(Map.Entry entry_lineadef: oldPopolazione.entrySet()){
            LineaDeformabile lineaDeformabile= (LineaDeformabile) entry_lineadef.getValue();
                deltaS = deltaS + lineaDeformabile.getVal_fitness();
                contPOP++;
            }

        //Indice che associa alla lineaDeformabile alla prob. di Selezione propria.
        i = 0;
        //Probabilità di selezione è uguale fitness elemento diviso sommatoria dei fitness
          for(Map.Entry entry_lineadef: oldPopolazione.entrySet()){
            LineaDeformabile lineaDeformabile = (LineaDeformabile) entry_lineadef.getValue();
            probSel[i] = lineaDeformabile.getVal_fitness() / deltaS;
            lineaDeformabile=null;
            i++;
        }
        
        /*
        // creo la roulette vera e propria
        for(int k=0; k<probSel.length; k++){
            if(k==0){
                roulSel[k]= 0 + probSel[k];
            }
            else{
                roulSel[k] = roulSel[k-1] + probSel[k];
            }
        }
        */  
 
    //    System.out.println("");
     //   System.out.println("Inizio Roulette ");
        //Prima stanghetta roulette posta a 0
        roulSel[0] = 0.0;
    //    System.out.println("ROULETTE[0] = "+ roulSel[0]);
        // creo la roulette vera e propria
        for(int k=1; k<probSel.length; k++){
            roulSel[k] = roulSel[k-1] + probSel[k-1];  
     //       System.out.println("ROULETTE[" + k+"] = "+ roulSel[k]);
        }
        roulSel[oldPopolazione.size()] = 1.0;
     //   System.out.println("ROULETTE["+oldPopolazione.size()+"] = "+ roulSel[oldPopolazione.size()]);
      //  System.out.println("");
        
 
        
        
        //contatore che mi fa da chiave dell'hashmap della mating pool
        int conHM = 0;  
        // finche non estraggo tutti gli elementi della vecchia popolazione
        //TODO perfezionare qui
        while (oldPopolazione.size() != matingPool.size()) {
            
            //Devo generare dei numeri random con millesimali...  la prendo per buona cosi ma può
            //essere migliorata notevolmente   ||| È FONDAMENTALE QUESTO PASSO |||. 
            
            randomSelecter = generaRandomSelecter(rDiv, r);
            vectRandomSelecter[conHM] = randomSelecter;
          //  System.out.println("");
        //    System.out.println("Random selecter= " + randomSelecter);
            int contatore=0;
            // devo selezionare individuo estratto dal numero randomico.
            for (int j = 0; j < oldPopolazione.size() ; j++) {
    
                //prendo la lineaDeformabile su cui lavorare            
                LineaDeformabile lineaDefWork = (LineaDeformabile) oldPopolazione.get(j);
                if (randomSelecter < roulSel[j + 1] && randomSelecter > roulSel[j]) {

                    // per aggiornare al fitness reale
                    Double valFitnessWork = fitnessUPD[j];
                    lineaDefWork.setVal_fitness(valFitnessWork);
                    LineaDeformabile askatasuna;
                    askatasuna = lineaDefWork.clone();

                    // Giustamente gli indici dell'hashmap che cambiano
                    matingPool.put(conHM, askatasuna);
                    
                   // System.out.println("elemento selezionato: " + j );
                    
                    conHM++;
                    contatore++;

                }              
            }
        }
               
    /*    //Grafico RandomSelecter
        final GraficoRandomSelecter grcfRS;
        grcfRS = new GraficoRandomSelecter("Grafico valori randomici generati a ogni generazione", vectRandomSelecter );
        grcfRS.pack();
        RefineryUtilities.centerFrameOnScreen(grcfRS);
        grcfRS.setVisible(true);
    */    
    }

    
    /**
     * Genera il numero randomico per la selezione dell'individuo da immettere
     * nella matingPool
     *
     * @param rDiv generatore randomico per la selezione del tipo di divisione
     * @param r generatore randomico che genera il double per la selezione
     * @return randomSelecter il numero che porta alla selezione dell'individuo
     */
    private Double generaRandomSelecter(Random rDiv, Random r) {
        switch (rDiv.nextInt(5)) {
            case 0:
                return (Double) r.nextDouble() / 10;
            case 1:
                return (Double) r.nextDouble() / 1;
            case 2:
                return (Double) r.nextDouble() / 1;
            case 3:
                return (Double) r.nextDouble() / 10;
            case 4:
                return (Double) r.nextDouble() / 1;
            default:
                System.out.println("errore random selecter");
                return 9999999.0;
        }
    }

    /**
     * Metodo che permette l'attuazione del crossover: a) ad 1-punto b) ad
     * 2-punti c) uniforme d) aritmetico e della Mutazione In questo caso si è
     * usato ad un punto
     *
     * @param matingPool piscina di accoppiamento che contiene gli individui
     * candidati all'accopiamento.
     * @param newPopolazione popolazione da caricare che formerà la nuova
     * popolazione.
     */
    private void crossover_mutazione() throws Exception {

        System.out.println("");
        
        for (Map.Entry entry_lineaD : matingPool.entrySet()) {
            LineaDeformabile aux = new LineaDeformabile();
            aux = (LineaDeformabile) entry_lineaD.getValue();
            //   System.out.println("mating pool name " + aux.toString());
        }

        int iterazione = 1;
        Integer iterazione_plus = 0;
        //Prendo due elementi sequenziali della matingPool
        //stessi quadrati stessa posizione scoppia.
      
        while (!matingPool.isEmpty()) {

            //Posizione quadrati L1
            ArrayList<Integer> quadratiPosL1;
            quadratiPosL1 = new ArrayList<>();

            //Quadrati L1
            ArrayList<Quadrato> quadratiL1;
            quadratiL1 = new ArrayList<>();

            //Posizione quadrati L2
            ArrayList<Integer> quadratiPosL2;
            quadratiPosL2 = new ArrayList<>();

            //Quadrati L2
            ArrayList<Quadrato> quadratiL2;
            quadratiL2 = new ArrayList<>();

            //nuovo array posizioni quadrati ( considero il secondo individuo per effettuare una simil_Mutazione )
            ArrayList<Integer> newQuadratiL2;
            newQuadratiL2 = new ArrayList<>();

            // quadrati da modificare.
            int quadrati_da_scambiare;

            // numero estratto per effettuare il crossover
            Double pC;
            pC = rm.nextDouble();

            //Copio le linee 
            LineaDeformabile L1;
            L1 = matingPool.remove(iterazione_plus);

            LineaDeformabile L2;
            L2 = matingPool.remove(iterazione_plus + 1);

            //numeri quadrati deformati di ogni linea
            int rangeL1, rangeL2;
            rangeL1 = L1.getQuadratiDeformati().size();
            rangeL2 = L2.getQuadratiDeformati().size();

            //ArrayList di supporto per il salavataggio dell
            //Se Pc ( probabilità di crossover ) è maggiore di sogliaCross ( ordine 10^-1 )
            if (pC < sogliaCross) {

                // il maschio è L1 
                quadrati_da_scambiare = (int) rangeL1 / 2;

                // applico un tipo di crossover ad un punto 
                //Casto i dati per una migliore gestione Divido posizione e 
                // quadrato ma son collegati manualmente dall'indice
                for (Map.Entry entry_lineaDef : L1.getQuadratiDeformati().entrySet()) {

                    //Salvo il quadrato in variabile temporanea
                    Quadrato auxQ = (Quadrato) entry_lineaDef.getValue();

                    //entro nell'hashmap e creo la coppia chiave valore per L1.
                    quadratiPosL1.add((Integer) entry_lineaDef.getKey());
                    quadratiL1.add(auxQ);

                }

                //Casto i dati per una migliore gestione Divido posizione e 
                // quadrato ma son collegati manualmente dall'indice
                for (Map.Entry entry_lineaDef : L2.getQuadratiDeformati().entrySet()) {

                    //Salvo il quadrato in variabile temporanea
                    Quadrato auxQ = (Quadrato) entry_lineaDef.getValue();

                    //entro nell'hashmap e creo la coppia chiave valore per L2.
                    quadratiPosL2.add((Integer) entry_lineaDef.getKey());
                    quadratiL2.add(auxQ);
                }

                // se contiene valori uguali creo una mutazione 
                //vedo se ci sono incompatibilità 
                //L2 chiavi replicate TODO
                checkIncompatibilita(quadratiPosL1, quadratiPosL2, newQuadratiL2);

                //devo aggiornare le prime due posizioni di L2 prima di procedere alla trasformazione
                for (int h = 0; h < rangeL2; h++) {
                    L2.getQuadratiDeformati().remove(quadratiPosL2.get(h), quadratiL2.get(h));

                }
                for (int h = 0; h < rangeL2; h++) {
                    L2.getQuadratiDeformati().put(newQuadratiL2.get(h), quadratiL2.get(h));
                }

                // devo prendere gli indici a metà 
                for (int i = quadrati_da_scambiare; i < rangeL1; i++) {
                    if (L1.getQuadratiDeformati().remove(quadratiPosL1.get(i), quadratiL1.get(i))) {
                        // Se stessa chiave skippa e fai qualche mutazione ... o simile
                        L1.getQuadratiDeformati().put(newQuadratiL2.get(i), quadratiL2.get(i));
                    } 
                }

                for (int i = quadrati_da_scambiare; i < rangeL2; i++) {
                    //devo stare attento qui e vedere se leva e sostituisce quelli giusti. //aggiungo quadrato non aggiunto
                    if (L2.getQuadratiDeformati().remove(newQuadratiL2.get(i), quadratiL2.get(i))) {
                        L2.getQuadratiDeformati().put(quadratiPosL1.get(i), quadratiL1.get(i));

                    } 
                }

                //Devo aggiungere L1 e L2 nella nuova popolazione
                Integer ind = iterazione_plus;
                ind = ind + selELit; //inizio dall'indice dopo gli elementi aggiunti all'elitarismo
                Integer ind2 = ind + 1;
                newPop.put(ind, L1);
                newPop.put(ind2, L2);

            } else {
                // Devo aggiungere gli individui L1 e L2 non mutati nella nuova popolazione
                Integer ind = iterazione_plus;
                ind = ind + selELit; //inizio dall'indice dopo gli elementi aggiunti all'elitarismo
                Integer ind2 = ind + 1;
                newPop.put(ind, L1);
                newPop.put(ind2, L2);
            }
            iterazione++;
            iterazione_plus = iterazione_plus + 2;
        }
    }

    /**
     * Vede se ci sono incompatibilità nelle posizioni dei vettori creando una
     * sorta di mutazione
     *
     * @param quadratiPosL1 posizioni originarie primo individuo
     * @param quadratiPosL2 posizioni originarie secondo individuo
     * @param newQuadratiL2 nuove posizioni compatibili con l'indivuo in esame.
     */
    private void checkIncompatibilita(ArrayList<Integer> quadratiPosL1, ArrayList<Integer> quadratiPosL2, ArrayList<Integer> newQuadratiL2) {

        //Insieme di posizioni di L1 e L2
        Set elementiL1 = new TreeSet();
        Set elementiL2 = new TreeSet();
        SortedSet elementiNewL2 = new TreeSet();

        Integer newPos = null;

// stampo i due vettori 
        for (Integer pos : quadratiPosL1) {
            elementiL1.add(pos);
        }

        for (Integer pos : quadratiPosL2) {
            elementiL2.add(pos);
        }

        //TODO deve migliorare crea delle chiavi che alla fine sono uguali a quelle di L1
        //per tutti gli elementi di L2
        for (Integer pos2 : quadratiPosL2) {
            newPos = pos2;
            //se pos2 è contenuto in L1
            if (elementiL1.contains(pos2)) {
                //aggiorno la posizione presente in pos2
                do {
                    //evito lo zero e includo il la size !!! importante !!! size deve essere stessa lunghezza delle linea deformabile.
                    newPos = ((newPos + 2) % lungLinea) + 1;
                    //Finche non ho elementi non contenuti ne in L1 ne in L2    
                } while ((elementiL1.contains(newPos)) || (elementiL2.contains(newPos)) || (elementiNewL2.contains(newPos)));

                elementiNewL2.add(newPos);
            } else {
                elementiNewL2.add(newPos);
            }

        }

        for(Object numeroPos : elementiNewL2) {
            Integer pos = (Integer) numeroPos;
            newQuadratiL2.add(pos);

        }

    }

    public HashMap<Integer, LineaDeformabile> getOldPopolazione() {
        return oldPopolazione;
    }

    public void setOldPopolazione(HashMap<Integer, LineaDeformabile> oldPopolazione) {
        this.oldPopolazione = oldPopolazione;
    }
    
    
     private void mutazione(){
         // Variabili su cui lavorare
     
       
         // per tutte le linee deformabili della nuova popolazione
         for(Map.Entry entry_lineaDef : newPop.entrySet()){
            // Variabili su cui lavorare
            HashMap pos_set = new HashMap();
            LineaDeformabile lineaDef = (LineaDeformabile) entry_lineaDef.getValue();
            // seleziono un quadrato della linea e lo modifico.
                 
            // genero un intero randomico che permette la selezione casuale di un quadrato
            int rmSelect = rm.nextInt(contatoreMosse);
     //       System.out.println("rmSelect " + rmSelect);
            int contMS=0;    
            //scelgo in base al numero randomico il quadrato da mutare
            for(Map.Entry entry_quad_mod : lineaDef.getQuadratiDeformati().entrySet()){
                    
                Integer pos_work;
                pos_work = ( Integer ) entry_quad_mod.getKey();
                     
                //Carico l'insieme con i dati delle posizioni dei quadrati
                pos_set.put(contMS,pos_work);   
                contMS++;
            }
            // devo modificare la posizione estratta dal random selecter
            Integer pos_work_now = (Integer) pos_set.get(rmSelect);
            // devo modificare il quadrato nella posizione selezionata nella lineaDeformabile
            Quadrato quadrato_work = (Quadrato ) lineaDef.getQuadratiDeformati().get(pos_work_now);
            
            // per modificare: devo sapere la modifica che aveva e selezionarla una nuova diversa
            // dalla precedente.
            String modifica = quadrato_work.nome_def;
             switch (modifica) {
                 case "LL":
                     switchLL(quadrato_work);
                     break;
                 case "LR":
                     switchLR(quadrato_work);
                     break;
                 case "UL":
                     switchUL(quadrato_work);
                     break;
                 case "UR":
                     switchUR(quadrato_work);
                     break;
                 default:
                     System.err.println("errore modifica out of bound");
                     break;
             }
          //imposto il quadrato mutato nella linea deformabile 
          lineaDef.getQuadratiDeformati().put(pos_work_now, quadrato_work);
            
        }  
    }
     
     private void switchLL(Quadrato quadrato_work ){
        //genero un altro numero casuale
        int intMod = rq.nextInt(numMod);  
     
        switch(intMod){
            case 0:
                quadrato_work.nome_def="LR";
                break;
            case 1: 
                quadrato_work.nome_def="UL";
                break;
            case 2: 
                quadrato_work.nome_def="UR";
                break; 
            default:
                System.err.println("errore modifica numMOD");
                break;
        }
         
     }
     
     
    private void switchLR(Quadrato quadrato_work) {
        //genero un altro numero casuale
        int intMod = rq.nextInt(numMod);

        switch (intMod) {
            case 0:
                quadrato_work.nome_def = "LL";
                break;
            case 1:
                quadrato_work.nome_def = "UL";
                break;
            case 2:
                quadrato_work.nome_def = "UR";
                break;
            default:
                System.err.println("errore modifica numMOD");
                break;
        }
    }
          
          
    private void switchUL(Quadrato quadrato_work ){
        //genero un altro numero casuale
        int intMod = rq.nextInt(numMod);  
        
                switch(intMod){
            case 0:
                quadrato_work.nome_def="LR";
                break;
            case 1: 
                quadrato_work.nome_def="LL";
                break;
            case 2: 
                quadrato_work.nome_def="UR";
                break; 
            default:
                System.err.println("errore modifica numMOD");
                break;
        }
         
    }
               
    private void switchUR(Quadrato quadrato_work ){
        //genero un altro numero casuale
        int intMod = rq.nextInt(numMod);  
         
                switch(intMod){
            case 0:
                quadrato_work.nome_def="LR";
                break;
            case 1: 
                quadrato_work.nome_def="UL";
                break;
            case 2: 
                quadrato_work.nome_def="LL";
                break; 
            default:
                System.err.println("errore modifica numMOD");
                break;
        }
    }
     
     
     
     
}
