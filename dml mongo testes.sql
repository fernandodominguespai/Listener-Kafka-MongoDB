

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
  { $set: { totalItems: 0, updatedAt: new Date() } }
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