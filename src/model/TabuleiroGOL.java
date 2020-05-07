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

public class TabuleiroGOL extends JPanel implements ComponentListener, MouseListener, MouseMotionListener, Runnable {

    private static final long serialVersionUID = 1L;
    private Dimension dimensaoTabuleiro = null;
    private ArrayList<Cell> ponto = new ArrayList<Cell>(0);
    private static int tamanhoCelula = 10;
    private int iteracaoPorSegundo;

    public TabuleiroGOL() {
        addComponentListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void atualizarTamanhoAray() {
        ArrayList<Cell> removeList = new ArrayList<Cell>(0);
        for (Cell atual : ponto) {
            if ((atual.x > dimensaoTabuleiro.width - 1) || (atual.y > dimensaoTabuleiro.height - 1)) {
                removeList.add(atual);
            }
        }
        ponto.removeAll(removeList);
        repaint();
    }

    public void addPoint(int x, int y, boolean infected) {
        ponto.add(new Cell(x, y, infected, 0));
        repaint();
    }

    public void addPoint(MouseEvent me, boolean infected) {
        int x = me.getPoint().x / tamanhoCelula - 1;
        int y = me.getPoint().y / tamanhoCelula - 1;
        if ((x >= 0) && (x < dimensaoTabuleiro.width) && (y >= 0) && (y < dimensaoTabuleiro.height)) {
            addPoint(x, y, infected);
        }
    }

    public void removePoint(int x, int y) {
        ponto.remove(new Cell(x, y));
    }

    /**
     * Inicializa o ArrayList que representa o tabuleiro com valores vazios
     */
    public void resetBoard() {
        ponto.clear();
    }

    /**
     * Inicializa o tabuleiro com valores randômicos
     */
    public void preencherTabRandom(int porcentagem) {
        for (int x = 0; x < dimensaoTabuleiro.width; x++) {
            for (int y = 0; y < dimensaoTabuleiro.height; y++) {
                if (Math.random() * 100 < porcentagem) {
                    addPoint(x, y, false);
                }
            }//Fim do for y
        }//Fim do for x
    }

    /**
     * Método para desenhar o grid do tabuleiro do Jogo da Vida
     */
    public void paintComponent(Graphics g) {
        iteracaoPorSegundo = JogoDaVida.getIteracaoPorSegundo();

        super.paintComponent(g);
        try {
            for (Cell novoPonto : ponto) {
                if (novoPonto.isInfected() == true) {
                    g.setColor(Color.red); // Celulas infectadas aparecem com a cor vermelha
                } else {
                    g.setColor(Color.blue); // Celulas saudaveis aparecem com a cor azul
                }

                g.fillRect(tamanhoCelula + (tamanhoCelula * novoPonto.x), tamanhoCelula + (tamanhoCelula * novoPonto.y), tamanhoCelula, tamanhoCelula);
            }
        } catch (ConcurrentModificationException cme) {
        }

        g.setColor(Color.BLACK);
        for (int x = 0; x <= dimensaoTabuleiro.width; x++) {
            g.drawLine(((x * tamanhoCelula) + tamanhoCelula), tamanhoCelula, (x * tamanhoCelula) + tamanhoCelula, tamanhoCelula + (tamanhoCelula * dimensaoTabuleiro.height));
        }
        for (int y = 0; y <= dimensaoTabuleiro.height; y++) {
            g.drawLine(tamanhoCelula, ((y * tamanhoCelula) + tamanhoCelula), tamanhoCelula * (dimensaoTabuleiro.width + 1), ((y * tamanhoCelula) + tamanhoCelula));
        }
    }

    public void componentResized(ComponentEvent e) {
        dimensaoTabuleiro = new Dimension(getWidth() / tamanhoCelula - 2, getHeight() / tamanhoCelula - 2);
        atualizarTamanhoAray();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        // Mouse was released (user clicked)

        int count = e.getClickCount();

        if (count == 1) { // Um click: adicionar uma celula saudavel
            addPoint(e, false);
        } else if (count == 2) { // Dois clicks: adicionar uma celula infectada
            addPoint(e, true);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        // Mouse is being dragged, user wants multiple selections
        addPoint(e, false);
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void run() {
        int vizinhosVivosSaudaveis = 0;
        int vizinhosVivosInfectados = 0;
        ArrayList<Cell> celulasVivas = new ArrayList<Cell>(0);

        // matriz trocada de boolean para Cell para facilitar o acesso as
        // propriedades de cada celula
        Cell[][] tabuleiro = new Cell[dimensaoTabuleiro.width][dimensaoTabuleiro.height];
        for (Cell atual : ponto) {
            tabuleiro[atual.x][atual.y] = atual;
        }

        for (int x = 0; x < dimensaoTabuleiro.width; x++) {
            for (int y = 0; y < dimensaoTabuleiro.height; y++) {

                vizinhosVivosSaudaveis = contarVizinhosVivosSaudaveis(x, y, tabuleiro);
                vizinhosVivosInfectados = contarVizinhosVivosInfectados(x, y, tabuleiro);

                Cell atual = tabuleiro[x][y];

                if (atual != null) {

                    boolean morre = false;
                    boolean infectada = false;

                    /*
                        Regra de Morte
                        - Uma celula infectada morre na transicao entre iteracoes
                        - Uma celula morre de superpopulacao quando tem pelo menos 7 vizinhos vivos (infectados ou nao)
                     */
//                    if (atual.isInfected() && Math.random() < 0.007) {
                    if (atual.isInfected() || (vizinhosVivosInfectados + vizinhosVivosSaudaveis) > 6) {
                        morre = true;
                    }

                    /*
                        Regra de Infeccao
                        - Se possui pelo menos um vizinho infectado a celula fica infectada
                        - Se a celula esta infectada ela continua infectada (utilizado apenas na variante com probabilidades)
                     */
                    if ((vizinhosVivosInfectados > 0)) {
//                        if ((vizinhosVivosInfectados > 0 && Math.random() > 0.8) || atual.isInfected()) {
//                        if (atual.getDaysInfected() > 8) {
//                            infectada = false;
//                            morre = false;
//                        } else {
                        infectada = true;
//                        }
                    }

                    if (!morre) {
                        celulasVivas.add(new Cell(x, y, infectada, atual.getDaysInfected() + 1));
                    }

                } else {
                    /*
                        Regra de Nascimento
                        - Uma celula nasce caso tenha exatamente dois vizinhos vivos saudaveis
                     */
//                    if (vizinhosVivosSaudaveis == 2 && Math.random() > 0.7) {
                    if (vizinhosVivosSaudaveis == 2) {
                        celulasVivas.add(new Cell(x, y, false, 0));
                    }
                }// Fim do else
            }// Fim do for j
        }//Fim do for i

        resetBoard();
        ponto.addAll(celulasVivas);
        repaint();

        try {
            Thread.sleep(1000 / iteracaoPorSegundo);
            run();
        } catch (InterruptedException ex) {
        }
    }//Fim do método run()

    /**
     * Contar o número de células vivas saudaveis, de acordo com a posição i,j
     */
    private int contarVizinhosVivosSaudaveis(int x, int y, Cell[][] tabuleiro) {
        /**
         * ----------------------------------------------------- | | y=acima | |
         * | x=esquerda | (x,y) | x=direita | | | y=abaixo | |
         * -----------------------------------------------------
         *
         * ------------------------------------------------------------ |
         * (esquerda, acima) | (x ,acima) | (direita, acima) | | (esquerda, y) |
         * (x,y) | (direita,y) | | (esquerda, abaixo) | (x,abaixo) | (direita,
         * abaixo) |
         * ------------------------------------------------------------
         *
         */

        int vizinhanca = 0;
        int acima = 0, abaixo = 0, direita = 0, esquerda = 0;

        if (y == 0) {
            acima = dimensaoTabuleiro.height - 1;
        } else {
            acima = y - 1;
        }

        if (y == dimensaoTabuleiro.height - 1) {
            abaixo = 0;
        } else {
            abaixo = y + 1;
        }

        if (x == 0) {
            esquerda = dimensaoTabuleiro.width - 1;
        } else {
            esquerda = x - 1;
        }

        if (x == dimensaoTabuleiro.width - 1) {
            direita = 0;
        } else {
            direita = x + 1;
        }

        // Vizinho saudavel existe se sua celula nao estiver vazia e nao estiver infectada
        if (tabuleiro[esquerda][acima] != null && tabuleiro[esquerda][acima].isInfected() == false) {
            vizinhanca++;
        }
        if (tabuleiro[x][acima] != null && tabuleiro[x][acima].isInfected() == false) {
            vizinhanca++;
        }
        if (tabuleiro[direita][acima] != null && tabuleiro[direita][acima].isInfected() == false) {
            vizinhanca++;
        }
        if (tabuleiro[esquerda][y] != null && tabuleiro[esquerda][y].isInfected() == false) {
            vizinhanca++;
        }
        if (tabuleiro[direita][y] != null && tabuleiro[direita][y].isInfected() == false) {
            vizinhanca++;
        }
        if (tabuleiro[esquerda][abaixo] != null && tabuleiro[esquerda][abaixo].isInfected() == false) {
            vizinhanca++;
        }
        if (tabuleiro[x][abaixo] != null && tabuleiro[x][abaixo].isInfected() == false) {
            vizinhanca++;
        }
        if (tabuleiro[direita][abaixo] != null && tabuleiro[direita][abaixo].isInfected() == false) {
            vizinhanca++;
        }

        return vizinhanca;
    }

    private int contarVizinhosVivosInfectados(int x, int y, Cell[][] tabuleiro) {
        /**
         * ----------------------------------------------------- | | y=acima | |
         * | x=esquerda | (x,y) | x=direita | | | y=abaixo | |
         * -----------------------------------------------------
         *
         * ------------------------------------------------------------ |
         * (esquerda, acima) | (x ,acima) | (direita, acima) | | (esquerda, y) |
         * (x,y) | (direita,y) | | (esquerda, abaixo) | (x,abaixo) | (direita,
         * abaixo) |
         * ------------------------------------------------------------
         *
         */

        int vizinhanca = 0;
        int acima = 0, abaixo = 0, direita = 0, esquerda = 0;

        if (y == 0) {
            acima = dimensaoTabuleiro.height - 1;
        } else {
            acima = y - 1;
        }

        if (y == dimensaoTabuleiro.height - 1) {
            abaixo = 0;
        } else {
            abaixo = y + 1;
        }

        if (x == 0) {
            esquerda = dimensaoTabuleiro.width - 1;
        } else {
            esquerda = x - 1;
        }

        if (x == dimensaoTabuleiro.width - 1) {
            direita = 0;
        } else {
            direita = x + 1;
        }

        // Vizinho infectado existe se sua celula nao estiver vazia e estiver infectada
        if (tabuleiro[esquerda][acima] != null && tabuleiro[esquerda][acima].isInfected()) {
            vizinhanca++;
        }
        if (tabuleiro[x][acima] != null && tabuleiro[x][acima].isInfected()) {
            vizinhanca++;
        }
        if (tabuleiro[direita][acima] != null && tabuleiro[direita][acima].isInfected()) {
            vizinhanca++;
        }
        if (tabuleiro[esquerda][y] != null && tabuleiro[esquerda][y].isInfected()) {
            vizinhanca++;
        }
        if (tabuleiro[direita][y] != null && tabuleiro[direita][y].isInfected()) {
            vizinhanca++;
        }
        if (tabuleiro[esquerda][abaixo] != null && tabuleiro[esquerda][abaixo].isInfected()) {
            vizinhanca++;
        }
        if (tabuleiro[x][abaixo] != null && tabuleiro[x][abaixo].isInfected()) {
            vizinhanca++;
        }
        if (tabuleiro[direita][abaixo] != null && tabuleiro[direita][abaixo].isInfected()) {
            vizinhanca++;
        }

        return vizinhanca;
    }
}
