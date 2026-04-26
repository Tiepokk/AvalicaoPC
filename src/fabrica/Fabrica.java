package fabrica;

import veiculo.Veiculo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Fabrica {
    private final Semaphore estoquePecas = new Semaphore(500);
    private final Semaphore esteiraSolicitacao = new Semaphore(5);

    private final Veiculo[] esteiraCircular = new Veiculo[40];
    private int head = 0;
    private int tail = 0;
    private int contadorGlobal = 1;

    private final Semaphore vagasEsteira = new Semaphore(40);
    private final Semaphore itensEsteira = new Semaphore(0);
    private final Semaphore mutexEsteira = new Semaphore(1);

    public void solicitarPeca(int estacaoId) throws InterruptedException {
        esteiraSolicitacao.acquire();
        estoquePecas.acquire();
        Thread.sleep(100);
        esteiraSolicitacao.release();
    }

    public void produzir(int estId, int funcId) throws InterruptedException {
        solicitarPeca(estId);

        String cor = (contadorGlobal % 3 == 1) ? "R" : (contadorGlobal % 3 == 2) ? "G" : "B";
        String tipo = (contadorGlobal % 2 == 1) ? "SUV" : "SEDAN";

        vagasEsteira.acquire();
        mutexEsteira.acquire();

        int posicao = tail;
        Veiculo v = new Veiculo(contadorGlobal++, cor, tipo, estId, funcId);
        v.setPosFabrica(posicao);
        esteiraCircular[tail] = v;

        System.out.printf("PRODUÇÃO: " + v + " | Pos Esteira: " + posicao + "%n");

        tail = (tail + 1) % 40;

        mutexEsteira.release();
        itensEsteira.release();
    }

    public void iniciarServidor() {
        try (ServerSocket server = new ServerSocket(12345)) {
            while (true) {
                Socket lojaSocket = server.accept();
                new Thread(() -> enviarParaLoja(lojaSocket)).start();
            }
        } catch (IOException e) {
            System.out.printf("Erro no servidor: " + e.getMessage());
        }
    }

    private void enviarParaLoja(Socket s) {
        try {
            ObjectInputStream inId = new ObjectInputStream(s.getInputStream());
            int idLojaCompradora = inId.readInt();

            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            while (true) {
                itensEsteira.acquire();
                mutexEsteira.acquire();

                Veiculo v = esteiraCircular[head];
                esteiraCircular[head] = null;
                int posOriginal = head;
                head = (head + 1) % 40;

                mutexEsteira.release();
                vagasEsteira.release();

                v.setLojaId(idLojaCompradora);
                out.writeObject(v);
                out.flush();

                System.out.printf("LOG VENDA LOJA: %s | Loja: %d | Saiu da Pos Fabrica: %d%n",
                        v.toString(), idLojaCompradora, posOriginal);
            }
        } catch (Exception _) { }
    }

    public static void main(String[] args) {
        Fabrica fabrica = new Fabrica();

        for (int i = 1; i <= 4; i++) {
            new Estacao(i, fabrica).iniciar();
        }

        fabrica.iniciarServidor();
    }
}