/*link all holidays to the head office*/
insert into office_holiday (holiday_id, office_id) 
select h.holiday_id, o.office_id from holiday h, office o
where o.office_level_id = 1;