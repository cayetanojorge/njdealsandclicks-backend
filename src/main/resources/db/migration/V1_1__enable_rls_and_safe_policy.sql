-- #############################################################
-- ✅ Attivazione RLS (Row Level Security) + Policy permissive
-- #############################################################
--
-- Questo script abilita la Row Level Security (RLS) su tutte
-- le tabelle pubbliche principali del progetto e definisce una
-- policy base **molto permissiva**:
--
--   - Consente accesso completo (`SELECT`, `INSERT`, `UPDATE`, `DELETE`)
--   - A utenti autenticati (`authenticated`) via Supabase JWT
--   - E ai backend/servizi con `service_role` (es. Spring Boot con JDBC)
--
-- Le clausole:
--   - `USING (true)`: tutte le righe sono visibili
--   - `WITH CHECK (true)`: tutte le righe possono essere inserite/modificate
--
-- ⚠️ Questa configurazione **non impone nessun filtro** sui dati.
--    È pensata per:
--      - ambienti interni
--      - backend che già implementano autorizzazioni custom
--      - evitare warning/errori dal linter di Supabase
--
-- 🔐 Se in futuro il frontend dovesse accedere direttamente al DB (es. via Supabase client),
--     sarà opportuno ridefinire policy più restrittive per singolo ruolo/utente.
--
-- 📌 Per maggiori info: https://supabase.com/docs/guides/auth/row-level-security
--
--
-- Gli RLS sono regole a livello di riga in PostgreSQL/Supabase che permettono di controllare chi può vedere o modificare quali righe, anche se si usa lo stesso ruolo utente.
-- 👉 In parole semplici:
-- . Senza RLS: se hai accesso alla tabella, puoi vedere tutte le righe.
-- . Con RLS attivo: solo le righe che rispettano la policy definita ti saranno accessibili.
-- Servono soprattutto quando:
-- . usi Supabase client dal frontend via API, e quindi vuoi limitare cosa può leggere l’utente loggato
-- . più utenti accedono allo stesso schema ma devono vedere solo i loro dati
--
-- #############################################################


-- CATEGORY
ALTER TABLE public.category ENABLE ROW LEVEL SECURITY;
CREATE POLICY category_full_access
  ON public.category
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- ENTITY_INITIALIZED
ALTER TABLE public.entity_initialized ENABLE ROW LEVEL SECURITY;
CREATE POLICY entity_initialized_full_access
  ON public.entity_initialized
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- SUBSCRIPTION
ALTER TABLE public.subscription ENABLE ROW LEVEL SECURITY;
CREATE POLICY subscription_full_access
  ON public.subscription
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- APP_USER
ALTER TABLE public.app_user ENABLE ROW LEVEL SECURITY;
CREATE POLICY app_user_full_access
  ON public.app_user
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- CURRENCY
ALTER TABLE public.currency ENABLE ROW LEVEL SECURITY;
CREATE POLICY currency_full_access
  ON public.currency
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- COUNTRY
ALTER TABLE public.country ENABLE ROW LEVEL SECURITY;
CREATE POLICY country_full_access
  ON public.country
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- NEWSLETTER
ALTER TABLE public.newsletter ENABLE ROW LEVEL SECURITY;
CREATE POLICY newsletter_full_access
  ON public.newsletter
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- NEWSLETTER_CATEGORY
ALTER TABLE public.newsletter_category ENABLE ROW LEVEL SECURITY;
CREATE POLICY newsletter_category_full_access
  ON public.newsletter_category
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- PRODUCT
ALTER TABLE public.product ENABLE ROW LEVEL SECURITY;
CREATE POLICY product_full_access
  ON public.product
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- NEWSLETTER_PRODUCT
ALTER TABLE public.newsletter_product ENABLE ROW LEVEL SECURITY;
CREATE POLICY newsletter_product_full_access
  ON public.newsletter_product
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);

-- PRICE_HISTORY
ALTER TABLE public.price_history ENABLE ROW LEVEL SECURITY;
CREATE POLICY price_history_full_access
  ON public.price_history
  FOR ALL
  TO authenticated, service_role
  USING (true)
  WITH CHECK (true);




-- | Parte                            | Significato                                                              |
-- | -------------------------------- | ------------------------------------------------------------------------ |
-- | `ENABLE ROW LEVEL SECURITY`      | Attiva la protezione RLS per la tabella, necessaria per far funzionare   |
-- |                                  | qualsiasi `POLICY`.                                                      |
-- | `FOR ALL`                        | Applica la policy a tutte le operazioni: `SELECT`, `INSERT`,             |
-- |                                  | `UPDATE`, `DELETE`. Equivale a scrivere ognuna di esse esplicitamente.   |
-- | `TO authenticated, service_role` | Autorizza l'accesso solo agli utenti autenticati tramite Supabase JWT    |
-- |                                  | o backend con `service_role` (es. JDBC usando `postgres` o altro utente) |
-- | `USING (true)`                   | Nessun filtro sulle righe **visibili**: tutte sono accessibili.          |
-- | `WITH CHECK (true)`              | Nessun filtro sulle righe **inseribili o modificabili**: tutto è ammesso.|
