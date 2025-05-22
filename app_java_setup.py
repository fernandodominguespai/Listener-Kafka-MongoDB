#%% importando frameworks
# importando frameworks
import os
import threading
import subprocess

#%% iniciando metodos
# iniciando metodos
def is_container_running(container_name="order-db"):
    print(f"[CHECK] Verificando se o container '{container_name}' está rodando...")
    result = subprocess.run(
        ["docker", "ps", "--filter", f"name={container_name}", "--filter", "status=running", "--format", "{{.Names}}"],
        shell=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )
    if container_name in result.stdout:
        print(f"[CHECK] Container '{container_name}' está rodando.\n")
        return True
    else:
        print(f"[ERRO] Container '{container_name}' NÃO está rodando. Interrompendo pipeline.\n")
        return False


def build_application(app_path):
    print(f"[BUILD] Iniciando build da aplicação em: {app_path}")
    result = subprocess.run(
        ["gradle", "build", "-x", "test"],
        cwd=app_path,
        shell=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )
    print(f"[BUILD] Resultado do build de {app_path}:\n{result.stdout}")
    print(f"[BUILD] Aplicação {app_path} finalizada!\n")


def docker_compose_up():
    print("[DOCKER] Subindo container 'order-service' com Docker Compose...")
    result = subprocess.run(
        ["docker-compose", "up", "-d", "order-service"],
        shell=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True
    )
    print("[DOCKER] Resultado:\n", result.stdout)
    print("[DOCKER] Pipeline finalizado!")

#%% Execução principal
# Execução principal
if __name__ == "__main__":
    print("[PIPELINE] Pipeline iniciado!\n")

    # Caminho raiz do projeto Java (onde está o build.gradle)
    app_dir = r"C:\ProjetosJava\Arquitetura de Microserviços padrao Saga orquestrado Kafka\Listener Kafka MongoDB\order-service"

    if is_container_running("order-db"):
        build_thread = threading.Thread(target=build_application, args=(app_dir,))
        build_thread.start()
        build_thread.join()

        docker_thread = threading.Thread(target=docker_compose_up)
        docker_thread.start()
        docker_thread.join()
    else:
        print("[PIPELINE] Encerrado devido à dependência não satisfeita (order-db).")

# %%
