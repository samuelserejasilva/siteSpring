/* 
  V3 — normaliza a unicidade de (provider, provider_id)
  - Garante NOT NULL nos campos
  - Remove qualquer índice/constraint anterior com o mesmo nome
  - Cria um ÚNICO índice consistente
*/

START TRANSACTION;

-- 1) Garante que as colunas não aceitam NULL (boa prática para chaves lógicas únicas)
ALTER TABLE users
  MODIFY provider     VARCHAR(50)  NOT NULL,
  MODIFY provider_id  VARCHAR(255) NOT NULL;

-- 2) Remove qualquer índice/constraint anterior com o nome esperado
--    (em MariaDB, UNIQUE CONSTRAINT é representado como índice; DROP INDEX remove ambos)
ALTER TABLE users DROP INDEX IF EXISTS uk_users_provider_pid;

-- 3) Cria um ÚNICO índice padronizado
CREATE UNIQUE INDEX uk_users_provider_pid ON users(provider, provider_id);

COMMIT;
