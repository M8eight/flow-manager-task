create table file_metadata
(
    file_id       uuid primary key,
    status        varchar(10) not null default 'PENDING'
        constraint chk_file_metadata_status check ( status in ('PENDING', 'COMPLETED', 'FAILED') ),
    result_bucket varchar(255),
    result_key    varchar(255),
    error_code    varchar(50),
    error_message text,
    processed_at  timestamptz,
    created_at    timestamptz not null default now()
);

create table outbox
(
    id uuid primary key,
    payload jsonb not null,
    status varchar(10) not null default 'PENDING'
        constraint chk_outbox_status check ( status in ('PENDING', 'SUCCESS', 'FAILED') ),
    retry_attempts int not null default 0,
    next_attempt_at timestamptz default now(),
    last_error text,
    created_at timestamptz not null default now(),
    published_at timestamptz
);

create index idx_outbox_ready on outbox (next_attempt_at) where status = 'PENDING';