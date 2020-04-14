package de.uni_hannover.hci.montagsmaler.NeuralNet.Datas;

public class Data {

    double[] eingabeVektor;
    int ergebnisWert;

    public Data(double[] in, int out){
        eingabeVektor =  in;
        ergebnisWert = out;
    }
    /**
     * Returns the input vector.
     *
     * @return input vector (int-vector).
     */
    public double[] getEingabe(){
        return eingabeVektor;
    }

    /**
     * Returns the output value.
     *
     * @return output value: int.
     */
    public int getAusgabe(){
        return ergebnisWert;
    }
}

