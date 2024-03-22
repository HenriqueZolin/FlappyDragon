
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    //tamanho em pixel da tela
    int alturaTela = 640;
    int larguraTela = 360;
    
    
    //Fazendo download das imagens do src
    Image bottomPipeImg;
    Image birdImg;
    Image backGroundImg;
    Image topPipeImg;
    
        
    //Lógica e funcionamento do jogo
    Bird bird;
    Timer gameloop;
    Timer surgeCano;
    int velocidadePulo = 0; //Velocidade eixo Y
    int gravidade = 1;
    int velocidadeCano = -5; //Velocidade eixo X
    int score = 0;
    boolean gameOver = true;
        
    ArrayList<Cano> listaCanos;
        
    public FlappyBird() {
        setPreferredSize(new Dimension(larguraTela, alturaTela));
        setFocusable(true);     //foca na tela
        addKeyListener(this);  //pede uma tecla 
        
        backGroundImg = new ImageIcon(getClass().getResource("./backgroundfp.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./birdDragon.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./topTower.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottomTower.png")).getImage();
        
        bird  = new Bird(birdImg);
        gameloop = new Timer(1000/60, this); //vai desenhar 60 frames por segundo
        
        listaCanos = new ArrayList<Cano>();  //vai guardar todos os canos para o loop
        
        surgeCano = new Timer(2000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                alocarCanos();
            }
        });
        //surgeCano.start();
        //gameloop.start();
        
    }
    
    public void alocarCanos(){
        Cano canoTopo = new Cano(topPipeImg);
        canoTopo.y = (int) (Math.random() * (-50 + -400));
        Cano canoBaixo = new Cano(bottomPipeImg);
        canoBaixo.y = canoTopo.y + 650;  //150 de espaço entre os canos e 500 de altura dos canos 
        
        listaCanos.add(canoTopo);
        listaCanos.add(canoBaixo);
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    
    public void draw(Graphics g){
        g.drawImage(backGroundImg, 0, 0, larguraTela, alturaTela, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.largura, bird.altura, null);
        
        
            
        for(int i=0; i<listaCanos.size(); i+=2){
            Cano cano = listaCanos.get(i);
            Cano cano2 = listaCanos.get(i+1);
            g.drawImage(cano.img,cano.x, cano.y, cano.largura, cano.altura, null);
            g.drawImage(cano2.img,cano2.x, cano2.y, cano2.largura, cano2.altura, null);
        }
        
        g.setColor(Color.white);
        g.fillRect(5, 5, 165, 40);
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            if(score==0){
                g.setColor(Color.white);
                g.fillRect(5, 5, 200, 40);
                g.setColor(Color.black);
                g.drawString("Press Enter", 10, 35);
            }else{
                g.setColor(Color.white);
                g.fillRect(5, 5, 250, 40);
                g.setColor(Color.black);
                g.drawString("Game Over = " + String.valueOf((int) score), 10, 35);
            }
        }else{
            g.drawString("Score = " + String.valueOf((int) score), 10, 35);
        }

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        movimento();
        repaint();
    }
    
    
    public void movimento(){   //seta os movimentos
        velocidadePulo += gravidade;
        Cano cano;
        Cano cano2;
        bird.y += velocidadePulo;
        bird.y = Math.max(bird.y, 0); //altura máxima
        bird.y = Math.min(bird.y, 640);
        for(int i=0;i<listaCanos.size();i+=2){
            cano = listaCanos.get(i);
            cano2 = listaCanos.get(i+1);
            
            cano.x += velocidadeCano;
            cano2.x += velocidadeCano;
            
            if(bird.x == cano.x){
            cano.passou = true;
            score = pontuacao(cano.passou);  //compara o eixo x do cano com o do passaro
            
            }else{
                cano.passou = false;
            }
            
            int colisaoX = bird.x;  //pra bater no exato momento que chegar no cano e n contar pontuação a mais;
            
            //calcula as areas onde estas os canos;
            if((colisaoX < (cano.x + cano.largura) &&
                (colisaoX + bird.largura) > cano.x &&
                bird.y < (cano.y + cano.altura) &&       //representa a colisao com o cnao de cima
                (bird.y + (bird.altura - 10)) > cano.y) || 
                (colisaoX < (cano2.x + cano2.largura) &&
                (colisaoX + bird.largura) > cano2.x &&    //colisao com cano de baixo
                bird.y > cano2.y - 40 &&
                (bird.y + bird.altura) < cano2.y + cano2.altura) ||
                (bird.y == 640)){                       //colisao se cair
                gameOver = true;
                fimJogo();
            }
        }
        
        
    }
    
    public int pontuacao(boolean p){
        if(p){
            score++;
            try {
                playSound("collect-points-190037.wav");
            } catch (LineUnavailableException ex) {
                Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(FlappyBird.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return score;
    }

    public void fimJogo(){
        surgeCano.stop();
        gameloop.stop();
    }
    
    public void playSound(String soundFile) throws MalformedURLException, LineUnavailableException, IOException, UnsupportedAudioFileException {
        File f = new File("./" + soundFile);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocidadePulo = -10;
        }
        
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            if(gameOver){
                bird.y = alturaTela/2;
                velocidadePulo = 0;
                listaCanos.clear();
                score = 0;
                gameOver = false;
                gameloop.start();
                surgeCano.start();
            }
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
