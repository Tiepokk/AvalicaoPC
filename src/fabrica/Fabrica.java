package fabrica;

import comum.LogService;
import comum.Veiculo;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Fabrica {
    private final Semaphore estoquePecas = new Semaphore(500); //
    private final Semaphore esteiraSolicitacao = new Semaphore(5); //

    private final Veiculo[] esteiraCircular = new Veiculo[40]; //
    private int head = 0;
    private int tail = 0;
    private int contadorGlobal = 1;

    private final Semaphore vagasEsteira = new Semaphore(40);
    private final Semaphore itensEsteira = new Semaphore(0);
    private final Semaphore mutexEsteira = new Semaphore(1);

    public void solicitarPeca(int estacaoId) throws InterruptedException {
        esteiraSolicitacao.acquire(); //
        estoquePecas.acquire(); //
        Thread.sleep(100);
        esteiraSolicitacao.release();
    }

    public void produzir(int estId, int funcId) throws InterruptedException {
        solicitarPeca(estId);

        // Lógica de alternância conforme requisitos
        String cor = (contadorGlobal % 3 == 1) ? "R" : (contadorGlobal % 3 == 2) ? "G" : "B";
        String tipo = (contadorGlobal % 2 == 1) ? "SUV" : "SEDAN";

        vagasEsteira.acquire();
        mutexEsteira.acquire();

        int posicao = tail;
        Veiculo v = new Veiculo(contadorGlobal++, cor, tipo, estId, funcId); // [cite: 40, 43, 44]
        v.setPosFabrica(posicao);
        esteiraCircular[tail] = v;

        // Log de Produção [cite: 38]
        LogService.gravar("log_producao_fabrica.txt", "PRODUÇÃO: " + v + " | Pos Esteira: " + posicao); // [cite: 45]

        tail = (tail + 1) % 40;

        mutexEsteira.release();
        itensEsteira.release();
    }

    public void iniciarServidor() {
        try (ServerSocket server = new ServerSocket(12345)) { // [cite: 67]
            while (true) {
                Socket lojaSocket = server.accept();
                new Thread(() -> enviarParaLoja(lojaSocket)).start();
            }
        } catch (IOException e) {
            LogService.gravar("erros_fabrica.txt", "Erro no servidor: " + e.getMessage());
        }
    }

    private void enviarParaLoja(Socket s) {
        try (ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())) {
            while (true) {
                itensEsteira.acquire();
                mutexEsteira.acquire();

                Veiculo v = esteiraCircular[head];
                esteiraCircular[head] = null;
                int posOriginal = head;
                head = (head + 1) % 40;

                mutexEsteira.release();
                vagasEsteira.release();

                out.writeObject(v); // [cite: 52]

                // Log de Venda para Loja [cite: 46]
                LogService.gravar("log_venda_para_loja.txt", "VENDA PARA LOJA: " + v + " | Saiu da Pos: " + posOriginal);
            }
        } catch (Exception e) {
            LogService.gravar("erros_fabrica.txt", "Conexão com loja encerrada.");
        }
    }

    public static void main(String[] args) {
        Fabrica fabrica = new Fabrica();

        // Inicia as 4 estações [cite: 13]
        for (int i = 1; i <= 4; i++) {
            new Estacao(i, fabrica).iniciar();
        }

        // Inicia o serviço de rede para as lojas [cite: 63]
        fabrica.iniciarServidor();
    }
}