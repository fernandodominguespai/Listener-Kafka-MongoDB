#!/bin/bash

echo "🚀 Iniciando ou corrigindo Replica Set no MongoDB..."

# Tenta iniciar o Replica Set (ignora erro se já estiver iniciado)
docker exec -i order-db mongosh --eval "
try {
  rs.status();
} catch(e) {
  rs.initiate();
}
"

sleep 5

# Corrige o host do Replica Set para 'order-db:27017'
docker exec -i order-db mongosh --eval "
cfg = rs.conf();
cfg.members[0].host = 'order-db:27017';
rs.reconfig(cfg, {force: true});
"

echo "✅ Replica Set corrigido!"

sleep 5

# criar a collection com pre-images habilitado 
# Confirme  docker exec -it order-db mongosh --eval "db.adminCommand({ getParameter: 1, enableMajorityReadConcern: 1 })"
docker exec -i order-db mongosh --eval "
use ordersdb;
db.createCollection('orders', {
  changeStreamPreAndPostImages: { enabled: true }
});
"

echo "✅ criar a collection com pre-images habilitado!"

sleep 5

# Ative pre-images na collection:
# docker exec -it order-db mongosh --eval "db.getCollectionInfos({ name: \"orders\" })"  
# db.getCollectionInfos({ name: "orders" })
docker exec -i order-db mongosh --eval "
use ordersdb;
db.runCommand({
  collMod: "orders",
  changeStreamPreAndPostImages: { enabled: true }
});
"

echo "✅ Ative pre-images na collection!"

sleep 5

echo "🔥 Deletando conector antigo (se existir)..."

# Deleta o conector antigo
curl -X DELETE http://localhost:8083/connectors/mongo-connector || true

sleep 5

echo "🚀 Criando novo conector MongoDB -> Kafka..."

# Cria o novo conector MongoDB -> Kafka
curl -X POST http://localhost:8083/connectors \
-H "Content-Type: application/json" \
-d '{
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
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false"
  }
}'

echo "✅ Novo conector criado com sucesso!"

echo "🎯 Tudo pronto! Agora insira documentos no MongoDB para testar."
