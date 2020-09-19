alter table location
    add column use_table_number boolean not null default false,
    add column use_sector boolean not null default false;
