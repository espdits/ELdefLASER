package org.dellapenna.research.ldr;

/*prova cambiamenti per git*/
import Servizi.GestioneSalvataggio;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Giuseppe Della Penna
 */
public class DeformazioneLaser {

    //Insieme di Individui creati prima poplazione.
    ArrayList<LineaDeformabile> primaPopolazione = new ArrayList<>();
    //Insieme di Individui creati per la seconda popolazione
    ArrayList<LineaDeformabile> secondaPopolazione = new ArrayList<>();

    //la coda contiene gli stati (linee deformate e tempo corrente) da valutare
    //QUESTA STRUTTURA DOVREBBE ANDARE SU DISCO;
    //la coda contiene gli stati (linee deformate e tempo corrente) da valutare
    //QUESTA STRUTTURA DOVREBBE ANDARE SU DISCO
    Queue<Stato> coda = new ArrayDeque(100);

    //l'insieme degli stati esplorati contiene le firme (hashCode) di tutti gli stati già valutati
    Set<Integer> esplorati = new HashSet(500);
    //la dinamica (inversa) costruita durante l'esplorazione. Necessaria per poter ricostruire il piano di lavoro
    //una volta raggiunto l'obiettivo.
    //QUESTA STRUTTURA DOVREBBE ANDARE SU DISCO
    Map<Integer, List<MossaStato>> dinamica_inversa = new HashMap<>();

    Random rq = new Random(); //per le prove
    Random rm = new Random(); //per le prove

    //determina le prossime mosse valide da applicare allo stato corrente. Dovrebbe enumerarle o usare delle euristiche
    //per capire quelle più opportune. In questo esempio procediamo a caso...
    private List<MossaApplicata> prossimeMosseValide(Stato stato) {
        List<MossaApplicata> prossimeMosse = new ArrayList();

        for (int test = 1; test < 4; ++test) {
            int quadrato_da_deformare;
            Mossa mossa_da_applicare;
            do {
                quadrato_da_deformare = rq.nextInt(stato.getLine().lunghezza_linea);
                switch (rm.nextInt(4)) {
                    default:
                    case 0:
                        mossa_da_applicare = Mossa.a_s;
                        break;
                    case 1:
                        mossa_da_applicare = Mossa.a_d;
                        break;
                    case 2:
                        mossa_da_applicare = Mossa.b_s;
                        break;
                    case 3:
                        mossa_da_applicare = Mossa.b_d;
                        break;
                }
            } while (stato.getLine().isDeformabile(mossa_da_applicare, quadrato_da_deformare, stato.getTime() + 1));
            prossimeMosse.add(new MossaApplicata(mossa_da_applicare, quadrato_da_deformare));
        }
        return prossimeMosse;
    }

    //determina se uno stato contiene una linea corrispondente al nostro obiettivo finale
    private boolean goalRaggiunto(Stato stato, Linea obiettivo) {
        return obiettivo.compareTo(stato.getLine());
    }

    private void aggiornaGrafo(Stato stato_provenienza, MossaApplicata mossa, Stato stato_arrivo) {
        if (!dinamica_inversa.containsKey(stato_arrivo.hashCode())) {
            dinamica_inversa.put(stato_arrivo.hashCode(), new ArrayList<>());
        }
        dinamica_inversa.get(stato_arrivo.hashCode()).add(new MossaStato(mossa, stato_provenienza.hashCode()));
    }

    private void stampa_piano(Integer firma_stato) {
        List<MossaStato> mossePossibili;
        do {
            System.out.print(firma_stato);
            mossePossibili = dinamica_inversa.get(firma_stato);
            //ci interessa un piano qualsiasi, quindi prendiamo sempre la prima mossa possibile (la lista deve contenerne almeno una)
            if (mossePossibili != null && !mossePossibili.isEmpty()) {
                System.out.print(" <- (" + mossePossibili.get(0).getMossa() + ") <- " + mossePossibili.get(0).getStato());
                firma_stato = mossePossibili.get(0).getStato();
            }
        } while (mossePossibili != null && !mossePossibili.isEmpty());
    }

    //loop di esplorazione
    public void loop() {
        //lo stato iniziale
        Stato stato_iniziale = new Stato(new LineaDeformabile(), 0);
        //il goal
        Linea obiettivo = new Linea();

        //verifichiamo (caso limite) se il nostro goal coincide con la linea iniziale
        if (goalRaggiunto(stato_iniziale, obiettivo)) {
            System.out.println("L'obiettivo è raggiunto in partenza!");
            return;
        }

        //inizializziamo la coda di esplorazione
        coda.add(stato_iniziale);
        //esploriamo gli stati in coda
        while (!coda.isEmpty()) {
            //prendiamo uno stato dalla coda
            Stato stato_da_espandere = coda.remove();
            //determiniamo le prossime mosse applicabili sullo stato corrente
            List<MossaApplicata> prossimeMosse = prossimeMosseValide(stato_da_espandere);
            for (MossaApplicata mossa : prossimeMosse) {
                //per ogni mossa applicabile, calcoliamo lo stato che raggiungeremmo (nuova linea e tempo incrementato)
                Stato stato_successivo = new Stato(stato_da_espandere.getLine(), stato_da_espandere.getTime() + 1);
                stato_successivo.getLine().deforma(mossa.getMossa(), mossa.getPosizione_quadrato(), stato_successivo.getTime());
                //verifichiamo se questa linea l'abbiamo già valutata
                if (!esplorati.contains(stato_successivo.hashCode())) {
                    //se la linea è nuova, l'aggiungiamo alla coda degli stati da valutare
                    //e la inseriamo nel set degli stati già generati
                    esplorati.add(stato_successivo.hashCode());
                    coda.add(stato_successivo);
                    //aggiorniamo la dinamica ricostruita
                    aggiornaGrafo(stato_da_espandere, mossa, stato_successivo);
                    //un po' di output diagnostico...
                    if (esplorati.size() % 10 == 0) {
                        System.out.println("Stati visitati: " + esplorati.size() + ". Lunghezza coda: " + coda.size());
                    }
                    //verifichiamo se è il nostro goal (cioè se la linea che contiene è uguale all'obiettivo)
                    if (goalRaggiunto(stato_successivo, obiettivo)) {
                        //se abbiamo raggiunto il goal stampiamo un piano e chiudiamo tutto
                        stampa_piano(stato_successivo.hashCode());
                        return;
                    }
                }

            }
        }
    }

    /**
     * LOOP2 metodo per generare una popolazione salvare lavori valutare con la
     * funzione di fitness e risalvare valori
     *
     * @throws java.io.IOException
     */
    public void loop2() throws IOException, Exception {
        int[] pos = {22, 28, 32, 37, 45};

        //inizializzazione Arraylist
        Popolazione pop = new Popolazione();
        //creo linea
        Linea linea = new Linea();
        linea.createManualLine(linea, pos);
        linea.stampaLinea(linea);
        //creo prima popolazione
        //TODO PRIMA POPOLAZIONE RISULTA VUOTO RISOLVERE!!! 
        pop.creaPopolazione(primaPopolazione);
        primaPopolazione = pop.getPrimaPOP();

        //confronto le linee della popolazione con la linea generata e valuto il fitness
        for (LineaDeformabile lineaDef : primaPopolazione) {
            lineaDef.setVal_fitness(pop.valFitness(linea, lineaDef));
        }

        GestioneSalvataggio.salvaDATA(primaPopolazione, pop.getContatoreMosse());

        //Devo creare una nuova popolazione dalla precedente:
        //  1 APPLICO ELITARISMO 
        //  2 APPLICO ROULETTE WHEEL SELECTION
        //  3 APPLICO PARENT SELECTION
        //  4 CROSS-OVER
        //  5 MUTAZIONE
        //QUI entra e va a creare la seconda popolazione
        secondaPopolazione = pop.nextPopolazione(primaPopolazione);
        // stampa controllo
        
        //Aggiornamento Fitness nuova popolazione
        for(LineaDeformabile lineaDef2 : secondaPopolazione){
            lineaDef2.setVal_fitness(pop.valFitness(linea, lineaDef2));
        }
        
        
        
        
        int i = 1;
        for (LineaDeformabile lineadef : secondaPopolazione) {

            System.out.println("valori nel vettore di array 2 popolazione " + i + " con valore " + lineadef.getVal_fitness());
            i++;
        }
    }

    public static void main(String args[]) throws IOException, Exception {
        DeformazioneLaser instance = new DeformazioneLaser();
        instance.loop2();
        //ATTUALMENTE, SE LANCIATA VA IN LOOP INFINITO NON ESSENDOCI CONDIZIONI DI USCITA!!!
    }
}