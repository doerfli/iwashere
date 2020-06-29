create table visit (
    id int8 not null,
    guest_id int8 not null,
    location_id int8 not null,
    registration_date timestamp not null,
    primary key (id),
    foreign key (guest_id) REFERENCES guest(id),
    foreign key (location_id) REFERENCES location(id)
);

create index
    idx_visit_guest_id ON visit(guest_id);
create index
    idx_visit_location_id ON visit(location_id);
