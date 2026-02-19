-- No seu script de init ou via terminal após subir o banco:
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuários de teste (As senhas devem estar criptografadas com BCrypt no seu app)
INSERT INTO users (username, email, password, role) VALUES
('admin_user', 'admin@test.com', '$2a$10$YourBCryptHashHere', 'ROLE_ADMIN'),
('manager_user', 'manager@test.com', '$2a$10$YourBCryptHashHere', 'ROLE_MANAGER'),
('sub_user', 'sub@test.com', '$2a$10$YourBCryptHashHere', 'ROLE_SUB'),
('common_user', 'user@test.com', '$2a$10$YourBCryptHashHere', 'ROLE_USER');