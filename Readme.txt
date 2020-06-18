# LINGI2347-Generation-based fuzzer

name : SALEY ABDOU
first name : Djafarou
Noma : 24031600

5 files to crash the given program 
- version.img : crash the converter program by changing the value of the version field using the value 80
- height.img : the highest byte of the height is change to value 128 and when it'll be converter in signed number it's negative number. So the memory allocation will be negative and the program crash.
- comment.img : crash the converter program by creating a 1672-character commentary
- header.img : crash the converter program using the value 117 in the byte field at the end of the header
- dimension.img : crash when creating an image with dimension widthxheight corresponding to : 68000x68000

To run our program : 
make executable the converter program with command : sudo chmod +x converter_linux_x8664
compile the code with command : javac fuzzer.java
run the program with command : java fuzzer
