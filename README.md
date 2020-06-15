# LINGI2347-Generation-based fuzzer


5 files to crash the given program 
- version.img : crash the converter program by changing the value of the version field using the value 80
- height.img : crash the converter program by changing the value of the height field using the value 128
- comment.img : crash the converter program by creating a 1672-character commentary
- header.img : crash the converter program using the value 117 in the byte field at the end of the header

To run our program : 
make executable the converter program with command : sudo chmod +x converter_linux_x8664
compile the code with command : javac fuzzer.java
run the program with command : java fuzzer
