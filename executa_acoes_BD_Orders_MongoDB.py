#%% importando frameworks
# importando frameworks
from pymongo import MongoClient
from pymongo.errors import ServerSelectionTimeoutError
from datetime import datetime
import sys
import subprocess

#%% Metodos Conectando ao MongoDB e operações
# Conectando ao MongoDB e operações
def run_mongo_inside_container(js_command):
    try:
        result = subprocess.run(
            ["docker", "exec", "-i", "order-db", "mongosh", "--quiet", "--eval", js_command],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        if result.returncode != 0:
            print("[ERRO] Falha ao executar comando no MongoDB:")
            print(result.stderr)
        else:
            print("[OK] Comando executado com sucesso:")
            print(result.stdout)
    except Exception as e:
        print("[ERRO] Exceção ao tentar executar comando:")
        print(e)

#%% execução principal
# execução principal
if __name__ == "__main__":
    print("Iniciando operações MongoDB dentro do container...")
    
    run_mongo_inside_container("""
        db = connect("ordersdb");
        db.orders.insertOne({
            "id": "64429e987a8b646915b3735f",
            "products": [
                { "product": { "code": "COMIC_BOOKS", "unitValue": 15.5 }, "quantity": 3 },
                { "product": { "code": "BOOKS", "unitValue": 9.9 }, "quantity": 1 }
            ],
            "totalItems": 4,
            "totalAmount": 25.4,
            "createdAt": new Date("2025-04-29T14:32:56.335943085Z"),
            "updatedAt": new Date("2025-04-29T14:32:56.335943085Z")
        });
    """)

    run_mongo_inside_container("""
        db = connect("ordersdb");
        db.orders.updateOne(
            { id: '64429e987a8b646915b3735f' }, 
            { $set: { totalItems: 1, updatedAt: new Date() } }
        );
    """)

    run_mongo_inside_container("""
        db = connect("ordersdb");
        db.orders.deleteOne({ id: '64429e987a8b646915b3735f' });
    """)

# %%
