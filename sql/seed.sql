SET NAMES utf8mb4;
USE lavanderia_roger;

CREATE TABLE IF NOT EXISTS loja (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    telefone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS peca (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    preco DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loja_id INT NOT NULL,
    peca_id INT NOT NULL,
    numero VARCHAR(40),
    data_ticket DATE NOT NULL,
    quantidade INT NOT NULL DEFAULT 1,
    descricao VARCHAR(255),
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_ticket_loja FOREIGN KEY (loja_id) REFERENCES loja(id),
    CONSTRAINT fk_ticket_peca FOREIGN KEY (peca_id) REFERENCES peca(id)
);

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE ticket;
TRUNCATE TABLE peca;
TRUNCATE TABLE loja;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO loja (id, nome, telefone) VALUES
(1, 'Hotel Plaza Center', '(11) 3344-2100'),
(2, 'Pousada Beira Rio', '(11) 98765-4321'),
(3, 'Motel Estrela', '(11) 3222-8899'),
(4, 'Hospital Santa Clara', '(11) 3456-1000'),
(5, 'Clinica Ortho Vida', '(11) 3999-2211'),
(6, 'Hotel Executivo Paulista', '(11) 3100-5500'),
(7, 'Spa Harmonia', '(11) 98888-1010'),
(8, 'Restaurante Sabor Mineiro', '(11) 3555-7070');

INSERT INTO peca (id, nome, preco) VALUES
(1, 'Edredom', 45.00),
(2, 'Cortina', 30.00),
(3, 'Lencol', 18.00),
(4, 'Toalha de banho', 8.00),
(5, 'Toalha de rosto', 5.00),
(6, 'Fronha', 6.00),
(7, 'Colcha', 35.00),
(8, 'Roupao', 22.00),
(9, 'Manta', 28.00),
(10, 'Toalha de mesa', 15.00);

INSERT INTO ticket (loja_id, peca_id, numero, data_ticket, quantidade, descricao, valor_total) VALUES
(1, 3, 'T-1840', '2026-08-18', 40, 'Troca semanal dos apartamentos', 720.00),
(1, 4, 'T-1841', '2026-08-18', 60, 'Toalhas do 2 e 3 andar', 480.00),
(1, 6, 'T-1842', '2026-08-18', 40, NULL, 240.00),
(6, 1, 'T-1843', '2026-08-18', 8, 'Edredons da suite executiva', 360.00),
(4, 3, 'T-1844', '2026-08-18', 80, 'Enxoval da internacao', 1440.00),
(8, 10, 'T-1845', '2026-08-18', 12, 'Toalhas do almoco de domingo', 180.00),

(2, 3, 'T-1850', '2026-08-19', 16, 'Check-out da pousada', 288.00),
(2, 4, 'T-1851', '2026-08-19', 20, NULL, 160.00),
(3, 1, 'T-1852', '2026-08-19', 10, 'Edredom com mancha de oleo', 450.00),
(3, 7, 'T-1853', '2026-08-19', 6, 'Urgente para o fim de semana', 210.00),
(7, 8, 'T-1854', '2026-08-19', 14, 'Roupoes da sauna', 308.00),
(7, 4, 'T-1855', '2026-08-19', 25, NULL, 200.00),

(1, 1, 'T-1860', '2026-08-20', 12, 'Suites 201 a 212', 540.00),
(1, 3, 'T-1861', '2026-08-20', 36, NULL, 648.00),
(6, 3, 'T-1862', '2026-08-20', 24, 'Andar corporativo', 432.00),
(6, 6, 'T-1863', '2026-08-20', 24, NULL, 144.00),
(4, 4, 'T-1864', '2026-08-20', 90, 'Toalhas do centro cirurgico', 720.00),
(4, 5, 'T-1865', '2026-08-20', 90, NULL, 450.00),
(5, 3, 'T-1866', '2026-08-20', 10, 'Lencois das enfermarias', 180.00),

(2, 9, 'T-1870', '2026-08-21', 8, 'Mantas do inverno', 224.00),
(8, 10, 'T-1871', '2026-08-21', 18, 'Jantar de sexta, molho de tomate', 270.00),
(8, 2, 'T-1872', '2026-08-21', 4, 'Cortinas do salao', 120.00),
(1, 2, 'T-1873', '2026-08-21', 6, 'Cortinas do lobby', 180.00),
(3, 3, 'T-1874', '2026-08-21', 20, NULL, 360.00),
(7, 9, 'T-1875', '2026-08-21', 10, 'Mantas da sala de massagem', 280.00),

(1, 3, 'T-1880', '2026-08-22', 44, 'Troca do sabado', 792.00),
(1, 4, 'T-1881', '2026-08-22', 55, NULL, 440.00),
(1, 1, 'T-1882', '2026-08-22', 10, 'Edredom com pelo de animal', 450.00),
(6, 7, 'T-1883', '2026-08-22', 8, 'Colchas das suites deluxe', 280.00),
(4, 3, 'T-1884', '2026-08-22', 70, NULL, 1260.00),
(2, 4, 'T-1885', '2026-08-22', 18, 'Check-out lotado', 144.00),

(3, 1, 'T-1890', '2026-08-23', 12, 'Fim de semana, mancha de vinho', 540.00),
(3, 8, 'T-1891', '2026-08-23', 8, NULL, 176.00),
(7, 8, 'T-1892', '2026-08-23', 16, 'Roupoes do spa', 352.00),
(7, 4, 'T-1893', '2026-08-23', 30, 'Toalhas da hidro', 240.00),
(8, 10, 'T-1894', '2026-08-23', 20, 'Almoco de domingo', 300.00),
(1, 5, 'T-1895', '2026-08-23', 40, NULL, 200.00),

(4, 3, 'T-1900', '2026-08-24', 85, 'Enxoval da pediatria', 1530.00),
(4, 4, 'T-1901', '2026-08-24', 100, NULL, 800.00),
(5, 4, 'T-1902', '2026-08-24', 15, 'Toalhas do consultorio', 120.00),
(5, 6, 'T-1903', '2026-08-24', 12, NULL, 72.00),
(1, 3, 'T-1904', '2026-08-24', 32, 'Apartamentos 101 a 116', 576.00),
(6, 1, 'T-1905', '2026-08-24', 6, 'Suites presidenciais', 270.00),
(2, 3, 'T-1906', '2026-08-24', 12, NULL, 216.00),

(1, 3, 'T-1910', '2026-08-25', 38, NULL, 684.00),
(1, 6, 'T-1911', '2026-08-25', 38, 'Fronhas com maquiagem', 228.00),
(6, 3, 'T-1912', '2026-08-25', 28, 'Evento corporativo', 504.00),
(6, 10, 'T-1913', '2026-08-25', 10, 'Toalhas do cafe da manha', 150.00),
(4, 1, 'T-1914', '2026-08-25', 6, 'Edredons da UTI', 270.00),
(7, 2, 'T-1915', '2026-08-25', 3, 'Cortinas da recepcao', 90.00),

(2, 1, 'T-1920', '2026-08-26', 5, 'Chale 4 e 5', 225.00),
(2, 9, 'T-1921', '2026-08-26', 6, NULL, 168.00),
(3, 7, 'T-1922', '2026-08-26', 8, 'Colcha com cheiro de cigarro', 280.00),
(8, 10, 'T-1923', '2026-08-26', 16, 'Jantar a la carte', 240.00),
(1, 2, 'T-1924', '2026-08-26', 4, 'Cortina do restaurante', 120.00),
(5, 3, 'T-1925', '2026-08-26', 8, NULL, 144.00),
(4, 5, 'T-1926', '2026-08-26', 70, 'Toalhas de rosto da clinica', 350.00),

(1, 3, 'T-1930', '2026-08-27', 42, 'Troca da quinta', 756.00),
(1, 4, 'T-1931', '2026-08-27', 50, NULL, 400.00),
(1, 1, 'T-1932', '2026-08-27', 9, 'Edredom molhado de chuva', 405.00),
(6, 8, 'T-1933', '2026-08-27', 12, 'Roupoes do fitness', 264.00),
(4, 3, 'T-1934', '2026-08-27', 75, NULL, 1350.00),
(7, 4, 'T-1935', '2026-08-27', 22, 'Toalhas da piscina', 176.00),
(2, 4, 'T-1936', '2026-08-27', 14, NULL, 112.00),

(1, 3, 'T-1940', '2026-08-28', 40, 'Apartamentos do 4 andar', 720.00),
(1, 7, 'T-1941', '2026-08-28', 7, NULL, 245.00),
(6, 3, 'T-1942', '2026-08-28', 22, 'Hospedes de congresso', 396.00),
(6, 6, 'T-1943', '2026-08-28', 22, NULL, 132.00),
(4, 4, 'T-1944', '2026-08-28', 95, 'Centro cirurgico e UTI', 760.00),
(3, 1, 'T-1945', '2026-08-28', 9, 'Mancha de chocolate', 405.00),
(8, 10, 'T-1946', '2026-08-28', 14, 'Almoco executivo', 210.00),
(5, 4, 'T-1947', '2026-08-28', 12, NULL, 96.00),

(1, 3, 'T-1950', '2026-08-29', 36, 'Troca de sabado', 648.00),
(1, 4, 'T-1951', '2026-08-29', 48, 'Toalhas do 1 andar', 384.00),
(1, 6, 'T-1952', '2026-08-29', 36, NULL, 216.00),
(2, 3, 'T-1953', '2026-08-29', 18, 'Fim de semana na pousada', 324.00),
(2, 4, 'T-1954', '2026-08-29', 22, NULL, 176.00),
(3, 1, 'T-1955', '2026-08-29', 11, 'Edredom com mancha de vinho', 495.00),
(3, 3, 'T-1956', '2026-08-29', 16, NULL, 288.00),
(6, 1, 'T-1957', '2026-08-29', 7, 'Suites 701 a 707', 315.00),
(7, 8, 'T-1958', '2026-08-29', 15, 'Roupoes da sauna e spa', 330.00),
(8, 10, 'T-1959', '2026-08-29', 22, 'Casamento no salao, molho de vinho', 330.00),

(1, 3, 'T-1960', '2026-08-30', 40, 'Troca de domingo', 720.00),
(1, 4, 'T-1961', '2026-08-30', 52, 'Toalhas dos andares 2 e 3', 416.00),
(1, 1, 'T-1962', '2026-08-30', 8, 'Edredons das suites master', 360.00),
(1, 5, 'T-1963', '2026-08-30', 40, NULL, 200.00),
(2, 3, 'T-1964', '2026-08-30', 14, 'Check-out da manha', 252.00),
(2, 9, 'T-1965', '2026-08-30', 5, 'Mantas dos chalés', 140.00),
(3, 7, 'T-1966', '2026-08-30', 7, 'Urgente para hoje a noite', 245.00),
(4, 3, 'T-1967', '2026-08-30', 78, 'Enxoval da internacao', 1404.00),
(4, 4, 'T-1968', '2026-08-30', 88, NULL, 704.00),
(5, 3, 'T-1969', '2026-08-30', 9, 'Lencois das enfermarias', 162.00),
(6, 3, 'T-1970', '2026-08-30', 26, 'Andar corporativo', 468.00),
(6, 8, 'T-1971', '2026-08-30', 10, 'Roupoes do fitness', 220.00),
(7, 4, 'T-1972', '2026-08-30', 28, 'Toalhas da hidro e piscina', 224.00),
(7, 8, 'T-1973', '2026-08-30', 12, NULL, 264.00),
(8, 10, 'T-1974', '2026-08-30', 18, 'Almoco de domingo', 270.00);

ALTER TABLE loja AUTO_INCREMENT = 9;
ALTER TABLE peca AUTO_INCREMENT = 11;
