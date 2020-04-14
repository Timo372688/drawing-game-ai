package de.uni_hannover.hci.montagsmaler.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.image.WritableRaster;


/*
* Contains methods to operate on image files.
 */
public class Actions {
    static String currentDir = System.getProperty("user.dir");
    static String s = "";

    /* Speichert bei Knopfdruck den Canvas-Snapshot in 35/Pictures.
     * Dateiname des .png-Bildes ist eine Zahl, die hochgezählt wird,
     * je nachdem wie viele Bilder bereits existieren.
     */
    public static void saveImg(Canvas canvas) {

        final WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        final WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), writableImage);
        //read count.txt to create filenames in rising order (1.png,2.png,3.png...
        try {
            FileReader fr = new FileReader(currentDir + "/Pictures/count.txt" );
            int i;
            s = "";
            while ((i = fr.read()) != -1) {
                s += (char) i;
                System.out.println(s);
            }
            String filename = currentDir + "/Pictures/" + s;
            File image = new File(filename);
            int adder = Integer.parseInt(s);
            adder++;

            //update the count.txt after getting the current filename
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", image);
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentDir + "/Pictures/count.txt"));
                System.out.println(adder);
                String newvalue = String.valueOf(adder);
                System.out.println(newvalue);
                writer.write(newvalue);
                writer.close();
            } catch (final IOException e) {}
        } catch (final IOException e) {}
    }


    //Methode zum Erstellen des binary Arrays, welche von dem NN verwendet wird. (reads out mnist pngs)
    public static double[] trainImg(String path) {
        BufferedImage bimg = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        try {
                bimg = ImageIO.read(new File(path));
        } catch (IOException ioex) {}

        double[] formImg = new double[bimg.getHeight() * bimg.getWidth()];
        int count = 0;

        for (int i = 0; i < bimg.getHeight(); i++) {
            for (int j = 0; j < bimg.getWidth(); j++) {
                if (bimg.getRGB(j, i) != Color.BLACK.getRGB()) {
                    formImg[count] = 1;
                } else {
                    formImg[count] = 0;
                }
                count++;
            }
        }
        return formImg;
    }

    /* Formatiert eine .png-Datei in ein int-Array.
     * Es wird jeder Pixel in der .png-Datei durchlaufen und
     * falls ein Pixel gleich der Farbe schwarz ist, wird im
     * Array eine 1 gespeichert, sonst 0.
     */
    public static double[] formatImg(String path) {
        BufferedImage bimg = new BufferedImage(532, 532, BufferedImage.TYPE_INT_ARGB);
        //if no path is specified, the image is taken from the last saved image
        try {
            if (path == null) {
                bimg = ImageIO.read(new File(currentDir + "/Pictures/" + s));
            } else {
                bimg = ImageIO.read(new File(path));
            }
        } catch (IOException ioex) {}

        double[] formImg = new double[bimg.getHeight() * bimg.getWidth()];
        int count = 0;

        for (int i = 0; i < bimg.getHeight(); i++) {
            for (int j = 0; j < bimg.getWidth(); j++) {
                if (bimg.getRGB(j, i) == Color.BLACK.getRGB()) {
                    formImg[count] = 1;
                } else {
                    formImg[count] = 0;
                }
                count++;
            }
        }
        return formImg;
    }

    // Formatiert den Canvas Snapshot in das binary-array, ohne es den Snapshot zu speichern.
    public static double[] evalGuess(Canvas canvas) {
        final WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        final WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), writableImage);

        BufferedImage bimg = SwingFXUtils.fromFXImage(snapshot, null);

        double[] formImg = new double[bimg.getHeight() * bimg.getWidth()];
        int count = 0;

        for (int i = 0; i < bimg.getHeight(); i++) {
            for (int j = 0; j < bimg.getWidth(); j++) {
                if (bimg.getRGB(j, i) == Color.BLACK.getRGB()) {
                    formImg[count] = 1.0;
                } else {
                    formImg[count] = 0.0;
                }
                count++;
            }
        }
        return formImg;
    }

    //creates a double-array from an image file.
    //the array contains the pixel data of the image (1 for black pixels, 0 for white)
    public static double[] formatImgFromImg(Image image) {
        //System.out.println("Höhe = " + image.getHeight() + " Weite = " + image.getWidth());

        BufferedImage bimg = SwingFXUtils.fromFXImage(image, null);
        double[] formImg = new double[bimg.getHeight() * bimg.getWidth()];
        int count = 0;

        for (int i = 0; i < bimg.getHeight(); i++) {
            for (int j = 0; j < bimg.getWidth(); j++) {
                if (bimg.getRGB(j, i) != Color.WHITE.getRGB()) {
                    formImg[count] = 1.0;
                } else {
                    formImg[count] = 0.0;
                }
                count++;
            }
        }


        // Test
        /*
        int test = 0;
        System.out.println("zeilen = " + bimg.getHeight() + " Spalten = " + bimg.getWidth());
        for (int i= 0; i < bimg.getHeight(); i++ ) {
            System.out.print("Zeile=" + i+ " ");
            for (int j = 0; j < bimg.getWidth(); j++) {
                System.out.print(formImg[test] + " ");
                test++;
            }
            System.out.println();
        }
        */

        return formImg;
    }

    /*  Skaliert das binary-array auf 28x28 Pixel.
    *   takes an image array of pixeldata, the height and the width of the image to convert
     */
    public static javafx.scene.image.Image scaleImgArray(double[] imgArray, double heightC, double widthC){

        int height = (int)heightC;
        int width = (int)widthC;

        //count whitespace from all edges

        //from left edge
        boolean empty = true;
        int countLeft = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (imgArray[j * width + i] == 1 && empty == true) {
                    empty = false;
                }
            }
            if(empty == true) {
                countLeft++;
            }
        }


        //from right edge
        empty = true;
        int countRight = 0;

        for (int i = width-1; i >= 0; i--) {
            for (int j = 0; j < height; j++) {
                if (imgArray[j * width + i] == 1 && empty == true) {
                    empty = false;
                }
            }
            if(empty == true) {
                countRight++;
            }
        }


        //from upper edge
        empty = true;
        int countUpper = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (imgArray[i * width + j] == 1 && empty == true) {
                    empty = false;
                }
            }
            if(empty == true) {
                countUpper++;
            }
        }


        //from lower edge
        empty = true;
        int countLower = 0;
        for (int i = height-1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                if (imgArray[i * width + j] == 1 && empty == true) {
                    empty = false;
                }
            }
            if(empty == true) {
                countLower++;
            }
        }

        //define dimensions of newarray without white edges
        int newHeight = height - countUpper - countLower;
        int newWidth = width - countLeft - countRight;


        //create new array with scaled down dimensions
        double[] downSized = new double[newHeight*newWidth];
        for(int i = 0; i < newHeight; i++){
            for(int j = 0; j < newWidth; j++){
                if(imgArray[(i+countUpper)*width + (j+countLeft)] == 1){
                    downSized[i*newWidth + j] = 1;
                }
            }
        }

        //print the whole array to the console
 /*       int counter = 0;
        for(int i = 0; i < downSized.length; i++){
            counter++;
            System.out.print(downSized[i]);
            if(counter%newWidth == 0){
                System.out.println();
            }
        }
*/

        //turn the scaled down array back into an image and scale it down to 28*28 pixels
        double[] downSized3 = BinaryToRGB(downSized, newHeight, newWidth);
        BufferedImage toScale = getImageFromArray(downSized3, newWidth, newHeight);
        javafx.scene.image.Image toScale2 = SwingFXUtils.toFXImage(toScale, null);
        ImageView imageView = new ImageView(toScale2);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitHeight(28);
        imageView.setFitWidth(28);

        //at this point the image is automatically scaled to 28*X, so the image has to be converted back to 28*28 dimensions manually
        javafx.scene.image.Image result = imageView.snapshot(null, null);
        //check which dimension is bigger to decide where to add 0s
        if(result.getHeight() < 28|| result.getWidth() < 28){
            double[] temp = formatImgFromImg(result);
            double[] quadr = new double[28*28];
            double buffer;


            boolean biggerHeight;

            if(result.getHeight() > result.getWidth()){
                biggerHeight = true;
                buffer = result.getHeight() - result.getWidth();
            }else{
                biggerHeight = false;
                buffer = result.getWidth() - result.getHeight();
            }
            System.out.println("Bigger Height: "+ biggerHeight);
            System.out.println("Buffer: "+ buffer);
            int buffer2 = (int)buffer;
            if(buffer2 %2 != 0){
                buffer2--;
            }
            System.out.println("Buffer2: "+ buffer2);
            if (biggerHeight == true) {
                for(int i = 0; i < 28; i++) {
                    for(int j = 0; j < 28-(int)buffer; j++)
                        if(temp[i*(28-(int)buffer) + j] == 1){
                            quadr[i*28 + j+(buffer2/2)] = 1;
                        }
                }
            } else {
                for(int i = 0; i < 28-(int)buffer; i++) {
                    for(int j = 0; j < 28; j++){
                        if(temp[i*28 + j] == 1){
                            quadr[(i+buffer2/2)*28 + j] = 1;
                        }
                    }

                }
            }

            double[] resultArray = BinaryToRGB(quadr, 28, 28);
            BufferedImage quadrImg = getImageFromArray(resultArray, 28, 28);
            javafx.scene.image.Image quadrImg2 = SwingFXUtils.toFXImage(quadrImg, null);
            ImageView imageView2 = new ImageView(quadrImg2);
            imageView2.setSmooth(true);
            imageView2.setPreserveRatio(false);
            imageView2.setFitHeight(28);
            imageView2.setFitWidth(28);
            javafx.scene.image.Image endresult = imageView2.snapshot(null, null);
            return endresult;
        }
        return result;

    }


    public double[] MatrixToArray(double[][] matrix){
        double[] array = new double[matrix.length*matrix[0].length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix.length; j++){
                array[i*j] = matrix[i][j];
            }
        }
        return array;
    }


   public double[][] ArrayToMatrix(double[] data){
       double[][] matrix = new double[990][560];
       int counter = 0;
       for(int i = 0; i < 990; i++){
           for(int j = 0; j < 560; j++ ){
               matrix[i][j] = data[i*560] + j;
           }
       }
       return matrix;
   }

    public static double[] BinaryToRGB(double[] binaryArr, int height, int width){
        double[] rgb = new double[binaryArr.length*3];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int band = 0; band < 3; band++){
                    if(binaryArr[i*height+j] == 1) {
                        rgb[((i * height) + j) * 3 + band] = 0;
                    }else{
                        rgb[((i * height) + j) * 3 + band] = 255;
                    }
                }

            }
        }
        return rgb;
    }


    public static BufferedImage getImageFromArray(double[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0, 0, width, height, pixels);
        image.setData(raster);
        return image;
    }



    // Testmethode
    public static void testRGB(String path) {
        BufferedImage bimg = new BufferedImage(532, 532, BufferedImage.TYPE_INT_ARGB);
        try {
            bimg = ImageIO.read(new File( currentDir + path));
        } catch (IOException ioex) {}

        System.out.println("Rgb black = " + Color.BLACK.getRGB() + "\n");


        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                System.out.print(bimg.getRGB(j, i) + " ");
            }
            System.out.println();
        }
    }
}
