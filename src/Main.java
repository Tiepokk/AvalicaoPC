import fabrica.Fabrica;
import loja.LojaMain;
import cliente.Cliente;

public class Main {
    public static void main(String[] args) {
        // 1. Inicia a Fábrica em uma thread separada
        new Thread(() -> {
            System.out.println("[SISTEMA] Iniciando Fábrica...");
            Fabrica.main(new String[]{});
        }).start();

        // Pequena pausa para garantir que o servidor da fábrica suba antes das lojas
        aguardar(2000);

        // 2. Inicia as 3 Lojas (simulando os 3 processos remotos)
        for (int i = 1; i <= 3; i++) {
            final String idLoja = String.valueOf(i);
            new Thread(() -> {
                System.out.println("[SISTEMA] Iniciando Loja " + idLoja + "...");
                LojaMain.main(new String[]{idLoja});
            }).start();
        }

        //  Pausa para as lojas se conectarem à fábrica
        aguardar(3000);

        // 3. Inicia os Clientes
        new Thread(() -> {
            System.out.println("[SISTEMA] Iniciando 20 Clientes...");
            Cliente.main(new String[]{});
        }).start();

        System.out.println("[SISTEMA] Todo o ecossistema está rodando!");
    }

    private static void aguardar(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}