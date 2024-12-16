import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner teclado = new Scanner(System.in);

        char caractereJogador = obterCaractereUsuario(teclado);
        char caractereComputador = obterCaractereComputador(caractereJogador);

        boolean jogadorComeca = sortearValorBooleano();

        System.out.println("\n" + (jogadorComeca ? "O jogador começa!" : "O computador começa!") + "\n");

        char[][] tabuleiro = inicializarTabuleiro();

        while (true) {
            if (jogadorComeca) {
                tabuleiro = processarVezUsuario(teclado, tabuleiro, caractereJogador);
            } else {
                tabuleiro = processarVezComputador(tabuleiro, caractereComputador);
            }

            exibirTabuleiro(tabuleiro);

            if (teveGanhador(tabuleiro, caractereJogador)) {
                exibirVitoriaUsuario();
                break;
            } else if (teveGanhador(tabuleiro, caractereComputador)) {
                exibirVitoriaComputador();
                break;
            } else if (teveEmpate(tabuleiro)) {
                exibirEmpate();
                break;
            }

            jogadorComeca = !jogadorComeca;
        }

        teclado.close();
    }

    public static char obterCaractereUsuario(Scanner teclado) {
        char simbolo;

        System.out.print("Escolha seu símbolo (X ou O): ");
        simbolo = teclado.next().charAt(0);

        teclado.nextLine();

        while (simbolo != 'X' && simbolo != 'O' && simbolo != 'x' && simbolo != 'o') {
            System.out.println("Símbolo inválido! Escolha 'X' ou 'O'.");
            System.out.print("Escolha novamente: ");
            simbolo = teclado.next().charAt(0);
            teclado.nextLine();
        }

        return Character.toUpperCase(simbolo);
    }

    public static char obterCaractereComputador(char simboloJogador) {
        return (simboloJogador == 'X') ? 'O' : 'X';
    }

    public static boolean sortearValorBooleano() {
        return new Random().nextBoolean();
    }

    public static char[][] inicializarTabuleiro() {
        char[][] tabuleiro = new char[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tabuleiro[i][j] = ' ';
            }
        }

        exibirTabuleiro(tabuleiro);
        return tabuleiro;
    }

    public static void exibirTabuleiro(char[][] tabuleiro) {
        limparTela();
        System.out.println("Tabuleiro:");
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                System.out.print(tabuleiro[i][j]);
                if (j < tabuleiro[i].length - 1)
                    System.out.print(" | ");
            }
            System.out.println();
            if (i < tabuleiro.length - 1) {
                System.out.println("--+---+--");
            }
        }
    }

    public static void limparTela() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Não foi possível limpar a tela.");
        }
    }

    public static char[][] processarVezUsuario(Scanner teclado, char[][] tabuleiro, char caractereUsuario) {
        System.out.println("Sua vez de jogar!");
        int[] jogada = obterJogadaUsuario(retornarPosicoesLivres(tabuleiro), teclado);
        return retornarTabuleiroAtualizado(tabuleiro, jogada, caractereUsuario);
    }

    public static char[][] processarVezComputador(char[][] tabuleiro, char caractereComputador) {
        System.out.println("Vez do computador!");
        int[] jogada = obterJogadaComputador(retornarPosicoesLivres(tabuleiro));
        return retornarTabuleiroAtualizado(tabuleiro, jogada, caractereComputador);
    }

    public static int[] obterJogadaUsuario(String posicoesLivres, Scanner teclado) {
        while (true) {
            try {
                System.out.println("Digite linha e coluna separados por espaço:");
                String entrada = teclado.nextLine().trim();
                String[] valores = entrada.split("\\s+");

                if (valores.length != 2) {
                    System.out.println("Entrada inválida! Digite dois números.");
                    continue;
                }

                int linha = Integer.parseInt(valores[0]) - 1;
                int coluna = Integer.parseInt(valores[1]) - 1;

                if (linha < 0 || linha > 2 || coluna < 0 || coluna > 2) {
                    System.out.println("Valores fora dos limites! Escolha entre 1 e 3.");
                    continue;
                }

                int[] jogada = { linha, coluna };

                if (jogadaValida(jogada, posicoesLivres)) {
                    return jogada;
                } else {
                    System.out.println("Posição ocupada! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite números.");
            }
        }
    }

    public static int[] obterJogadaComputador(String posicoesLivres) {
        String[] jogadasLivres = posicoesLivres.split(";");

        if (jogadasLivres.length == 0) {
            throw new IllegalStateException("Sem posições livres!");
        }

        Random random = new Random();
        String jogada = jogadasLivres[random.nextInt(jogadasLivres.length)];
        return converterJogadaStringParaVetorInt(jogada);
    }

    public static boolean jogadaValida(int[] jogada, String posicoesLivres) {
        String posicao = (jogada[0] + 1) + " " + (jogada[1] + 1);
        return posicoesLivres.contains(posicao);
    }

    public static int[] converterJogadaStringParaVetorInt(String jogada) {
        String[] valores = jogada.split("\\s+");
        return new int[] { Integer.parseInt(valores[0]) - 1, Integer.parseInt(valores[1]) - 1 };
    }

    public static char[][] retornarTabuleiroAtualizado(char[][] tabuleiro, int[] jogada, char caractereJogador) {
        tabuleiro[jogada[0]][jogada[1]] = caractereJogador;
        return tabuleiro;
    }

    public static boolean teveGanhador(char[][] tabuleiro, char caractereJogador) {
        return teveGanhadorLinha(tabuleiro, caractereJogador) ||
                teveGanhadorColuna(tabuleiro, caractereJogador) ||
                teveGanhadorDiagonalPrincipal(tabuleiro, caractereJogador) ||
                teveGanhadorDiagonalSecundaria(tabuleiro, caractereJogador);
    }

    public static boolean teveEmpate(char[][] tabuleiro) {
        return retornarPosicoesLivres(tabuleiro).isEmpty();
    }

    public static boolean teveGanhadorLinha(char[][] tabuleiro, char caractereJogador) {
        for (int p = 0; p < 3; p++) {
            if (tabuleiro[p][0] == caractereJogador &&
                    tabuleiro[p][1] == caractereJogador &&
                    tabuleiro[p][2] == caractereJogador) {
                return true;
            }
        }
        return false;
    }

    public static boolean teveGanhadorColuna(char[][] tabuleiro, char caractereJogador) {
        for (int c = 0; c < 3; c++) {
            if (tabuleiro[0][c] == caractereJogador &&
                    tabuleiro[1][c] == caractereJogador &&
                    tabuleiro[2][c] == caractereJogador) {
                return true;
            }
        }
        return false;
    }

    public static boolean teveGanhadorDiagonalPrincipal(char[][] tabuleiro, char caractereJogador) {
        return tabuleiro[0][0] == caractereJogador &&
                tabuleiro[1][1] == caractereJogador &&
                tabuleiro[2][2] == caractereJogador;
    }

    public static boolean teveGanhadorDiagonalSecundaria(char[][] tabuleiro, char caractereJogador) {
        return tabuleiro[0][2] == caractereJogador &&
                tabuleiro[1][1] == caractereJogador &&
                tabuleiro[2][0] == caractereJogador;
    }

    public static String retornarPosicoesLivres(char[][] tabuleiro) {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tabuleiro[i][j] == ' ') {
                    resultado.append((i + 1)).append(" ").append((j + 1)).append(";");
                }
            }
        }

        return resultado.toString();
    }

    public static void exibirVitoriaComputador() {
        System.out.println("O computador venceu!");
        System.out.println("     _______");
        System.out.println("    |       |");
        System.out.println("    | ^   ^ |");
        System.out.println("    |   u   |");
        System.out.println("    |  \\_/  |");
        System.out.println("    |_______|");
        System.out.println("   /         \\");
        System.out.println("  /___________\\");
    }

    public static void exibirVitoriaUsuario() {
        System.out.println("O usuário venceu!");
        System.out.println("   O     O     O     O     O");
        System.out.println("  /|\\   /|\\   /|\\   /|\\   /|\\");
        System.out.println("  / \\   / \\   / \\   / \\   / \\");
        System.out.println("  \\o/   \\o/   \\o/   \\o/   \\o/");
        System.out.println("   |     |     |     |     |");
        System.out.println("  / \\   / \\   / \\   / \\   / \\");
        System.out.println("Parabéns! Você Ganhou!");
    }

    public static void exibirEmpate() {
        System.out.println("Ocorreu empate! Jogue novamente para ter um vencedor!!!");
        System.out.println("   _______       _______");
        System.out.println("  |       |     |       |");
        System.out.println("  |   0   |  X  |   0   |");
        System.out.println("  |_______|     |_______|");
    }
}
