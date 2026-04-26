package loja;

import veiculo.Veiculo;
import java.util.concurrent.Semaphore;

public class EstoqueLoja {
    private final int CAPACIDADE = 20;
    private final Veiculo[] esteiraLoja = new Veiculo[CAPACIDADE];

    private int head = 0;
    private int tail = 0;

    private final Semaphore vagasVazias = new Semaphore(CAPACIDADE);
    private final Semaphore itensDisponiveis = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);

    public int adicionarVeiculo(Veiculo v) throws InterruptedException {
        vagasVazias.acquire();
        mutex.acquire();

        int posicaoInsercao = tail;
        esteiraLoja[tail] = v;
        tail = (tail + 1) % CAPACIDADE;

        mutex.release();
        itensDisponiveis.release();

        return posicaoInsercao;
    }

    public Veiculo venderVeiculo() throws InterruptedException {
        itensDisponiveis.acquire();
        mutex.acquire();

        Veiculo v = esteiraLoja[head];
        esteiraLoja[head] = null;
        head = (head + 1) % CAPACIDADE;

        mutex.release();
        vagasVazias.release();

        return v;
    }
}