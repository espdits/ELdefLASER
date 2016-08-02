/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dellapenna.research.ldr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author marco
 */
public class Popolazione {

    //Variabile che conta le mosse da effettuare per creare la prima popolazione
    final int contatoreMosse = 5;

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
        // ArrayList contenente gli individui generati
        // primaPopolazione = new ArrayList<>();
        // fino a che non creo 50 individui 

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
                    // System.err.println("numero estratto"+quadrato_da_deformare);
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

                    // System.err.println("isDeformabile"+individuo.isDeformabile(mossa_da_applicare,quadrato_da_deformare));
                } while (!(individuo.isDeformabile(mossa_da_applicare, quadrato_da_deformare)));
                // Deformo un quadrato della linea deformabile
                individuo.deforma(mossa_da_applicare, quadrato_da_deformare);

                //System.err.println("quadrato defotmato" + quadrato_da_deformare + "numero trasformazioni"+j + "DIM_POPOLAZIONE"+i);
            }
            // Aggiungo un individuo ogni 5 trasformazioni

            //Setto nome individuo, per controlli futuri
            individuo.setName(String.valueOf(i));
            
            primaPOP.put(i,individuo);

        }
        //Stampe di controllo della popolazione
        //    System.out.println("individuo nella poplazione " +it);
        //   });

      //  System.out.println("Popolazione" + primaPopolazione.size());
   
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
                //System.out.println("quadrato non schedato aggiunto" + posQLD);

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

     //   ArrayList<LineaDeformabile> newPopolazione;
     //   newPopolazione = new ArrayList<>();
        
     //   ArrayList<LineaDeformabile> matingPool;
     //   matingPool = new ArrayList<>();

        matingPool.clear();
        newPop.clear();
     
     
        
        // prima selezione ELITARISMO

        elitarism();
     /*   for(Map.Entry entry_lineaDef : newPop.entrySet()){
             LineaDeformabile lineaDef = (LineaDeformabile) entry_lineaDef.getValue();
             System.out.println(" Valori dopo elitarismo nome: "+ lineaDef.getName()+" valore fitness: " + lineaDef.getVal_fitness() );
        }
        
        for(Map.Entry entry_lineaDef : oldPopolazione.entrySet()){
            LineaDeformabile lineaDef = (LineaDeformabile) entry_lineaDef.getValue();
            System.out.println("old Popolazione nome: " + lineaDef.getName() + " valore fitness: " + lineaDef.getVal_fitness());
        }*/
       
        //System.out.println("oldpos size" + oldPopolazione.size());
        
        
        // creazione mating pool e roulette wheel selection
        rouletteWheelSelection();
        //matingPool = rouletteWheelSelection(newPopolazione, oldPopolazione);

        // Applico parent selection o direttamente crossover e mutazione?
        //Applico direttamente crossover e mutazione
     
        crossover_mutazione();
         
      //  System.out.println("newPop size " + newPop.size());
        //Stampo della new popolazione 
    /*    for(Map.Entry entry_newPop: newPop.entrySet()){
            LineaDeformabile aux = new LineaDeformabile();
            aux= (LineaDeformabile) entry_newPop.getValue();
            System.out.println("newPOP : " + aux.getName());
        }*/
        
        
        
        // si riparte da qui ma devo aggiungere gli individui nella popolazione
        
        
       // return newPopolazione;
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
        
        
      
        
 /*       //metto valori piccolissimi nell'array
        for(int i=0; i<arrayValFitness.length; i++){
            arrayValFitness[i]= Double.NEGATIVE_INFINITY;
        }
        */
       
   
        // vettore di ritorno
       // ArrayList<LineaDeformabile> newPopolazione;
       // newPopolazione = new ArrayList<>();
/*
        Collections.sort(oldPopolazione, new Comparator<LineaDeformabile>() { Collections.sort(oldPopolazione, new Comparator<LineaDeformabile>() {

            @Override
            public int compare(LineaDeformabile o1, LineaDeformabile o2) {
                return (o1.getVal_fitness()).compareTo(o2.getVal_fitness());
            }
        });
        Collections.reverse(oldPopolazione);
*/

        for(Map.Entry entry_lineaDef: oldPopolazione.entrySet()){
            //prendo la lineaDeformabile su cui lavorare
            LineaDeformabile lineaWork = (LineaDeformabile) entry_lineaDef.getValue();
            Integer posLW = (Integer) entry_lineaDef.getKey();
            // devo salvare le chiavi per poter poi prendere le linee deformabili con valore di fitness più alto.
            arrayValFitness.add(lineaWork);
       //     arrayPosOldPop.add(posLW);
       
        }
        
        Collections.sort(arrayValFitness, new Comparator<LineaDeformabile>() {

        @Override
        public int compare(LineaDeformabile o1, LineaDeformabile o2) {
            return (o1.getVal_fitness()).compareTo(o2.getVal_fitness());
            }
        });
        Collections.reverse(arrayValFitness);
        
    /*    for(LineaDeformabile lineaDef : arrayValFitness){
            System.out.println("val fitness Elitarismo " + lineaDef.getVal_fitness());
        }*/
        

        for (int i = 0; i < selELit; i++) {

            //aggiungo gli elementi con fitness più alto nella nuova popolazione
            newPop.put(i,arrayValFitness.get(i));

        }
      //  System.out.println("size arrayfitness " + arrayValFitness.size());
        
        //Salvo le posizioni delle linee deformabili da eliminare
        
        //per ogni linea deformabile presente nela nuova popolazione
        for (Map.Entry lineaDef : newPop.entrySet()) {
            LineaDeformabile lDef = (LineaDeformabile) lineaDef.getValue();
            boolean selector = true;
            //se la vecchia popolazione contiene la linea della nuova popolazione
            if (oldPopolazione.containsValue(lDef)) {
                // per ogni lineaDeformabile della vecchia popolazione
                for (Map.Entry entry_lineaDefOld : oldPopolazione.entrySet()) {
                    //non so se è ottima come cosa ma test
                    if(selector) {
                        Integer pos = (Integer) entry_lineaDefOld.getKey();
                        LineaDeformabile lineaDefOld = (LineaDeformabile) entry_lineaDefOld.getValue();
                        //  System.out.println("linea 1 " + lineaDefOld.getName() + "linea 2 "+ lDef.getName());
                        if (lineaDefOld.equals(lDef) && !(arrayPos.contains(pos))) {
          //                  System.out.println("TROVATA");
                            arrayPos.add(pos);
                            selector=false;
                        }
                    }
                }
            }
        }
                
     //   System.out.println("prima remove size oldPop " + oldPopolazione.size() + " size chiavi eliminazione "+ arrayPos.size());    
       
     
     
        for(Integer num: arrayPos){
    //        System.out.println("posizione la quale verrà rimossa " + num);
            oldPopolazione.remove(num);
        }
                       
      //  System.out.println("dopo remove size oldPop " + oldPopolazione.size());   
        
        
      /*  for (int i = 0; i < selELit; i++) {

            //  System.out.println("elemento fitness rimosso " + oldPopolazione.get(0).getVal_fitness());
            oldPopolazione.remove(0);
        }*/

   //     return newPopolazione;
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
    private void rouletteWheelSelection() {

        Random r, rDiv;
        r = new Random();
        rDiv = new Random();
        
        Set newSetMatingPool = new TreeSet();

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
        roulSel = new Double[oldPopolazione.size()];
             
        
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
        
        
        
        Collections.sort(arrayValFitness, new Comparator<LineaDeformabile>() {

        @Override
        public int compare(LineaDeformabile o1, LineaDeformabile o2) {
            return (o1.getVal_fitness()).compareTo(o2.getVal_fitness());
            }
        });
        Collections.reverse(arrayValFitness);
        
        //Stampa e riempimento oldPopolazione in ordine decrescente
        int i=0;
        for(LineaDeformabile lineaDef: arrayValFitness){
            
           // System.out.println("valore in array fitness PROVA: " + lineaDef.getVal_fitness());
            oldPopolazione.put(i, lineaDef);
            i++;
        }
                 
        
                

        

        //ArrayList ordinato e prendo ultimo elemento che è il piu piccolo per effettuare una statistica
        // con valori tutti > 0
        fitnessMIN = oldPopolazione.get(oldPopolazione.size()-1).getVal_fitness();
   //     System.out.println("");
     //   System.out.println("fitness minimo " + fitnessMIN);
     //  System.out.println("");
      /*  for(Map.Entry entry_lineadef: oldPopolazione.entrySet()){
            LineaDeformabile lineadef = (LineaDeformabile) entry_lineadef.getValue();
            System.out.println("prova stampa fintess valutazione linea nome:  " + lineadef.getName() + " valore " + lineadef.getVal_fitness());
        }
        System.out.println("");
        System.out.println("Fitness MIN " + fitnessMIN);
        System.out.println("");
      */ 
        i = 0;
        //aggiorno le fitness di tutto l'arraylist
        for(Map.Entry entry_lineadef: oldPopolazione.entrySet()){
            LineaDeformabile lineaDef = (LineaDeformabile) entry_lineadef.getValue();
            //   System.out.println("valore fitness " + lineaDeformabile.getVal_fitness());
            fitnessUPD[i] = lineaDef.getVal_fitness();
            Double aux_fitness;
            aux_fitness = lineaDef.getVal_fitness();
            
           // System.out.println("aux _ fitness " + aux_fitness);
            //se è negativo cambio segno 
            if(fitnessMIN<0){
                 lineaDef.setVal_fitness(aux_fitness + -fitnessMIN + 0.00001);
            }
            //altrimenti potrei fare anche nulla ma per prova ora faccio aggiungere ugualmente.
            else{
                 lineaDef.setVal_fitness(aux_fitness + fitnessMIN + 0.00001);
            }
        //    System.out.println("valore fitness aggiornato  " + lineaDeformabile.getVal_fitness());
        //    System.out.println("valore fitness vector  " + fitnessUPD[i]);
            i++;
        }

        //Somme della fitness di tutta la popolazione
        for(Map.Entry entry_lineadef: oldPopolazione.entrySet()){
            LineaDeformabile lineaDeformabile= (LineaDeformabile) entry_lineadef.getValue();
                deltaS = deltaS + lineaDeformabile.getVal_fitness();
                contPOP++;
            }
        /*    else{
                System.out.println("errore fitness negativa ");
            }*/
            
        
        i = 0;
        //Probabilità di selezione è uguale fitness elemento diviso sommatoria dei fitness
          for(Map.Entry entry_lineadef: oldPopolazione.entrySet()){
       //   for(int j=0; j<oldPopolazione.size();j++){
          //  LineaDeformabile lineaDeformabile= (LineaDeformabile) oldPopolazione.get(j);
            LineaDeformabile lineaDeformabile = (LineaDeformabile) entry_lineadef.getValue();
            probSel[i] = lineaDeformabile.getVal_fitness() / deltaS;
    //        System.out.println("probabilita di selezione " + probSel[i]);
            lineaDeformabile=null;
            i++;
        }
        
        
        // creo la roulette vera e propria
        for(int k=0; k<probSel.length; k++){
            if(k==0){
                roulSel[k]= 0 + probSel[k];
            }
            else{
                roulSel[k] = roulSel[k-1] + probSel[k];
            }
        }
        
 //       System.out.println("");
        // stampa di prova
 /*       for(Double aux : roulSel){
            System.out.println("roul sel " + aux);
        }*/
          
 //       System.out.println("");
        //contatore che mi fa da chiave dell'hashmap della mating pool
        int conHM = 0;  
        // finche non estraggo tutti gli elementi della vecchia popolazione
        //TODO perfezionare qui
        while (oldPopolazione.size() != matingPool.size()) {
            
  //          System.out.println("");
  //          System.out.println("while mating pool roulette wheel selection \n oldpopolazione " + oldPopolazione.size() + " \n mating size  "  + matingPool.size());

            //Devo generare dei numeri random con millesimali...  la prendo per buona cosi ma può
            //essere migliorata notevolmente   ||| È FONDAMENTALE QUESTO PASSO |||. 
            
            randomSelecter = generaRandomSelecter(rDiv, r);
     
            int contatore=0;
            // devo selezionare individuo estratto dal numero randomico.
            for (int j = 0; j < oldPopolazione.size() - 1; j++) {
             //   for(Map.Entry entry_lineaDef: oldPopolazione.entrySet()){
            //prendo la lineaDeformabile su cui lavorare
                    
                      LineaDeformabile lineaDefWork = (LineaDeformabile) oldPopolazione.get(j);
                         
     
    //            LineaDeformabile lineaDefWork = (LineaDeformabile) oldPopolazione.get(j);
          
           //     System.out.println("random SELECTER prima for: "+ randomSelecter);
                // random selecter mi prende piu individui quando dovrebbere essere uno alla
                // volta.
              //   System.out.println("random SELECTER generato: "+ randomSelecter);
                
                if (randomSelecter < roulSel[j + 1] && randomSelecter > roulSel[j]) {
                    
                    
  //                  System.out.println("j+1: " + roulSel[j+1]+ "  j: "+ roulSel[j]);
   //                 System.out.println("random SELECTER selezionato: "+ randomSelecter);
                
                    // per aggiornare al fitness reale
                    Double valFitnessWork = fitnessUPD[j];
                    lineaDefWork.setVal_fitness(valFitnessWork);

                    
                    // Giustamente gli indici dell'hashmap cambiano
                    matingPool.put(conHM,lineaDefWork);
                 
                     
             //       System.out.println("pos: " + conHM + " indivuo in mating POOOOOOLLL " + lineaDefWork.toString());
                    conHM++;
                    contatore++;
              //      System.out.println("");
               //     System.out.println("numeri individui inseriti in mating pool " + contatore);
                 //   System.out.println("");

                }
                
            //  System.out.println("j e mating pool size " + j + "   " + matingPool.size());

            }
        }
    }       
                    
                    
                    /*
                    if(newSetMatingPool.isEmpty()){
                        System.out.println("sto dentro a 0");
                        matingPool.add(lineaDefWork);
                        System.out.println("erereeererererer");
                      
                        newSetMatingPool.add((LineaDeformabile)lineaDefWork);
                        System.out.println("iriririririririri");
                    }
                    else if(!newSetMatingPool.contains(lineaDefWork)){
                        System.out.println(" non contente in Insieme " + lineaDefWork.toString());
                        matingPool.add(lineaDefWork);
                        newSetMatingPool.add((LineaDeformabile)lineaDefWork);
                        
                    }
                    else{
                        LineaDeformabile newLineaDefWork;
                        newLineaDefWork = (LineaDeformabile)lineaDefWork.clone();
                        /*newLineaDefWork.setName(lineaDefWork.getName());
                        newLineaDefWork.setQuadrati_deformati(lineaDefWork.getQuadratiDeformati());
                        newLineaDefWork.setVal_fitness(lineaDefWork.getVal_fitness());*/
                   /*     newSetMatingPool.add((LineaDeformabile) newLineaDefWork);
                        matingPool.add(newLineaDefWork);
                        System.out.println(" contente in Insieme " + newLineaDefWork.toString());
                    }*/ 
    /*
                }

            }

            //   System.out.println("random number " + randomSelecter);
        }

      //  System.out.println("SOMMA DELLE PROBABILITÀ DI SELEZIONE ( DEVE ESSERE UGUALE A 1 )");
        Double provaSel = 0.0;
        for (Double giro : probSel) {
            provaSel = provaSel + giro;
        }

   //     System.out.println("");
   //     System.out.println(provaSel);
   //     System.out.println("");

   //     System.out.println("INDIVIDUI NELLA MATING POOL");

        //Supporto
        int indice_aiuto = 0;
        for (LineaDeformabile giro : matingPool) {
    //        System.out.println(indice_aiuto + ")NOME INDIVIDUO  " + giro.getName() + "  fintess individuo nella mating pool  " + giro.getVal_fitness());
            indice_aiuto++;
        }
      //  System.out.println("popolazione attuale selezionata " + contPOP + "   Val Delta S " + deltaS);
        System.out.println("prima del riempimento insieme");
      //  Devo rimpire l'insieme
   //   for(LineaDeformabile lineaD: matingPool){
     //     newSetMatingPool.add(lineaD);
       //   }
      
        System.out.println("size elementi newMatingPool " + newSetMatingPool.size());
      
      
      
      
      
        return matingPool;

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
                //     System.out.println("sto in 0");
                return (Double) r.nextDouble()/ 10;
            //         break;
            case 1:
                //   System.out.println("sto in 1");
                return (Double) r.nextDouble() / 1;
            //   break;
            case 2:
                // System.out.println("sto in 2");
                return (Double) r.nextDouble() / 100;
            //       break;
            case 3:
                //  System.out.println("sto in 3");
                return (Double) r.nextDouble() / 100;
            //   break;
            case 4:
                // System.out.println("sto in 4");
                return (Double) r.nextDouble() / 1;
            //  break;
            default:
                System.out.println("errore random selecter");
                return 9999999.0;

            //  break;
        }

    }

    /**
     * Metodo che permette l'attuazione del crossover: a) ad 1-punto b) ad
     * 2-punti c) uniforme d) aritmetico e della Mutazione
     * In questo caso si è usato ad un punto
     *
     * @param matingPool piscina di accoppiamento che contiene gli individui
     * candidati all'accopiamento.
     * @param newPopolazione popolazione da caricare che formerà la nuova
     * popolazione.
     */
    private void crossover_mutazione() throws Exception {

        // Cloning inutile, ArrayList matingPool istanziato nel metodo GeneraNextPopolazione
        //ArrayList<LineaDeformabile> cloneMating;

        System.out.println("");
      //  cloneMating = new ArrayList<>();
        //cloneMating=matingPool;
       // cloneMating.addAll(matingPool);
        for(Map.Entry entry_lineaD: matingPool.entrySet()){
            LineaDeformabile aux = new LineaDeformabile();
            aux = (LineaDeformabile) entry_lineaD.getValue();
         //   System.out.println("mating pool name " + aux.toString());
        }
       
       
        int iterazione = 1;
        Integer iterazione_plus=0;
        //Prendo due elementi sequenziali della matingPool
        //stessi quadrati stessa posizione scoppia.
        while (!matingPool.isEmpty()) {
  //          System.out.println("mutazione e crossover mating pool while size" + matingPool.size());

        //    System.out.println("");
      //      System.out.println("|||||||||||||||||||||Iterazione NUMERO " + iterazione + "  |||||||||||||||||||||||||||");
        //    System.out.println("");

            //Posizione quadrati L1
            ArrayList<Integer> quadratiPosL1;
            quadratiPosL1 = new ArrayList<>();

            //Quadrati L1
            ArrayList<Quadrato> quadratiL1;
            quadratiL1 = new ArrayList<>();

            //Posizione quadrati L1
            ArrayList<Integer> quadratiPosL2;
            quadratiPosL2 = new ArrayList<>();

            //Quadrati L2
            ArrayList<Quadrato> quadratiL2;
            quadratiL2 = new ArrayList<>();

            //nuovo array posizioni quadrati
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
    //        System.out.println("stampo elemento L1 mating pool " + L1.toString());
            
            LineaDeformabile L2;
            L2 = matingPool.remove(iterazione_plus+1);
   //         System.out.println("stampo elemento L2 mating pool " + L2.toString());

            //numeri quadrati deformati di ogni linea
            int rangeL1, rangeL2;
            rangeL1 = L1.getQuadratiDeformati().size();
            rangeL2 = L2.getQuadratiDeformati().size();

            //ArrayList di supporto per il salavataggio dell
            //Se Pc ( probabilità di crossover ) è maggiore di sogliaCross ( ordine 10^-1 )
            if (pC < sogliaCross) {

            //    System.out.println("range L1 " + rangeL1 + " range L2  " + rangeL2);
            //    System.out.println("------------------------------------------------------------------");

                //n° quadrati da modificare
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

     //               System.out.println(" NON DEFORMATO KEY L1  " + entry_lineaDef.getKey() + "   TIPO DI MODIFICA: " + auxQ.nome_def);
                }

         //       System.out.println("---------------------------------------------------------------");

                //Casto i dati per una migliore gestione Divido posizione e 
                // quadrato ma son collegati manualmente dall'indice
                for (Map.Entry entry_lineaDef : L2.getQuadratiDeformati().entrySet()) {

                    //Salvo il quadrato in variabile temporanea
                    Quadrato auxQ = (Quadrato) entry_lineaDef.getValue();

                    //entro nell'hashmap e creo la coppia chiave valore per L2.
                    quadratiPosL2.add((Integer) entry_lineaDef.getKey());
                    quadratiL2.add(auxQ);

          //          System.out.println(" NON DEFORMATO KEY L2  " + entry_lineaDef.getKey() + "  TIPO DI MODIFICA: " + auxQ.nome_def);
                }

    //            System.out.println("");

                // se contiene valori uguali creo una mutazione 
                //vedo se ci sono incompatibilità 
                //L2 chiavi replicate TODO
          //     System.out.println("check incompatibilità prima");
                checkIncompatibilita(quadratiPosL1, quadratiPosL2, newQuadratiL2);

                //devo aggiornare le prime due posizioni di L2 prima di procedere alla trasformazione
                for (int h = 0; h < rangeL2; h++) {
                    // 
                    L2.getQuadratiDeformati().remove(quadratiPosL2.get(h), quadratiL2.get(h));

                }
                for (int h = 0; h < rangeL2; h++) {
                    L2.getQuadratiDeformati().put(newQuadratiL2.get(h), quadratiL2.get(h));
                }

                for (Map.Entry entry_lineaDef : L2.getQuadratiDeformati().entrySet()) {
                    Quadrato auxQ = (Quadrato) entry_lineaDef.getValue();
        //            System.out.println("KEY L2 NON DEF DOPO MUT " + entry_lineaDef.getKey() + "  Quadrato " + auxQ.nome_def);
                }

     //           System.out.println("");
      //          System.out.println(" |-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-|-| ");
      //          System.out.println("        indice divisione " + quadrati_da_scambiare);
     //          System.out.println("");
                // devo prendere gli indici a metà 
                for (int i = quadrati_da_scambiare; i < rangeL1; i++) {
                    if (L1.getQuadratiDeformati().remove(quadratiPosL1.get(i), quadratiL1.get(i))) {
                        //     System.out.println("LA MIA SIZE: " + rangeL1 + " pos quadrati L2 " + quadratiPosL2.size() + "  " + quadratiL2.size() + " pos quadrati L1 " + quadratiPosL1.size() + "  " + quadratiL1.size());
                        // Se stessa chiave skippa e fai qualche mutazione ... o simile
                        //vecchio versione L1.getQuadratiDeformati().put(quadratiPosL2.get(i), quadratiL2.get(i)); 
                        L1.getQuadratiDeformati().put(newQuadratiL2.get(i), quadratiL2.get(i));
                    } else {
      //                  System.out.println("remove fallita L1");
     //                   System.out.println("Stato vettore posizione          quadrato");
     //                   System.out.println(quadratiPosL1.get(i) + "        " + quadratiL1.get(i).nome_def);
                    }
                }

                for (int i = quadrati_da_scambiare; i < rangeL2; i++) {
                    //devo stare attento qui e vedere se leva e sostituisce quelli giusti. //aggiungo quadrato non aggiunto
                    if (L2.getQuadratiDeformati().remove(newQuadratiL2.get(i), quadratiL2.get(i))) {
                        //       System.out.println("LA MIA SIZE: " + rangeL2 + " pos quadrati L2 " + quadratiPosL2.size() + "  " + quadratiL2.size() + " pos quadrati L1 " + quadratiPosL1.size() + "  " + quadratiL1.size());
                        L2.getQuadratiDeformati().put(quadratiPosL1.get(i), quadratiL1.get(i));

                    } else {
        //                System.out.println("remove fallita L2");
       //                 System.out.println("Stato vettore posizione          quadrato");
       //                 System.out.println(newQuadratiL2.get(i) + "        " + quadratiL2.get(i).nome_def);
                    }
                }

       //         System.out.println("----------------------------------------------");

                for (Map.Entry entry_lineaDef : L1.getQuadratiDeformati().entrySet()) {
                    Quadrato auxQ; 
                    auxQ =(Quadrato) entry_lineaDef.getValue();
      //              System.out.println("KEY L1 dopomod " + entry_lineaDef.getKey() + " Tipo di modifica " + auxQ.nome_def);
                }

         //       System.out.println("");

                for (Map.Entry entry_lineaDef : L2.getQuadratiDeformati().entrySet()) {
                    Quadrato auxQ; 
                    auxQ =(Quadrato) entry_lineaDef.getValue();
     //               System.out.println("KEY L2 dopomod " + entry_lineaDef.getKey() + " Tipo di modifica " + auxQ.nome_def);
                }
                
                
                //Devo aggiungere L1 e L2 nella nuova popolazione
                Integer ind = iterazione_plus;
                ind=ind+selELit; //inizio dall'indice dopo gli elementi aggiunti all'elitarismo
                Integer ind2 = ind+1;
        //        System.out.println("ind : " + ind + " ind2 : " + ind2);
                newPop.put(ind,L1);
                newPop.put(ind2,L2);
                
                

    //            System.out.println("_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_");

            }
            else{
                // Devo aggiungere gli individui L1 e L2 non mutati nella nuova popolazione
                Integer ind = iterazione_plus;
                ind=ind+selELit; //inizio dall'indice dopo gli elementi aggiunti all'elitarismo
                Integer ind2 = ind+1;
   //             System.out.println("ind : " + ind + " ind2 : " + ind2);
                newPop.put(ind,L1);
                newPop.put(ind2,L2);
            }
            iterazione++;
            iterazione_plus=iterazione_plus+2;
     //       System.out.println("");
     //       System.out.println("iterazione plus "  + iterazione_plus);
        }
        //controllo la dimensione degli individui della nuova popolazione
     //   System.out.println("");
   //     System.out.println("NUMERO POPOLAZIONE NUOVA " + newPopolazione.size());        
   //     System.out.println("");

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
 //       System.out.println("");
  //      System.out.println("check Incompatibilità e quasi mutazione direi.");
  //      System.out.println("");
  //      System.out.println(" |||||||||||||||||||| Check Modifiche |||||||||||||||||||||| ");

        for (Integer pos : quadratiPosL1) {
       //     System.out.println("L1 POS =           " + pos);
            elementiL1.add(pos);
        }

       // System.out.println("");

        for (Integer pos : quadratiPosL2) {
   //         System.out.println("L2 POS =           " + pos);
            elementiL2.add(pos);
        }
      //  System.out.println("");
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
                //  newQuadratiL2.add(newPos);
                
                elementiNewL2.add(newPos);
            //    System.out.println("POSIZIONE CALCOLATA NUOVA " + newPos);
                
            } else {
                //  newQuadratiL2.add(newPos);
                elementiNewL2.add(newPos);
            //    System.out.println("POSIZIONE CALCOLATA NUOVA " + newPos);
            }

        }
       // System.out.println("");

        //DEVO FARE UN CONTROLLO delle posizione replicate

        /*
        for(int i=0; i<newQuadratiL2.size()-1; i++){
            if(newQuadratiL2.get(i)==newQuadratiL2.get(i+1)){
                while((elementiL1.contains(newQuadratiL2.get(i+1)))||(elementiL2.contains(newQuadratiL2.get(i+1)))||(elementiNewL2.contains(newQuadratiL2.get(i+1)))){
                    int posNew2;
                    posNew2=newQuadratiL2.get(i+1);
                    posNew2=((posNew2+2)%primaPOP.size())+1;
                    newQuadratiL2.set(i+1, posNew2);
                }
            }
          
                }
         */
        
        
        for (Object numeroPos : elementiNewL2) {
            Integer pos = (Integer) numeroPos;
      //      System.out.println(pos + " = Posizione nell'insieme ");
            newQuadratiL2.add(pos);

        }

        /*
        Iterator itr = elementiNewL2.iterator();
        while (itr.hasNext()) {
            Object element = itr.next();
            System.out.println((Integer) element + " = Posizione nell'insieme ");
            newQuadratiL2.add((Integer) element);
            System.out.println("SCOPPIA");
        }
        System.out.println("DOPO LO SCOPPIO");*/
     //   System.out.println("");

        /*
        for(int i=0; i<newQuadratiL2.size()-1; i++){
            if(newQuadratiL2.get(i)==newQuadratiL2.get(i+1)){
                int posNew = newQuadratiL2.get(i);
                do{
                    posNew=((posNew+2)%primaPOP.size())+1;
                    
                }while(elementiL1.contains(posNew)||elementiL2.contains(posNew)||elementiNewL2.contains(posNew));
                newQuadratiL2.remove(i+1);
                newQuadratiL2.add(i+1, newPos);
                System.out.println("");
                System.out.println("pos aggiornata " + (i+1) + " valore aggiornato  " + newQuadratiL2.get(i+1));
                System.out.println("");
            }  
            
        }
        
         */
 /*

        for (Integer aux : newQuadratiL2) {
            System.out.println("");
            System.out.println(" vettore valore  " + aux);
            System.out.println("");
        }*/
    }

    /*
        
        
        //Supporto stampa
        for (Integer pos : newQuadratiL2) {
            System.out.println("newArrayQuadratiL2 =     " + pos);
        }


}
     */

    public HashMap<Integer, LineaDeformabile> getOldPopolazione() {
        return oldPopolazione;
    }

    public void setOldPopolazione(HashMap<Integer, LineaDeformabile> oldPopolazione) {
        this.oldPopolazione = oldPopolazione;
    }
    
    
    
    
}