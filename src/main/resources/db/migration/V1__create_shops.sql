CREATE TABLE shops (
                       id       BIGSERIAL PRIMARY KEY,
                       name     VARCHAR(120) NOT NULL,
                       address  VARCHAR(255) NOT NULL,
                       category VARCHAR(60)  NOT NULL
);

INSERT INTO shops (name, address, category) VALUES
                                                ('Le Fournil',         '12 rue Solferino, Lille',   'Boulangerie'),
                                                ('Boucherie Flamande', '28 rue Solferino, Lille',   'Boucherie'),
                                                ('Café des Sports',    '5 place Sébastopol, Lille', 'Café');