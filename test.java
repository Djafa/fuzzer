import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.lang.ProcessBuilder;

import java.io.OutputStream;

public class test{

	public static void main(String [] args){

       
       byte[] data =initData("testinput.img"); // we fetch the file in an array of byte

       /* Crash test about negative value for the width or the height */
        System.out.println("===== Test negative picture size =====");
        //negativeDimensionPicture(data, Paths.get("testInputGen1.img"));

       /* Crash test about old version */
       System.out.println("===================== Test old version ok ================");
       //testOnTheOldVersion(data,Paths.get("testInputGen5.img"));

       /* Crash test  about author name is to big */
       System.out.println("===================== Test big author name ================");
       //Syetem.out.println("la première valeur de l'author name : " )
       //testOnTheAuthorName(data,Paths.get("testInputGen3.img"));

              /* Crash test about the number color is upper than 256 */
        System.out.println("===== Test upper 256 value for the number color =====");
        //testOnNumberColor(data, Paths.get("testInputGen2.img"));

        /* Crash test about width and height too large */
        System.out.println("===== Test huge picture size =====");
        //testOnTheHugeDimension(data,Paths.get("testInputGen4.img"));

         /* Crash test  about author name is to big */
       System.out.println("===================== Test big com name ================");
       //Syetem.out.println("la première valeur de l'author name : " )
       testOnTheCom(data,Paths.get("testInputGen3.img"));





       

    }


/*********************************************** test methode section *******************************************************************************/
   

    /**
     *  Crash test about old version of the converter_static program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheOldVersion(byte[] data, Path path) {
        byte [] crashData;
        
        for (int i = -10; i < 110; i++) {
        	//System.out.println("la valeur en bbb" + (byte) i );
            crashData= genDataWithSpecificVersion(data, (byte) i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }


            /* Run the converter_static exe */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[DETECTED]: Crash regarding an old version : v-"+i);
                //return;
            }

        }

    }



    /**
     *  Crash test about the size of the author name in the input file for the converter_static program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheAuthorName(byte[] data, Path path) {
        byte [] crashData;
        int [] hexaIndex = new int[]{5,6,7,8,9}; //de 5 à 9 pour le contenu de l'author name
        for (int i = -30; i < 256; i+=10) {
            //crashData= genDataWithBigName(data,i);
             crashData=genCrashData(data,hexaIndex,new byte[]{(byte)0xff,(byte) 0xff,(byte)0xff,(byte)0xff,(byte)0x11});
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            run_process(path);

             /* Run the converter_static exe */

             /*

            
           
            if (testOnConverter(run_process(path),path)) {
                System.out.println("[FOUND]: Crash about the author name with length: " + i);
                return;
            }

            */

            
        }
    }

     /**
     *  Crash test about the size of the author name in the input file for the converter_static program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheCom(byte[] data, Path path) {
        byte [] crashData;
         int [] hexaIndex = new int[]{35,36, 37,38, 39}; //de 34 à 38 pour le contenu du commentaire
        for (int i = 200; i < 3000; i++) {
            crashData= genDataWithBigCom(data,i);
            //crashData=genCrashData(data,hexaIndex,new byte[]{(byte)i, (byte)i, (byte)i, (byte)i, (byte)i});
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            run_process(path);

             /* Run the converter_static exe */

             /*

            
           
            if (testOnConverter(run_process(path),path)) {
                System.out.println("[FOUND]: Crash about the author name with length: " + i);
                return;
            }

            */

            
        }
    }

     /**
     *  Crash test about number of colors the color table can contain
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnNumberColor(byte[] data, Path path) {
        byte[] crashData;
        int [] hexaIndex = new int[]{21}; //de 21 à 23 pour le contenu de l'author name
        for (int i = 1; i < 256; i++) {// 256 is the max value for a byte
            //crashData=genCrashData(data,21,(byte)i); // 21 it's the byte position to make a big number of color
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte) i});
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            run_process(path);
            /* Run the converter_static exe */


            /*
            
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about the color number upper than 256");
                return;
            }

            */
        }
    }

     /**
     *  Crash test about dimension of the pixels table can have
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheHugeDimension(byte[] data, Path path) {
        byte [] crashData;
        int [] hexaIndex = new int[]{12,14}; // 12, 13? 14 for the width and 16,17,18 for height
        for (int i = 1; i <256; i++) {
            //crashData=genCrashData(data, 16, (byte)2); //16 is the indice of the height
            //crashData=genCrashData(data, 17, (byte)2);
            //crashData=genCrashData(data, 18, (byte)2);
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte)255,(byte) 1});
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            run_process(path);
            /*
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about huge picture dimension");
            }

            */
        }
    }


    /*************************************************************************** getter method *************************************************************************************/


    /**
     * Generator input file with the version initialize to the version parameter
     * @param data is a byte array with the good format for the input converter_static program
     * @param version is byte value between 0 and 100 (100 it's the largest accepted value by the converter_static program)
     * @return a byte array based on the data variable with the specific version
     */
    private static byte[] genDataWithSpecificVersion(byte[] data, byte version) {
        byte [] newData = new byte[data.length];
        System.arraycopy(data,0,newData,0,data.length);
        newData[2]= version;
        return newData;
    }


        /**
     * Generator input file with a author name with nameLength size
     * @param data is a byte array with the good format for the input converter_static program
     * @param nameLength the length of author name we want in the input file
     * @return a byte array based on the data variable with the author name of specific length
     */
    private static byte[] genDataWithBigName(byte[] data, int nameLength) {
        byte [] newData = new byte[(data.length-5)+nameLength];// 5 it's for the old size name
        System.arraycopy(data,0,newData,0,5);
        /* Creation of the new name with random value */
        for (int i = 5; i < nameLength+4 ; i++) {
            newData[i]= (byte) (Math.floor(Math.random()*255)+1); // 1 because we don't want a zero value   
        }
        newData[nameLength+4]=(byte) 0x00;

        for (int i = (nameLength+4)+1, j= 11; i < newData.length && j < data.length; i++,j++) {
            newData[i]=data[j];
        }
        return newData;
    }

        /**
     * Generator input file with a author name with nameLength size
     * @param data is a byte array with the good format for the input converter_static program
     * @param nameLength the length of author name we want in the input file
     * @return a byte array based on the data variable with the author name of specific length
     */
    private static byte[] genDataWithBigCom(byte[] data, int nameLength) {
        byte [] newData = new byte[(data.length-5)+nameLength];// 5 it's for the old size name
        System.arraycopy(data,0,newData,0,35);
        /* Creation of the new name with random value */
        for (int i = 35; i < nameLength+4 ; i++) {
            newData[i]= (byte) ((Math.floor(Math.random()*255)+1)); // 1 because we don't want a zero value   
        }
        newData[nameLength+4]=(byte) 0x00;

        for (int i = (nameLength+4)+1, j=41 ; i < newData.length && j < data.length; i++,j++) { //avais is 41 au début
            newData[i]=data[j];
        }
        return newData;
    }


        /**
     * Modifier byte
     * @param data is a byte array with the good format for the input converter_static program
     * @param index of the byte will be modified
     * @param crashValue is the value that should be crashed the converter_static program
     * @return a byte array with the value at the index "index" modified by the crashValue
     */
    private static byte[] genCrashData(byte[] data, int index, byte crashValue) {
        byte[] res = new byte[data.length];
        System.arraycopy( data, 0, res, 0, data.length );
        res[index]=crashValue;
        return res;
    }


    /**
     * Simple method to modify some indexes with some byte values
     * @param data is the byte array containing a base of data for the converter progam
     * @param index is a table of indexes where one crash values are injected
     * @param crashValue is a table of crash values
     * @return a byte array with values at indexes in index table modified by values in crashValue table
     */
    private static byte[] genCrashData(byte[] data, int[] index, byte[] crashValue) {
        byte[] res = new byte[data.length];
        System.arraycopy( data, 0, res, 0, data.length );
        for (int i = 0; i < index.length; i++) {
            res[index[i]]=crashValue[i];
        }
        return res;
    }



   

    /**
     *  Crash test about the value of height particularly negative value
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void negativeDimensionPicture(byte[] data, Path path) {
        byte[] crashOne;
        for (int i = -80; i <256; i++) {// 256 is the max value for a byte
        	Integer p = new Integer(4);
            crashOne= genCrashData(data,16,(byte)i);// 17 is the index byte to make the negative value for the height
            try {
                Files.write(path,crashOne);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Run the converter_static exe */

            run_process(path);
            
            /*
            if(testOnConverter(run_process(path),path)){
                System.out.println("[FOUND]: Crash about negative dimension");
                return;
            }

            */
        }

    }


     /**
     * Test if the result of the execution have crashed the program
     * @param resultOfTheRun is boolean flag if the string result execution of
     *                       converter_static program containing the crash message
     * @param inputFile is the path where the test file will be generated
     * @return true if the file have been crashed the program and false otherwise
     */
    private static boolean testOnConverter(boolean resultOfTheRun, Path inputFile) {
        /* If the program is not crashing we delete the file */
        if (!resultOfTheRun) {
            try {
                Files.delete(inputFile);
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", inputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }



    /****************************************************************************************************** partie liée à la classe mère aux opérations plus hardware **************************************************/




    static byte[] initData(String filename) {
        File fileName = new File(filename);
        return read_file(fileName);
    }

     private static byte[] read_file(File file) {
        byte[] data = new byte[0];
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }





    //méthode pour lancer la ligne de commande

        /**
     * Function run the converter_static pragram with the file specified by the inputFile variable
     * @param inputFile the input file we give to the exec file.
     * @return true if the execution message contains the crash
     */
    static boolean run_process(Path inputFile) {
        try {
            String line;
            StringBuilder msgExec = new StringBuilder();
            

            ProcessBuilder processBuilder = new ProcessBuilder();

            processBuilder.command("./converter_linux_x8664", inputFile.toString(), "testoutput.img");


            Process process = processBuilder.start();

            process.waitFor();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));


             while ((line = stdInput.readLine()) != null) {
                msgExec.append(line);
            }

            System.out.println("output of command ./converter_linux_x8664" + inputFile.toString() + " testoutput.img : "+ msgExec.toString());

            stdInput.close();


            return msgExec.toString().toLowerCase().contains("crashed");



        }
        catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }



}