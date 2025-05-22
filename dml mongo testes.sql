

db.orders.find()



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




{
    "before": null,
    "after": "{\"_id\": {\"$oid\": \"6811137d0c9f202a34d861e0\"},\"id\": \"64429e987a8b646915b3735f\",\"products\": [{\"product\": {\"code\": \"COMIC_BOOKS\",\"unitValue\": 15.5},\"quantity\": 3},{\"product\": {\"code\": \"BOOKS\",\"unitValue\": 9.9},\"quantity\": 1}],\"totalItems\": 4,\"totalAmount\": 25.4,\"createdAt\": \"2025-04-29T14:32:56.335943085\",\"updatedAt\": \"2025-04-29T14:32:56.335943085\"}",
    "updateDescription": null,
    "source": {
        "version": "2.7.0.Final",
        "connector": "mongodb",
        "name": "mongo",
        "ts_ms": 1745949565000,
        "snapshot": "false",
        "db": "ordersdb",
        "sequence": null,
        "ts_us": 1745949565000000,
        "ts_ns": 1745949565000000000,
        "collection": "orders",
        "ord": 2,
        "lsid": null,
        "txnNumber": null,
        "wallTime": 1745949565621
    },
    "op": "c",
    "ts_ms": 1745949565830,
    "transaction": null
}

{
    "before": null,
    "after": "{\"_id\": {\"$oid\": \"68122a1c92da88b53346576c\"},\"id\": \"64429e987a8b646915b3735f\",\"products\": [{\"product\": {\"code\": \"COMIC_BOOKS\",\"unitValue\": 15.5},\"quantity\": 3},{\"product\": {\"code\": \"BOOKS\",\"unitValue\": 9.9},\"quantity\": 1}],\"totalItems\": 0,\"totalAmount\": 25.4,\"createdAt\": \"2025-04-29T14:32:56.335943085\",\"updatedAt\": {\"$date\": 1746034410610}}",
    "updateDescription": {
        "removedFields": null,
        "updatedFields": "{\"totalItems\": 0, \"updatedAt\": {\"$date\": \"2025-04-30T17:33:30.61Z\"}}",
        "truncatedArrays": null
    },
    "source": {
        "version": "2.7.0.Final",
        "connector": "mongodb",
        "name": "mongo",
        "ts_ms": 1746034410000,
        "snapshot": "false",
        "db": "ordersdb",
        "sequence": null,
        "ts_us": 1746034410000000,
        "ts_ns": 1746034410000000000,
        "collection": "orders",
        "ord": 1,
        "lsid": null,
        "txnNumber": null,
        "wallTime": 1746034410616
    },
    "op": "u",
    "ts_ms": 1746034410820,
    "transaction": null
}

{
    "before": null,
    "after": null,
    "updateDescription": null,
    "source": {
        "version": "2.7.0.Final",
        "connector": "mongodb",
        "name": "mongo",
        "ts_ms": 1745953508000,
        "snapshot": "false",
        "db": "ordersdb",
        "sequence": null,
        "ts_us": 1745953508000000,
        "ts_ns": 1745953508000000000,
        "collection": "orders",
        "ord": 1,
        "lsid": null,
        "txnNumber": null,
        "wallTime": 1745953508204
    },
    "op": "d",
    "ts_ms": 1745953508263,
    "transaction": null
}

Struct{id={"$oid": "68121c1692da88b533465769"}}