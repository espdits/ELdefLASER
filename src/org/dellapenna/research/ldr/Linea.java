package org.dellapenna.research.ldr;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author Giuseppe Della Penna
 *
 * codifica in qualche modo compatto la linea target
 *
 */
public class Linea {

    public static final int threshold_tempo_modifica = 1;
    private static final Quadrato quadrato_default = new Quadrato();
    ///
    private Map<Integer, Quadrato> quadrati_deformati = new TreeMap(); //la treemap mantiene i quadrati ordinati e ha tempo di accesso log(n)
    public final int lunghezza_linea = 50; //da definire caso per caso

    //Metodo che permette la realizzazione di una linea Deformabile con X quadrati modificati
    public void createManualLine(Linea linea, int[] pos) {
        for (int intero : pos) {
            Quadrato q = createQuadrato(intero);
            }
        linea.deforma(Mossa.a_s, pos[0]);
        linea.deforma(Mossa.a_s, pos[1]);
        linea.deforma(Mossa.b_s, pos[2]);
        linea.deforma(Mossa.b_s, pos[3]);
        linea.deforma(Mossa.b_s, pos[4]);
        
    }

// applica la mossa m al quadrato nella posizione indicata
    public void deforma(Mossa m, int posizione_quadrato) {
        Quadrato q = createQuadrato(posizione_quadrato);
        m.applica(q);
    }

//preleva un quadrato dall'array sparso *in sola lettura*. Se il quadrato non è stato
//deformato, ne viene restituito uno di default immutabile
    public Quadrato getQuadrato(int posizione) {
        if (quadrati_deformati.containsKey(posizione)) {
            return quadrati_deformati.get(posizione);
        } else {
            return quadrato_default;
        }
    }

     
    //preleva un quadrato dall'array sparso *per la modifica*. Se il quadrato non è stato
    //deformato, ne viene restituito uno non inizializzato, che è inserito nella lista
    //di quelli deformati.
    public Quadrato createQuadrato(int posizione) {
        if (quadrati_deformati.containsKey(posizione)) {
            return quadrati_deformati.get(posizione);
        } else {
            Quadrato q = new Quadrato();
            quadrati_deformati.put(posizione, q);
            return q;
        }
    }

//restituisce true se le due linee sono simili modulo l'errore
    boolean compareTo(LineaDeformabile corrente) {
        return false; //temporaneo
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.quadrati_deformati);
        hash = 17 * hash + this.lunghezza_linea;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Linea other = (Linea) obj;
        if (this.lunghezza_linea != other.lunghezza_linea) {
            return false;
        }
        if (!Objects.equals(this.quadrati_deformati, other.quadrati_deformati)) {
            return false;
        }
        return true;
    }

    
    //Metodo di supporto per la stampa di posizione di modifica e tipo di modifica della linea
    public void stampaLinea(Linea linea){
    
   
        for (Map.Entry entry : quadrati_deformati.entrySet()) {
            // Utilizza il nuovo elemento (coppia chiave-valore)
            // dell'hashmap
            // Stampa a schermo la coppia chiave-valore;
            Quadrato aux;
            aux = (Quadrato) entry.getValue();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!LINEA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("Key = " + entry.getKey() + "          Tipo di modifica = " + aux.nome_def);
            
        }

                    System.out.println("|||||||||||||||||||||||||||||||||| LINEA - FINE |||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        }
    
    /**
     * Ritorna i quadrati deformati della linea
     * @return quadrati_deformati: quadrati che hanno subito una modifica
     */    
        public Map<Integer, Quadrato> getQuadratiDeformati(){
        return quadrati_deformati;
    }
    }
    
    
    
