alter table anchor.mentoring_unavailable_time
    add foreign key (mentor_id) references mentor (id);