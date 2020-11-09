import java.io.IOException;

public class Main {
        public static void main(String[] args) throws IOException {

              Converter converter = new Converter();

              System.out.print(converter.fileToXML("./row-input-data.txt"));
      }  
}
