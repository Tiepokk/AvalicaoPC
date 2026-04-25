package loja;

import comum.Veiculo;
import java.util.concurrent.Semaphore;

public class EstoqueLoja {
    // Capacidade da esteira da loja conforme lógica de buffer circular
    private final int CAPACIDADE = 20;
    private final Veiculo[] esteiraLoja = new Veiculo[CAPACIDADE];

    private int head = 0; // Ponteiro para retirada (venda)
    private int tail = 0; // Ponteiro para inserção (recebimento)

    // Semáforos para controle de concorrência e sincronização
    private final Semaphore vagasVazias = new Semaphore(CAPACIDADE);
    private final Semaphore itensDisponiveis = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1); // Garante integridade dos índices

    /**
     * Adiciona um veículo vindo da fábrica na esteira da loja.
     * Retorna a posição onde o veículo foi inserido para fins de log.
     */
    public int adicionarVeiculo(Veiculo v) throws InterruptedException {
        vagasVazias.acquire(); // Espera se a esteira da loja estiver lotada
        mutex.acquire();

        int posicaoInsercao = tail;
        esteiraLoja[tail] = v;
        tail = (tail + 1) % CAPACIDADE;

        mutex.release();
        itensDisponiveis.release(); // Sinaliza que um veículo está pronto para venda

        return posicaoInsercao;
    }

    /**
     * Retira um veículo da esteira para entregar ao cliente.
     * Se não houver veículos, a thread do cliente fica bloqueada aqui.
     */
    public Veiculo venderVeiculo() throws InterruptedException {
        itensDisponiveis.acquire(); // Requisito: Cliente espera se não houver veículo
        mutex.acquire();

        Veiculo v = esteiraLoja[head];
        esteiraLoja[head] = null;
        head = (head + 1) % CAPACIDADE;

        mutex.release();
        vagasVazias.release(); // Libera espaço para a loja pedir mais à fábrica

        return v;
    }
}