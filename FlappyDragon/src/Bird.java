
import java.awt.Image;

public class Bird {
    int larguraTela = 360;
    int alturaTela = 640;
    
    int x = larguraTela/8;
    int y = alturaTela/2;
    int largura = 45;
    int altura = 42;
    Image img;
    
    Bird(Image img){
        this.img = img;
    }
}
