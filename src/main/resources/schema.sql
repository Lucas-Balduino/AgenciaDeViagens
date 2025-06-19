-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS agencia_de_viagens;
USE agencia_de_viagens;

-- Tabela de serviços
CREATE TABLE IF NOT EXISTS servicos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    duracao INT NOT NULL,
    descricao TEXT
);

-- Tabela de pacotes
CREATE TABLE IF NOT EXISTS pacotes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    duracao INT NOT NULL,
    preco DECIMAL(10,2) NOT NULL
);

-- Tabela de ligação entre pacotes e serviços (1 pacote pode ter vários serviços)
CREATE TABLE IF NOT EXISTS pacote_servico (
    pacote_id BIGINT,
    servico_id BIGINT,
    PRIMARY KEY (pacote_id, servico_id),
    FOREIGN KEY (pacote_id) REFERENCES pacotes(id) ON DELETE CASCADE,
    FOREIGN KEY (servico_id) REFERENCES servicos(id) ON DELETE CASCADE
);

-- Tabela de clientes nacionais
CREATE TABLE IF NOT EXISTS nacional (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    cpf VARCHAR(14) UNIQUE NOT NULL
);

-- Tabela de clientes estrangeiros
CREATE TABLE IF NOT EXISTS estrangeiro (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100),
    passaporte VARCHAR(20) UNIQUE NOT NULL
);

-- Tabela de ligação entre clientes nacionais e pacotes
CREATE TABLE IF NOT EXISTS nacional_pacotes (
    nacional_id BIGINT,
    pacote_id BIGINT,
    PRIMARY KEY (nacional_id, pacote_id),
    FOREIGN KEY (nacional_id) REFERENCES nacional(id) ON DELETE CASCADE,
    FOREIGN KEY (pacote_id) REFERENCES pacotes(id) ON DELETE CASCADE
);

-- Tabela de ligação entre clientes estrangeiros e pacotes
CREATE TABLE IF NOT EXISTS estrangeiro_pacotes (
    estrangeiro_id BIGINT,
    pacote_id BIGINT,
    PRIMARY KEY (estrangeiro_id, pacote_id),
    FOREIGN KEY (estrangeiro_id) REFERENCES estrangeiro(id) ON DELETE CASCADE,
    FOREIGN KEY (pacote_id) REFERENCES pacotes(id) ON DELETE CASCADE
);


INSERT INTO servicos (nome, duracao, descricao) VALUES
('Translado Aeroporto-Hotel', 1, 'Transporte do aeroporto até o hotel'),
('Passeio Histórico', 1, 'Tour guiado pelo centro histórico'),
('Mergulho com cilindro', 1, 'Atividade de mergulho com equipamento'),
('Jantar típico', 1, 'Jantar regional incluso'),
('Seguro viagem', 7, 'Cobertura básica durante toda a viagem');

INSERT INTO pacotes (nome, destino, duracao, preco) VALUES
('Sol e Mar', 'Fortaleza', 7, 1899.99),
('Aventura Amazônica', 'Manaus', 10, 2599.90),
('Cultura e História', 'Ouro Preto', 5, 1599.50);

-- Pacote 1: Sol e Mar
INSERT INTO pacote_servico (pacote_id, servico_id) VALUES
(1, 1), -- Translado
(1, 3), -- Mergulho
(1, 5); -- Seguro

-- Pacote 2: Aventura Amazônica
INSERT INTO pacote_servico (pacote_id, servico_id) VALUES
(2, 1),
(2, 2),
(2, 5);

-- Pacote 3: Cultura e História
INSERT INTO pacote_servico (pacote_id, servico_id) VALUES
(3, 1),
(3, 2),
(3, 4),
(3, 5);

INSERT INTO nacional (nome, telefone, email, cpf) VALUES
('Carlos Silva', '61988884444', 'carlos@email.com', '123.456.789-00'),
('Fernanda Lima', '61999993333', 'fernanda@email.com', '321.654.987-00');

INSERT INTO estrangeiro (nome, telefone, email, passaporte) VALUES
('John Smith', '+1 202-555-0123', 'john.smith@usa.com', 'A123456789'),
('Marie Dubois', '+33 7 12 34 56 78', 'marie.dubois@france.fr', 'FR987654321');

-- Carlos comprou os pacotes 1 e 2
INSERT INTO nacional_pacotes (nacional_id, pacote_id) VALUES
(1, 1),
(1, 2);

-- Fernanda comprou o pacote 3
INSERT INTO nacional_pacotes (nacional_id, pacote_id) VALUES
(2, 3);


-- John comprou o pacote 1
INSERT INTO estrangeiro_pacotes (estrangeiro_id, pacote_id) VALUES
(1, 1);

-- Marie comprou os pacotes 2 e 3
INSERT INTO estrangeiro_pacotes (estrangeiro_id, pacote_id) VALUES
(2, 2),
(2, 3);

