package org.dellapenna.research.ldr;

/**
 *
 * @author Giuseppe Della Penna
 *
 * mossa astratta, a cui viene delagata la deformazione effettiva
 *
 */
public abstract class Mossa {

    public abstract void applica(Quadrato q);

    ////MOSSE PREDEFINITE STATICAMENTE
    public static class Deforma_a_s extends Mossa {
        
          
        @Override
        public void applica(Quadrato q) {
            //TODO: modificare q
            q.nome_def="UL";
        }

    };

    public static class Deforma_b_s extends Mossa {

        @Override
        public void applica(Quadrato q) {
            //TODO: modificare q
            q.nome_def="LL";
        }

    };

    public static class Deforma_a_d extends Mossa {

        @Override
        public void applica(Quadrato q) {
            //TODO: modificare q
            q.nome_def="UR";
        }

    };

    public static class Deforma_b_d extends Mossa {

        @Override
        public void applica(Quadrato q) {
            //TODO: modificare q
            q.nome_def="LR";
        }

    };

    ///
    public static Mossa a_s = new Deforma_a_s();
    public static Mossa a_d = new Deforma_a_d();
    public static Mossa b_s = new Deforma_b_s();
    public static Mossa b_d = new Deforma_b_d();
}
