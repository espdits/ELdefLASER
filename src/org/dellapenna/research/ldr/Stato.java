package org.dellapenna.research.ldr;

import java.util.Objects;

/**
 *
 * @author Giuseppe Della Penna
 * 
 * codifica uno stato della defoemazione, cioè una coppia (linea deformata corrente, tempo corrente)
 * l'inclusione del tempo nello stato è necessaria perchè alcune azioni dipendono dal tempo trascorso
 * da quelle precedenti.
 * 
 */
public class Stato {
    private final LineaDeformabile line;
    private final int time;    

    public Stato(LineaDeformabile linea, int tempo) {
        this.line = linea;
        this.time = tempo;
    }

    //e' importantissimo che questo metodo (e quelli chiamati ricorsivamente sugli oggetti aggregati)
    //sia ben scritto, perchè è usato per il confronto tra gli stati
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.line);
        hash = 79 * hash + this.time;
        return hash;
    }

    //e' importantissimo che questo metodo (e quelli chiamati ricorsivamente sugli oggetti aggregati)
    //sia ben scritto, perchè è usato per il confronto tra gli stati
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
        final Stato other = (Stato) obj;
        if (this.time != other.time) {
            return false;
        }
        if (!Objects.equals(this.line, other.line)) {
            return false;
        }
        return true;
    }
    
    

    /**
     * @return the linea
     */
    public LineaDeformabile getLine() {
        return line;
    }

    /**
     * @return the tempo
     */
    public int getTime() {
        return time;
    }
}
