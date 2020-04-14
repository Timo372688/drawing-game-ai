package de.uni_hannover.hci.montagsmaler.NeuralNet.Datas;

import de.uni_hannover.hci.montagsmaler.gui.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DataReader {

    static String currentDir = System.getProperty("user.dir");

    public static List[] getFile(){
        File dir = new File(currentDir + "/mnist_png/training/");
        String[] dirs = dir.list();

        List[] filenames = new List[10];
       for(int i=0; i<10; i++){
           File file = new File(currentDir + "/mnist_png/training/" + dirs[i]);
           String[] temp = file.list();
           List<String> list = Arrays.asList(temp);
           filenames[Integer.valueOf(dirs[i])] = list;
       }
        return filenames;
    }

    public static Data[] createTrainData(){
        Data[] trainDataArray = new Data[60000];
        List[] filenames = getFile();
        int counter =0;
        for(int i=0; i<10;i++){
            double[] in;
            for(int j=0; j<filenames[i].size() ;j++){
                in = Actions.trainImg(currentDir + "/mnist_png/training/" + i + "/"+ filenames[i].get(j));
                trainDataArray[counter] = new Data(in, i);
                counter++;
            }
        }
        return trainDataArray;
    }

    public static void print(Data[] datenQuatsch){
        System.out.println(datenQuatsch.length);
        for(int i=0; i < 2; i++){
            for(int j=0; j< datenQuatsch[i].getEingabe().length; j++ ){
                System.out.print(datenQuatsch[i].getEingabe()[j]);
            }
            System.out.println("\n Ausgabe: " + datenQuatsch[i].getAusgabe());
            System.out.println(currentDir);
        }
    }

    public static void print2(List[] filelist){

        for(int i=0; i<9;i++){
            for(int j=0; j<filelist[i].size() ;j++){
                System.out.println(filelist[i].get(j));

            }
        }
    }
}
