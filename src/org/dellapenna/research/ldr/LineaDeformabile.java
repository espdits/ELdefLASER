package org.dellapenna.research.ldr;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author Giuseppe Della Penna

 codifica la linea isDeformabile formata da quadrati consecutivi
 *
 */
public class LineaDeformabile implements Cloneable{

    public static final int threshold_tempo_modifica = 1;
    private static final Quadrato quadrato_default = new Quadrato();
    ///
    private Map<Integer, Quadrato> quadrati_deformati = new TreeMap(); //la treemap mantiene i quadrati ordinati e ha tempo di accesso log(n)
    public final int lunghezza_linea = 50; //da definire caso per caso
    private Double val_fitness;
    private String name;


    
    public LineaDeformabile(){
        this.val_fitness=0.0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
      //  System.err.println("objects "+Objects.hashCode(this.quadrati_deformati));
        hash = 29 * hash + Objects.hashCode(this.quadrati_deformati);
      //  System.err.println("hash1 "+hash);
        hash = 29 * hash + this.lunghezza_linea;
      //  System.err.println("lunghezza linea "+ lunghezza_linea);
      //  System.err.println("hash2 "+hash);
        
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
        final LineaDeformabile other = (LineaDeformabile) obj;
        if (this.lunghezza_linea != other.lunghezza_linea) {
            return false;
        }
        if (!Objects.equals(this.quadrati_deformati, other.quadrati_deformati)) {
            return false;
        }
        return true;
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

    //usare getQuadrato per prelevare i quadrati e poi calcolarne l'angolo in base alla deformazione
    public double calcolaAngolo(int posizione_quadrato1, int posizione_quadrato2) {
        return 0; //temporaneo
    }

    //applica la mossa m al quadrato nella posizione indicata al tempo specificato 
    public void deforma(Mossa m, int posizione_quadrato, int tempo) {
        Quadrato q = createQuadrato(posizione_quadrato);
        m.applica(q);
        q.last_modification_time = tempo;
    }

        //applica la mossa m al quadrato nella posizione indicata
    public void deforma(Mossa m, int posizione_quadrato) {
        Quadrato q = createQuadrato(posizione_quadrato);
        m.applica(q);
    }
    
    //determina se al tempo specificato il quadrato nella posizione data è isDeformabile
    public boolean isDeformabile(Mossa m, int posizione_quadrato, int tempo) {
        return ((tempo - getQuadrato(posizione_quadrato).last_modification_time > threshold_tempo_modifica)
                && (posizione_quadrato < lunghezza_linea - 1 && tempo - getQuadrato(posizione_quadrato + 1).last_modification_time > threshold_tempo_modifica)
                && (posizione_quadrato > 0 && tempo - getQuadrato(posizione_quadrato - 1).last_modification_time > threshold_tempo_modifica));
    }
    
    //determina se un quadrato nella posizione data è isDeformabile
    public boolean isDeformabile(Mossa m, int posizione_quadrato) {
        return ((posizione_quadrato <= lunghezza_linea )&&(posizione_quadrato > 0)&&(!quadrati_deformati.containsKey(posizione_quadrato)));
    }
    
    public Map<Integer, Quadrato> getQuadratiDeformati(){
        return quadrati_deformati;
    }

    /**
     * Ritorna il valore di fitness
     * @return val_fitness
     */
    public Double getVal_fitness() {
        return val_fitness;
    }

    /**
     *  Setta il valore di fitness della Linea Deformabile
     * @param val_fitness valore in double da settare
     */
    public void setVal_fitness(Double val_fitness) {
        this.val_fitness = val_fitness;
    }

    /**
     * Ritorna il nome dell'individuo
     * @return Nome individuo
     */
    public String getName() {
        return name;
    }

    /**
     * Permette l'immisione del nome dell'individuo per un controllo migliore
     * @param name nome dell'individuo
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setta i quadrati deformati 
     * @param quadrati_deformati quadrati deformati da settare
     */
    public void setQuadrati_deformati(Map<Integer, Quadrato> quadrati_deformati) {
        this.quadrati_deformati = quadrati_deformati;
    }
 
    @Override
    public Object clone() {
        try {
                return super.clone();
	    } catch (CloneNotSupportedException e) {
		e.printStackTrace();
		return null;
	    }
	}



  

}
    
