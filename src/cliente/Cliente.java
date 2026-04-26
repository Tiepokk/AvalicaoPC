package cliente;

import veiculo.Veiculo;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cliente extends Thread {
    private final int clienteId;
    private final List<Veiculo> garagem = new ArrayList<>();
    private final Random random = new Random();

    public Cliente(int id) {
        this.clienteId = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int lojaAlvo = random.nextInt(3) + 1;
                int portaLoja = 20000 + lojaAlvo;

                try (Socket socket = new Socket("localhost", portaLoja)) {
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    Veiculo veiculoComprado = (Veiculo) in.readObject();

                    if (veiculoComprado != null) {
                        garagem.add(veiculoComprado);

                        System.out.printf("COMPRA_CLIENTE - Cliente ID: %d | %s | Loja: %d | Total na Garagem: %d%n",
                                clienteId, veiculoComprado.toString(), lojaAlvo, garagem.size());
                    }
                } catch (Exception _) { }

                Thread.sleep(random.nextInt(3000) + 1000);

            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        System.out.printf("Iniciando 20 threads de clientes...%n");

        for (int i = 1; i <= 20; i++) {
            new Cliente(i).start();
        }
    }
}