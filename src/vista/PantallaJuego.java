package vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import logica.Logica;
import logica.LogicaExperto;
import logica.LogicaIntermedio;
import logica.LogicaPrincipiante;

/**
 *
 * @author cruzj
 */
public class PantallaJuego extends javax.swing.JFrame {

    static {
        CASILLA_BANDERA = new ImageIcon("imagenes/Minesweeper-flag.png");
        CASILLA_MINA = new ImageIcon("imagenes/mina.png");
    }

    public PantallaJuego() {
        laminaPrincipal = new JPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        modoSeleccionado = ModosJuego.PRINCIPIANTE;

        menuJuego = new javax.swing.JMenu("Juego");
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuItemNuevo = new javax.swing.JMenuItem("Nuevo");
        jSeparator = new javax.swing.JPopupMenu.Separator();
        jMenuItemPrincipiante = new javax.swing.JMenuItem("Principiante");
        jMenuItemIntermedio = new javax.swing.JMenuItem("Intermedio");
        jMenuItemExperto = new javax.swing.JMenuItem("Experto");

        setResizable(false);

        jMenuItemNuevo.addActionListener(new EscuchadorModoJuego(jMenuItemNuevo.getText()));
        jMenuItemPrincipiante.addActionListener(new EscuchadorModoJuego(jMenuItemPrincipiante.getText()));
        jMenuItemIntermedio.addActionListener(new EscuchadorModoJuego(jMenuItemIntermedio.getText()));
        jMenuItemExperto.addActionListener(new EscuchadorModoJuego(jMenuItemExperto.getText()));

        menuJuego.add(jMenuItemNuevo);
        menuJuego.add(jSeparator);
        menuJuego.add(jMenuItemPrincipiante);
        menuJuego.add(jMenuItemIntermedio);
        menuJuego.add(jMenuItemExperto);

        jMenuBar1.add(menuJuego);

        setJMenuBar(jMenuBar1);

        add(laminaPrincipal);
        iniciaJuego();
    }
    
    private class EscuchadorModoJuego implements ActionListener {

        public EscuchadorModoJuego(String modoJuego) {
            this.modoJuego = modoJuego;
        }

        public void actionPerformed(ActionEvent e) {
            if(!modoJuego.equals("Nuevo")){
                 modoSeleccionado = ModosJuego.valueOf(modoJuego.toUpperCase());
            }            
            iniciaJuego();
        }
        private String modoJuego;
    }
    
    private void actualizaTablero(){
        for(int i = 0; i < matrizVisual[0].length; i++){
            for(int j = 0; j < matrizVisual.length; j++){
                if(!logicaJuego.isDetonacion()){
                    if(logicaJuego.getTablero()[j][i].isPisado() && matrizVisual[j][i].isEnabled()){
                        matrizVisual[j][i].setEnabled(false);
                        matrizVisual[j][i].setBackground(new Color(201, 201, 201));
                        
                        int nMinas = logicaJuego.getTablero()[j][i].getnMinasAlrededor();
                        if(nMinas > 0){
                            matrizVisual[j][i].setText(logicaJuego.getTablero()[j][i].getnMinasAlrededor() + "");
                        }
                    }
                } else{
                    if(logicaJuego.getTablero()[j][i].isMina()){
                        if(logicaJuego.getTablero()[j][i].isPisado()){
                            matrizVisual[j][i].setBackground(Color.RED);
                        }
                        matrizVisual[j][i].setIcon(CASILLA_MINA);
                    }
                }
            }
        }        
    }


    private class EscuchadorPulsarCasilla implements ActionListener {

        public EscuchadorPulsarCasilla(int columna, int fila) {
            this.columna = columna;
            this.fila = fila;
        }

        public void actionPerformed(ActionEvent e) {
            if(!casillaPulsada){
                casillaPulsada = true;
                logicaJuego.colocarMinas(columna, fila);
                logicaJuego.calculaMinasAlrededor();
            }
            logicaJuego.pisarCasilla(columna, fila);
            
            if(logicaJuego.getCasillasPisadas() == logicaJuego.getnCasillasSinMina()){
                JOptionPane.showMessageDialog(PantallaJuego.this, "¡Has ganado!");
            }
            
            actualizaTablero();            
        }

        private int columna;
        private int fila;
    }

    private class EscuchadorClickDerecho extends MouseAdapter {

        public EscuchadorClickDerecho(int columna, int fila) {
            this.columna = columna;
            this.fila = fila;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                if(logicaJuego.colocarBandera(columna, fila)){
                    matrizVisual[columna][fila].setIcon(CASILLA_BANDERA);
                }else{
                    matrizVisual[columna][fila].setIcon(null);
                }
                
            }
        }

        private int columna;
        private int fila;
    }

    private void iniciaJuego() {
        if (laminaTablero != null) {
            laminaPrincipal.remove(laminaTablero);
        }

        laminaTablero = new JPanel();

        switch (modoSeleccionado) {
            case PRINCIPIANTE:
                logicaJuego = new LogicaPrincipiante();
                break;
            case INTERMEDIO:
                logicaJuego = new LogicaIntermedio();
                break;
            case EXPERTO:
                logicaJuego = new LogicaExperto();
                break;
        }
        matrizVisual = new JButton[logicaJuego.getnColumnas()][logicaJuego.getnFilas()];
        GridLayout layoutCasillas = new GridLayout(matrizVisual[0].length, matrizVisual.length);
        laminaTablero.setLayout(layoutCasillas);
        for (int i = 0; i < matrizVisual[0].length; i++) {
            for (int j = 0; j < matrizVisual.length; j++) {
                matrizVisual[j][i] = new JButton();
                matrizVisual[j][i].setPreferredSize(new Dimension(20, 20));
                matrizVisual[j][i].setBackground(new Color(237, 228, 228));
                matrizVisual[j][i].addActionListener(new EscuchadorPulsarCasilla(j, i));
                matrizVisual[j][i].addMouseListener(new EscuchadorClickDerecho(j, i));
                matrizVisual[j][i].setMargin(new Insets(0, 0, 0, 0));
                // botonCasilla.setBorder(null);
                laminaTablero.add(matrizVisual[j][i]);
            }
        }
        laminaPrincipal.add(laminaTablero);
        casillaPulsada = false;
        pack();
    }

    /*public int pideDificultad() {
        System.out.println("BIENVENIDO AL BUSCAMINAS\n"
                + "\uD83D\uDCA3 \uD83D\uDCA3 \uD83D\uDCA3 \uD83D\uDCA3 \uD83D\uDCA3 \uD83D\uDCA3 \uD83D\uDCA3 \uD83D\uDCA3 \uD83D\uDCA3 \n\n");

        int dificultad = 0;

        while (dificultad < 1 || dificultad > 3) {
            System.out.println("SELECCIONA UNA DIFICULTAD:");
            System.out.println("1.- PRINCIPIANTE");
            System.out.println("2.- INTERMEDIO");
            System.out.println("3.- EXPERTO");
            dificultad = entrada.nextInt();

            if (dificultad < 1 || dificultad > 3) {
                System.out.println("Dificultad incorrecta");
            }
        }
        return dificultad;
    }*/

 /*public void pintaTablero(Logica.Casilla[][] tablero) {
        System.out.println("\nTABLERO JUEGO: ");
        for (int i = 0; i < tablero[0].length; i++) {
            if (i == 0) {
                System.out.print("   ");
                for (int j = 0; j < tablero.length; j++) {
                    System.out.printf("%-4d", j);
                }
                System.out.println();
            }
            System.out.printf("%-3d", i);
            for (int j = 0; j < tablero.length; j++) {
                if (!tablero[j][i].isPisado()) {
                    if (tablero[j][i].isBandera()) {
                        System.out.printf("%-3s", BANDERA);
                    } else {
                        System.out.print(TIERRA + "  ");
                    }
                } else {
                    if (tablero[j][i].getnMinasAlrededor() == 0) {
                        System.out.print(PISADO + "  ");
                    } else if (tablero[j][i].getnMinasAlrededor() == 1) {
                        System.out.print("1  ");
                    } else if (tablero[j][i].getnMinasAlrededor() == 2) {
                        System.out.print("2  ");
                    } else if (tablero[j][i].getnMinasAlrededor() == 3) {
                        System.out.print("3  ");
                    } else if (tablero[i][j].getnMinasAlrededor() == 4) {
                        System.out.print("4  ");
                    } else if (tablero[j][i].getnMinasAlrededor() == 5) {
                        System.out.print("5  ");
                    } else if (tablero[j][i].getnMinasAlrededor() == 6) {
                        System.out.print("6  ");
                    } else if (tablero[j][i].getnMinasAlrededor() == 7) {
                        System.out.print("7  ");
                    } else if (tablero[j][i].getnMinasAlrededor() == 8) {
                        System.out.print("8  ");
                    }
                }
            }
            System.out.println();
        }
    }*/

 /*public int[] pideCasilla() {
        System.out.println("Introduce una coordenada X: ");
        int x = entrada.nextInt();
        System.out.println("Introduce una coordenada Y: ");
        int y = entrada.nextInt();
        return new int[]{x, y};
    }*/

 /*public int pideAccion() {
        System.out.println("ELIGE UNA ACCIÓN: ");
        System.out.println("1.- Revelar");
        System.out.println("2.- Colocar bandera");
        return entrada.nextInt();
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        laminaPrincipal = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        laminaPrincipal.setLayout(new java.awt.BorderLayout());
        getContentPane().add(laminaPrincipal, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PantallaJuego().setVisible(true);
            }
        });
    }

    public enum ModosJuego {
        PRINCIPIANTE, INTERMEDIO, EXPERTO
    }

    private boolean casillaPulsada;
    private ModosJuego modoSeleccionado;
    // Variables declaration - do not modify                     
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemExperto;
    private javax.swing.JMenuItem jMenuItemIntermedio;
    private javax.swing.JMenuItem jMenuItemNuevo;
    private javax.swing.JMenuItem jMenuItemPrincipiante;
    private javax.swing.JPopupMenu.Separator jSeparator;
    private javax.swing.JMenu menuJuego;
    private JPanel laminaTablero;
    private Logica logicaJuego;
    private static final ImageIcon CASILLA_BANDERA;
    private static final ImageIcon CASILLA_MINA;
    private JButton matrizVisual[][];
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel laminaPrincipal;
    // End of variables declaration//GEN-END:variables
}
