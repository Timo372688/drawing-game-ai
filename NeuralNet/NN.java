package de.uni_hannover.hci.montagsmaler.NeuralNet;
import java.util.Random;
import de.uni_hannover.hci.montagsmaler.NeuralNet.Datas.*;
import de.uni_hannover.hci.montagsmaler.NeuralNet.matrix.Matrix;
import de.uni_hannover.hci.montagsmaler.NeuralNet.matrix.Matrix;


public class NN{
    private double[][] Gewichte1;
    private double[][] Gewichte2;
    private double[] b1;
    private double[] b2;
    private int Epochen;
    private double Lernrate;
    private int inL_size;
    private int hL_size;
    private int outL_size;

    private double[] hL = new double[hL_size];
    private double[] outL = new double[outL_size];
    private double[] zh1;
    private double[] zh2;
    private double[] zo1;
    private double[] zo2;

    public double[][] getGewichte1() {
        return this.Gewichte1;
    }

    public double[][] getGewichte2() {
        return this.Gewichte2;
    }

    public double[] getB1() {
        return this.b1;
    }

    public double[] getB2() {
        return this.b2;
    }

    public NN(int E, double L, int ins, int hls, int outs){
        this.Epochen = E;
        this.Lernrate = L;
        this.inL_size = ins;
        this.hL_size = hls;
        this.outL_size = outs;
        Gewichte1 = new double[this.hL_size][this.inL_size];
        Gewichte2 = new double[this.outL_size][this.hL_size];
        b1 = new double[hL_size];
        b2 = new double[outL_size];
        Random rand = new Random();
        for(int i = 0;i < this.hL_size; i++){
            for(int j = 0;j < this.inL_size; j++){
                this.Gewichte1[i][j]=rand.nextDouble();
            }
        }
        for(int i = 0;i < this.outL_size; i++){
            for(int j = 0;j < this.hL_size; j++){
                this.Gewichte2[i][j]=rand.nextDouble();
            }
        }
        for(int i = 0;i < this.hL_size; i++){
            this.b1[i] = 0;
        }
        for(int i = 0;i < this.outL_size; i++){
            this.b2[i] = 0;
        }
    }

    public NN(int E, double L, double[][] G1, double[][] G2, double[] b1, double[] b2){
        this.Epochen = E;
        this.Lernrate = L;
        this.Gewichte1 = G1;
        this.Gewichte2 = G2;
        this.b1 = b1;
        this.b2 = b2;
        this.inL_size = this.Gewichte1[0].length;
        this.hL_size = this.Gewichte1.length;
        this.outL_size = this.Gewichte2.length;
    }
    //to do:(backpropagating)
    public void train(Data T[]){
        T = arrayMix(T);

        int[] Erk = new int[T.length];
        double[][] Gewichte1n = new double[this.hL_size][this.inL_size];
        double[][] Gewichte2n = new double[this.outL_size][this.hL_size];
        double[] b1n = new double[this.hL_size];
        double[] b2n = new double[this.outL_size];
        for(int e = 0; e < this.Epochen; e++){
            for(int j = 0; j < T.length; j++){
                System.out.println(j);
                if(T[j].getEingabe() != null) {
                    this.output(T[j].getEingabe());

                    if (T[j].getAusgabe() == maxpos(this.outL)) {
                        Erk[j] = 1;
                    } else { Erk[j] = 0;}
                    //Gewichte2
                    for (int k = 0; k < this.outL_size; k++) {
                        for (int i = 0; i < this.hL_size; i++) {
                            Gewichte2n[k][i] = this.Gewichte2[k][i] - this.Lernrate * this.hL[i] * dev_sig(this.outL[k]) * (this.outL[k] - convertout(T[j].getAusgabe())[k]);
                        }
                    }
                    //Bias2
                    for (int k = 0; k < this.outL_size; k++) {
                        b2n[k] = this.b2[k] - this.Lernrate * dev_sig(this.outL[k]) * (this.outL[k] - convertout(T[j].getAusgabe())[k]);
                    }
                    //Gewichte 1
                    for (int k = 0; k < this.hL_size; k++) {
                        for (int i = 0; i < this.inL_size; i++) {
                            double zw = 0;
                            for (int u = 0; u < this.outL_size; u++) {
                                zw += dev_sig(this.outL[u]) * (this.outL[u] - convertout(T[j].getAusgabe())[u]) * this.Gewichte1[u][k];
                            }
                            Gewichte1n[k][i] = Gewichte1[k][i] - this.Lernrate * T[j].getEingabe()[i] * dev_sig(this.hL[k]) * zw;
                        }
                    }
                    //Bias1
                    for (int k = 0; k < this.hL_size; k++) {
                        double zw = 0;
                        for (int u = 0; u < this.outL_size; u++) {
                            zw += dev_sig(this.outL[u]) * (this.outL[u] - convertout(T[j].getAusgabe())[u]) * this.Gewichte1[u][k];
                        }
                        b1n[k] = this.b1[k] - this.Lernrate * dev_sig(this.hL[k]) * zw;
                    }
                    this.Gewichte1 = Gewichte1n;
                    this.Gewichte2 = Gewichte2n;
                    this.b1 = b1n;
                    this.b2 = b2n;
                    System.out.println(Gewichte1n[5][350]);
                    System.out.println(this.outL[9]);
                }else{System.out.println("Null:" + j);}
            }
            System.out.println(Erate(Erk));

        }
    }


    private void output(double[] E){

        this.zh1 = Matrix.matMult(E, this.Gewichte1);
        this.zh2 = Matrix.matAdd(zh1, this.b1);
        this.hL = sig(zh2);
        this.zo1 = Matrix.matMult(this.hL, this.Gewichte2);
        this.zo2 = Matrix.matAdd(zo1,this.b2);
        this.outL = sig(zo2);

        System.out.print("outL = ");
        for (int i = 0; i < outL.length; i++) {
            System.out.print(outL[i] + " ");
        }
        System.out.println();
    }

    public int out(double[] E){
        this.output(E);
        return maxpos(outL);
    }

    /*
    public double output_h(double[] E, int i){
        double[] zh1 = Matrix.matMult(E, this.Gewichte1);
        double[] zh2 = Matrix.matAdd(zh1, this.b1);
        double[] h = sig(zh2);
        return h[i];
    }
     */

    private double Erate(int[] E){
        double e = 0;
        for(int i = 0; i < E.length; i++){
            e += E[i];
        }
        e = e / E.length;
        return e;
    }

    private double[] sig(double[] x){

        double[] y = new double[x.length];
        for(int i = 0; i < x.length; i++) {
            y[i] = 1 / (1 + Math.exp(-1*x[i]));
        }
        return y;
    }

    private double dev_sig(double sig){
        return sig*(1 - sig);

    }

    private static int maxpos(double[] m){
        double max = m[0];
        int n = 0;
        for (int i = 1; i < m.length; i++) {
            if (m[i] > max) {
                max = m[i];
                n = i;
            }
        }
        return n;
    }

    private static int maxpos(int[] m){
        double max = m[0];
        int n = 0;
        for (int i = 1; i < m.length; i++) {
            if (m[i] > max) {
                max = m[i];
                n = i;
            }
        }
        return n;
    }

    private static int[] convertout(int i){
        int[] out = new int[10];
        for (int j = 0; j < 10; j++){
            out[j] = 0;
        }
        out[i] = 1;
        return out;
    }

    private static Data[] arrayMix(Data[] data) {
        Data tmp;
        int rand;
        Random r = new Random();
        for (int i = 0; i < data.length; i++) {
            rand = r.nextInt(data.length);
            tmp = data[i];
            data[i] = data[rand];
            data[rand] = tmp;
        }
        return data;
    }
}



