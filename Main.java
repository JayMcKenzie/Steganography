import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class Main {
    private static File file = new File("C:\\Users\\kubam\\OneDrive\\Pulpit\\Politechnika\\5 semestr\\POD\\Stegano\\rabbit.png");                        //poczatkowy obraz
    private static File outputFile = new File("C:\\Users\\kubam\\OneDrive\\Pulpit\\Politechnika\\5 semestr\\POD\\Stegano\\rabbit_crypted.png");         //otwieramy obraz do edycji
    private static File cryptedImage = new File("C:\\Users\\kubam\\OneDrive\\Pulpit\\Politechnika\\5 semestr\\POD\\Stegano\\rabbit_crypted.png");       //zapisujemy obraz do edycji
    private static int x = 300, y = 300;        //parametry
    private static int a;                       //alfa
    private static int r;                       //red
    private static int g;                       //green
    private static int b;                       //blue
    private static ArrayList<String> tab = new ArrayList<>();       //lista w ktorej przechowywane sa wartosci binarne a r g b (zarowno przed zaszyfrowaniem wiadomosci jak i po)
    private static int[] values = new int[4];                       //lista w ktorej przechowywane sa dziesietne wartosci zaszyfrowanych a r g b
    private static StringBuilder decrypted = new StringBuilder("00000000");     //string przechowujacy odszyfrowana wartosc binarna maski
    private static int decrypted_number;        //odszyfrowana wartosc dziesietna maski


    private static void get_pixels(BufferedImage image){
        int img;
        img = image.getRGB(x, y);
        a = (img >> 24) & 0xff;
        r = (img >>16) & 0xff;
        g = (img >>8) & 0xff;
        b = img & 0xff;
        System.out.println("a: " + a + ", r: " + r + ", g: " + g + ", b: " + b);
    }

    private static void to_binary(){
        //zamieniamy wartosci dziesietne pikseli na binarne
        if(Integer.toBinaryString(a).length() < 8)
        {
            StringBuilder enlarge = new StringBuilder(Integer.toBinaryString(a));
            while (enlarge.length() < 8)
                enlarge.insert(0, '0');         //jesli binarki sa krotsze niz 8 znakow, dopisujemy zera na poczatek
            tab.add(enlarge.toString());                  //zapisujemy binarna wartosc do tablicy
        }
        else
            tab.add(Integer.toBinaryString(a));

            if(Integer.toBinaryString(r).length() < 8)
            {
                StringBuilder enlarge = new StringBuilder(Integer.toBinaryString(r));
                while (enlarge.length() < 8)
                    enlarge.insert(0, '0');         //jesli binarki sa krotsze niz 8 znakow, dopisujemy zera na poczatek
                tab.add(enlarge.toString());                  //zapisujemy binarna wartosc do tablicy
            }
            else
            tab.add(Integer.toBinaryString(r));

        if(Integer.toBinaryString(g).length() < 8)
        {
            StringBuilder enlarge = new StringBuilder(Integer.toBinaryString(g));
            while (enlarge.length() < 8)
                enlarge.insert(0, '0');
            tab.add(enlarge.toString());
        }
        else
            tab.add(Integer.toBinaryString(g));

        if(Integer.toBinaryString(b).length() < 8)
        {
            StringBuilder enlarge = new StringBuilder(Integer.toBinaryString(b));
            while (enlarge.length() < 8)
                enlarge.insert(0, '0');
            tab.add(enlarge.toString());
        }
        else
            tab.add(Integer.toBinaryString(b));

}

    private static int to_int(String binary_number) {
        return Integer.parseInt(binary_number, 2);
    }

    private static void encrypt(StringBuilder mask){
         int counter = 0;                                       //licznik zmieniajacy indeks w masce
         StringBuilder changed;                                 //zaszyfrowany string z binarną wartością r/g/b
         for(int i=0; i<tab.size()-1; i++){
             changed = new StringBuilder(tab.get(i+1));
             for(int j=5; j<8; j++){
                if (counter < 6){
                    changed.setCharAt(j, mask.charAt(counter));
                    counter++;
                }
                else{
                    if(counter < 8) {
                        changed.setCharAt(j + 1, mask.charAt(counter));
                        counter++;
                    }
                    else break;
                }
            }
            tab.set(i+1,changed.toString());
        }
    }

    private static void decrypt(){
         int counter = 0;
         for(int i=1; i<tab.size(); i++){
             StringBuilder reader = new StringBuilder(tab.get(i));
             for(int j=5; j<8; j++){
                 if(counter < 6){
                     decrypted.setCharAt(counter, reader.charAt(j));
                     counter++;
                 }
                 else if(counter < 8){
                     decrypted.setCharAt(counter, reader.charAt(j+1));
                     counter++;
                 }
             }
         }
         decrypted_number = to_int(decrypted.toString());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Algorytm najmniej znaczacego bitu\n\n");
        BufferedImage image = ImageIO.read(file);
        System.out.println("Pozycja piksela: " + x + ", " + y);
        System.out.println("Poczatkowe wartosci bitow: ");
        get_pixels(image);
        to_binary();
        System.out.println("\nBity przed zaszyfrowaniem (kolejno a,r,g,b): ");
        for (String value : tab)
            System.out.println(value);

        int mask = 69;
        StringBuilder mask_s = new StringBuilder(Integer.toBinaryString(mask));
        while (mask_s.length() < 8)
            mask_s.insert(0, '0');
        System.out.println("\nMaska: dziesietnie " + mask +", binarnie " + mask_s);
        encrypt(mask_s);

        System.out.println("\nBity po zaszyfrowaniu (kolejno a,r,g,b): ");
        for (String s : tab)
            System.out.println(s);

        for(int i=0; i<tab.size(); i++)
            values[i] = to_int(tab.get(i));
        System.out.println("\nWartosci dziesietne zaszyfrowanych pikseli (kolejno a,r,g,b): ");
        System.out.println(values[0]+ "\n" + values[1] + "\n" + values[2] + "\n" + values[3]);
        //ustawianie piksela na zaszyfrowane wartosci
        BufferedImage out_image = ImageIO.read(outputFile);
        int img;
        img = values[0] << 24 | values[1] << 16 | values[2] << 8 | values[3];
        out_image.setRGB(x,y, img);
        System.out.println("\nSprawdzenie, wartosc zaszyfrowanego piksela w obrazie: ");
        get_pixels(out_image);
        ImageIO.write(out_image, "png", cryptedImage);

        decrypt();
        System.out.println("\nOdszyfrowana wartosc z piksela: " + decrypted_number + ", binarnie: "+decrypted.toString());
    }
}
