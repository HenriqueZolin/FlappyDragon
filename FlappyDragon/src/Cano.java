
import java.awt.Image;

public class Cano {
    int larguraTela = 360;
    
    Image img;
    int x = larguraTela;
    int y = 0;
    int largura = 70;
    int altura = 500;
    boolean passou = false;
    
    public Cano(Image img) {
        this.img = img;
    }
    
}
