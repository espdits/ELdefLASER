package org.dellapenna.research.ldr;

/**
 *
 * @author Giuseppe Della Penna
 *
 * classe di servizio che codifica una mossa e il quadrato su cui applicarla
 *
 */
public class MossaApplicata {

    private final Mossa mossa;
    private final int posizione_quadrato;

    public MossaApplicata(Mossa mossa_da_applicare, int quadrato_da_deformare) {
        mossa = mossa_da_applicare;
        posizione_quadrato = quadrato_da_deformare;
    }

    /**
     * @return the mossa
     */
    public Mossa getMossa() {
        return mossa;
    }

    /**
     * @return the posizione_quadrato
     */
    public int getPosizione_quadrato() {
        return posizione_quadrato;
    }
}
