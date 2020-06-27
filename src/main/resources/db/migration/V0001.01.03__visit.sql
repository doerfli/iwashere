create table visit (
    id int8 not null,
    visitor_id int8 not null,
    location_id int8 not null,
    registration_date timestamp not null,
    primary key (id),
    foreign key (visitor_id) REFERENCES visitor(id),
    foreign key (location_id) REFERENCES location(id)
);

create index
    idx_visit_visitor_id ON visit(visitor_id);
create index
    idx_visit_location_id ON visit(location_id);
