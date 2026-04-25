package fabrica;
import java.util.concurrent.Semaphore;

class Estacao {
    private final int id;
    private final Fabrica fabrica;
    private final Semaphore[] ferramentas = new Semaphore[5]; // [cite: 14]
    private final Semaphore limit = new Semaphore(4); // Previne Deadlock [cite: 71]

    public Estacao(int id, Fabrica f) {
        this.id = id; this.fabrica = f;
        for (int i = 0; i < 5; i++) ferramentas[i] = new Semaphore(1);
    }

    public void iniciar() {
        for (int i = 0; i < 5; i++) new Funcionario(i, this).start();
    }

    static class Funcionario extends Thread {
        private final int fId;
        private final Estacao est;

        public Funcionario(int id, Estacao e) { this.fId = id; this.est = e; }

        public void run() {
            try {
                while (true) {
                    est.limit.acquire();
                    est.ferramentas[fId].acquire(); // Esquerda [cite: 15]
                    est.ferramentas[(fId + 1) % 5].acquire(); // Direita [cite: 15]

                    est.fabrica.produzir(est.id, fId);

                    est.ferramentas[(fId + 1) % 5].release();
                    est.ferramentas[fId].release();
                    est.limit.release();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) { }
        }
    }
}