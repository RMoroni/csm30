# csm30 Desenvolvimento Integrado de Sistemas (2019/1)

- banco de dados:

tabela cliente:
    id
    login
    senha
	
tabela imagem:
    id
    cliente_id -> FK
    data_hora_inicio -> timestamp
    data_hora_fim -> ||
    altura -> pixels
    largura -> pixels
    iteracoes -> unsigned int
    status -> char(1)
    caminho -> text

- passos:

# Cliente

Autenticar
Carregar o G
    Controle de Ganho
    Envia para Servidor
Listar Imagens
    Seleciona Imagem Reconstruída
    Baixa

# Servidor

Receber solicitação do cliente
    Coloca na fila (FIFO)
Pega próximo da fila
    Busca o modelo
    Reconstroi
    Salva imagem

- descrição do professor

1. Atividade 1

Implementar uma aplicação cliente com as seguintes características:

    Carregar a partir de um arquivo local um sinal de ultrassom;
    Realizar o controle de ganho de sinal através do procedimento descrito abaixo;
    Enviar o sinal tratado para um servidor de reconstrução de imagens;
    Carregar a partir do servidor todas as imagens reconstruídas por um usuário.
    Selecionar uma ou mais imagens e salvar localmente.
    
    Controle de Ganho:
        Colocar o sinal em uma matriz (794X64)
        Somar um valor para cada linha (inteira), exemplo: +1, +2, +3...
        Reshape para formato original (matriz coluna)
        Enviar para o Algoritmo

Cada imagem deverá conter no mínimo os seguintes dados:

    Identificação do usuário;
    Data e hora do início da reconstrução;
    Data e hora do término da reconstrução;
    Tamanho em pixels;
    O número de iterações executadas.

2. Atividade 2
Implementar um servidor para reconstrução de imagens:

    Receber os dados para reconstrução;
    Carregar o modelo de reconstrução de acordo com os parâmetros recebidos;
    Executar o algoritmo de reconstrução;
    Executar até que a norma L2 do resíduo (r) seja menor do que 1e10-4 .
    Salvar o resultado.
