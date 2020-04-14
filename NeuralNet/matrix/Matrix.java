package de.uni_hannover.hci.montagsmaler.NeuralNet.matrix;


public class Matrix{

    public static double[][] matMult(double[][] A, double[][] B){
        if(A[0].length == B.length){
            double[][]C = new double[A.length][B[0].length];
            for(int i = 0; i < A.length; i++){
                for (int k = 0; k < B[0].length; k++){
                    for (int j = 0; j < A[0].length; j++){
                        C[i][k] += A[i][j] * B[j][k];
                    }
                }
            }
            return C;
        }
        else{
            return null;
        }
    }

    public static double[] matMult(double[][] A, double[] B){
        if(A[0].length == B.length){
            double[] C = new double[A.length];
            for(int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[0].length; j++) {
                    C[i] += A[i][j] * B[j];
                }
            }
            return C;
        }
        else{
            return null;
        }
    }

    public static double[] matMult(double[] B, double[][] A){
        if(A[0].length == B.length){
            double[] C = new double[A.length];
            for(int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[0].length; j++) {
                    C[i] += A[i][j] * B[j];
                }
            }
            return C;
        }
        else{
            return null;
        }
    }

    public static double[][] matAdd(double[][] A, double[][] B){
        if(A.length == B.length && A[0].length == B[0].length){
            double[][] C = new double[A.length][A[0].length];
            for(int i = 0; i < A.length; i++){
                for (int j = 0; j < A[0].length; j++){
                    C[i][j] = A[i][j] + B[i][j];
                }
            }
            return C;
        }
        else{
            return null;
        }
    }

    public static double[] matAdd(double[] A, double[] B){
        if(A.length == B.length){
            double[] C = new double[A.length];
            for(int i = 0; i < A.length; i++){
                C[i] = A[i] + B[i];
            }
            return C;
        }
        else{
            return null;
        }
    }

    public static void matOut(double[][] A){
        if(A == null){
            System.out.print("null\n\n");
        }
        else{
            for(int i = 0; i < A.length; i++){
                System.out.print("|\t");
                for (int j = 0; j < A[0].length; j++){
                    System.out.print(A[i][j] + "\t");
                }
                System.out.print("|\n");
            }
            System.out.print("\n\n");
        }
    }
}
