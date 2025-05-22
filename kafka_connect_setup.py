#%% importando frameworks
# importando frameworks
import subprocess
import time
import requests
import json

#%% iniciando metodos
# iniciando metodos
def run_shell_command(command):
    try:
        result = subprocess.check_output(command, shell=True, stderr=subprocess.STDOUT)
        return result.decode().strip()
    except subprocess.CalledProcessError as e:
        return e.output.decode()

def is_container_running(name):
    result = run_shell_command(f'docker ps --filter "name={name}" --filter "status=running" --format "{{{{.Names}}}}"')
    return name in result

def start_containers_if_needed():
    if is_container_running("order-db"):
        print("[INFO] Container 'order-db' está rodando.")
        print("[INFO] Iniciando containers 'kafka' e 'kafka-connect'...")
        return run_shell_command("docker-compose up -d kafka kafka-connect")
    else:
        print("[ERRO] Container 'order-db' não está ativo. Abortei.")
        return None

def wait_for_kafka_connect(max_retries=15, delay=4):
    url = "http://localhost:8083/"
    print("[INFO] Aguardando Kafka Connect iniciar...")

    for i in range(max_retries):
        try:
            r = requests.get(url)
            if r.status_code == 200:
                print("[INFO] Kafka Connect está pronto.")
                return True
        except requests.exceptions.ConnectionError:
            pass

        print(f"[INFO] Tentativa {i+1}/{max_retries} falhou. Aguardando {delay}s...")
        time.sleep(delay)

    raise Exception("[ERRO] Kafka Connect não respondeu após múltiplas tentativas.")

def configure_mongo_connector():
    print("[INFO] Enviando configuração para o Kafka Connect...")

    url = "http://localhost:8083/connectors"
    headers = {"Content-Type": "application/json"}
    payload = {
        "name": "mongo-connector",
        "config": {
            "connector.class": "io.debezium.connector.mongodb.MongoDbConnector",
            "tasks.max": "1",
            "mongodb.hosts": "rs0/order-db:27017",
            "mongodb.name": "dbserver1",
            "mongodb.connection.string": "mongodb://order-db:27017/?replicaSet=rs0",
            "database.include.list": "ordersdb",
            "collection.include.list": "ordersdb.orders",
            "topic.prefix": "mongo",
            "snapshot.mode": "initial",
            "capture.mode": "change_streams_update_full",
            "event.processing.failure.handling.mode": "fail",
            "post.fetch.full.document": "required",
            "full.document.before.change": "whenAvailable",
            "value.converter": "org.apache.kafka.connect.json.JsonConverter",
            "key.converter": "org.apache.kafka.connect.json.JsonConverter",
            "key.converter.schemas.enable": False,
            "value.converter.schemas.enable": False
        }
    }

    response = requests.post(url, headers=headers, data=json.dumps(payload))

    if response.status_code in [200, 201]:
        print("[SUCESSO] Conector criado com sucesso.")
    elif response.status_code == 409:
        print("[AVISO] Conector já existe.")
    else:
        print(f"[ERRO] Falha ao criar conector: {response.status_code}\n{response.text}")

def show_connector_status(name="mongo-connector"):
    print("[INFO] Aguardando o conector estar pronto para consulta...")
    time.sleep(5)  # Aguarda 5 segundos antes de tentar consultar
    print("[INFO] Consultando status do conector...")
    try:
        r = requests.get(f"http://localhost:8083/connectors/{name}/status")
        if r.status_code == 200:
            print("[STATUS] Status atual do conector:")
            print(json.dumps(r.json(), indent=2))
        else:
            print(f"[ERRO] Não foi possível obter o status: {r.status_code}")
            print(r.text)
    except Exception as e:
        print(f"[ERRO] Falha ao consultar status: {e}")

def show_connector_config(name="mongo-connector"):
    print("[INFO] Consultando configuração do conector...")
    try:
        r = requests.get(f"http://localhost:8083/connectors/{name}/config")
        if r.status_code == 200:
            print("[CONFIG] Configuração atual do conector:")
            print(json.dumps(r.json(), indent=2))
        else:
            print(f"[ERRO] Não foi possível obter a configuração: {r.status_code}")
            print(r.text)
    except Exception as e:
        print(f"[ERRO] Falha ao consultar configuração: {e}")


#%% Execução principal
# Execução principal
if __name__ == "__main__":
    start_result = start_containers_if_needed()
    if start_result is not None:
        print(start_result, "\n")
        wait_for_kafka_connect()
        configure_mongo_connector()
        show_connector_status()
        show_connector_config()

# %% TESTES EXEC
# if __name__ == "__main__":
#     show_connector_config()


# %%
