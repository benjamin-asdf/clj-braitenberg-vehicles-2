-- todo















-- :name create-pledges-table
-- :command :execute
-- :result :raw
-- :doc Create pledges table
CREATE TABLE pledge (
  id serial PRIMARY KEY,
  data text NOT NULL,
  created_at timestamp with time zone DEFAULT now(),
  status text DEFAULT 'unresolved'
);



-- :name pledge-by-id :? :1
-- :doc Get pledge by id
select * from pledge
where id = :id





-- :name characters-by-ids-specify-cols :? :*
-- :doc Characters with returned columns specified
select :i*:cols from characters
where id in (:v*:ids)


-- -------------------------- ----------------------------
