
package org.dellapenna.research.ldr;

/**
 *
 * @author Giuseppe Della Penna
 *
 * classe di servizio per codificare una coppia (mossa, stato raggiunto)
 *
 */
public class MossaStato {
    private final MossaApplicata mossa;
    private final int stato;

    public MossaStato(MossaApplicata mossa, int stato) {
        this.mossa = mossa;
        this.stato = stato;
    }

    /**
     * @return the mossa
     */
    public MossaApplicata getMossa() {
        return mossa;
    }

    /**
     * @return the stato
     */
    public int getStato() {
        return stato;
    }

 
    
}
