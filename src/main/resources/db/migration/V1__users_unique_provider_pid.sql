/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  debbi
 * Created: 14 de set. de 2025
 */

-- garante unicidade por provider + provider_id (já cria índice)
ALTER TABLE users
  ADD CONSTRAINT uk_users_provider_pid UNIQUE (provider, provider_id);

DROP INDEX IF EXISTS uk_users_provider_pid ON users;
CREATE UNIQUE INDEX uk_users_provider_pid ON users(provider, provider_id);
