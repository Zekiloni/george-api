\echo '=== Initializing PostgreSQL for BTCPay Server, NBXplorer, and Keycloak ==='

-- Kreiramo baze samo ako već ne postoje
SELECT 'CREATE DATABASE btcpay WITH OWNER = ''g-user'' ENCODING = ''UTF8'' LC_COLLATE = ''en_US.utf8'' LC_CTYPE = ''en_US.utf8'''
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'btcpay')\gexec

SELECT 'CREATE DATABASE nbxplorer WITH OWNER = ''g-user'' ENCODING = ''UTF8'' LC_COLLATE = ''en_US.utf8'' LC_CTYPE = ''en_US.utf8'''
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'nbxplorer')\gexec

SELECT 'CREATE DATABASE keycloak WITH OWNER = ''g-user'' ENCODING = ''UTF8'' LC_COLLATE = ''en_US.utf8'' LC_CTYPE = ''en_US.utf8'''
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak')\gexec

SELECT 'CREATE DATABASE george WITH OWNER = ''g-user'' ENCODING = ''UTF8'' LC_COLLATE = ''en_US.utf8'' LC_CTYPE = ''en_US.utf8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'george')\gexec

-- Dajemo sve privilegije korisniku g-user
GRANT ALL PRIVILEGES ON DATABASE btcpay TO "g-user";
GRANT ALL PRIVILEGES ON DATABASE nbxplorer TO "g-user";
GRANT ALL PRIVILEGES ON DATABASE keycloak TO "g-user";
GRANT ALL PRIVILEGES ON DATABASE george TO "g-user";

-- Kreiramo public schema i dajemo ownership (za sigurnost)
\c btcpay
CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION "g-user";
GRANT ALL ON SCHEMA public TO "g-user";

\c nbxplorer
CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION "g-user";
GRANT ALL ON SCHEMA public TO "g-user";

\c keycloak
CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION "g-user";
GRANT ALL ON SCHEMA public TO "g-user";

\c george
CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION "g-user";
GRANT ALL ON SCHEMA public TO "g-user";

-- Vraćamo se na default bazu
\c postgres

\echo '=== PostgreSQL initialization complete! ==='