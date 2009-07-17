ALTER TABLE QUESTIONS DROP COLUMN SHORT_NAME;
ALTER TABLE QUESTIONS ADD COLUMN SHORT_NAME VARCHAR(50) NOT NULL;
ALTER TABLE QUESTIONS ADD UNIQUE(SHORT_NAME);
ALTER TABLE SURVEY_RESPONSE ADD COLUMN MULTI_SELECT_VALUE TEXT;

update DATABASE_VERSION set DATABASE_VERSION = 132 where DATABASE_VERSION = 131;