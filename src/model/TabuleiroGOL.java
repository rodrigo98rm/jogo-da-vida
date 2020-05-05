package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.JPanel;

import view.JogoDaVida;

public class TabuleiroGOL extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable 
{
	private static final long serialVersionUID = 1L;
	private Dimension dimensaoTabuleiro = null;
    private ArrayList<Cell> ponto = new ArrayList<Cell>(0);
    private static int tamanhoCelula = 150;
    private int iteracaoPorSegundo;

    public TabuleiroGOL() 
    {
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void atualizarTamanhoAray() 
    {
        ArrayList<Cell> removeList = new ArrayList<Cell>(0);
        for (Cell atual : ponto) {
            if ((atual.x > dimensaoTabuleiro.width-1) || (atual.y > dimensaoTabuleiro.height-1)) {
                removeList.add(atual);
            }
        }
        ponto.removeAll(removeList);
        repaint();
    }

    public void addPoint(int x, int y, boolean infected) 
    {
        if (!ponto.contains(new Cell(x,y, infected))) {
        	ponto.add(new Cell(x,y, infected));
        } 
        repaint();
    }

    public void addPoint(MouseEvent me) 
    {
        int x = me.getPoint().x/tamanhoCelula-1;
        int y = me.getPoint().y/tamanhoCelula-1;
        if ((x >= 0) && (x < dimensaoTabuleiro.width) && (y >= 0) && (y < dimensaoTabuleiro.height)) {
            addPoint(x,y, false);
        }
    }

    public void removePoint(int x, int y) 
    {
    	ponto.remove(new Cell(x,y));
    }

	/**
	* Inicializa o ArrayList que representa o tabuleiro com valores vazios
	 */
    public void resetBoard() 
    {
    	ponto.clear();
    }

    /**
     * Inicializa o tabuleiro com valores randômicos
	 */
    public void preencherTabRandom(int porcentagem) 
    {
        for (int x=0; x<dimensaoTabuleiro.width; x++) 
        {
            for (int y=0; y<dimensaoTabuleiro.height; y++) 
            {
                if (Math.random()*100 < porcentagem) 
                {
                    addPoint(x,y, false);
                }
            }//Fim do for y
        }//Fim do for x
    }

    /**
     * Método para desenhar o grid do tabuleiro do Jogo da Vida
     */
    public void paintComponent(Graphics g) 
    {
    	iteracaoPorSegundo = JogoDaVida.getIteracaoPorSegundo();
    	
        super.paintComponent(g);
        try {
            for (Cell novoPonto : ponto) 
            {
                g.setColor(Color.blue);
                g.fillRect(tamanhoCelula + (tamanhoCelula*novoPonto.x), tamanhoCelula + (tamanhoCelula*novoPonto.y), tamanhoCelula, tamanhoCelula);
            }
        } catch (ConcurrentModificationException cme) {}

        g.setColor(Color.BLACK);
        for (int x=0; x<=dimensaoTabuleiro.width; x++) {
            g.drawLine(((x*tamanhoCelula)+tamanhoCelula), tamanhoCelula, (x*tamanhoCelula)+tamanhoCelula, tamanhoCelula + (tamanhoCelula*dimensaoTabuleiro.height));
        }
        for (int y=0; y<=dimensaoTabuleiro.height; y++) {
            g.drawLine(tamanhoCelula, ((y*tamanhoCelula)+tamanhoCelula), tamanhoCelula*(dimensaoTabuleiro.width+1), ((y*tamanhoCelula)+tamanhoCelula));
        }
    }

    public void componentResized(ComponentEvent e) 
    {
        dimensaoTabuleiro = new Dimension(getWidth()/tamanhoCelula-2, getHeight()/tamanhoCelula-2);
        atualizarTamanhoAray();
    }

    public void componentMoved(ComponentEvent e) {}

    public void componentShown(ComponentEvent e) {}

    public void componentHidden(ComponentEvent e) {}

    public void mouseClicked(MouseEvent e) {
    int count = e.getClickCount();
        
        System.out.println(count);
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
        // Mouse was released (user clicked)
        
                       
//        addPoint(e);
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
        // Mouse is being dragged, user wants multiple selections
        addPoint(e);
    }

    public void mouseMoved(MouseEvent e) {}

    public void run() 
    {
    	int vizinhosVivos=0;
        ArrayList<Cell> celulasVivas = new ArrayList<Cell>(0);
        
        boolean[][] tabuleiro = new boolean[dimensaoTabuleiro.width][dimensaoTabuleiro.height];
        for (Cell atual : ponto) 
        {
            tabuleiro[atual.x][atual.y] = true;
            //System.out.println("x:"+atual.x+"       y:"+atual.y);
        }
        
        for (int x=0; x<dimensaoTabuleiro.width; x++) 
        {
            for (int y=0; y<dimensaoTabuleiro.height; y++) 
            {
            	//System.out.println("---------------------");
            	//System.out.println("Ponto("+x+","+y+"): ");
            	vizinhosVivos = contarVizinhosVivos(x, y, tabuleiro);
            	//System.out.println("Vizinhos: " + vizinhosVivos);
            	            	
                if ( tabuleiro[x][y] ) 
                {
                	/**
                	 * Regra de sobrevivência. 
                	 * Se uma célula está viva em um determinado instante, 
                	 * e se a quantidade de seus vizinhos vivos é igual a 
                	 * dois (02) ou três (03), a celula sobrevive na próxima 
                	 * iteração.
                	 */
                    if ((vizinhosVivos == 2) || (vizinhosVivos == 3)) 
                    {
                        celulasVivas.add(new Cell(x,y, false));
                    } 
                    
                    /**
                     * Regra de morte 1.
                     * Para uma célula viva, se em um instante a quantidade de vizinhos
                     * vivos for menor que dois (02), na próxima iteração esta célula morre
                     * por solidão (sub-população).
                     * 
                     * Regra de morte 2.
                     * Para uma célula viva, se em um determinado instante a quantidade de
                     * vizinhos vivos for maior do que três (03), na próxima iteração a
                     * célula morre por super população.
                     * 
                     */
                } else {
                    /**
                     * Regra de nascimento. 
                     * Se uma célula está morta em um determinado instante, mas
                     * tem exatamente três (03) vizinhos vivos, então esta célula
                     * se torna viva na próxima iteração.
                     */
                    if (vizinhosVivos == 3) 
                    {
                        celulasVivas.add(new Cell(x,y, false));
                    }
                }// Fim do else
            }// Fim do for j
        }//Fim do for i
        
        resetBoard();
        ponto.addAll(celulasVivas);
        repaint();
        
        try 
        {   
            Thread.sleep(1000/iteracaoPorSegundo);
        	//Thread.sleep(4000);
            run();
        } catch (InterruptedException ex) {}    	
    }//Fim do método run()
       
	/**
	 * Contar o número de células vivas, de acordo com a posição i,j
	*/
	private int contarVizinhosVivos(int x, int y,  boolean[][] tabuleiro) 
	{
		/**
		 *   -----------------------------------------------------
		 *  |                      |    y=acima    |              |
		 *  |         x=esquerda   |     (x,y)     |  x=direita   |                   
		 *  |                      |    y=abaixo   |              |
		 *   -----------------------------------------------------
		 *   
		 *   ------------------------------------------------------------
		 *  |  (esquerda, acima)   |  (x ,acima)   |   (direita, acima)   |    
		 *  |  (esquerda, y)       |  (x,y)        |   (direita,y)        | 
		 *  |  (esquerda, abaixo)  |  (x,abaixo)   |   (direita, abaixo)  |     
		 *   ------------------------------------------------------------           
		 *              
		 */
             
        int vizinhanca=0;
        int acima=0, abaixo=0, direita=0, esquerda=0;

        if (y == 0) 
            acima = dimensaoTabuleiro.height-1;
        else acima = y-1;       
        
        if (y == dimensaoTabuleiro.height-1) 
            abaixo = 0;
        else abaixo = y+1;
        
        if (x == 0) 
            esquerda = dimensaoTabuleiro.width-1;
        else esquerda = x-1;
        
        if (x == dimensaoTabuleiro.width-1) 
            direita = 0;    			
        else direita = x+1;      
        
        //System.out.println("acima: "+acima+"   abaixo: "+abaixo+"   esquerda: "+esquerda+"    direita: "+direita);
        
        if ( tabuleiro[esquerda][acima] )
        	vizinhanca++;
        if ( tabuleiro[x][acima]) 
            vizinhanca++;
        if ( tabuleiro[direita][acima] )
        	vizinhanca++;
        if ( tabuleiro[esquerda][y] )
        	vizinhanca++;
        if ( tabuleiro[direita][y] )
        	vizinhanca++;
        if ( tabuleiro[esquerda][abaixo] )
        	vizinhanca++;
        if ( tabuleiro[x][abaixo] )
        	vizinhanca++;
        if ( tabuleiro[direita][abaixo] )
        	vizinhanca++;

        return vizinhanca;
	}//Fim do método contarVizinhosVivos()
}