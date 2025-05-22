#%% importando frameworks
# importando frameworks
import subprocess
import time

#%% iniciando metodos
# iniciando metodos
def run_shell_command(command):
    try:
        result = subprocess.check_output(command, shell=True, stderr=subprocess.STDOUT)
        return result.decode()
    except subprocess.CalledProcessError as e:
        return e.output.decode()

def run_mongo_command(mongo_command):
    cmd = f'docker exec order-db mongosh --quiet --eval "{mongo_command}"'
    return run_shell_command(cmd)

def start_container(service_name="order-db"):
    command = f"docker-compose up --build -d {service_name}"
    return run_shell_command(command)

def wait_for_mongo(max_retries=10, delay=3):
    print("Aguardando o MongoDB iniciar...")
    for i in range(max_retries):
        result = run_mongo_command('db.runCommand({ ping: 1 })')
        if "ok" in result:
            print("MongoDB está pronto.")
            return True
        print(f"Tentativa {i+1} falhou. Aguardando {delay} segundos...")
        time.sleep(delay)
    raise Exception("MongoDB não respondeu após múltiplas tentativas.")

def initiate_replica_set_if_needed():
    check_status_cmd = (
        "docker exec order-db mongosh --quiet --eval "
        "\"try { var status = rs.status(); printjson(status); } "
        "catch(e) { print(e.codeName); }\""
    )
    result = run_shell_command(check_status_cmd)
    if "NotYetInitialized" in result or "no replset config" in result:
        print("Replica Set ainda não iniciado. Iniciando com rs.initiate()...")
        return run_mongo_command("rs.initiate();")
    elif "ok" in result:
        print("Replica Set já está iniciado.")
        return result
    else:
        print("Resultado inesperado ao verificar o Replica Set:")
        return result

#%% executando programa
# executando programa
if __name__ == "__main__":
    print("[Etapa 0] Iniciando container...")
    step0 = start_container()
    print(step0, "\n")

    # Etapa 1: Esperar Mongo iniciar
    wait_for_mongo()

    # Etapa 2: Iniciar ou checar Replica Set
    print("[Etapa 2] Verificando estado do Replica Set...")
    step1 = initiate_replica_set_if_needed()
    print(step1, "\n")

    # Etapa 3: Corrigir o host do Replica Set
    print("[Etapa 3] Corrigindo host do Replica Set...")
    step2 = run_mongo_command(
        "cfg = rs.conf(); cfg.members[0].host = 'order-db:27017'; rs.reconfig(cfg, {force: true});"
    )
    print(step2, "\n")

    # Etapa 4: Criar a collection com pre-images ativado
    print("[Etapa 4] Criando collection com pre-images...")
    js_commands = (
        "db = db.getSiblingDB('ordersdb');"
        "db.createCollection('orders');"
        "db.runCommand({ collMod: 'orders', changeStreamPreAndPostImages: { enabled: true } });"
        "printjson(db.getCollectionInfos({ name: 'orders' }));"
    )
    
    step3 = run_mongo_command(js_commands)
    print(step3, "\n")

    # Etapa 5: Ativar pre-images com collMod
    print("[Etapa 5] Ativando pre-images com collMod...")
    js_commands = (
        "db = db.getSiblingDB('ordersdb');"
        "db.runCommand({ collMod: 'orders', changeStreamPreAndPostImages: { enabled: true } });"
        "printjson(db.getCollectionInfos({ name: 'orders' }));"
    )
    step4 = run_mongo_command(js_commands)
    print(step4, "\n")

    

 

    

# %%
