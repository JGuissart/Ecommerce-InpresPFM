CREATE TABLE StatistiquesDescriptives
(
	idStatistiquesDescriptives INT PRIMARY KEY AUTO_INCREMENT,
	TypeMouvement VARCHAR(3),
	Moyenne DOUBLE,
	Mediane DOUBLE,
	EcartType DOUBLE,
	Mode VARCHAR(200)
);

CREATE TABLE StatsInferConformite
(
	idStatsInferConformite INT PRIMARY KEY AUTO_INCREMENT,
	pValue DOUBLE,
	TailleEchantillon INT,
	ReponseHypothese VARCHAR(200)
);

CREATE TABLE StatsInferHomogeneite
(
	idStatsInferHomogeneite INT PRIMARY KEY AUTO_INCREMENT,
	pValue DOUBLE,
	Destination1 VARCHAR(30),
	TailleEchantillon1 INT,
	Destination2 VARCHAR(30),
	TailleEchantillon2 INT,
	ReponseHypothese VARCHAR(200)
);

CREATE TABLE StatsInferANOVA
(
	idStatsInferANOVA INT PRIMARY KEY AUTO_INCREMENT,
	pValue DOUBLE,
	TailleEchantillon1 INT,
	TailleEchantillon2 INT,
	ReponseHypothese VARCHAR(200)
);