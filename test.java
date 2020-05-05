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

       
       byte[] data =initData("testinput.img");
       String string = new String(data);
         System.out.println("version before"+data[2]);

       testOnTheOldVersion(data,Paths.get("testInputGen5.img"));

       

    }


   

    /**
     *  Crash test about old version of the converter_static program
     * @param data is a byte array with the good format for the input converter_static program
     * @param path is the path where the test file will be generated
     */
    private static void testOnTheOldVersion(byte[] data, Path path) {
        byte [] crashData;
        
        for (int i = 0; i < 100; i++) {
        	//System.out.println("la valeur en bbb" + (byte) i );
            crashData= genDataWithSpecificVersion(data, (byte) i);
            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }



            /*

            

            crashData= genDataWithSpecificVersion(data, (byte) 100);

            try {
                Files.write(path,crashData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            */



            boolean n = run_process(path);

            System.out.println("résultat de : "+ n);


            

            /* Run the converter_static exe */
            //if (testOnConverter(run_process(path),path)){
                //System.out.println("[FOUND]: Crash about an old version : v-"+i);
                //return;
            //}






        }

    }
    


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
            
            //Process p = Runtime.getRuntime().exec("./converter_linux_x8664 " + inputFile + " testoutput.img");

            /*

            Process p = Runtime.getRuntime().exec("./launch.sh");

            p.waitFor();

        
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s = stdInput.readLine();

            System.out.println("le grale : " + s);

            */

            ProcessBuilder processBuilder = new ProcessBuilder();

            //processBuilder.command("./launch.sh","testInputGen5.img");

            processBuilder.command("./converter_linux_x8664","testInputGen5.img", "testoutput.img");


            Process process = processBuilder.start();

            process.waitFor();

             BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
             //BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));


             while ((line = stdInput.readLine()) != null) {
            	System.out.println("voici la ligne: "+line);
                msgExec.append(line);
            }

            stdInput.close();



            //System.out.println("le grale : " + s);

            


            /* recover all line of the execution message */

            /*
            while (once) {
            	line = bre.readLine();
            	System.out.println("voici la ligne: "+line);
                msgExec.append(line);
                once = false;
            }

            */
            return msgExec.toString().toLowerCase().contains("crashed");



        }
        catch (Exception err) {
        	System.out.println("on rentre dans le catch");
            err.printStackTrace();
        }
        return false;
    }



}