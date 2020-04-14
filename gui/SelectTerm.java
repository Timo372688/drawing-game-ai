package de.uni_hannover.hci.montagsmaler.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelectTerm {

    // Liste mit den Elementen, die gemalt werden können.
    static public List<Integer> drawElements;

    public SelectTerm(int i) {
        this.drawElements = new ArrayList<Integer>();

        for (int j = 0; j < i; j++) {
            this.drawElements.add(j);
        }
    }

    /* Wählt ein zufälliges Element aus der Liste aus und entfernt es aus der Liste.
     */
    public int getRandomElement() {
        Random rand = new Random();

        int randomIndex = rand.nextInt(drawElements.size());
        int element = drawElements.get(randomIndex);
        drawElements.remove(randomIndex);

        return element;
    }

}