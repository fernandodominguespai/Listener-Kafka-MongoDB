# Projeto: IDENTIFICAR ALTERAÇÕES NOS REGISTROS NO BD MongoDB E ENVIAR PARA O KAFKA


### Sumário:

* [Tecnologias](#tecnologias)
* [Ferramentas utilizadas](#ferramentas-utilizadas)
* [Arquitetura Proposta](#arquitetura-proposta)
* [Execução do projeto](#execu%C3%A7%C3%A3o-do-projeto)
  * [01 - Execução geral via automação com script em Python](#01---Execucao-geral-via-automacao-com-script-em-Python)
  * [02 - Executando manualmente via CLI(Intlij)](#02---Executando-manualmente-via-CLI-Intlij)
  * [03 - Para parar todos os containers](#03---para-parar-todos-os-containers)
* [As aplicações executarão nas seguintes portas](#As-aplicações-executarão-nas-seguintes-portas)
* [Executar para iniciar a transmissao](#Executar-para-iniciar-a-transmissao)
* [Acesso ao MongoDB](#acesso-ao-mongodb)
* [Acessando tópicos com Redpanda Console](#acessando-t%C3%B3picos-com-redpanda-console)
* [Autor](#Autor)

## Tecnologias

[Voltar ao início](#sum%C3%A1rio)

* **Java 17**
* **Spring Boot 3**
* **Apache Kafka**
* **MongoDB**
* **Docker**
* **docker-compose**
* **Redpanda Console**
* **Apache Kafka-connect**

## Ferramentas utilizadas

[Voltar ao início](#sum%C3%A1rio)

* **IntelliJ IDEA Community Edition**
* **Docker**
* **Gradle**
* **Postman**
* **PowerShel**
* **GitBash**

## Arquitetura Proposta

[Voltar ao início](#sum%C3%A1rio)

Em nossa arquitetura, teremos 4 serviços:

* **MongoDB**: serviço responsável por armazenar os registros e gerar streams para o inicio do processo. changeStreamPreAndPostImages.
* **Apache kafka-connect**: serviço responsável por monitorar as alterções de registro na collection ordersdb.orders MongoDB e produzir(producer) as mensagens.
* **Apache kafka**: serviço message broker, responsável por receber as mensagens geradas pelo kafka-connect e armazenar topico mongo.ordersdb.orders.
* **Java Order-Service**: microsserviço Java com Spring Boot e Gradle, responsável apenas por consumir as novas mensagens geradas no kafka topico mongo.ordersdb.orders. Por enquanto inserir em objetos entidades e mostrar logs.


Todos os serviços da arquitetura irão subir através do arquivo **docker-compose.yml**.

## Execução do projeto

[Voltar ao início](#sum%C3%A1rio)

1. Executar o `script` `mongodb_replica_setup.py` de automação para inicializar a imagem docker MongoDB com o container order-db do docker-compose.yml
   e realizar as configurações pre-requesitos para o banco de dados.
2. Executar o `script` `kafka_connect_setup.py` de automação para inicializar as imagens docker obsidiandynamics/kafka/redpanda com os containers kafka/redpanda-console/kafka-connect do docker-compose.yml 
   e realizar as configurações para o kafka-connect.
3. Executar o `script` `app_java_setup.py` de automação para realizar o build do app order-service 
   e inicializar a imagem docker order-service com a aplicação order-service-0.0.1-SNAPSHOT.jar.
    OU Executando as aplicações manualmente via CLI(inteliJ) (`java -jar` ou `gradle bootRun` ou via IntelliJ)

Para rodar as aplicações, será necessário ter instalado:

* **Docker**
* **Java 17**
* **Gradle 7.6 ou superior**
* **Python 3.11.4**

### 01 - Execucao geral via automacao com script em Python

[Voltar ao nível anterior](#execu%C3%A7%C3%A3o-do-projeto)

Basta executar os arquivos `.py`. Para isto, **é necessário ter o Python 3.11 instalado**.

Para executar: 
1. Entre no diretório raiz do repositório
2. Clicar com lado direito do mouse e "abrir o terminal"
3. Então executar os seguintes comandos:
   1. `python mongodb_replica_setup.py`
   2. `python kafka_connect_setup.py`
   3. `python app_java_setup.py` 

### 02 - Executando manualmente via CLI Intlij

[Voltar ao nível anterior](#execu%C3%A7%C3%A3o-do-projeto)

Para executar:
1. Entre no diretório raiz do repositório
2. Clicar com lado direito do mouse e "abrir o terminal"
3. Então executar os seguintes comandos:
    1. `python mongodb_replica_setup.py`
    2. `python kafka_connect_setup.py`
4. Então executar a aplicação no CLI(Intlij)

### 03 - Para parar todos os containers

[Voltar ao nível anterior](#execu%C3%A7%C3%A3o-do-projeto)

Para parar todos os containers, basta rodar:

`docker-compose down` 

Ou então:

`docker stop ($docker ps -aq)`
`docker container prune -f`

## As aplicações executarão nas seguintes portas:

[Voltar ao início](#sum%C3%A1rio)

* Order-Service: 3000
* Apache Kafka: 9092
* Apache kafka-connect: 8083
* Redpanda Console: 8081
* MongoDB (Order-DB): 27017

### Executar para iniciar a transmissao

O `script` `executa_acoes_BD_Orders_MongoDB.py` para dar o insert, update e delete.

[Voltar ao início](#sum%C3%A1rio)

Payload:

```sql
db.orders.insertOne({
  "id": "64429e987a8b646915b3735f",
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.5
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.9
      },
      "quantity": 1
    }
  ],
  "totalItems": 4,
  "totalAmount": 25.4,
  "createdAt": "2025-04-29T14:32:56.335943085",
  "updatedAt": "2025-04-29T14:32:56.335943085"
});


db.orders.updateOne(
{ id: '64429e987a8b646915b3735f' },
{ $set: { totalItems: 1, updatedAt: new Date() } }
);

db.orders.deleteOne(
{ id: '64429e987a8b646915b3735f' }
);
```

### Acesso ao MongoDB

Para conectar-se ao MongoDB via linha de comando (cli) diretamente do docker-compose, basta executar o comando abaixo:

**docker exec -i order-db mongosh**

Para listar os bancos de dados existentes:

**show dbs**

Para selecionar um banco de dados:

**use ordersdb**

Para visualizar as collections do banco:

**show collections**

Para realizar queries e validar se os dados existem:

**db.orders.find()**

**db.orders.find(id=ObjectId("65235b034a6fa17dc661679b"))**

**db.orders.find({ "products.product.code": "COMIC_BOOKS"})**

[Voltar ao início](#sum%C3%A1rio)

## Acessando tópicos com Redpanda Console

[Voltar ao início](#sum%C3%A1rio)

Para acessar o Redpanda Console e visualizar tópicos e publicar eventos, basta acessar:

http://localhost:8081

Você chegará nesta página onde vai encontrar o *Topico "mongo.ordersdb.orders"* que aparece logo depois da primeira interação com o BD.
Neste voce vai ver as mensagens de insert, update e delete.

![Redpanda](/Redpanda%20Kafka.png)

### Autor

### Fernando Domingues dos Santos
### Engenheiro de dados
