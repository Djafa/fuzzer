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

public class fuzzer{

    public static void main(String [] args){

       
       byte[] data =initData("testinput.img"); // we fetch the file in an array of byte


       /* Crash test about old version, we test different values and it crashes for value 80 */
       System.out.println("===================== Test old version ================");
       testOnTheOldVersion(data,Paths.get("version.img"));

       /* Crash test about height value too big, we test different values and it crashes for value 128 */
       System.out.println("===================== Test huge picture size =========="); //test on only height it works
       testOnTheHugeDimension(data,Paths.get("height.img"));

       /* Crash test  about the too big commentary that is creating an overflow and it crashes with a comment size of 1672 */
       System.out.println("===================== Test big com name ===============");
       testOnTheCom(data,Paths.get("comment.img"));
         
       /* Crash test regardng the byte filed for the end of the header, we test different values and it crashes for value 117 */
       System.out.println("===================== Test on end of header ===========");
       testOnEndOfHeader(data,Paths.get("header.img"));

       System.out.println("===================== Test on the black color ok ======");
       testOnBigValCol(data,Paths.get("color.img")); //here we test the black color 
    }


/*********************************************** test method section *******************************************************************************/
   

    /**
     *  Crash test about old version of the converter program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheOldVersion(byte[] data, Path path) {
        byte [] crashData;
        for (int i = -10; i < 110; i++) {
            crashData= genDataWithSpecificVersion(data, (byte) i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Run the converter program */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND] Crash regarding an old version : v-"+i);
                return;
            }

        }

    }

     /** HERE WE TEST WITH THE BLACK
     *  Crash test about old version of the converter_static program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnBigValCol(byte[] data, Path path) {
        byte [] crashData; 
         int [] hexaIndex = new int[]{21,22};
        for (int i = 1; i < 2000; i++) {
            //crashData= genDataWithBigValCol(data,i);
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte)i, (byte)i});
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /* Run the converter_static exe */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND] Crash regarding an overflow with the first color : v-"+i);
                return;
            }

        }

    }


    /**
     *  Crash test about the modification of the end of header field
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnEndOfHeader(byte[] data, Path path) {
        byte [] crashData;
        int [] hexaIndex = new int[]{41}; //index 41 for the end of the header
        for (int i = 0; i < 256; i+=1) {
             crashData=genCrashData(data,hexaIndex,new byte[]{(byte)i});
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Run the converter program */                
            if (testOnConverter(run_process(path),path)) {
                System.out.println("[FOUND] Crash about the end of header field with value: " + i);
                return;
            }

            

            
        }
    }


     /**
     *  Crash test about the number of characters used in the comment
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheCom(byte[] data, Path path) {
        byte [] crashData;
         int [] hexaIndex = new int[]{35,36, 37,38,39}; //de 34 à 38 pour le contenu du commentaire
        for (int i = 200; i < 3000; i++) {
            crashData= genDataWithBigCom(data,i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Run the converter program*/
            if (testOnConverter(run_process(path),path)) {
                System.out.println("[FOUND] Crash about the comment with a length of : " + i);
                return;
            }
            
        }
    }

    
     /**
     *  Crash test about dimensions, particularly for different values of the height
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheHugeDimension(byte[] data, Path path) {
        byte [] crashData;
        //int [] hexaIndex = new int[]{13,14,17,18}; // 12, 13? 14 for the width and 16,17,18 for height
        int [] hexaIndex = new int[]{16,19};
        for (int i = 1; i <5000; i++) {
            //crashData=genCrashData(data,hexaIndex,new byte[]{(byte)i,(byte)i,(byte)i,(byte) i});
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte)i,(byte) i});
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Run the converter program */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND] Crash about huge picture dimension for height : " + i);
                return;
            }

            
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
     * Generator input file with a author name with nameLength size
     * @param data is a byte array with the good format for the input converter_static program
     * @param nameLength the length of author name we want in the input file
     * @return a byte array based on the data variable with the author name of specific length
     */
    private static byte[] genDataWithBigColor(byte[] data, int nameLength) {
        byte [] newData = new byte[(data.length-3)+nameLength];// 5 it's for the old size name
        System.arraycopy(data,0,newData,0,21);
        /* Creation of the new name with random value */
        for (int i = 21; i < nameLength+4 ; i++) {
            newData[i]= (byte) ((Math.floor(Math.random()*255)+1)); // 1 because we don't want a zero value
               
        }
        newData[nameLength+4]=(byte) 0x00;

        for (int i = (nameLength+4)+1, j=25 ; i < newData.length && j < data.length; i++,j++) { //avais is 41 au début
            newData[i]=data[j];
        }
        return newData;
    }


            /** HERE WE TRY TO OVERFLOW THE COLOR TABLE
     * Generator input file with a author name with nameLength size
     * @param data is a byte array with the good format for the input converter_static program
     * @param nameLength the length of author name we want in the input file
     * @return a byte array based on the data variable with the author name of specific length
     */
    private static byte[] genDataWithBigValCol(byte[] data, int nameLength) {
        byte [] newData = new byte[(data.length-3)+nameLength];// 5 it's for the old size name
        System.arraycopy(data,0,newData,0,26); // on choisir le black one
        /* Creation of the new name with random value */
        for (int i = 26; i < nameLength+4 ; i++) {
            newData[i]= (byte) ((Math.floor(Math.random()*255)+1)); // 1 because we don't want a zero value
               
        }
        newData[nameLength+4]=(byte) 0x00;

        for (int i = (nameLength+4)+1, j=30 ; i < newData.length && j < data.length; i++,j++) { //avais is 41 au début
            newData[i]=data[j];
        }
        //je mdifie juste le nbr de couleur
        //newData[21] = (byte)4;
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
     * Test if the result of the execution have crashed the program
     * @param resultOfTheRun is boolean flag if the string result execution of
     *                       converter_static program containing the crash message
     * @param inputFile is the path where the test file will be generated
     * @return true if the file have been crashed the program and false otherwise
     */
    private static boolean testOnConverter(boolean resultOfTheRun, Path inputFile) {
        /* If the program is not crashing we delete the file */

        if (!resultOfTheRun) {
            /*
            try {
                Files.delete(inputFile);
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", inputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            return false;
        }
        return true;
    }



    /****************************************************************************************************** Hardware focused operations**************************************************/




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
            //I've decided here not to print out every output of the program
            //System.out.println("output of command ./converter_linux_x8664" + inputFile.toString() + " testoutput.img : "+ msgExec.toString());
            stdInput.close();
            return msgExec.toString().toLowerCase().contains("crashed");

        }
        catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }



}