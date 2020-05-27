insert into Address (country, region, zipcode, city, street, number, supplement)
values ('Slovakia', '', '08235', N'Štefanovce', '', '51', '');
insert into Address (country, region, zipcode, city, street, number, supplement)
values ('Slovakia', '', '97101', 'Prievidza', N'Dlhá', '26', '');
insert into Store (name, email, password, addressID)
values ('store', 'store@mail.com', 'password', 1);
insert into Warehouse (name, email, password, addressID)
values ('warehouse', 'warehouse@mail.com', 'password', 2);
GO;

insert into Product (name, weight, price) VALUES ('Deodorant', 0.12, 1.20);
insert into Product (name, weight, price) VALUES ('Shampoo', 0.2, 2.50);
insert into Product (name, weight, price) VALUES ('Washing gel', 1, 3);
insert into Product (name, weight, price) values ('Washing powder', 5, 2);
insert into Product (name, weight, price) values ('Dishwaser tablets', 0.5, 5);
GO;


insert into Stock (warehouseID, productID, quantity, minQuantity) VALUES (1, 1, 500, 100);
insert into Stock (warehouseID, productID, quantity, minQuantity) VALUES (1, 2, 100, 150);
insert into Stock (warehouseID, productID, quantity, minQuantity) VALUES (1, 3, 50, 49);
insert into Stock (warehouseID, productID, quantity, minQuantity) VALUES (1, 4, 300, 200);
insert into Stock (warehouseID, productID, quantity, minQuantity) VALUES (1, 5, 200, 150);
GO;

insert into [Order] (storeID, warehouseID, status, date) values (1, 1, 'PENDING', '2012-06-18T10:34:09');
insert into [Order] (storeID, warehouseID, status, date) values (1, 1, 'PENDING', '2012-06-18T10:34:09');
insert into OrderItem (orderID, quantity, unitPrice, productID, orderRevisionID) VALUES (1, 5, 2, 4, NULL);
insert into OrderItem (orderID, quantity, unitPrice, productID, orderRevisionID) VALUES (2, 4, 5, 5, NULL);
insert into OrderRevision (orderID, status, date, note)
VALUES (1, 'PENDING', '2012-06-18T10:34:09', 'Bring it fast');
insert into OrderRevision (orderID, status, date, note)
VALUES (2, 'PENDING', '2012-06-18T10:34:09', 'I do not care when you bring it');
insert into OrderItem (orderID, quantity, unitPrice, productID, orderRevisionID) VALUES (NULL, 5, 2, 4, 1);
insert into OrderItem (orderID, quantity, unitPrice, productID, orderRevisionID) VALUES (null, 4, 5, 5, 2);
GO;

insert into Address (country, region, zipcode, city, street, number, supplement)
VALUES ('Slovakia', '', '08001', 'Presov', 'Strojnicka', '223', '');
insert into Provider (name, email, available, addressID)
VALUES ('provider123', 'provider123@mail.com', 1, 3);
GO;

insert into StoreStockReport (storeID, date, note)
VALUES (1, '2012-06-18T10:34:09', 'First Report');
insert into StoreStockReport (storeID, date, note)
VALUES (1, '2017-06-18T10:34:09', 'Second Report');
insert into StoreStockReportItem (storeStockReportID, quantity, productID)
VALUES (1, 3, 1);
insert into StoreStockReportItem (storeStockReportID, quantity, productID)
VALUES (1, 5, 2);
insert into StoreStockReportItem (storeStockReportID, quantity, productID)
VALUES (2, 10, 3);
insert into StoreStockReportItem (storeStockReportID, quantity, productID)
VALUES (2, 3, 4);
insert into StoreStockReportItem (storeStockReportID, quantity, productID)
VALUES (2, 3, 5);
GO;
