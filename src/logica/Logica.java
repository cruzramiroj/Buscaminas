package logica;

public abstract class Logica {

    public Logica(int ancho, int altura, int nMinas) {
        this.nMinas = nMinas;
        casillasPisadas = 0;
        nFilas = altura;
        nColumnas = ancho;
        tablero = new Casilla[ancho][altura];
        nCasillasSinMina = ancho * altura - nMinas;
        detonacion = false;
        inicializarTablero();
    }

    public int getnFilas() {
        return nFilas;
    }

    public int getnColumnas() {
        return nColumnas;
    }

    public int getCasillasPisadas() {
        return casillasPisadas;
    }

    public Casilla[][] getTablero() {
        return tablero;
    }

    public boolean isDetonacion() {
        return detonacion;
    }

    public int getnCasillasSinMina() {
        return nCasillasSinMina;
    }

    public boolean colocarBandera(int x, int y) {
        if (!tablero[x][y].pisado) {
            tablero[x][y].bandera = !tablero[x][y].bandera;
        }
        return tablero[x][y].bandera;
    }

    private void inicializarTablero() {
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[0].length; j++) {
                tablero[i][j] = new Casilla();
            }
        }
    }

    public void calculaMinasAlrededor() {
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[i].length; j++) {
                if (!(tablero[i][j].mina)) {
                    int contador = 0;

                    for (int posX = -1; posX <= 1; posX++) {
                        int incrementoY;
                        if (posX == 0) {
                            incrementoY = 2;
                        } else {
                            incrementoY = 1;
                        }
                        for (int posY = -1; posY <= 1; posY += incrementoY) {
                            if (!(i + posX < 0 || i + posX >= tablero.length || j + posY < 0
                                    || j + posY >= tablero[0].length) && tablero[i + posX][j + posY].mina) {
                                contador++;
                            }
                        }
                    }
                    tablero[i][j].nMinasAlrededor = contador;
                }

            }
        }
    }

    private boolean comprobacionAlrededoresPulsado(int xPulsado, int yPulsado, int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (x + i == xPulsado && y + j == yPulsado) {
                    return true;
                }
            }
        }
        return false;
    }

    public void colocarMinas(int x, int y) {
        for (int i = 0; i < nMinas; i++) {
            int xAleatorio, yAleatorio;
            do {
                xAleatorio = (int) (Math.random() * tablero.length);
                yAleatorio = (int) (Math.random() * tablero[0].length);
            } while (tablero[xAleatorio][yAleatorio].mina || comprobacionAlrededoresPulsado(x, y, xAleatorio, yAleatorio));
            tablero[xAleatorio][yAleatorio].mina = true;
        }
    }

    public void pisarCasilla(int x, int y) {
        if (tablero[x][y].pisado) {
            //Pisar todas las casillas de alrededor menos las que tengan bandera
        } else if (!tablero[x][y].bandera) {
            tablero[x][y].pisado = true;
            casillasPisadas++;
            if (tablero[x][y].mina) {
                detonacion = true;
            } else {
                revelaAlrededores(x, y);
            }
        }
    }

    private void revelaAlrededores(int x, int y) {
        for (int posX = -1; posX <= 1; posX++) {
            int incrementoY;
            if (posX == 0) {
                incrementoY = 2;
            } else {
                incrementoY = 1;
            }
            for (int posY = -1; posY <= 1; posY += incrementoY) {
                int vx, vy;
                vx = x + posX;
                vy = y + posY;
                if (!(vx < 0 || vx >= tablero.length || vy < 0 || vy >= tablero[0].length) && !tablero[vx][vy].bandera
                        && !tablero[vx][vy].pisado && !tablero[vx][vy].mina) {
                    tablero[vx][vy].pisado = true;
                    casillasPisadas++;
                    if (tablero[vx][vy].nMinasAlrededor == 0) {
                        revelaAlrededores(vx, vy);
                    }
                }
            }
        }
    }

    public class Casilla {

        protected Casilla() {
            pisado = false;
            nMinasAlrededor = 0;
            bandera = false;
            mina = false;
        }

        public boolean isPisado() {
            return pisado;
        }

        public int getnMinasAlrededor() {
            return nMinasAlrededor;
        }

        public boolean isBandera() {
            return bandera;
        }

        public boolean isMina() {
            return mina;
        }

        private boolean bandera;
        private boolean mina;
        private boolean pisado;
        private int nMinasAlrededor;
    }

    private int nCasillasSinMina;
    private final int nMinas;
    private int casillasPisadas;
    private final Casilla[][] tablero;
    private boolean detonacion = false;
    private int nFilas;
    private int nColumnas;
}
