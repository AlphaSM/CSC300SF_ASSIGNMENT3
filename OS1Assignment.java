import java.io.DataInputStream;
import java.io.FileInputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class OS1Assignment{
  //Function that uses the hashtable and the virtualaddress as a page address to get the corresponding frame number and then the physical address
  public static long translate(HashMap<Long, Long> pageTable, long virtualAddress) {
    //Binary Format 
    // Extract offset and page number from virtual address
    long offset = virtualAddress & 0x7F; // Get the last 7 bits for offset (mask with all 7 LSBs set to 1)
    long pageNumber = virtualAddress >> 7; // Shift right by 7 bits to isolate the page number (no masking needed)

    // Check if page number exists in the page table
    if (!pageTable.containsKey(pageNumber)) {
      throw new IllegalArgumentException("Virtual page number " + pageNumber + " not found in page table");
    }

    // Get physical frame number from page table
    long physicalFrameNumber = pageTable.get(pageNumber);

    // Combine 5 bit physical frame number and 7 bit offset to get a 12 bit physical address
    return (physicalFrameNumber << 7) | offset;
  }

  public static void translateDisplayAndSave(HashMap<Long, Long> pageTable, long virtualAddress, ArrayList<String>  Arr) {
    try {
      //saves physical address as long
      long physicalAddress = translate(pageTable, virtualAddress);
      //save to list
      Arr.add(String.format("0x%018X", physicalAddress));
      System.out.printf("Virtual Address: 0x%08X ==> Physical Address: 0x%01X\n", virtualAddress, physicalAddress);
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
    }
  }

  public static void main(String[] args) throws IOException {

    //Page table from example using hashtable 
    //takes in long values 
    HashMap<Long, Long> pageTable = new HashMap<>();
    pageTable.put(0L, 2L);
    pageTable.put(1L, 4L);
    pageTable.put(2L, 1L);
    pageTable.put(3L, 7L);
    pageTable.put(4L, 3L);
    pageTable.put(5L, 5L);
    pageTable.put(6L, 6L);

    //Reading in parameter from command line
    // Error check to see if filename is provided
    if (args.length != 1) {
      //How to run program
      System.err.println("Please Run these instructions");
      System.err.println("1. javac OS1Assignment.java");//javac OS1Assignment.java
      System.err.println("2. java OS1Assignment <filename>");//java OS1Assignment OS1sequence 
      System.err.println("Please enter a file that exists for filename");
      return;
    }
    

    //Get filename from command line argument
    String filename = args[0];

    //String filename = "OS1sequence";  // Replace with your actual filename
    
    //reads input file which is filename 
    DataInputStream inputStream = null;
    //checks to see if the file actually exists 
    try{
    //DataInputStream inputStream = new DataInputStream(new FileInputStream(filename));
    inputStream = new DataInputStream(new FileInputStream(filename));
    }
    catch (IOException e) {
      // Handle the exception, e.g., print error message
      System.err.println("Error with file: " + filename);
      return;
    }
    //specify which output file to save at 
    String outputfilename = "output-OS1";
    //array list to store physical addresses 
    ArrayList<String> myArr = new ArrayList<String>();
    //assuming file will always have no errors 
    while (inputStream.available() > 0) {
      // Read 8 bytes as an unsigned long
      long address = inputStream.readLong();
      //reverse
      long reverseAddress = Long.reverseBytes(address);
      //translate, display and save to array
      translateDisplayAndSave(pageTable, reverseAddress,myArr);
    }
    //close file reader
    inputStream.close();

    // Specify the file path
    Path out = Paths.get(outputfilename);

    // Write the ArrayList to the file output-OS1
    try {
        Files.write(out, myArr, Charset.defaultCharset());
    } catch (IOException e) {
        e.printStackTrace();
    }


  }
}
