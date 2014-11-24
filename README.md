1. Objetivo:
O objetivo deste trabalho é praticar os conceitos de programação
paralela (threads) e redes de computadores (sockets).

2. Especificação:
O trabalho será desenvolvido em duas partes: servidor e cliente. Ambas
as partes deverão obedecer aos respectivos protocolos.
Servidor:
· O servidor deverá aceitar várias conexões através de uma porta
configurável através da linha de comando, especificado com o
parâmetro –p. Em caso de ausência do parâmetro, a porta padrão
deverá ser 8885.
· Cada cliente que conectar ao servidor receberá um identificador
inteiro único. O identificador não precisa ser reaproveitado, ou
seja, quando um cliente desconectar, não é necessário passar o seu
identificador a um novo cliente.
· Todas as mensagens recebidas pelo servidor deverão ser impressas no
console. Não há necessidade de interface gráfica para o servidor.
Cliente:
· O cliente deverá possuir em sua tela de abertura, uma opção para o
usuário digitar o endereço e porta do servidor ao deseja conectar-se
e também um campo com o apelido que usará no servidor.
· Depois de conectado, o usuário terá uma tela com um mínimo de três
seções: mensagens recebidas do servidor, mensagem a ser enviada e
lista de usuários conectados.
· O usuário poderá escolher entre enviar mensagens globais ou
privadas, ou seja, para todos os usuários ou para apenas um usuário.
· As mensagens recebidas deverão ser identificadas com globais ou
privadas na tela. A forma de identificação fica a cargo do
programador, mas o usuário deve ser capaz de distinguir claramente
entre os dois tipos de mensagens.
· A lista de usuários conectados ao servidor deverá ser
automaticamente atualizada a cada 60 segundos.

3. Protocolo:
· As mensagens serão enviadas/recebidas através da utilização dos
protocolos TCP/IP.
· O protocolo do chat deverá possuir o seguinte formato:
+---------+------------+-------------------+----------+

| serviço | tam. dados | dados 			   | checksum |

+---------+------------+-------------------+----------+

| 1 byte  | 2 bytes    | (tam.dados) bytes | 2 bytes  |

+---------+------------+-------------------+----------+
· O campo serviço possui comprimento de 1 byte e designará qual
serviço será executado no cliente/servidor. A lista de serviços será
apresentada adiante.
· O campo tam. dados informa a quantidade de bytes existentes no
próximo campo da mensagem.
· O campo dados possui os dados necessários para a realização do
serviço.
· O campo checksum possui dois bytes de validação para garantir que a
mensagem recebida não foi violada. O valor do checksum é calculado
como a soma de todos os bytes da mensagem (serviço, tamanho e
dados).

4. Serviços:
Serviço 0x01 - Olá
O cliente, após conectar-se ao servidor, deverá enviar uma mensagem de
olá informando o apelido que deseja utilizar na rede.
O servidor consultará em sua lista de clientes se existe algum cliente
com o mesmo apelido conectado. Em caso negativo, o servidor responderá
ao cliente uma mensagem de olá com o seu identificador no servidor.
Caso o apelido já esteja em uso, o servidor responderá negativamente
(serviço 0x7F). Nesse caso, o cliente deverá escolher outro apelido e
enviar a mensagem de olá novamente.
O servidor só aceitará troca de mensagens com o cliente após aceitar o
apelido escolhido.
Caractere....: 2 bytes
Apelido......: Vetor de caracteres
Identificador: Inteiro
Exemplos:
// Olá servidor, meu apelido é “Leandro”
Cliente -> Servidor : 0x01 – 0x000E – Leandro – CHECKSUM
// Olá “Leandro”, seu identificador é 3
Servidor -> Cliente : 0x01 – 0x0004 – 0x00000003 – CHECKSUM (Positiva)
Serviço 0x02 – Mudar apelido
O cliente possui a liberdade de mudar seu apelido a qualquer momento.
Para isso deverá enviar uma mensagem de mudança de apelido com o seu
novo apelido. Caso o servidor aceite o novo apelido, uma mensagem de
mudança de apelido será enviada a todos os clientes conectados com o
identificador do cliente que trocou o apelido seguido do novo apelido.
Caso o apelido já esteja em uso, o servidor responderá negativamente
(serviço 0x7F). Nesse caso, o cliente deverá escolher outro apelido e
enviar a mensagem de olá novamente.
Caractere....: 2 bytes
Apelido......: Vetor de caracteres
Identificador: Inteiro
Exemplos:
// Mude meu apelido para “Leandro Maia”
Cliente -> Servidor: 0x02 – 0x0018 – Leandro Maia – CHECKSUM
// O cliente de identificador 3 mudou o apelido para “Leandro Maia”
Servidor -> Cliente: 0x02 – 0x001C – 0x00000003 Leandro Maia –
CHECKSUM
Serviço 0x03 – Clientes conectados
Para que o cliente mantenha-se atualizado, este serviço solicita ao
servidor a lista de identificadores de todos os clientes a ele
conectados.
Identificador: Inteiro
Exemplos:
// Quais clientes estão conectados ?
Cliente -> Servidor: 0x03 – 0x0000 – CHECKSUM
// Cliente 3, 4 e 7
Servidor -> Cliente: 0x03 – 0x000C – 0x00000003 0x00000004 0x00000007
– CHECKSUM
Serviço 0x04 – Requisitar apelido
A fim de manter os apelidos atualizados, esse serviço solicita ao
servidor o apelido do cliente referente ao identificador.
Caso o servidor não encontre o cliente com o respectivo identificador,
ele responderá negativamente (serviço 0x7F)
Caractere....: 2 bytes
Apelido......: Vetor de caracteres
Identificador: Inteiro
// Qual o apelido do cliente 4 ?
Cliente -> Servidor: 0x04 – 0x0004 – 0x00000004 – CHECKSUM
// É Humberto Honda
Servidor -> Cliente: 0x04 – 0x001C – Humberto Honda – CHECKSUM
Serviço 0x05 – Enviar mensagem
O cliente pode enviar dois tipos de mensagem: global ou privada. A
diferença entre as mensagens é que a mensagem global será recebida
pelo servidor e distribuída a todos os clientes, inclusive ao cliente
que enviou a mensagem, já a mensagem privada será recebida pelo
servidor e entregue apenas ao próprio cliente e ao cliente
especificado na mensagem.
Caractere....: 2 bytes
Mensagem.....: Vetor de caracteres
Identificador: Inteiro
// Cliente 7 envia mensagem global “Quer tc ?” (ID = 0x000000)
Cliente -> Servidor: 0x05 – 0x001A – 0x00000000 0x00000000 – Quer tc ?
– CHECKSUM
// Servidor envia para todos informando que veio do cliente 7
Servidor -> Cliente: 0x05 – 0x001A – 0x00000007 0x00000000 – Quer tc ?
- CHECKSUM
// Cliente 7 envia mensagem privada “De onde tc ?” para o cliente 5
Cliente -> Servidor: 0x05 – 0x0020 – 0x00000000 0x00000005 – De onde
tc ? – CHECKSUM
// Servidor envia para 5 e 7 a mensagem
Servidor -> Cliente: 0x05 – 0x0020 – 0x00000007 0x00000005 – Quer tc ?
- CHECKSUM
Serviço 0x0A – Tchau
Antes de finalizar a conexão com o servidor, o cliente avisa que irá
encerrar a conexão. A conexão só deverá ser fechada quando o cliente
receber a mensagem de confirmação do servidor. O servidor envia a
mensagem de tchau para todos os clientes conectados.
Identificador: Inteiro
// Cliente 3 informa que irá sair
Cliente -> Servidor: 0x0A – 0x0000 – CHECKSUM
// Servidor avisa a todos os clientes que o 3 está saindo
Servidor -> Cliente: 0x0A – 0x0004 – 0x00000003 - CHECKSUM
Serviço 0x7F – Serviço Negado
Quando uma requisição de serviço é negada por qualquer motivo, o
servidor envia ao cliente uma mensagem de serviço negado. A mensagem
possui o identificador do serviço negado. Quando o servidor recebe uma
mensagem com o checksum errado, é enviada uma mensagem de serviço
negado com o identificador de serviço especial 0xFF.
IMPORTANTE: O cliente, ao receber uma mensagem com o checksum
inválido, simplesmente ignora a mensagem.
Serviço: 1 byte
Exemplo:
// Mude meu apelido para “Leandro Maia”
Cliente -> Servidor: 0x02 – 0x000C – Leandro Maia – CHECKSUM
// Serviço 0x02 negado (Apelido já está em uso)
Servidor -> Cliente: 0x7F – 0x0001 – 0x02 – CHECKSUM
// Aviso de mensagem com o checksum errado
Servidor -> Cliente: 0x7F – 0x0001 – 0xFF – CHECKSUM
// Aviso de mensagem que ainda não aceitou o OLÁ
Servidor -> Cliente: 0x7F – 0x0001 – 0xEE – CHECKSUM

5. Comportamentos:
· O servidor não aceitará qualquer serviço do cliente até que ele se
apresente através do serviço Olá (0x01).
· Qualquer mensagem mal formada (ex.: Mensagem de olá sem o apelido,
etc) deverá ser negada.
· Cliente ou servidor, ao receber 3 mensagens seguidas com checksum
inválido, encerrará a conexão com o par.
· Todas as mensagens enviadas pelo cliente deverão receber
confirmação.
· O servidor não envia qualquer mensagem sem requisição.
· Todas as mensagens recebidas, tanto por parte do cliente quanto por
parte do servidor, deverão ser armazenadas em um buffer de mensagens
e assim que for armazenada, o tratador deverá ser notificado.
· IMPORTANTE: Tanto cliente, quanto servidor, deverá funcionar
completamente até a camada de negócios!

6. Resumo dos motivos Nack:
MOTIVO_CHECKSUM_INVALIDO = (byte)0xFF;
MOTIVO_CLIENTE_SEM_OLA = (byte)0xEE;
MOTIVO_MENSAGEM_MAL_FORMADA = (byte)0xDD;
MOTIVO_CLIENTE_NAO_IDENTIFICADO = (byte)0xCC;
MOTIVO_CLIENTE_JA_ESTA_REGISTRADO = (byte)0xBB;

7. Auxílio:
Junto com a especificação do trabalho existe uma classe chamada
"Uteis". Essa classe poderá ser utilizada livremente no trabalho.