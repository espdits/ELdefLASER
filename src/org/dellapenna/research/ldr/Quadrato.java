package org.dellapenna.research.ldr;

/**
 *
 * @author Giuseppe Della Penna
 *
 * codifica un quadrato della linea deformabile
 *
 */
public class Quadrato {

    public int last_modification_time = 0; //tempo dell'ultima modifica
    public double deformazione = 0; //quali numeri servono per codificare la deformazione del quadrato??
    public String nome_def = "";
    public boolean flagSelezione= false;
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.last_modification_time;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.deformazione) ^ (Double.doubleToLongBits(this.deformazione) >>> 32));
        return hash;
    }

    //per equals consideriamo solo la deformazione, non il tempo (?)
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
        final Quadrato other = (Quadrato) obj;
        if (Double.doubleToLongBits(this.deformazione) != Double.doubleToLongBits(other.deformazione)) {
            return false;
        }
        return true;
    }
    
    
}
