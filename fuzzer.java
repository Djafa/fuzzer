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
    private static void testOnNegativeHeight(byte[] data, Path path) {
        byte [] crashData;
        //int [] hexaIndex = new int[]{13,14,17,18}; // 12, 13? 14 for the width and 16,17,18 for height
        int [] hexaIndex = new int[]{19};
        for (int i = 1; i <5000; i++) {
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte)i});
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

     /**
     *  Crash test about dimensions, particularly for different values of the height
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnHugeDimension(byte[] data, Path path) {
        byte [] crashData;
        //int [] hexaIndex = new int[]{13,14,17,18}; // 12, 13? 14 for the width and 16,17,18 for height
        int [] hexaIndex = new int[]{12,13,14,15,16,17,18,19};
        for (int i = 1; i <50000; i=i+10000) {
            crashData=genCrashData(data,hexaIndex,new byte[]{(byte)160,(byte)9,(byte)1,(byte)0,(byte)160,(byte)9,(byte)1,(byte)0});
            try{
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Run the converter program */
            if (testOnConverter(run_process(path),path)){
                System.out.println("[FOUND] Crash about width*height: 68000x68000");
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
     * Generator input file with a comment name with nameLength size
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
            // we return false if it did not crashed
            return false;
        }
        return true;
    }



    /****************************************************************************************************** Hardware focused operations**************************************************/



        /**
     * Function run the converter pragram with the file specified by the inputFile variable
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

            stdInput.close();
            return msgExec.toString().toLowerCase().contains("crashed");

        }
        catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }


    public static void main(String [] args){

        
  
       // we generate here the initial file
       byte [] data = {(byte)0xab, (byte)0xcd, (byte)0x64, (byte)0x00 , (byte)0x01 , (byte)0x52 , (byte)0x61 , (byte)0x6d  , (byte)0x69 , (byte)0x6e , (byte)0x00 , (byte)0x02 , (byte)0x02 , (byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x02 , (byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x0a , (byte)0x02 , (byte)0x00 , (byte)0x00  , (byte)0x00, 
       (byte)0x0b , (byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0x00 , (byte)0xff , (byte)0xff , (byte)0xff , (byte)0x00,
       (byte)0x0c , (byte)0x48 , (byte)0x65 , (byte)0x6c , (byte)0x6c , (byte)0x6f  , (byte)0x00 , (byte)0x00 , (byte)0x01 , (byte)0x01 , (byte)0x00 };


       /* Crash test about old version, we test different values and it crashes for value 80 */
       System.out.println("===================== Test old version ================");
       testOnTheOldVersion(data,Paths.get("version.img"));

       /* Crash test about height value too big, we test different values and it crashes for value 128 */
       System.out.println("===================== Test negative value of height ==="); //test on only height it works
       testOnNegativeHeight(data,Paths.get("height.img"));

       /* Crash test  about the too big commentary that is creating an overflow and it crashes with a comment size of 1672 */
       System.out.println("===================== Test big com name ===============");
       testOnTheCom(data,Paths.get("comment.img"));
         
       /* Crash test regardng the byte filed for the end of the header, we test different values and it crashes for value 117 */
       System.out.println("===================== Test on end of header ===========");
       testOnEndOfHeader(data,Paths.get("header.img"));

       /* Crash test regardng the size of the image heightxwidth equal to: 68000x68000 */
       System.out.println("===================== Test on last ====================");
       testOnHugeDimension(data,Paths.get("dimension.img"));

    }



}