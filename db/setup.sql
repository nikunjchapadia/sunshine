CREATE TABLE weather(_id INTEGER PRIMARY KEY,date TEXT NOT NULL,min REAL NOT NULL,max REAL NOT NULL,humidity REAL NOT NULL,pressure REAL NOT NULL);









.tables


.schema


INSERT INTO weather VALUES(1,'20140625',16,20,0,1029);



SELECT * FROM weather;



.header on

SELECT * FROM weather;



INSERT INTO weather VALUES(2,'20140626',17,21,0,1031);
INSERT INTO weather VALUES(3,'20140627',18,22,0,1055);
INSERT INTO weather VALUES(4,'20140628',18,21,10,1070);





SELECT * FROM weather WHERE date == 20140626;



SELECT _id,date,min,max FROM weather WHERE date > 20140625 AND date < 20140628;



SELECT * FROM weather WHERE min >= 18 ORDER BY max ASC;






UPDATE weather SET min = 0, max = 100 where date >= 20140626 AND date <= 20140627;


DELETE FROM weather WHERE humidity != 0;


ALTER TABLE weather ADD COLUMN description TEXT NOT NULL DEFAULT 'Sunny';
