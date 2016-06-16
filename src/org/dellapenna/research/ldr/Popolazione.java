/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dellapenna.research.ldr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author marco
 */
public class Popolazione {

    //Variabile che conta le mosse da effettuare per creare la prima popolazione
    final int contatoreMosse = 5;

    //Variabile che imposta la dimensione della popolazione
    final int dimPopolazione = 50;

    //ArrayList salvataggio prima popolazione ?!!??!?!
    ArrayList<LineaDeformabile> primaPOP = new ArrayList<>();

    Random rq = new Random(); //per le prove
    Random rm = new Random(); //per le prove

    /**
     * Metodo che crea la prima popolazione, dove ogni individuo ha subito un
     * numero di mosse prestabilito, salvandola in un file esterno.
     *
     * @param primaPopolazione vettore di appoggio per creare la prima
     * popolazione
     * @throws java.io.IOException I/O eccezzione momentaneamente non gestita
     */
    public void creaPopolazione(ArrayList<LineaDeformabile> primaPopolazione) throws IOException {
        // ArrayList contenente gli individui generati
        primaPopolazione = new ArrayList<>();
        //fino a che non creo 50 individui 

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
            primaPopolazione.add(individuo);

        }
        //Stampe di controllo della popolazione
        //    System.out.println("individuo nella poplazione " +it);
        //   });

        System.out.println("Popolazione" + primaPopolazione.size());
        primaPOP = primaPopolazione;

        //Salvo su file
        // gestioneSalvataggio.salvaDATA(primaPopolazione, contatoreMosse);
    }

    /**
     * Metodo che valuta la fitness di un individuo rispetto la linea data
     *
     * @param linea foram che si vuole ottenere
     * @param lineaDeformabile individuo in esame
     * @return val_fitness valore della funzione di fitness
     */
    public double valFitness(Linea linea, LineaDeformabile lineaDeformabile) {

        double val_fitness = 0;
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
                double auxFit;
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
                double auxFit;
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
                double auxFit;
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
                System.out.println("quadrato non schedato aggiunto" + posQLD);

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
                System.out.println("Differenza aux " + (int) entry_linea.getKey() + " valore : " + aux_dif);
               
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

        /*
        for (Quadrato quadrato_non_schedato : quadrati_non_schedati) {
            for (int i = 0; i < posizioni_quadrati_non_schedati.size(); i++) {
                if (linea.getQuadratiDeformati().get(i).flagSelezione == false) {

                }
            }

        }
         */
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
    private double checkAndValutation(Quadrato quadrato_linea, Quadrato quadrato_lineaDef) {
        double val_fitness;
        val_fitness = 0;
        switch (quadrato_linea.nome_def) {
            default:
                System.err.println("Non ci deve stare qui");
                return 0;
            case "UL":
                switch (quadrato_lineaDef.nome_def) {
                    default:
                        System.err.println("Non ci deve stare qui");
                        return 0;
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
                        return 0;

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
                        return 0;
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
                        return 0;
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
    public ArrayList getPrimaPOP() {
        return primaPOP;
    }

    /**
     * ritorna il numero di mosse da effettuare sulla linea deformabile
     *
     * @return numero mosse da fare sulla linea deformabile
     */
    public int getContatoreMosse() {
        return contatoreMosse;
    }

}