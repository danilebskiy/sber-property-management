-- Создание пользователя admin для основного приложения
CREATE USER admin WITH PASSWORD 'admin123' SUPERUSER;

-- Создание базы данных для каждого сервиса
CREATE DATABASE task_db;

--CREATE DATABASE user_db;
/*
CREATE DATABASE property_db;
CREATE DATABASE notification_db;
CREATE DATABASE asset_db;
CREATE DATABASE maintenance_db;
CREATE DATABASE report_db;
CREATE DATABASE integration_db;
CREATE DATABASE auth_db;
CREATE DATABASE workflow_db;
CREATE DATABASE audit_db;
*/
-- Создание пользователей
CREATE USER task_user WITH PASSWORD 'task123';
/*
CREATE USER user_user WITH PASSWORD 'user123';
CREATE USER property_user WITH PASSWORD 'property123';
CREATE USER notification_user WITH PASSWORD 'notification123';
CREATE USER asset_user WITH PASSWORD 'asset123';
CREATE USER maintenance_user WITH PASSWORD 'maintenance123';
CREATE USER report_user WITH PASSWORD 'report123';
CREATE USER integration_user WITH PASSWORD 'integration123';
CREATE USER auth_user WITH PASSWORD 'auth123';
CREATE USER workflow_user WITH PASSWORD 'workflow123';
CREATE USER audit_user WITH PASSWORD 'audit123';
*/
-- Выдача прав
ALTER DATABASE task_db OWNER TO task_user;
/*
GRANT ALL PRIVILEGES ON DATABASE user_db TO user_user;
GRANT ALL PRIVILEGES ON DATABASE property_db TO property_user;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO notification_user;
GRANT ALL PRIVILEGES ON DATABASE asset_db TO asset_user;
GRANT ALL PRIVILEGES ON DATABASE maintenance_db TO maintenance_user;
GRANT ALL PRIVILEGES ON DATABASE report_db TO report_user;
GRANT ALL PRIVILEGES ON DATABASE integration_db TO integration_user;
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
GRANT ALL PRIVILEGES ON DATABASE workflow_db TO workflow_user;
GRANT ALL PRIVILEGES ON DATABASE audit_db TO audit_user;
 */

-- Дать права admin на все базы данных
GRANT CONNECT ON DATABASE task_db TO admin;
/*
GRANT ALL PRIVILEGES ON DATABASE user_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE property_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE asset_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE maintenance_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE report_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE integration_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE auth_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE workflow_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE audit_db TO admin;
 */
-- ПРАВА ПОЛЬЗОВАТЕЛЯМ НА ИХ БАЗЫ
GRANT CONNECT ON DATABASE task_db TO task_user;

-- НАСТРОЙКА БАЗЫ TASK_DB
\c task_db admin

REVOKE ALL ON DATABASE task_db FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;

ALTER SCHEMA public OWNER TO task_user;

GRANT USAGE ON SCHEMA public TO task_user;
GRANT CREATE ON SCHEMA public TO task_user;

ALTER DEFAULT PRIVILEGES FOR ROLE task_user IN SCHEMA public
GRANT ALL ON TABLES TO task_user;

ALTER DEFAULT PRIVILEGES FOR ROLE task_user IN SCHEMA public
GRANT ALL ON SEQUENCES TO task_user;

CREATE SCHEMA IF NOT EXISTS task_schema;

--НАЗНАЧЕНИЕ ВЛАДЕЛЬЦА И ПРАВ НА TASK_SCHEMA
ALTER SCHEMA task_schema OWNER TO task_user;

-- Отзыв прав у PUBLIC на рабочую схему
REVOKE ALL ON SCHEMA task_schema FROM PUBLIC;

GRANT USAGE ON SCHEMA task_schema TO task_user;
GRANT CREATE ON SCHEMA task_schema TO task_user;

-- Права на существующие таблицы
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA task_schema TO task_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA task_schema TO task_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA task_schema TO task_user;

-- Права по умолчанию для будущих объектов
ALTER DEFAULT PRIVILEGES FOR ROLE task_user IN SCHEMA task_schema
GRANT ALL ON TABLES TO task_user;

ALTER DEFAULT PRIVILEGES FOR ROLE task_user IN SCHEMA task_schema
GRANT ALL ON SEQUENCES TO task_user;

ALTER DEFAULT PRIVILEGES FOR ROLE task_user IN SCHEMA task_schema
GRANT ALL ON FUNCTIONS TO task_user;

-- Устанавливаем search_path для пользователя task_user
ALTER ROLE task_user SET search_path TO task_schema, public;

-- Устанавливаем search_path для всей базы task_db
ALTER DATABASE task_db SET search_path TO task_schema, public;
-- Включение расширения для UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Админ должен иметь возможность просматривать и управлять объектами
GRANT USAGE ON SCHEMA task_schema TO admin;
GRANT SELECT ON ALL TABLES IN SCHEMA task_schema TO admin;

-- Права по умолчанию для админа на будущие таблицы
ALTER DEFAULT PRIVILEGES FOR ROLE task_user IN SCHEMA task_schema
GRANT SELECT ON TABLES TO admin;


\c postgres