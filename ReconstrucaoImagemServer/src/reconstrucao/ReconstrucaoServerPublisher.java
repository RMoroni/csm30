package reconstrucao;

import javax.xml.ws.Endpoint;

public class ReconstrucaoServerPublisher{
   
    public static void init_cgne(){
        CGNE cgne = new CGNE();
        Thread thread = new Thread(cgne);
        
        /*
        Por padrão, prioridade normal, caso seja
        necessário que a reconstrução seja mais rápida,
        setar prioridade no máximo, caso seja necessário
        que o muitos clientes consigam fazer requisição
        setar no minímo.
        */
        //thread.setPriority(Thread.MAX_PRIORITY);
        //thread.setPriority(thread.MIN_PRIORITY);
        
        thread.start();
    }
    public static void main(String[] args)
  {
    /*
    Uma ideia interessante (mas não precisa pra essa matéria)
    seria balancear a prioridade das threads. Cria uma thread
    para o cgne e uma para o web service. De acordo com a demanda
    de clientes aumentar/reduzir as prioridades das threads.
    */
    System.out.println("Web Service Iniciado...");
    init_cgne();
    Endpoint.publish("http://0.0.0.0:9876/recon",
    new ReconstrucaoServerImpl());
  }
}
