CREATE TABLE pledge (
  id serial PRIMARY KEY,
  data text NOT NULL,
  created_at timestamp with time zone DEFAULT now(),
  status text DEFAULT 'unresolved'
);
