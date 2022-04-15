package model.simplification;

import model.LCSystem;

public abstract class Simplification {
    protected LCSystem system;

    public Simplification(final LCSystem s) {
        this.system = s;
    }

    public abstract void run();

    /**
     * Retourne le système de contraintes linéaires utilisé par les algorithmes de Daalmans.
     *
     * @return un système de contraintes linéaires
     */
    public LCSystem getSystem() {
        return this.system;
    }
}
